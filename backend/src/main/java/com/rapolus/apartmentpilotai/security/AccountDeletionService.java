package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.operations.SubscriptionService;
import com.rapolus.apartmentpilotai.store.Db;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Account deletion (Google Play's user-data policy). The person's account is closed at once and every personal field
 * is anonymised; the row itself stays because approved payments, expenses and audit entries reference it and the
 * association must keep its accounts. Approved money is never deleted or rewritten.
 */
@Service public class AccountDeletionService {
    public static final String FORMER="Former resident";
    private final Db db;
    private final PasswordEncoder encoder;
    private final SubscriptionService subscriptions;
    public AccountDeletionService(Db db,PasswordEncoder encoder,SubscriptionService subscriptions) {
        this.db=db;
        this.encoder=encoder;
        this.subscriptions=subscriptions;
    }
    /** What the app shows before asking for the PIN; also tells an Admin in advance that they are the last one. */
    public Map<String,Object> preview(Account a) {
        Map<String,Object> view=new LinkedHashMap<>();
        boolean lastAdmin="ADMIN".equals(a.role())&&otherActiveAdmins(a.tenantId(),a.id())==0;
        view.put("lastAdmin",lastAdmin);
        view.put("flatLabel",flatLabel(a.tenantId(),a.flatId()));
        view.put("keeps",List.of("Payments, receipts and expense records you were part of, shown as \""+FORMER+"\"","Notices, polls and bookings already recorded for your flat"));
        view.put("removes",List.of("Your name, mobile number, email and PIN","Your sign-in on every device","Your claim on the flat, so the Admin can approve the next resident"));
        return view;
    }
    @Transactional public Map<String,Object> deleteOwnAccount(Account a,String pin) {
        db.lockTenant(a.tenantId());
        var u=db.one("select pin_hash,role,flat_id from ap_user where id=? and tenant_id=? and status='ACTIVE' and deleted_at is null for update",a.id(),a.tenantId());
        if(!encoder.matches(pin,u.get("pinHash").toString()))throw new ApiError(HttpStatus.BAD_REQUEST,"PIN is incorrect.");
        anonymise(a.tenantId(),a.id(),a.id(),"the account holder");
        return Map.of("deleted",true,"message","Your account has been deleted. Financial records of your flat stay with the apartment, shown as \""+FORMER+"\".");
    }
    /** Web-page request: stored for the operator; nothing is changed yet. A matching apartment is told. */
    @Transactional public Map<String,Object> request(String mobile,String apartmentName,String note,String clientHash) {
        UUID id=UUID.randomUUID();
        db.update("insert into ap_deletion_request(id,mobile,apartment_name,note,client_hash) values(?,?,?,?,?)",id,mobile,apartmentName.trim(),note==null?"":note.trim(),clientHash);
        matching(mobile,apartmentName).ifPresent(user-> {
            UUID tenant=(UUID)user.get("tenantId");
            db.audit(tenant,null,"ACCOUNT_DELETION_REQUESTED",user.get("id"),null,"Requested on the web page · request "+id);
            db.rows("select id from ap_user where tenant_id=? and role='ADMIN' and status='ACTIVE'",tenant).forEach(adm->db.notify(tenant,(UUID)adm.get("id"),"DELETION_REQUEST:"+id,"Account deletion requested",String.valueOf(user.get("flatLabel"))+" · a resident asked, on the web, for their account to be deleted","members"));
        });
        // The reply never says whether the mobile or apartment exists.
        return Map.of("received",true,"reference",id.toString().substring(0,8).toUpperCase(Locale.ROOT),"message","Your request has been recorded. It is verified before anything is deleted, and your records are then handled as described in the privacy policy.");
    }
    public List<Map<String,Object>> openRequests(String providerKey) {
        subscriptions.provider(providerKey);
        return db.rows("select id,mobile,apartment_name,note,status,outcome,created_at,handled_at from ap_deletion_request where status='RECEIVED' order by created_at");
    }
    /** Operator action after verifying the request (for example a call back to the number). Same anonymisation as in-app. */
    @Transactional public Map<String,Object> execute(String providerKey,UUID requestId,boolean approve,String outcome) {
        subscriptions.provider(providerKey);
        var r=db.one("select mobile,apartment_name from ap_deletion_request where id=? and status='RECEIVED' for update",requestId);
        String result=outcome==null?"":outcome.trim();
        if(approve) {
            var user=matching(r.get("mobile").toString(),r.get("apartmentName").toString()).orElseThrow(()->ApiError.conflict("No active account matches this mobile and apartment name."));
            UUID tenant=(UUID)user.get("tenantId"),id=(UUID)user.get("id");
            db.lockTenant(tenant);
            db.one("select id from ap_user where id=? and status='ACTIVE' and deleted_at is null for update",id);
            anonymise(tenant,id,null,"the operator after a web request");
            result=result.isEmpty()?"Deleted after verification":result;
        }
        db.update("update ap_deletion_request set status=?,outcome=?,handled_at=now() where id=?",approve?"DONE":"REJECTED",result,requestId);
        return Map.of("id",requestId,"status",approve?"DONE":"REJECTED");
    }
    private Optional<Map<String,Object>> matching(String mobile,String apartmentName) {
        return db.find("select u.id,u.tenant_id,f.label as flat_label from ap_user u join ap_tenant t on t.id=u.tenant_id left join ap_flat f on f.tenant_id=u.tenant_id and f.id=u.flat_id where u.mobile=? and u.status='ACTIVE' and u.deleted_at is null and lower(t.name)=lower(?)",mobile,apartmentName.trim());
    }
    private long otherActiveAdmins(UUID tenant,UUID except) {
        return db.count("select count(*) from ap_user where tenant_id=? and role='ADMIN' and status='ACTIVE' and id<>?",tenant,except);
    }
    private String flatLabel(UUID tenant,UUID flat) {
        if(flat==null)return "";
        return db.find("select label from ap_flat where tenant_id=? and id=?",tenant,flat).map(f->f.get("label").toString()).orElse("");
    }
    // Caller holds the tenant lock and the user row lock.
    private void anonymise(UUID tenant,UUID id,UUID actor,String by) {
        var u=db.one("select role,flat_id,mobile from ap_user where id=? and tenant_id=?",id,tenant);
        if("ADMIN".equals(u.get("role"))&&otherActiveAdmins(tenant,id)==0)throw ApiError.conflict("You are the last active Admin of this apartment. Make another member Admin first, or ask for the whole apartment to be removed.");
        String label=flatLabel(tenant,(UUID)u.get("flatId"));
        String mobile=u.get("mobile").toString();
        String name=label.isEmpty()?FORMER:FORMER+" · "+label;
        // 'deleted:' + 32 hex digits = 40 characters: unique per user and never a valid login mobile.
        String placeholder="deleted:"+id.toString().replace("-","");
        db.update("update ap_user set name=?,mobile=?,email='',pin_hash=?,status='DISABLED',rejection_reason='Account deleted',deleted_at=now() where id=? and tenant_id=?",name,placeholder,encoder.encode(Tokens.create()),id,tenant);
        db.update("delete from ap_session where user_id=?",id);
        db.update("delete from ap_join_receipt where user_id=?",id);
        // Directory entries typed in by the Admin that carry the same number lose it; the flat record itself stays.
        db.update("update ap_member_record set mobile='' where tenant_id=? and mobile=?",tenant,mobile);
        db.audit(tenant,actor,"ACCOUNT_DELETED",id,u.get("role")+(label.isEmpty()?"":" · "+label),"Deleted by "+by+"; personal details removed, financial records kept as \""+FORMER+"\"");
        db.rows("select id from ap_user where tenant_id=? and role='ADMIN' and status='ACTIVE' and id<>?",tenant,id).forEach(adm->db.notify(tenant,(UUID)adm.get("id"),"ACCOUNT_DELETED:"+id,"Account deleted",(label.isEmpty()?"A member":"Flat "+label)+" deleted their account. The flat can be assigned to the next resident.","members"));
    }
}
