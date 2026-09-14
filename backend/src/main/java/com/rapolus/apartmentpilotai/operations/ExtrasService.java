package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.*;
@Service public class ExtrasService {
    private final Db db;
    private final Commands c;
    public ExtrasService(Db db,Commands c) {
        this.db=db;
        this.c=c;
    }
    public List<Map<String,Object>> polls(Account a) {
        return db.rows("select id,title,description,closes_at,closed,created_at from ap_poll where tenant_id=? order by created_at desc limit 200",a.tenantId());
    }
    public Map<String,Object> poll(Account a,UUID id) {
        var p=db.one("select id,title,description,closes_at,closed from ap_poll where tenant_id=? and id=?",a.tenantId(),id);
        p.put("options",db.rows("select o.id,o.label,count(v.flat_id) as votes from ap_poll_option o left join ap_poll_vote v on v.option_id=o.id where o.poll_id=? group by o.id,o.label,o.position order by o.position",id));
        p.put("myVote",a.flatId()==null?"":db.find("select option_id from ap_poll_vote where poll_id=? and flat_id=?",id,a.flatId()).map(x->x.get("optionId")).orElse(""));
        p.put("open",!Boolean.TRUE.equals(p.get("closed"))&&Instant.now().isBefore(Instant.parse(p.get("closesAt").toString())));
        return p;
    }
    @Transactional public Object createPoll(Account a,Map<String,Object> body) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"POLL_CREATE",body);
        if(old.isPresent())return Map.of("id",old.get());
        Instant closes=V.time(body,"closesAt");
        if(!closes.isAfter(Instant.now())||closes.isAfter(Instant.now().plus(Duration.ofDays(90))))throw new IllegalArgumentException("Choose a closing time within 90 days.");
        Object options=body.get("options");
        if(!(options instanceof List<?> choices)||choices.size()<2||choices.size()>6)throw new IllegalArgumentException("Provide 2–6 options.");
        List<String> labels=new ArrayList<>();
        for(Object value:choices) {
            if(!(value instanceof String label)||label.trim().isEmpty()||label.trim().length()>120)throw new IllegalArgumentException("Invalid poll option.");
            if(labels.contains(label.trim()))throw new IllegalArgumentException("Duplicate poll option.");
            labels.add(label.trim());
        }
        String title=V.text(body,"title",160);
        UUID id=UUID.randomUUID();
        db.update("insert into ap_poll(id,tenant_id,title,description,closes_at,created_by) values(?,?,?,?,?,?)",id,a.tenantId(),title,V.opt(body,"description",1000),V.sql(closes),a.id());
        for(int i=0; i<labels.size(); i++)db.update("insert into ap_poll_option(id,poll_id,label,position) values(?,?,?,?)",UUID.randomUUID(),id,labels.get(i),i);
        for(var u:db.rows("select id from ap_user where tenant_id=? and status='ACTIVE'",a.tenantId()))db.notify(a.tenantId(),(UUID)u.get("id"),"POLL:"+id,"New apartment poll",title,"poll:"+id);
        c.remember(a,"POLL_CREATE",body,id);
        c.audit(a,"POLL_CREATED",id,null,title);
        return Map.of("id",id);
    }
    @Transactional public Object vote(Account a,UUID id,Map<String,Object> body) {
        db.lockTenant(a.tenantId());
        if(a.flatId()==null)throw new IllegalArgumentException("Voting needs an active flat association.");
        var p=poll(a,id);
        if(!Boolean.TRUE.equals(p.get("open")))throw ApiError.conflict("Voting has closed.");
        UUID option=V.id(body,"optionId");
        db.one("select id from ap_poll_option where poll_id=? and id=?",id,option);
        db.update("insert into ap_poll_vote(tenant_id,poll_id,flat_id,option_id,user_id) values(?,?,?,?,?) on conflict(poll_id,flat_id) do update set option_id=excluded.option_id,user_id=excluded.user_id,created_at=now()",a.tenantId(),id,a.flatId(),option,a.id());
        return Map.of("ok",true);
    }
    @Transactional public Object close(Account a,UUID id) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        poll(a,id);
        db.update("update ap_poll set closed=true where tenant_id=? and id=?",a.tenantId(),id);
        c.audit(a,"POLL_CLOSED",id,null,"Closed");
        return Map.of("ok",true);
    }
    public List<Map<String,Object>> vehicles(Account a) {
        return db.rows("select v.id,v.registration,v.kind,v.parking_label,v.active,v.flat_id,f.label as flat_label from ap_vehicle v join ap_flat f on f.id=v.flat_id where v.tenant_id=? and (v.active=true or ?=true) order by v.registration",a.tenantId(),a.role().equals("ADMIN"));
    }
    @Transactional public Object saveVehicle(Account a,UUID id,Map<String,Object> body) {
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"VEHICLE_SAVE",body);
        if(old.isPresent())return Map.of("id",old.get());
        UUID flat=a.flatId();
        if(a.role().equals("ADMIN"))flat=V.id(body,"flatId");
        if(flat==null)throw new IllegalArgumentException("Select a flat.");
        c.flat(a,flat);
        String number=V.text(body,"registration",20).replaceAll("[ -]","").toUpperCase(Locale.ROOT);
        if(!number.matches("[A-Z0-9]{5,15}"))throw new IllegalArgumentException("Check the vehicle registration.");
        String kind=V.choice(body,"kind","CAR","BIKE","OTHER"),parking=V.opt(body,"parkingLabel",40);
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_vehicle(id,tenant_id,flat_id,registration,kind,parking_label,created_by) values(?,?,?,?,?,?,?)",id,a.tenantId(),flat,number,kind,parking,a.id());
        } else {
            var v=db.one("select flat_id from ap_vehicle where tenant_id=? and id=?",a.tenantId(),id);
            if(!a.role().equals("ADMIN")&&!flat.equals(v.get("flatId")))throw ApiError.forbidden();
            db.update("update ap_vehicle set flat_id=?,registration=?,kind=?,parking_label=?,active=? where tenant_id=? and id=?",flat,number,kind,parking,V.bool(body,"active",true),a.tenantId(),id);
        }
        c.remember(a,"VEHICLE_SAVE",body,id);
        c.audit(a,"VEHICLE_SAVED",id,null,number);
        return Map.of("id",id);
    }
    @Transactional public Object contactOwner(Account a,UUID id,Map<String,Object> body) {
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"VEHICLE_CONTACT",body);
        if(old.isPresent())return Map.of("id",old.get());
        var v=db.one("select flat_id,registration from ap_vehicle where tenant_id=? and id=? and active=true",a.tenantId(),id);
        String note=V.text(body,"message",300);
        UUID request=UUID.randomUUID();
        var owners=db.rows("select id from ap_user where tenant_id=? and flat_id=? and status='ACTIVE'",a.tenantId(),v.get("flatId"));
        if(owners.isEmpty())throw ApiError.conflict("No active flat account is available. Contact the Admin.");
        for(var owner:owners)db.notify(a.tenantId(),(UUID)owner.get("id"),"VEHICLE_CONTACT:"+request,"Vehicle request · "+v.get("registration"),a.name()+": "+note,"vehicle:"+id);
        c.remember(a,"VEHICLE_CONTACT",body,request);
        c.audit(a,"VEHICLE_CONTACT_REQUESTED",id,null,"Sent through in-app inbox; phone number not disclosed");
        return Map.of("id",request,"recipients",owners.size());
    }
}
