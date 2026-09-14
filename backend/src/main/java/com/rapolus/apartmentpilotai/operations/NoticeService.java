package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.*;
@Service public class NoticeService {
    private final Db db;
    private final Commands c;
    public NoticeService(Db db,Commands c) {
        this.db=db;
        this.c=c;
    }
    public List<Map<String,Object>> list(Account a) {
        return db.rows("select n.* from ap_notice n where n.tenant_id=?"+(a.staff()?"":" and n.status='PUBLISHED' and exists(select 1 from ap_notice_recipient r where r.notice_id=n.id and r.user_id=?)")+" order by n.pinned desc,n.created_at desc limit 300",a.staff()?new Object[] {
            a.tenantId()
        }
        :new Object[] {
            a.tenantId(),a.id()
        }
        );
    }
    public Map<String,Object> get(Account a,UUID id) {
        var n=db.one("select * from ap_notice where tenant_id=? and id=?",a.tenantId(),id);
        if(!a.staff()&&(!n.get("status").equals("PUBLISHED")||db.count("select count(*) from ap_notice_recipient where notice_id=? and user_id=?",id,a.id())==0))throw ApiError.forbidden();
        if(a.staff()) {
            n.put("recipients",db.rows("select r.user_id,u.name,f.label as flat_label,r.read_at,r.acknowledged_at from ap_notice_recipient r join ap_user u on u.id=r.user_id left join ap_flat f on f.id=u.flat_id where r.tenant_id=? and r.notice_id=? order by u.name",a.tenantId(),id));
        }
        n.put("deliveryMode","IN_APP_ONLY");
        n.put("externalDeliveryStatus","NOT_CONFIGURED");
        return n;
    }
    public Map<String,Object> preview(Account a,UUID id) {
        a.requireStaff();
        var notice=get(a,id);
        if(!notice.get("status").equals("DRAFT"))throw ApiError.conflict("Only an unpublished draft can be previewed.");
        Map<String,Object> out=new LinkedHashMap<>(notice);
        out.remove("recipients");
        out.put("recipientCount",recipientAccounts(a,notice).size());
        return out;
    }
    @Transactional public Object save(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        Map<String,Object> command=new LinkedHashMap<>(p);
        if(id!=null)command.put("_noticeId",id.toString());
        var old=c.previous(a,"NOTICE_SAVE",command);
        if(old.isPresent())return Map.of("id",old.get());
        String title=V.text(p,"title",100),body=V.text(p,"body",2000),audience=p.containsKey("audience")?V.choice(p,"audience","ALL","BLOCK","SELECTED","UNPAID"):"ALL",value=V.opt(p,"audienceValue",500);
        String type=p.containsKey("noticeType")?V.choice(p,"noticeType","GENERAL","MAINTENANCE","SERVICE_ALERT","EMERGENCY"):"GENERAL";
        String category=V.opt(p,"category",60);
        if(category.isBlank())category="Other";
        Instant scheduled=V.optionalTime(p,"scheduledAt");
        if(scheduled!=null&&!scheduled.isAfter(Instant.now()))throw new IllegalArgumentException("Schedule must be in the future.");
        if(!Set.of("ALL","UNPAID").contains(audience)&&value.isBlank())throw new IllegalArgumentException("Choose recipients.");
        if(audience.equals("BLOCK")&&db.count("select count(*) from ap_flat where tenant_id=? and block=?",a.tenantId(),value)==0)throw new IllegalArgumentException("Unknown block.");
        if(audience.equals("SELECTED")) {
            for(String f:value.split(","))if(db.count("select count(*) from ap_flat where tenant_id=? and label=?",a.tenantId(),f.trim())==0)throw new IllegalArgumentException("Unknown selected flat: "+f.trim());
        }
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_notice(id,tenant_id,title,body,status,created_by,audience,audience_value,pinned,scheduled_at,acknowledge,notice_type,category,phone_notify,schedule_confirmed) values(?,?,?,?,'DRAFT',?,?,?,?,?,?,?,?,?,false)",id,a.tenantId(),title,body,a.id(),audience,value,V.bool(p,"pinned",false),V.sql(scheduled),V.bool(p,"acknowledge",false),type,category,V.bool(p,"phoneNotify",false));
        } else {
            var existing=get(a,id);
            if(!existing.get("status").equals("DRAFT"))throw ApiError.conflict("Published notices are immutable. Publish a correction rather than silently changing a message.");
            db.update("update ap_notice set title=?,body=?,audience=?,audience_value=?,pinned=?,scheduled_at=?,acknowledge=?,notice_type=?,category=?,phone_notify=?,schedule_confirmed=false where tenant_id=? and id=?",title,body,audience,value,V.bool(p,"pinned",false),V.sql(scheduled),V.bool(p,"acknowledge",false),type,category,V.bool(p,"phoneNotify",false),a.tenantId(),id);
        }
        if(V.bool(p,"publish",false)&&scheduled==null)publishInternal(a,id);
        c.remember(a,"NOTICE_SAVE",command,id);
        c.audit(a,"NOTICE_SAVED",id,null,title);
        return Map.of("id",id);
    }
    private boolean recipient(Account a,Map<String,Object> n,Map<String,Object> u) {
        if(Set.of("ADMIN","TREASURER").contains(u.get("role")))return true;
        String type=n.get("audience").toString(),v=n.get("audienceValue").toString();
        return switch(type) {
            case "ALL"->true;
            case "BLOCK"->v.equals(u.get("block"));
            case "SELECTED"->Arrays.stream(v.split(",")).map(String::trim).anyMatch(s->s.equals(u.get("label")));
            case "UNPAID"->u.get("flatId")!=null&&db.count("select count(*) from ap_bill b where b.tenant_id=? and b.flat_id=? and b.amount+b.late_fee>coalesce((select sum(p.amount) from ap_payment p where p.bill_id=b.id and p.status='APPROVED' and not exists(select 1 from ap_payment_reversal rv where rv.payment_id=p.id)),0)",a.tenantId(),u.get("flatId"))>0;
            default->false;
        };
    }
    private List<Map<String,Object>> recipientAccounts(Account a,Map<String,Object> n) {
        return db.rows("select u.id,u.flat_id,u.role,f.label,f.block from ap_user u left join ap_flat f on f.id=u.flat_id where u.tenant_id=? and u.status='ACTIVE'",a.tenantId()).stream().filter(u->recipient(a,n,u)).toList();
    }
    private void publishInternal(Account a,UUID id) {
        var n=get(a,id);
        if(n.get("status").equals("PUBLISHED"))return;
        var recipients=recipientAccounts(a,n);
        if(recipients.isEmpty())throw new IllegalArgumentException("No active recipients in this audience.");
        db.update("update ap_notice set status='PUBLISHED',scheduled_at=null,schedule_confirmed=false where tenant_id=? and id=?",a.tenantId(),id);
        for(var u:recipients) {
            db.update("insert into ap_notice_recipient values(?,?,?,null,null) on conflict do nothing",a.tenantId(),id,u.get("id"));
            db.notify(a.tenantId(),(UUID)u.get("id"),"NOTICE:"+id,n.get("title").toString(),"New notice in your apartment","notice:"+id);
        }
        c.audit(a,"NOTICE_PUBLISHED",id,"Draft","Published to snapshot of selected recipients");
    }
    @Transactional public Object schedule(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        Map<String,Object> command=new LinkedHashMap<>(p);
        command.put("_noticeId",id.toString());
        var old=c.previous(a,"NOTICE_SCHEDULE",command);
        if(old.isPresent())return Map.of("id",old.get(),"status","SCHEDULED","externalDeliveryStatus","NOT_CONFIGURED");
        var n=get(a,id);
        if(!n.get("status").equals("DRAFT"))throw ApiError.conflict("Only an unpublished draft can be scheduled.");
        if(n.get("scheduledAt")==null||!Instant.parse(n.get("scheduledAt").toString()).isAfter(Instant.now()))throw new IllegalArgumentException("Choose a future publishing time.");
        if(recipientAccounts(a,n).isEmpty())throw new IllegalArgumentException("No active recipients in this audience.");
        db.update("update ap_notice set schedule_confirmed=true where tenant_id=? and id=?",a.tenantId(),id);
        c.remember(a,"NOTICE_SCHEDULE",command,id);
        c.audit(a,"NOTICE_SCHEDULED",id,"Draft",n.get("scheduledAt").toString()+" · in-app only");
        return Map.of("id",id,"status","SCHEDULED","externalDeliveryStatus","NOT_CONFIGURED");
    }
    @Transactional public Object publish(Account a,UUID id) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var n=get(a,id);
        if(n.get("scheduledAt")!=null&&Instant.parse(n.get("scheduledAt").toString()).isAfter(Instant.now()))throw ApiError.conflict("This notice is scheduled for later. Edit the draft to publish now.");
        publishInternal(a,id);
        return Map.of("ok",true);
    }
    @Transactional public Object read(Account a,UUID id,boolean ack) {
        var n=get(a,id);
        if(ack&&!Boolean.TRUE.equals(n.get("acknowledge")))throw new IllegalArgumentException("Acknowledgement was not requested.");
        db.update("update ap_notice_recipient set read_at=coalesce(read_at,now()),acknowledged_at=case when ? then coalesce(acknowledged_at,now()) else acknowledged_at end where tenant_id=? and notice_id=? and user_id=?",ack,a.tenantId(),id,a.id());
        return Map.of("ok",true);
    }
    @Transactional public Object pin(Account a,UUID id,boolean pinned) {
        a.requireStaff();
        get(a,id);
        db.update("update ap_notice set pinned=? where tenant_id=? and id=?",pinned,a.tenantId(),id);
        return Map.of("ok",true);
    }
}
