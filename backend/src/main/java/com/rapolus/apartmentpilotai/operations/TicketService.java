package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.domain.OperationsRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.*;
@Service public class TicketService {
    private final Db db;
    private final Commands c;
    public TicketService(Db db,Commands c) {
        this.db=db;
        this.c=c;
    }
    public List<Map<String,Object>> list(Account a,String kind) {
        if(!Set.of("ISSUE","COMPLAINT").contains(kind))throw new IllegalArgumentException("Invalid issue type.");
        return db.rows("select t.*,v.name as vendor_name,(select count(distinct u.flat_id) from ap_ticket_follow f join ap_user u on u.id=f.user_id where f.ticket_id=t.id and u.status='ACTIVE') as affected_count from ap_ticket t left join ap_contact v on v.id=t.vendor_id where t.tenant_id=? and t.kind=?"+(!a.staff()&&kind.equals("COMPLAINT")?" and t.flat_id=?":"")+" order by t.updated_at desc limit 300",!a.staff()&&kind.equals("COMPLAINT")?new Object[] {
            a.tenantId(),kind,a.flatId()
        }
        :new Object[] {
            a.tenantId(),kind
        }
        );
    }
    public Map<String,Object> get(Account a,UUID id) {
        var t=db.one("select t.*,v.name as vendor_name from ap_ticket t left join ap_contact v on v.id=t.vendor_id where t.tenant_id=? and t.id=?",a.tenantId(),id);
        if(!a.staff()&&t.get("kind").equals("COMPLAINT")&&!Objects.equals(a.flatId(),t.get("flatId")))throw ApiError.forbidden();
        t.put("events",db.rows("select e.id,e.message,e.status,e.created_at,u.name as actor from ap_ticket_event e join ap_user u on u.id=e.actor_id where e.tenant_id=? and e.ticket_id=? order by e.created_at",a.tenantId(),id));
        t.put("following",db.count("select count(*) from ap_ticket_follow where ticket_id=? and user_id=?",id,a.id())>0);
        t.put("affectedCount",db.count("select count(distinct u.flat_id) from ap_ticket_follow f join ap_user u on u.id=f.user_id where f.ticket_id=? and u.status='ACTIVE'",id));
        return t;
    }
    public List<Map<String,Object>> affected(Account a,UUID id) {
        a.requireStaff();
        var ticket=get(a,id);
        if(!ticket.get("kind").equals("ISSUE"))throw ApiError.forbidden();
        return db.rows("select * from(select distinct on(f.id) f.id as flat_id,f.label as flat_label,f.block,u.id as user_id,u.name from ap_ticket_follow x join ap_user u on u.tenant_id=x.tenant_id and u.id=x.user_id join ap_flat f on f.tenant_id=u.tenant_id and f.id=u.flat_id where x.tenant_id=? and x.ticket_id=? and u.status='ACTIVE' order by f.id,u.name) affected order by flat_label",a.tenantId(),id);
    }
    @Transactional public Object create(Account a,Map<String,Object> p) {
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"TICKET_CREATE",p);
        if(old.isPresent())return Map.of("id",old.get());
        String kind=V.choice(p,"kind","ISSUE","COMPLAINT"),title=V.text(p,"title",100),category=V.text(p,"category",40),scope=kind.equals("COMPLAINT")?"PRIVATE":V.text(p,"scope",40);
        UUID flat=a.flatId();
        if(a.staff()&&p.get("flatId")!=null)flat=V.optionalId(p,"flatId");
        if(flat!=null)c.flat(a,flat);
        if(kind.equals("COMPLAINT")&&flat==null)throw new IllegalArgumentException("Choose a flat for a private request.");
        UUID id=UUID.randomUUID();
        db.update("insert into ap_ticket(id,tenant_id,kind,flat_id,created_by,title,description,category,scope,priority) values(?,?,?,?,?,?,?,?,?,?)",id,a.tenantId(),kind,flat,a.id(),title,V.text(p,"description",2000),category,scope,V.choice(p,"priority","NORMAL","HIGH","URGENT"));
        event(a,id,"OPEN","Reported");
        if(kind.equals("ISSUE"))db.update("insert into ap_ticket_follow values(?,?,?) on conflict do nothing",a.tenantId(),id,a.id());
        db.notifyStaff(a.tenantId(),"TICKET:"+id,"New "+kind.toLowerCase(Locale.ROOT),title,"ticket:"+id);
        c.remember(a,"TICKET_CREATE",p,id);
        c.audit(a,"TICKET_CREATED",id,null,title);
        return Map.of("id",id);
    }
    private void event(Account a,UUID id,String status,String note) {
        db.update("insert into ap_ticket_event(id,tenant_id,ticket_id,actor_id,message,status) values(?,?,?,?,?,?)",UUID.randomUUID(),a.tenantId(),id,a.id(),note,status);
    }
    @Transactional public Object update(Account a,UUID id,Map<String,Object> p) {
        db.lockTenant(a.tenantId());
        var t=get(a,id);
        var old=c.previous(a,"TICKET_UPDATE",p);
        if(old.isPresent())return Map.of("id",old.get());
        String next=V.choice(p,"status","OPEN","ASSIGNED","IN_PROGRESS","RESOLVED","CLOSED"),note=V.text(p,"note",1000);
        if(!a.staff()&&!t.get("kind").equals("COMPLAINT")&&!Objects.equals(t.get("createdBy"),a.id()))throw ApiError.forbidden();
        OperationsRules.ticketTransition(t.get("status").toString(),next,a.staff());
        UUID vendor=a.staff()?V.optionalId(p,"vendorId"):(UUID)t.get("vendorId");
        if(a.staff())c.vendor(a,vendor);
        if(next.equals("ASSIGNED")&&vendor==null)throw new IllegalArgumentException("Choose a vendor before assigning.");
        Instant eta=a.staff()?V.optionalTime(p,"eta"):(t.get("eta")==null?null:Instant.parse(t.get("eta").toString()));
        boolean broadcast=a.staff()&&t.get("kind").equals("ISSUE")&&V.bool(p,"notifyAll",false);
        db.update("update ap_ticket set status=?,vendor_id=?,eta=?,updated_at=now() where tenant_id=? and id=?",next,vendor,V.sql(eta),a.tenantId(),id);
        event(a,id,next,note);
        UUID event=UUID.randomUUID();
        var users=db.rows("select id from ap_user where tenant_id=? and status='ACTIVE' and (?=true or id=? or role in ('ADMIN','TREASURER') or id in(select user_id from ap_ticket_follow where ticket_id=?))",a.tenantId(),broadcast,t.get("createdBy"),id);
        for(var u:users)db.notify(a.tenantId(),(UUID)u.get("id"),"TICKET-UPDATE:"+event,"Issue updated",next+" · "+t.get("title"),"ticket:"+id);
        c.remember(a,"TICKET_UPDATE",p,id);
        c.audit(a,"TICKET_STATUS",id,t.get("status").toString(),next);
        return Map.of("id",id);
    }
    @Transactional public Object comment(Account a,UUID id,Map<String,Object> p) {
        db.lockTenant(a.tenantId());
        get(a,id);
        var old=c.previous(a,"TICKET_COMMENT",p);
        if(old.isPresent())return Map.of("id",old.get());
        event(a,id,null,V.text(p,"message",2000));
        c.remember(a,"TICKET_COMMENT",p,id);
        return Map.of("id",id);
    }
    @Transactional public Object follow(Account a,UUID id) {
        db.lockTenant(a.tenantId());
        var t=get(a,id);
        if(!t.get("kind").equals("ISSUE"))throw ApiError.forbidden();
        if(Set.of("RESOLVED","CLOSED").contains(t.get("status")))throw ApiError.conflict("This incident has ended.");
        db.update("insert into ap_ticket_follow values(?,?,?) on conflict do nothing",a.tenantId(),id,a.id());
        return Map.of("following",true);
    }
    public List<Map<String,Object>> services(Account a) {
        return db.rows("select s.*,v.name as vendor_name from ap_service s left join ap_contact v on v.id=s.vendor_id where s.tenant_id=?"+(a.staff()?"":" and s.active=true")+" order by s.next_on",a.tenantId());
    }
    public Map<String,Object> service(Account a,UUID id) {
        var s=db.one("select s.*,v.name as vendor_name from ap_service s left join ap_contact v on v.id=s.vendor_id where s.tenant_id=? and s.id=?",a.tenantId(),id);
        var visits=db.rows("select id,scheduled_on,completed_on,note,cost,expense_id from ap_service_visit where tenant_id=? and service_id=? order by completed_on desc",a.tenantId(),id);
        if(!a.staff())visits.forEach(v-> {
            v.remove("cost");
            v.remove("expenseId");
        }
        );
        s.put("visits",visits);
        return s;
    }
    @Transactional public Object saveService(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"SERVICE_SAVE",p);
        if(old.isPresent())return Map.of("id",old.get());
        String title=V.text(p,"title",100),frequency=V.choice(p,"frequency","MONTHLY","QUARTERLY","HALF_YEARLY","YEARLY");
        UUID vendor=V.optionalId(p,"vendorId");
        c.vendor(a,vendor);
        LocalDate next=V.date(p,"nextOn");
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_service(id,tenant_id,title,category,vendor_id,frequency,next_on,active) values(?,?,?,?,?,?,?,?)",id,a.tenantId(),title,V.text(p,"category",40),vendor,frequency,V.sql(next),V.bool(p,"active",true));
        } else {
            service(a,id);
            db.update("update ap_service set title=?,category=?,vendor_id=?,frequency=?,next_on=?,active=? where id=? and tenant_id=?",title,V.text(p,"category",40),vendor,frequency,V.sql(next),V.bool(p,"active",true),id,a.tenantId());
        }
        c.remember(a,"SERVICE_SAVE",p,id);
        c.audit(a,"SERVICE_SAVED",id,null,title);
        return Map.of("id",id);
    }
    @Transactional public Object completeService(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var s=service(a,id);
        var old=c.previous(a,"SERVICE_COMPLETE",p);
        if(old.isPresent())return Map.of("id",old.get());
        if(!Boolean.TRUE.equals(s.get("active")))throw ApiError.conflict("Service is inactive.");
        LocalDate cycle=LocalDate.parse(s.get("nextOn").toString()),requested=V.date(p,"scheduledOn");
        if(!cycle.equals(requested))throw ApiError.conflict("The scheduled cycle changed. Refresh before completing.");
        LocalDate date=V.date(p,"completedOn");
        if(date.isAfter(V.today()))throw new IllegalArgumentException("Completion cannot be future-dated.");
        var cost=V.zeroMoney(p,"cost");
        UUID expense=null,visit=UUID.randomUUID();
        String note=V.text(p,"note",1000);
        if(cost.signum()>0) {
            if(s.get("category").toString().length()>30)throw new IllegalArgumentException("Use a service category of at most 30 characters before recording an expense.");
            if(V.bool(p,"paid",false)&&db.count("select count(*) from ap_settings where tenant_id=? and opening_date>?",a.tenantId(),V.sql(date))>0)throw new IllegalArgumentException("Completion expense predates the opening balance.");
            expense=UUID.randomUUID();
            db.update("insert into ap_expense(id,tenant_id,title,category,amount,paid_on,paid,public,created_by,request_key) values(?,?,?,?,?,?,?,?,?,?)",expense,a.tenantId(),s.get("title"),s.get("category"),cost,V.sql(date),V.bool(p,"paid",false),true,a.id(),V.id(p,"requestKey"));
        }
        db.update("insert into ap_service_visit(id,tenant_id,service_id,scheduled_on,completed_on,note,cost,expense_id) values(?,?,?,?,?,?,?,?)",visit,a.tenantId(),id,V.sql(cycle),V.sql(date),note,cost,expense);
        LocalDate next=OperationsRules.next(cycle,s.get("frequency").toString());
        while(!next.isAfter(date))next=OperationsRules.next(next,s.get("frequency").toString());
        db.update("update ap_service set next_on=? where tenant_id=? and id=?",V.sql(next),a.tenantId(),id);
        c.remember(a,"SERVICE_COMPLETE",p,id);
        c.audit(a,"SERVICE_COMPLETED",id,cycle.toString(),date.toString());
        return Map.of("id",id,"nextOn",next,"expenseId",expense==null?"":expense);
    }
}
