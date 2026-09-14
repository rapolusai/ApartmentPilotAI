package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.domain.OperationsRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.*;
@Service public class BookingService {
    private final Db db;
    private final Commands c;
    public BookingService(Db db,Commands c) {
        this.db=db;
        this.c=c;
    }
    public List<Map<String,Object>> resources(Account a) {
        var rows=db.rows("select id,name,kind,capacity,bookable,private_flat_id,owner_consent,available_from,available_until from ap_resource where tenant_id=? order by kind,name",a.tenantId());
        for(var r:rows) {
            r.put("conflicts",db.rows("select other_id from ap_resource_conflict where tenant_id=? and resource_id=?",a.tenantId(),r.get("id")));
            r.put("blocks",db.rows("select id,starts_at,ends_at,reason from ap_resource_block where tenant_id=? and resource_id=? order by starts_at",a.tenantId(),r.get("id")));
        }
        return rows;
    }
    @Transactional public Object saveResource(Account a,UUID id,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"RESOURCE_SAVE",p);
        if(old.isPresent())return Map.of("id",old.get());
        String name=V.text(p,"name",80),kind=V.choice(p,"kind","SPACE","CAR","BIKE");
        int capacity=V.num(p,"capacity",1,100);
        UUID owner=V.optionalId(p,"privateFlatId");
        if(owner!=null)c.flat(a,owner);
        boolean enabled=V.bool(p,"bookable",true);
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_resource(id,tenant_id,name,kind,capacity,bookable,private_flat_id) values(?,?,?,?,?,?,?)",id,a.tenantId(),name,kind,capacity,enabled,owner);
        } else {
            db.one("select id from ap_resource where tenant_id=? and id=?",a.tenantId(),id);
            if(db.count("select count(*) from ap_booking_item i join ap_booking b on b.id=i.booking_id where i.resource_id=? and b.status='APPROVED' and b.ends_at>now()",id)>0)throw ApiError.conflict("Cancel or complete active reservations before changing this resource.");
            db.update("update ap_resource set name=?,kind=?,capacity=?,bookable=?,owner_consent=case when private_flat_id is distinct from ? then false else owner_consent end,private_flat_id=? where tenant_id=? and id=?",name,kind,capacity,enabled,owner,owner,a.tenantId(),id);
        }
        if(p.containsKey("conflicts")) {
            Object raw=p.get("conflicts");
            if(!(raw instanceof List<?> values)||values.size()>100)throw new IllegalArgumentException("Invalid conflicting-space selection.");
            db.update("delete from ap_resource_conflict where tenant_id=? and (resource_id=? or other_id=?)",a.tenantId(),id,id);
            for(Object v:values) {
                UUID other=V.uid(v);
                if(other.equals(id))throw new IllegalArgumentException("A space cannot conflict with itself.");
                db.one("select id from ap_resource where tenant_id=? and id=?",a.tenantId(),other);
                db.update("insert into ap_resource_conflict values(?,?,?) on conflict do nothing",a.tenantId(),id,other);
                db.update("insert into ap_resource_conflict values(?,?,?) on conflict do nothing",a.tenantId(),other,id);
            }
        }
        c.remember(a,"RESOURCE_SAVE",p,id);
        c.audit(a,"RESOURCE_SAVED",id,null,name);
        return Map.of("id",id);
    }
    @Transactional public Object release(Account a,UUID id,Map<String,Object> p) {
        db.lockTenant(a.tenantId());
        var r=db.one("select private_flat_id from ap_resource where tenant_id=? and id=?",a.tenantId(),id);
        if(a.flatId()==null||!a.flatId().equals(r.get("privateFlatId")))throw ApiError.forbidden();
        boolean consent=V.bool(p,"consent",false);
        Instant start=V.time(p,"start"),end=V.time(p,"end");
        if(consent)OperationsRules.window(start,end,Instant.now());
        if(!consent&&db.count("select count(*) from ap_booking_item i join ap_booking b on b.id=i.booking_id where i.resource_id=? and b.status='APPROVED' and b.ends_at>now()",id)>0)throw ApiError.conflict("Contact the Admin to cancel approved reservations before withdrawing this release.");
        if(consent&&db.count("select count(*) from ap_booking_item i join ap_booking b on b.id=i.booking_id where i.resource_id=? and b.status='APPROVED' and b.ends_at>now() and (b.starts_at<? or b.ends_at>?)",id,V.sql(start),V.sql(end))>0)throw ApiError.conflict("The release must cover existing approved reservations.");
        db.update("update ap_resource set owner_consent=?,available_from=?,available_until=? where tenant_id=? and id=?",consent,V.sql(start),V.sql(end),a.tenantId(),id);
        c.audit(a,"PRIVATE_SPACE_RELEASE",id,null,consent?start+" / "+end:"Withdrawn");
        return Map.of("ok",true);
    }
    @Transactional public Object block(Account a,UUID id,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"RESOURCE_BLOCK",p);
        if(old.isPresent())return Map.of("id",old.get());
        db.one("select id from ap_resource where tenant_id=? and id=?",a.tenantId(),id);
        Instant start=V.time(p,"start"),end=V.time(p,"end");
        OperationsRules.window(start,end,Instant.now());
        for(var linked:db.rows("select other_id from ap_resource_conflict where tenant_id=? and resource_id=?",a.tenantId(),id))if(hasBookings(a,(UUID)linked.get("otherId"),start,end,null))throw ApiError.conflict("A linked physical space has an approved reservation.");
        if(hasBookings(a,id,start,end,null))throw ApiError.conflict("Approved reservations overlap. Resolve those bookings first.");
        UUID block=UUID.randomUUID();
        db.update("insert into ap_resource_block(id,tenant_id,resource_id,starts_at,ends_at,reason) values(?,?,?,?,?,?)",block,a.tenantId(),id,V.sql(start),V.sql(end),V.text(p,"reason",300));
        c.remember(a,"RESOURCE_BLOCK",p,block);
        c.audit(a,"SPACE_BLOCKED",id,null,start+" / "+end);
        return Map.of("id",block);
    }
    @Transactional public Object removeBlock(Account a,UUID id) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        if(db.update("delete from ap_resource_block where tenant_id=? and id=?",a.tenantId(),id)!=1)throw ApiError.missing();
        c.audit(a,"SPACE_BLOCK_REMOVED",id,null,"Availability restored");
        return Map.of("ok",true);
    }
    private List<Map<String,Object>> usage(Account a,UUID resource,Instant start,Instant end,UUID excluding) {
        return db.rows("select b.starts_at,b.ends_at,i.quantity from ap_booking_item i join ap_booking b on b.id=i.booking_id where i.tenant_id=? and i.resource_id=? and i.offered=false and b.status='APPROVED' and b.starts_at<? and b.ends_at>? and (?::uuid is null or b.id<>?::uuid)",a.tenantId(),resource,V.sql(end),V.sql(start),excluding,excluding);
    }
    private boolean hasBookings(Account a,UUID resource,Instant start,Instant end,UUID excluding) {
        return !usage(a,resource,start,end,excluding).isEmpty();
    }
    private int free(Account a,Map<String,Object> resource,Instant start,Instant end,UUID excluding) {
        UUID rid=(UUID)resource.get("id");
        if(!Boolean.TRUE.equals(resource.get("bookable")))return 0;
        if(resource.get("privateFlatId")!=null) {
            if(!Boolean.TRUE.equals(resource.get("ownerConsent"))||resource.get("availableFrom")==null||resource.get("availableUntil")==null)return 0;
            Instant from=Instant.parse(resource.get("availableFrom").toString()),until=Instant.parse(resource.get("availableUntil").toString());
            if(start.isBefore(from)||end.isAfter(until))return 0;
        }
        if(db.count("select count(*) from ap_resource_block where tenant_id=? and (resource_id=? or resource_id in(select other_id from ap_resource_conflict where tenant_id=? and resource_id=?)) and starts_at<? and ends_at>?",a.tenantId(),rid,a.tenantId(),rid,V.sql(end),V.sql(start))>0)return 0;
        for(var conflict:db.rows("select other_id from ap_resource_conflict where tenant_id=? and resource_id=?",a.tenantId(),rid))if(hasBookings(a,(UUID)conflict.get("otherId"),start,end,excluding))return 0;
        List<OperationsRules.Reservation> reserved=new ArrayList<>();
        for(var r:usage(a,rid,start,end,excluding))reserved.add(new OperationsRules.Reservation(Instant.parse(r.get("startsAt").toString()),Instant.parse(r.get("endsAt").toString()),((Number)r.get("quantity")).intValue()));
        return Math.max(0,((Number)resource.get("capacity")).intValue()-OperationsRules.peak(start,end,reserved));
    }
    @Transactional(readOnly=true,isolation=org.springframework.transaction.annotation.Isolation.REPEATABLE_READ) public List<Map<String,Object>> availability(Account a,Instant start,Instant end) {
        OperationsRules.window(start,end,Instant.now());
        var rs=resources(a);
        for(var r:rs) {
            r.put("available",free(a,r,start,end,null));
            r.remove("privateFlatId");
        }
        return rs;
    }
    private void verify(Account a,Instant start,Instant end,List<Map<String,Object>> items,UUID exclude,boolean checkAvailability) {
        OperationsRules.window(start,end,Instant.now());
        Set<UUID> ids=new HashSet<>();
        for(var item:items) {
            UUID id=V.id(item,"resourceId");
            if(!ids.add(id))throw new IllegalArgumentException("Select each space once.");
            var r=db.one("select * from ap_resource where tenant_id=? and id=?",a.tenantId(),id);
            int quantity=V.num(item,"quantity",1,100);
            if(quantity>((Number)r.get("capacity")).intValue())throw new IllegalArgumentException("Requested quantity exceeds configured capacity.");
            if(!Boolean.TRUE.equals(r.get("bookable")))throw ApiError.conflict("A selected space is unavailable.");
            if(checkAvailability&&quantity>free(a,r,start,end,exclude))throw ApiError.conflict(r.get("name")+" is unavailable. Choose another slot.");
        }
        for(UUID id:ids)for(var r:db.rows("select other_id from ap_resource_conflict where tenant_id=? and resource_id=?",a.tenantId(),id))if(ids.contains((UUID)r.get("otherId")))throw ApiError.conflict("The event area occupies one of the selected parking spaces. Choose non-conflicting spaces.");
    }
    private void items(Account a,UUID id,List<Map<String,Object>> list,boolean offered) {
        for(var i:list)db.update("insert into ap_booking_item values(?,?,?,?,?)",a.tenantId(),id,V.id(i,"resourceId"),V.num(i,"quantity",1,100),offered);
    }
    private void event(Account a,UUID id,String status,String note) {
        db.update("insert into ap_booking_event(id,tenant_id,booking_id,actor_id,status,note) values(?,?,?,?,?,?)",UUID.randomUUID(),a.tenantId(),id,a.id(),status,note);
    }
    public List<Map<String,Object>> list(Account a) {
        return db.rows("select b.*,f.label as flat_label from ap_booking b left join ap_flat f on f.id=b.flat_id where b.tenant_id=?"+(a.role().equals("ADMIN")?"":" and b.created_by=?")+" order by b.starts_at desc limit 300",a.role().equals("ADMIN")?new Object[] {
            a.tenantId()
        }
        :new Object[] {
            a.tenantId(),a.id()
        }
        );
    }
    public Map<String,Object> get(Account a,UUID id) {
        var b=db.one("select b.*,f.label as flat_label from ap_booking b left join ap_flat f on f.id=b.flat_id where b.tenant_id=? and b.id=?",a.tenantId(),id);
        if(!a.role().equals("ADMIN")&&!a.id().equals(b.get("createdBy")))throw ApiError.forbidden();
        b.put("items",db.rows("select i.resource_id,r.name,r.kind,i.quantity,i.offered from ap_booking_item i join ap_resource r on r.id=i.resource_id where i.tenant_id=? and i.booking_id=? order by i.offered,r.kind",a.tenantId(),id));
        b.put("events",db.rows("select status,note,created_at from ap_booking_event where tenant_id=? and booking_id=? order by created_at",a.tenantId(),id));
        return b;
    }
    @Transactional public Object create(Account a,Map<String,Object> p) {
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"BOOKING_CREATE",p);
        if(old.isPresent())return Map.of("id",old.get());
        if(!V.bool(p,"acceptRules",false))throw new IllegalArgumentException("Accept the apartment booking rules.");
        Instant start=V.time(p,"start"),end=V.time(p,"end");
        var lines=V.items(p,"items",50);
        verify(a,start,end,lines,null,true);
        UUID flat=a.flatId();
        if(a.role().equals("ADMIN"))flat=V.optionalId(p,"flatId");
        if(flat!=null)c.flat(a,flat);
        UUID id=UUID.randomUUID();
        String title=V.text(p,"title",100);
        db.update("insert into ap_booking(id,tenant_id,flat_id,created_by,title,event_type,guests,starts_at,ends_at,notes) values(?,?,?,?,?,?,?,?,?,?)",id,a.tenantId(),flat,a.id(),title,V.choice(p,"eventType","BIRTHDAY","MARRIAGE","FAMILY","FESTIVAL","MEETING","OTHER"),V.num(p,"guests",1,1000),V.sql(start),V.sql(end),V.opt(p,"notes",1000));
        items(a,id,lines,false);
        event(a,id,"PENDING","Requested; not yet reserved");
        db.notifyStaff(a.tenantId(),"BOOKING:"+id,"Event request",title,"booking:"+id);
        c.remember(a,"BOOKING_CREATE",p,id);
        c.audit(a,"BOOKING_REQUESTED",id,null,title);
        return Map.of("id",id,"status","PENDING");
    }
    @Transactional public Object decide(Account a,UUID id,Map<String,Object> p) {
        db.lockTenant(a.tenantId());
        var b=get(a,id);
        var old=c.previous(a,"BOOKING_DECISION",p);
        if(old.isPresent())return Map.of("id",old.get());
        String action=V.choice(p,"action","APPROVE","REJECT","CANCEL","COMPLETE","OFFER","ACCEPT");
        String status=b.get("status").toString(),note=V.opt(p,"note",500);
        int revision=V.num(p,"revision",1,Integer.MAX_VALUE);
        if(revision!=((Number)b.get("revision")).intValue())throw ApiError.conflict("Booking changed. Refresh before reviewing.");
        Instant start=Instant.parse(b.get("startsAt").toString()),end=Instant.parse(b.get("endsAt").toString());
        if(Set.of("APPROVE","REJECT","COMPLETE","OFFER").contains(action))a.requireAdmin();
        if(action.equals("APPROVE")) {
            if(!status.equals("PENDING"))throw ApiError.conflict("Only pending requests can be approved.");
            verify(a,start,end,asInput(a,id,false),id,true);
            status="APPROVED";
        } else if(action.equals("OFFER")) {
            if(!Set.of("PENDING","ALTERNATIVE").contains(status))throw ApiError.conflict("Request is not awaiting a decision.");
            Instant offeredStart=V.time(p,"start"),offeredEnd=V.time(p,"end");
            var lines=V.items(p,"items",50);
            verify(a,offeredStart,offeredEnd,lines,id,true);
            if(note.isBlank())throw new IllegalArgumentException("Explain the proposed alternative.");
            db.update("delete from ap_booking_item where tenant_id=? and booking_id=? and offered=true",a.tenantId(),id);
            items(a,id,lines,true);
            db.update("update ap_booking set offered_start=?,offered_end=? where id=?",V.sql(offeredStart),V.sql(offeredEnd),id);
            status="ALTERNATIVE";
        } else if(action.equals("ACCEPT")) {
            if(!a.id().equals(b.get("createdBy")))throw ApiError.forbidden();
            if(!status.equals("ALTERNATIVE"))throw ApiError.conflict("No alternative is waiting.");
            start=Instant.parse(b.get("offeredStart").toString());
            end=Instant.parse(b.get("offeredEnd").toString());
            verify(a,start,end,asInput(a,id,true),id,true);
            db.update("delete from ap_booking_item where booking_id=? and offered=false",id);
            db.update("update ap_booking_item set offered=false where booking_id=? and offered=true",id);
            db.update("update ap_booking set starts_at=?,ends_at=?,offered_start=null,offered_end=null where id=?",V.sql(start),V.sql(end),id);
            status="APPROVED";
        } else if(action.equals("REJECT")) {
            if(!Set.of("PENDING","ALTERNATIVE").contains(status))throw ApiError.conflict("This request cannot be rejected now.");
            if(note.isBlank())throw new IllegalArgumentException("Enter a rejection reason.");
            status="REJECTED";
        } else if(action.equals("CANCEL")) {
            if(!Set.of("PENDING","ALTERNATIVE","APPROVED").contains(status))throw ApiError.conflict("This request has already ended.");
            if(note.isBlank())throw new IllegalArgumentException("Enter a cancellation reason.");
            status="CANCELLED";
        } else {
            if(!status.equals("APPROVED")||Instant.now().isBefore(end))throw ApiError.conflict("Complete only after the approved booking end time.");
            status="COMPLETED";
        }
        db.update("update ap_booking set status=?,decision_note=?,revision=revision+1 where tenant_id=? and id=?",status,note,a.tenantId(),id);
        event(a,id,status,note.isBlank()?status:note);
        db.notify(a.tenantId(),(UUID)b.get("createdBy"),"BOOKING:"+id+":"+(revision+1),"Booking updated",status+" · "+b.get("title"),"booking:"+id);
        c.remember(a,"BOOKING_DECISION",p,id);
        c.audit(a,"BOOKING_STATUS",id,b.get("status").toString(),status);
        return Map.of("id",id,"status",status);
    }
    private List<Map<String,Object>> asInput(Account a,UUID id,boolean offered) {
        return db.rows("select resource_id::text as resource_id,quantity from ap_booking_item where tenant_id=? and booking_id=? and offered=?",a.tenantId(),id,offered);
    }
}
