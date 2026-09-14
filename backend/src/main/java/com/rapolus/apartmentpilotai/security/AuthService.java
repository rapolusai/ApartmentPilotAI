package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.store.Db;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service public class AuthService {
    private final Db db;
    private final PasswordEncoder encoder;
    private final boolean localRegistration;
    private final int hours;
    public AuthService(Db db,PasswordEncoder encoder,@Value("${app.local-registration-enabled:false}")boolean localRegistration,@Value("${app.session-hours:12}")int hours) {
        this.db=db;
        this.encoder=encoder;
        this.localRegistration=localRegistration;
        this.hours=hours;
    }
    private void requireLocal() {
        if(!localRegistration)throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Account verification has not been configured. Registration is disabled outside the local profile.");
    }
    @Transactional public Map<String,Object> register(Requests.Register r) {
        requireLocal();
        UUID tenant=UUID.randomUUID(),user=UUID.randomUUID();
        db.update("insert into ap_tenant(id,name,city) values(?,?,?)",tenant,r.apartmentName().trim(),r.city().trim());
        for(int i=1; i<=r.flats(); i++)db.update("insert into ap_flat(id,tenant_id,label) values(?,?,?)",UUID.randomUUID(),tenant,"A-"+(100+i));
        db.update("insert into ap_user(id,tenant_id,name,mobile,pin_hash,role,status) values(?,?,?,?,?,'ADMIN','ACTIVE')",user,tenant,r.name().trim(),r.mobile(),encoder.encode(r.pin()));
        db.audit(tenant,user,"APARTMENT_CREATED",tenant,null,r.apartmentName());
        return issueSession(user);
    }
    @Transactional public Map<String,Object> login(Requests.Login r) {
        var opt=db.find("select id,pin_hash,status,role from ap_user where mobile=?",r.mobile());
        if(opt.isEmpty() || !encoder.matches(r.pin(),opt.get().get("pinHash").toString()) || !r.role().equals(opt.get().get("role"))) throw new ApiError(HttpStatus.UNAUTHORIZED,"Incorrect mobile, PIN or login type.");
        var u=opt.get();
        if(!u.get("status").equals("ACTIVE"))throw new ApiError(HttpStatus.FORBIDDEN,"Account awaiting approval or not active. Contact the apartment admin.");
        return issueSession(UUID.fromString(u.get("id").toString()));
    }
    private Map<String,Object> issueSession(UUID user) {
        String raw=Tokens.create();
        Instant expires=Instant.now().plus(Duration.ofHours(hours));
        db.update("insert into ap_session(token_hash,user_id,expires_at) values(?,?,?)",Tokens.digest(raw),user,java.sql.Timestamp.from(expires));
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("token",raw);
        result.put("expiresAt",expires.toString());
        result.put("account",db.one("select u.id,u.tenant_id,u.flat_id,u.name,u.mobile,u.role,t.name as apartment_name from ap_user u join ap_tenant t on t.id=u.tenant_id where u.id=?",user));
        return result;
    }
    @Transactional public Map<String,Object> invite(Account a) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        String code=Tokens.create().substring(0,14).toUpperCase(Locale.ROOT);
        Instant expires=Instant.now().plus(Duration.ofDays(7));
        db.update("update ap_invite set active=false where tenant_id=?",a.tenantId());
        db.update("insert into ap_invite(id,tenant_id,code_hash,expires_at) values(?,?,?,?)",UUID.randomUUID(),a.tenantId(),Tokens.digest(code),java.sql.Timestamp.from(expires));
        db.audit(a.tenantId(),a.id(),"INVITE_ROTATED",a.tenantId(),null,"7-day resident invitation");
        return Map.of("code",code,"expiresAt",expires.toString());
    }
    public Map<String,Object> previewInvite(String raw) {
        var invite=db.one("select i.tenant_id,t.name from ap_invite i join ap_tenant t on t.id=i.tenant_id where i.code_hash=? and i.active=true and i.expires_at>now()",Tokens.digest(raw.trim().toUpperCase(Locale.ROOT)));
        UUID tenant=(UUID)invite.get("tenantId");
        var flats=db.rows("select f.label from ap_flat f where f.tenant_id=? and f.active=true and not exists(select 1 from ap_user u where u.tenant_id=f.tenant_id and u.flat_id=f.id and u.status in ('ACTIVE','PENDING')) order by f.label",tenant);
        return Map.of("apartmentName",invite.get("name"),"flats",flats);
    }
    @Transactional public Map<String,Object> join(Requests.Join r) {
        requireLocal();
        var invite=db.one("select tenant_id from ap_invite where code_hash=? and active=true and expires_at>now()",Tokens.digest(r.invite().trim().toUpperCase(Locale.ROOT)));
        UUID tenant=UUID.fromString(invite.get("tenantId").toString());
        db.lockTenant(tenant);
        // Recheck after obtaining tenant lock in case an admin rotated the code concurrently.
        db.one("select id from ap_invite where tenant_id=? and code_hash=? and active=true and expires_at>now()",tenant,Tokens.digest(r.invite().trim().toUpperCase(Locale.ROOT)));
        UUID flat=UUID.fromString(db.one("select id from ap_flat where tenant_id=? and label=? and active=true",tenant,r.flatLabel().trim().toUpperCase(Locale.ROOT)).get("id").toString());
        UUID user=UUID.randomUUID();
        db.update("insert into ap_user(id,tenant_id,flat_id,name,mobile,pin_hash,role,status) values(?,?,?,?,?,?,'RESIDENT','PENDING')",user,tenant,flat,r.name().trim(),r.mobile(),encoder.encode(r.pin()));
        db.audit(tenant,user,"RESIDENT_REQUESTED",flat,null,"Pending");
        db.rows("select id from ap_user where tenant_id=? and role='ADMIN' and status='ACTIVE'",tenant).forEach(u->db.notify(tenant,(UUID)u.get("id"),"JOIN:"+user,"Join request",r.flatLabel()+" · "+r.name(),"members"));
        return Map.of("status","PENDING","message","Request submitted. Sign in after admin approval.");
    }
    @Transactional public void approve(Account a,UUID id) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        int n=db.update("update ap_user set status='ACTIVE' where id=? and tenant_id=? and status='PENDING'",id,a.tenantId());
        if(n!=1)throw ApiError.conflict("This request is not pending.");
        db.audit(a.tenantId(),a.id(),"RESIDENT_APPROVED",id,"Pending","Active");
        db.notify(a.tenantId(),id,"JOIN_APPROVED:"+id,"Welcome home","Your apartment access is approved.","home");
    }
    @Transactional public void reject(Account a,UUID id,String reason) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        if(db.update("update ap_user set status='REJECTED',rejection_reason=? where id=? and tenant_id=? and status='PENDING'",reason,id,a.tenantId())!=1)throw ApiError.conflict("This request is not pending.");
        db.audit(a.tenantId(),a.id(),"RESIDENT_REJECTED",id,"Pending",reason);
    }
    @Transactional public void grantTreasurer(Account a,UUID id) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        if(a.id().equals(id))throw ApiError.conflict("Keep an active Admin account.");
        if(db.update("update ap_user set role='TREASURER' where id=? and tenant_id=? and status='ACTIVE' and role='RESIDENT'",id,a.tenantId())!=1)throw ApiError.conflict("Choose an active resident.");
        // Revoke existing sessions: privilege changes require reauthentication.
        db.update("delete from ap_session where user_id=?",id);
        db.audit(a.tenantId(),a.id(),"TREASURER_GRANTED",id,"Resident","Treasurer");
    }
    public void logout(String bearer) {
        if(bearer!=null&&bearer.startsWith("Bearer "))db.update("delete from ap_session where token_hash=?",Tokens.digest(bearer.substring(7)));
    }
}
