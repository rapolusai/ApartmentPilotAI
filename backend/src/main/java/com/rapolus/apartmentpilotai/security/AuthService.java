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
    private final InviteCipher inviteCipher;
    private final boolean localRegistration;
    private final int hours;
    // Unknown mobiles are compared against this hash so they take as long as a wrong PIN and do not reveal registration.
    // Same cost as SecurityConfig's encoder; generated per process, it matches no PIN anyone can know.
    private static final class Dummy {
        static final String HASH=new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12).encode(Tokens.create());
    }
    public AuthService(Db db,PasswordEncoder encoder,InviteCipher inviteCipher,@Value("${app.local-registration-enabled:false}")boolean localRegistration,@Value("${app.session-hours:12}")int hours) {
        this.db=db;
        this.encoder=encoder;
        this.inviteCipher=inviteCipher;
        this.localRegistration=localRegistration;
        this.hours=hours;
    }
    private void requireLocal() {
        if(!localRegistration)throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Account verification has not been configured. Registration is disabled outside the local profile.");
    }
    @Transactional public Map<String,Object> register(Requests.Register r) {
        requireLocal();
        PinPolicy.requireNew(r.pin());
        if(db.find("select id from ap_user where mobile=?",r.mobile()).isPresent())throw ApiError.conflict("An account already exists for these details. Sign in or contact your apartment admin.");
        UUID tenant=UUID.randomUUID(),user=UUID.randomUUID();
        db.update("insert into ap_tenant(id,name,city,address,pincode) values(?,?,?,?,?)",tenant,r.apartmentName().trim(),r.city().trim(),r.address()==null?"":r.address().trim(),r.pinCode()==null?"":r.pinCode());
        db.update("insert into ap_block(id,tenant_id,name) values(?,?,'A')",UUID.randomUUID(),tenant);
        for(int i=1; i<=r.flats(); i++)db.update("insert into ap_flat(id,tenant_id,label) values(?,?,?)",UUID.randomUUID(),tenant,"A-"+(100+i));
        db.update("insert into ap_user(id,tenant_id,name,mobile,pin_hash,role,status) values(?,?,?,?,?,'ADMIN','ACTIVE')",user,tenant,r.name().trim(),r.mobile(),encoder.encode(r.pin()));
        db.audit(tenant,user,"APARTMENT_CREATED",tenant,null,r.apartmentName());
        return issueSession(user);
    }
    @Transactional public Map<String,Object> login(Requests.Login r) {
        var opt=db.find("select id,pin_hash,status,role from ap_user where mobile=?",r.mobile());
        boolean pinMatches=encoder.matches(r.pin(),opt.map(u->u.get("pinHash").toString()).orElse(Dummy.HASH));
        if(opt.isEmpty() || !pinMatches || !r.role().equals(opt.get().get("role"))) throw new ApiError(HttpStatus.UNAUTHORIZED,"Incorrect mobile, PIN or login type.");
        var u=opt.get();
        if(!u.get("status").equals("ACTIVE"))throw new ApiError(HttpStatus.FORBIDDEN,"Account awaiting approval or not active. Contact the apartment admin.");
        var session=issueSession(UUID.fromString(u.get("id").toString()));
        // Additive: legacy 4-5 digit or weak PINs still sign in, and the app is told a change is due (Profile > Change PIN).
        session.put("pinChangeRequired",PinPolicy.changeRequired(r.pin()));
        return session;
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
    private Optional<Map<String,Object>> latestInvite(UUID tenant) {
        return db.find("select id,active,expires_at,created_at,code_ciphertext,request_key from ap_invite where tenant_id=? order by created_at desc,id desc limit 1",tenant);
    }
    private Instant expiryOf(Map<String,Object> row) {
        return Instant.parse(row.get("expiresAt").toString());
    }
    private Map<String,Object> inviteView(UUID tenant,Map<String,Object> row) {
        Map<String,Object> view=new LinkedHashMap<>();
        view.put("exists",row!=null);
        view.put("displayConfigured",inviteCipher.configured());
        if(row==null) {
            view.put("active",false);
            view.put("valid",false);
            view.put("codeAvailable",false);
            view.put("displayError",null);
            view.put("expiresAt",null);
            view.put("expiresOn",null);
            view.put("code",null);
            return view;
        }
        Instant expiry=expiryOf(row);
        boolean active=Boolean.TRUE.equals(row.get("active"));
        String ciphertext=(String)row.get("codeCiphertext");
        String code=null;
        String displayError=null;
        if(ciphertext!=null&&inviteCipher.configured()) {
            try { code=inviteCipher.decrypt(tenant,UUID.fromString(row.get("id").toString()),ciphertext); }
            catch(ApiError unreadable) { displayError="Current code cannot be displayed with this server key. Restore the key or explicitly generate a new code."; }
        }
        view.put("active",active);
        view.put("valid",active&&expiry.isAfter(Instant.now()));
        view.put("codeAvailable",code!=null);
        view.put("displayError",displayError);
        view.put("expiresAt",expiry.toString());
        view.put("expiresOn",expiry.minusNanos(1).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate().toString());
        view.put("code",code);
        return view;
    }
    public Map<String,Object> inviteStatus(Account a) {
        a.requireAdmin();
        return inviteView(a.tenantId(),latestInvite(a.tenantId()).orElse(null));
    }
    @Transactional public Map<String,Object> invite(Account a,Requests.InviteRotate request) {
        a.requireAdmin();
        if(!inviteCipher.configured())throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Invite display encryption is NOT_CONFIGURED. Configure APP_INVITE_DISPLAY_KEY before issuing codes.");
        db.lockTenant(a.tenantId());
        var replay=db.find("select id,active,expires_at,created_at,code_ciphertext,request_key from ap_invite where tenant_id=? and request_key=?",a.tenantId(),request.requestKey());
        if(replay.isPresent())return inviteView(a.tenantId(),replay.get());
        var previous=latestInvite(a.tenantId());
        String code=Tokens.create().substring(0,14).toUpperCase(Locale.ROOT);
        Instant expires=previous.map(this::expiryOf).filter(x->x.isAfter(Instant.now())).orElseGet(()->Instant.now().plus(Duration.ofDays(7)));
        UUID inviteId=UUID.randomUUID();
        String ciphertext=inviteCipher.encrypt(a.tenantId(),inviteId,code);
        db.update("update ap_invite set active=false where tenant_id=?",a.tenantId());
        // now() is transaction-start time: a waiting rotation could otherwise sort before the prior winner.
        db.update("insert into ap_invite(id,tenant_id,code_hash,expires_at,code_ciphertext,request_key,created_at) values(?,?,?,?,?,?,clock_timestamp())",inviteId,a.tenantId(),Tokens.digest(code),java.sql.Timestamp.from(expires),ciphertext,request.requestKey());
        db.audit(a.tenantId(),a.id(),"INVITE_ROTATED",inviteId,previous.isPresent()?"Previous code invalidated":"No previous code","New code issued; approved accounts unchanged");
        return inviteView(a.tenantId(),latestInvite(a.tenantId()).orElseThrow());
    }
    @Transactional public Map<String,Object> inviteExpiry(Account a,LocalDate expiry) {
        a.requireAdmin();
        LocalDate today=LocalDate.now(ZoneId.of("Asia/Kolkata"));
        if(expiry.isBefore(today)||expiry.isAfter(today.plusYears(1)))throw new ApiError(HttpStatus.BAD_REQUEST,"Choose today or a date within one year.");
        db.lockTenant(a.tenantId());
        var current=latestInvite(a.tenantId()).orElseThrow(ApiError::missing);
        Instant old=expiryOf(current);
        Instant until=expiry.plusDays(1).atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();
        if(!old.equals(until)) {
            db.update("update ap_invite set expires_at=? where id=? and tenant_id=?",java.sql.Timestamp.from(until),current.get("id"),a.tenantId());
            db.audit(a.tenantId(),a.id(),"INVITE_EXPIRY_CHANGED",current.get("id"),old.toString(),until.toString());
        }
        return inviteView(a.tenantId(),latestInvite(a.tenantId()).orElseThrow());
    }
    @Transactional public Map<String,Object> inviteActivation(Account a,boolean activate) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var current=latestInvite(a.tenantId()).orElseThrow(ApiError::missing);
        boolean old=Boolean.TRUE.equals(current.get("active"));
        if(activate) {
            if(current.get("codeCiphertext")==null)throw ApiError.conflict("Rotate the legacy invite before activating it.");
            if(!inviteCipher.configured())throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Invite display encryption is NOT_CONFIGURED.");
            if(!expiryOf(current).isAfter(Instant.now()))throw ApiError.conflict("Set a future expiry before activating this invite.");
            // Fail closed if this process cannot decrypt the same code it would reactivate.
            inviteCipher.decrypt(a.tenantId(),UUID.fromString(current.get("id").toString()),(String)current.get("codeCiphertext"));
        }
        if(old!=activate) {
            db.update("update ap_invite set active=? where id=? and tenant_id=?",activate,current.get("id"),a.tenantId());
            db.audit(a.tenantId(),a.id(),activate?"INVITE_ACTIVATED":"INVITE_REVOKED",current.get("id"),old?"Active":"Revoked",activate?"Active":"Revoked");
        }
        return inviteView(a.tenantId(),latestInvite(a.tenantId()).orElseThrow());
    }
    public Map<String,Object> previewInvite(String raw) {
        var invite=db.one("select i.tenant_id,t.name,t.city from ap_invite i join ap_tenant t on t.id=i.tenant_id where i.code_hash=? and i.active=true and i.expires_at>now()",Tokens.digest(raw.trim().toUpperCase(Locale.ROOT)));
        UUID tenant=(UUID)invite.get("tenantId");
        var flats=db.rows("select f.label from ap_flat f where f.tenant_id=? and f.active=true and not exists(select 1 from ap_user u where u.tenant_id=f.tenant_id and u.flat_id=f.id and u.status in ('ACTIVE','PENDING')) order by f.label",tenant);
        return Map.of("apartmentName",invite.get("name"),"city",invite.get("city"),"flats",flats);
    }
    @Transactional public Map<String,Object> join(Requests.Join r) {
        requireLocal();
        PinPolicy.requireNew(r.pin());
        var invite=db.one("select tenant_id from ap_invite where code_hash=? and active=true and expires_at>now()",Tokens.digest(r.invite().trim().toUpperCase(Locale.ROOT)));
        UUID tenant=UUID.fromString(invite.get("tenantId").toString());
        db.lockTenant(tenant);
        // Recheck after obtaining tenant lock in case an admin rotated the code concurrently.
        db.one("select id from ap_invite where tenant_id=? and code_hash=? and active=true and expires_at>now()",tenant,Tokens.digest(r.invite().trim().toUpperCase(Locale.ROOT)));
        UUID flat=UUID.fromString(db.one("select id from ap_flat where tenant_id=? and label=? and active=true",tenant,r.flatLabel().trim().toUpperCase(Locale.ROOT)).get("id").toString());
        var previous=db.find("select u.id,u.tenant_id,u.flat_id,u.status,u.pin_hash,u.role,f.label as flat_label from ap_user u left join ap_flat f on f.tenant_id=u.tenant_id and f.id=u.flat_id where u.mobile=? for update of u",r.mobile());
        if(previous.isPresent()) {
            var old=previous.get();
            boolean sameFlat=flat.toString().equals(String.valueOf(old.get("flatId")));
            String status=old.get("status").toString();
            // A rejected applicant may correct the flat (the usual reason for rejection); a pending request keeps its flat.
            boolean ownRequest=tenant.toString().equals(old.get("tenantId").toString())&&"RESIDENT".equals(old.get("role"))&&(sameFlat?List.of("PENDING","REJECTED").contains(status):"REJECTED".equals(status))&&encoder.matches(r.pin(),old.get("pinHash").toString());
            if(!ownRequest)throw ApiError.conflict("An account already exists for these details. Sign in or contact your apartment admin.");
            UUID existing=UUID.fromString(old.get("id").toString());
            if("PENDING".equals(status))return joinReceipt(existing);
            if(!sameFlat&&db.count("select count(*) from ap_user where tenant_id=? and flat_id=? and status in ('ACTIVE','PENDING')",tenant,flat)>0)throw ApiError.conflict("This flat already has a resident or a pending request. Check the flat number or contact your apartment admin.");
            // The flat index remains the final arbiter if another resident has since claimed it.
            db.update("update ap_user set status='PENDING',rejection_reason=null,name=?,resident_type=?,flat_id=? where id=? and tenant_id=?",r.name().trim(),r.residentType(),flat,existing,tenant);
            db.update("delete from ap_join_receipt where user_id=?",existing);
            String label=r.flatLabel().trim().toUpperCase(Locale.ROOT);
            db.audit(tenant,existing,"RESIDENT_RESUBMITTED",flat,sameFlat?"Rejected":"Rejected · "+old.get("flatLabel"),sameFlat?"Pending":"Pending · "+label);
            notifyJoin(tenant,existing,r);
            return joinReceipt(existing);
        }
        UUID user=UUID.randomUUID();
        db.update("insert into ap_user(id,tenant_id,flat_id,name,mobile,pin_hash,role,status,resident_type) values(?,?,?,?,?,?,'RESIDENT','PENDING',?)",user,tenant,flat,r.name().trim(),r.mobile(),encoder.encode(r.pin()),r.residentType());
        db.audit(tenant,user,"RESIDENT_REQUESTED",flat,null,"Pending");
        notifyJoin(tenant,user,r);
        return joinReceipt(user);
    }
    private void notifyJoin(UUID tenant,UUID user,Requests.Join r) {
        String event="JOIN:"+user+":"+UUID.randomUUID();
        db.rows("select id from ap_user where tenant_id=? and role='ADMIN' and status='ACTIVE'",tenant).forEach(u->db.notify(tenant,(UUID)u.get("id"),event,"Join request",r.flatLabel()+" · "+r.name(),"members"));
    }
    private Map<String,Object> joinReceipt(UUID user) {
        String receipt=Tokens.create();
        Instant expires=Instant.now().plus(Duration.ofDays(7));
        db.update("insert into ap_join_receipt(token_hash,user_id,expires_at) values(?,?,?)",Tokens.digest(receipt),user,java.sql.Timestamp.from(expires));
        Map<String,Object> result=new LinkedHashMap<>(joinStatus(receipt));
        result.put("receipt",receipt);
        result.put("expiresAt",expires.toString());
        return result;
    }
    public Map<String,Object> joinStatus(String receipt) {
        var row=db.one("select u.status,u.rejection_reason,t.name as apartment_name,f.label as flat_label from ap_join_receipt r join ap_user u on u.id=r.user_id join ap_tenant t on t.id=u.tenant_id join ap_flat f on f.id=u.flat_id and f.tenant_id=u.tenant_id where r.token_hash=? and r.expires_at>now()",Tokens.digest(receipt));
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("status",row.get("status"));
        result.put("apartmentName",row.get("apartmentName"));
        result.put("flatLabel",row.get("flatLabel"));
        result.put("reason",row.get("rejectionReason")==null?"":row.get("rejectionReason"));
        return result;
    }
    @Transactional public void approve(Account a,UUID id,boolean verified) {
        a.requireAdmin();
        if(!verified)throw new ApiError(HttpStatus.BAD_REQUEST,"Confirm that the resident and flat are verified.");
        db.lockTenant(a.tenantId());
        int n=db.update("update ap_user set status='ACTIVE' where id=? and tenant_id=? and status='PENDING'",id,a.tenantId());
        if(n!=1) {
            // A record outside the caller's apartment must look absent, not "already decided".
            if(db.find("select id from ap_user where id=? and tenant_id=?",id,a.tenantId()).isEmpty())throw ApiError.missing();
            throw ApiError.conflict("This request is not pending.");
        }
        db.audit(a.tenantId(),a.id(),"RESIDENT_APPROVED",id,"Pending","Active");
        db.notify(a.tenantId(),id,"JOIN_APPROVED:"+id,"Welcome home","Your apartment access is approved.","home");
    }
    public Map<String,Object> member(Account a,UUID id) {
        a.requireAdmin();
        return db.one("select u.id,u.name,case when u.deleted_at is null then u.mobile else '' end as mobile,u.role,u.status,u.resident_type,u.rejection_reason,u.created_at,u.deleted_at,f.label as flat_label from ap_user u left join ap_flat f on f.tenant_id=u.tenant_id and f.id=u.flat_id where u.tenant_id=? and u.id=?",a.tenantId(),id);
    }
    @Transactional public void reject(Account a,UUID id,String reason) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var existing=member(a,id);
        if(!existing.get("status").equals("PENDING"))throw ApiError.conflict("This request is not pending.");
        String explanation=reason.trim();
        db.update("update ap_user set status='REJECTED',rejection_reason=? where id=? and tenant_id=?",explanation,id,a.tenantId());
        db.audit(a.tenantId(),a.id(),"RESIDENT_REJECTED",id,"Pending",explanation);
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
