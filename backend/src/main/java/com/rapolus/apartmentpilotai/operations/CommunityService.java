package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import java.time.*;
@Service public class CommunityService {
    private final Db db;
    private final Commands commands;
    private final PasswordEncoder encoder;
    public CommunityService(Db db,Commands commands,PasswordEncoder encoder) {
        this.db=db;
        this.commands=commands;
        this.encoder=encoder;
    }
    public Map<String,Object> apartment(Account a) {
        return db.one("select id,name,city,address,pincode from ap_tenant where id=?",a.tenantId());
    }
    @Transactional public Object saveApartment(Account a,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        String name=V.text(p,"name",80);
        db.update("update ap_tenant set name=?,city=?,address=?,pincode=? where id=?",name,V.text(p,"city",80),V.opt(p,"address",300),V.opt(p,"pincode",6),a.tenantId());
        commands.audit(a,"APARTMENT_UPDATED",a.tenantId(),null,name);
        return apartment(a);
    }
    public List<Map<String,Object>> blocks(Account a) {
        a.requireAdmin();
        return db.rows("select b.id,b.name,count(f.id)::int as flat_count from ap_block b left join ap_flat f on f.tenant_id=b.tenant_id and f.block=b.name where b.tenant_id=? group by b.id,b.name order by b.name",a.tenantId());
    }
    @Transactional public Object saveBlock(Account a,UUID id,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        Map<String,Object> command=new HashMap<>(p);
        if(id!=null)command.put("_blockId",id.toString());
        var previous=commands.previous(a,"BLOCK_SAVE",command);
        if(previous.isPresent())return db.one("select id,name from ap_block where tenant_id=? and id=?",a.tenantId(),previous.get());
        String name=V.text(p,"name",8).toUpperCase(Locale.ROOT);
        String before="New";
        if(id!=null)before=db.one("select name from ap_block where tenant_id=? and id=?",a.tenantId(),id).get("name").toString();
        if(db.count("select count(*) from ap_block where tenant_id=? and name=? and (?::uuid is null or id<>?::uuid)",a.tenantId(),name,id,id)>0)throw ApiError.conflict("This block already exists.");
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_block(id,tenant_id,name) values(?,?,?)",id,a.tenantId(),name);
        } else {
            db.update("update ap_block set name=? where tenant_id=? and id=?",name,a.tenantId(),id);
        }
        commands.remember(a,"BLOCK_SAVE",command,id);
        commands.audit(a,"BLOCK_SAVED",id,before,name);
        return Map.of("id",id,"name",name);
    }
    public List<Map<String,Object>> flats(Account a) {
        if(!a.staff())return db.rows("select id,label,block,floor,occupied,active from ap_flat where tenant_id=? and id=?",a.tenantId(),a.flatId());
        return db.rows("select f.id,f.label,f.block,f.floor,f.active,f.occupied,u.id as member_id,u.name,u.role,u.status from ap_flat f left join ap_user u on u.tenant_id=f.tenant_id and u.flat_id=f.id and u.status in ('ACTIVE','PENDING') where f.tenant_id=? order by f.label",a.tenantId());
    }
    @Transactional public Object saveFlat(Account a,UUID id,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var prev=commands.previous(a,"FLAT_SAVE",p);
        if(prev.isPresent())return Map.of("id",prev.get());
        boolean active=V.bool(p,"active",true),occupied=V.bool(p,"occupied",true);
        String label=V.text(p,"label",20).toUpperCase(Locale.ROOT),block=V.text(p,"block",20).toUpperCase(Locale.ROOT);
        db.one("select id from ap_block where tenant_id=? and name=?",a.tenantId(),block);
        int floor=V.num(p,"floor",-5,150);
        long count=db.count("select count(*) from ap_flat where tenant_id=?",a.tenantId());
        if(id==null) {
            if(count>=50)throw ApiError.conflict("This release supports up to 50 flats.");
            id=UUID.randomUUID();
            db.update("insert into ap_flat(id,tenant_id,label,block,floor,active,occupied) values(?,?,?,?,?,?,?)",id,a.tenantId(),label,block,floor,active,occupied);
        } else {
            commands.flat(a,id);
            if(!active&&db.count("select count(*) from ap_user where tenant_id=? and flat_id=? and status in ('ACTIVE','PENDING')",a.tenantId(),id)>0)throw ApiError.conflict("End the current flat account before deactivating its flat.");
            db.update("update ap_flat set label=?,block=?,floor=?,active=?,occupied=? where id=? and tenant_id=?",label,block,floor,active,occupied,id,a.tenantId());
        }
        commands.remember(a,"FLAT_SAVE",p,id);
        commands.audit(a,"FLAT_SAVED",id,null,label);
        return Map.of("id",id);
    }
    public List<Map<String,Object>> directory(Account a) {
        var rows=db.rows("select u.id,u.flat_id,u.name,u.mobile,u.role,u.status,u.resident_type,f.label as flat_label,'ACCOUNT' as source from ap_user u left join ap_flat f on f.id=u.flat_id where u.tenant_id=? and u.status='ACTIVE' union all select m.id,m.flat_id,m.name,m.mobile,'RESIDENT','INVITED',m.resident_type,f.label,'RECORD' from ap_member_record m join ap_flat f on f.id=m.flat_id where m.tenant_id=? and m.active=true order by name",a.tenantId(),a.tenantId());
        for(var r:rows)if(!a.role().equals("ADMIN")&&!Objects.equals(r.get("id"),a.id()))r.remove("mobile");
        return rows;
    }
    @Transactional public Object memberRecord(Account a,UUID id,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var old=commands.previous(a,"MEMBER_RECORD",p);
        if(old.isPresent())return Map.of("id",old.get());
        UUID flat=V.id(p,"flatId");
        commands.flat(a,flat);
        String name=V.text(p,"name",80),mobile=V.opt(p,"mobile",10),type=V.choice(p,"residentType","OWNER","TENANT");
        if(!mobile.isEmpty()&&!mobile.matches("[6-9][0-9]{9}"))throw new IllegalArgumentException("Mobile must contain ten digits.");
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_member_record(id,tenant_id,flat_id,name,mobile,resident_type) values(?,?,?,?,?,?)",id,a.tenantId(),flat,name,mobile,type);
        } else {
            db.one("select id from ap_member_record where tenant_id=? and id=?",a.tenantId(),id);
            db.update("update ap_member_record set name=?,mobile=?,resident_type=?,active=? where id=? and tenant_id=?",name,mobile,type,V.bool(p,"active",true),id,a.tenantId());
        }
        commands.remember(a,"MEMBER_RECORD",p,id);
        commands.audit(a,"MEMBER_RECORD_SAVED",id,null,"Directory record only; no account access");
        return Map.of("id",id);
    }
    @Transactional public Object changeRole(Account a,UUID id,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        if(a.id().equals(id))throw ApiError.conflict("Use another Admin to change your access.");
        String role=V.choice(p,"role","ADMIN","TREASURER","RESIDENT");
        var u=db.one("select role,flat_id from ap_user where tenant_id=? and id=? and status='ACTIVE'",a.tenantId(),id);
        if(role.equals("RESIDENT")&&u.get("flatId")==null)throw ApiError.conflict("A resident must have an assigned flat.");
        db.update("update ap_user set role=? where tenant_id=? and id=?",role,a.tenantId(),id);
        db.update("delete from ap_session where user_id=?",id);
        commands.audit(a,"ROLE_CHANGED",id,u.get("role").toString(),role);
        return Map.of("ok",true);
    }
    @Transactional public Object endAccess(Account a,UUID id,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        if(a.id().equals(id))throw ApiError.conflict("You cannot end your own Admin access.");
        String reason=V.text(p,"reason",300);
        var u=db.one("select role,flat_id from ap_user where tenant_id=? and id=? and status='ACTIVE'",a.tenantId(),id);
        db.update("update ap_user set status='DISABLED',rejection_reason=? where tenant_id=? and id=?",reason,a.tenantId(),id);
        db.update("delete from ap_session where user_id=?",id);
        commands.audit(a,"ACCESS_ENDED",id,"Active",reason);
        return Map.of("ok",true,"message","Old access ended. Invite and verify the new resident; flat bills stay with the flat.");
    }
    @Transactional public Object profile(Account a,Map<String,Object> p) {
        String name=V.text(p,"name",80),email=V.opt(p,"email",120);
        if(!email.isBlank()&&!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))throw new IllegalArgumentException("Invalid email.");
        db.update("update ap_user set name=?,email=? where id=? and tenant_id=?",name,email,a.id(),a.tenantId());
        commands.audit(a,"PROFILE_UPDATED",a.id(),null,"Profile updated");
        return Map.of("name",name,"email",email);
    }
    @Transactional public Object changePin(Account a,Map<String,Object> p) {
        String current=V.text(p,"currentPin",6),next=V.text(p,"newPin",6);
        if(!next.matches("[0-9]{6}"))throw new IllegalArgumentException("Use a six-digit PIN.");
        var u=db.one("select pin_hash from ap_user where id=? and tenant_id=? for update",a.id(),a.tenantId());
        if(!encoder.matches(current,u.get("pinHash").toString()))throw new ApiError(org.springframework.http.HttpStatus.BAD_REQUEST,"Current PIN is incorrect.");
        db.update("update ap_user set pin_hash=? where id=?",encoder.encode(next),a.id());
        db.update("delete from ap_session where user_id=?",a.id());
        commands.audit(a,"PIN_CHANGED",a.id(),null,"Sessions revoked");
        return Map.of("signInAgain",true);
    }
    public List<Map<String,Object>> committee(Account a) {
        return db.rows("select c.*,f.label as flat_label from ap_committee c left join ap_flat f on f.id=c.flat_id where c.tenant_id=?"+(a.staff()?"":" and c.public=true and c.active=true")+" order by c.title",a.tenantId());
    }
    @Transactional public Object saveCommittee(Account a,UUID id,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var old=commands.previous(a,"COMMITTEE",p);
        if(old.isPresent())return Map.of("id",old.get());
        UUID flat=V.optionalId(p,"flatId");
        if(flat!=null)commands.flat(a,flat);
        String name=V.text(p,"name",80),title=V.text(p,"title",60);
        LocalDate start=V.date(p,"startOn"),end=V.optionalDate(p,"endOn");
        if(end!=null&&end.isBefore(start))throw new IllegalArgumentException("Term end cannot precede its start.");
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_committee(id,tenant_id,name,title,flat_id,start_on,end_on,public,active) values(?,?,?,?,?,?,?,?,?)",id,a.tenantId(),name,title,flat,V.sql(start),V.sql(end),V.bool(p,"public",true),V.bool(p,"active",true));
        } else {
            db.one("select id from ap_committee where id=? and tenant_id=?",id,a.tenantId());
            db.update("update ap_committee set name=?,title=?,flat_id=?,start_on=?,end_on=?,public=?,active=? where id=? and tenant_id=?",name,title,flat,V.sql(start),V.sql(end),V.bool(p,"public",true),V.bool(p,"active",true),id,a.tenantId());
        }
        commands.remember(a,"COMMITTEE",p,id);
        commands.audit(a,"COMMITTEE_SAVED",id,null,title+"; no application permission change");
        return Map.of("id",id);
    }
    public List<Map<String,Object>> contacts(Account a,String kind) {
        if(!Set.of("VENDOR","CONTACT").contains(kind))throw new IllegalArgumentException("Invalid contact type.");
        return db.rows("select * from ap_contact where tenant_id=? and kind=?"+(a.staff()?"":" and public=true and active=true")+" order by name",a.tenantId(),kind);
    }
    @Transactional public Object saveContact(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=commands.previous(a,"CONTACT",p);
        if(old.isPresent())return Map.of("id",old.get());
        String kind=V.choice(p,"kind","VENDOR","CONTACT"),name=V.text(p,"name",100),category=V.text(p,"category",40),phone=V.opt(p,"phone",20),notes=V.opt(p,"notes",500);
        if(!phone.isBlank()&&!phone.matches("[+0-9 ()-]{3,20}"))throw new IllegalArgumentException("Invalid contact number.");
        LocalDate amc=V.optionalDate(p,"amcEnd");
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_contact(id,tenant_id,kind,name,category,phone,notes,public,active,amc_end) values(?,?,?,?,?,?,?,?,?,?)",id,a.tenantId(),kind,name,category,phone,notes,V.bool(p,"public",true),V.bool(p,"active",true),V.sql(amc));
        } else {
            db.one("select id from ap_contact where tenant_id=? and id=?",a.tenantId(),id);
            db.update("update ap_contact set name=?,category=?,phone=?,notes=?,public=?,active=?,amc_end=? where id=? and tenant_id=?",name,category,phone,notes,V.bool(p,"public",true),V.bool(p,"active",true),V.sql(amc),id,a.tenantId());
        }
        commands.remember(a,"CONTACT",p,id);
        commands.audit(a,"CONTACT_SAVED",id,null,name);
        return Map.of("id",id);
    }
}
