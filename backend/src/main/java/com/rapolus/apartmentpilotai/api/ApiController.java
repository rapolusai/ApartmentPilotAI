package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.finance.FinanceService;
import com.rapolus.apartmentpilotai.store.Db;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.time.YearMonth;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
@RestController @RequestMapping("/api/v1") public class ApiController {
    private final com.rapolus.apartmentpilotai.operations.NoticeService noticeService;
    private final AuthService auth;
    private final FinanceService finance;
    private final Db db;
    private final RateGate gate;
    private final LoginGuard guard;
    private final boolean local;
    public ApiController(com.rapolus.apartmentpilotai.operations.NoticeService noticeService,AuthService auth,FinanceService finance,Db db,RateGate gate,LoginGuard guard,@Value("${app.local-registration-enabled:false}")boolean local) {
        this.noticeService=noticeService;
        this.auth=auth;
        this.finance=finance;
        this.db=db;
        this.gate=gate;
        this.guard=guard;
        this.local=local;
    }
    @GetMapping("/status") Map<String,Object> status() {
        return Map.of("application","ApartmentPilotAI","version","0.2.0","localRegistration",local,"releaseReady",false);
    }
    @PostMapping("/auth/register") Object register(@Valid @RequestBody Requests.Register r,HttpServletRequest req) {
        RateGate.enforce(gate.count("register:"+req.getRemoteAddr()),10);
        return auth.register(r);
    }
    @PostMapping("/auth/login") Object login(@Valid @RequestBody Requests.Login r,HttpServletRequest req) {
        String ip=req.getRemoteAddr();
        RateGate.enforce(gate.count("login-ip:"+ip),100);
        if(!guard.enabled()) {
            RateGate.enforce(gate.count("login-mobile:"+r.mobile()),10);
            return auth.login(r);
        }
        guard.reserve(r.mobile(),ip);
        try {
            var session=auth.login(r);
            guard.succeeded(r.mobile(),ip);
            return session;
        } catch(ApiError e) {
            // 401 = wrong mobile, PIN or login type: the reserved failure stands. 403 = correct PIN on an inactive account.
            if(e.status()==HttpStatus.UNAUTHORIZED)guard.failed(r.mobile());
            else if(e.status()==HttpStatus.FORBIDDEN)guard.succeeded(r.mobile(),ip);
            throw e;
        }
    }
    @GetMapping("/auth/invites/{code}") Object previewInvite(@PathVariable String code,HttpServletRequest req) {
        RateGate.enforce(gate.count("invite-lookup:"+req.getRemoteAddr()),30);
        return auth.previewInvite(code);
    }
    @PostMapping("/auth/join") Object join(@Valid @RequestBody Requests.Join r,HttpServletRequest req) {
        RateGate.enforce(gate.count("join:"+req.getRemoteAddr()),20);
        return auth.join(r);
    }
    @PostMapping("/auth/join-status") Object joinStatus(@Valid @RequestBody Requests.JoinStatus r,HttpServletRequest req) {
        RateGate.enforce(gate.count("join-status:"+req.getRemoteAddr()),60);
        return auth.joinStatus(r.receipt());
    }
    @PostMapping("/logout") Object logout(@RequestHeader("Authorization")String token) {
        auth.logout(token);
        return Map.of("ok",true);
    }
    @GetMapping("/me") Object me(@AuthenticationPrincipal Account a) {
        return db.one("select u.id,u.tenant_id,u.flat_id,u.name,u.mobile,u.role,t.name as apartment_name from ap_user u join ap_tenant t on t.id=u.tenant_id where u.id=?",a.id());
    }
    @GetMapping("/flats") Object flats(@AuthenticationPrincipal Account a) {
        a.requireStaff();
        return db.rows("select f.id,f.label,f.active,f.occupied,u.name,u.status from ap_flat f left join ap_user u on u.tenant_id=f.tenant_id and u.flat_id=f.id and u.status in ('ACTIVE','PENDING') where f.tenant_id=? order by f.label",a.tenantId());
    }
    @GetMapping("/members") Object members(@AuthenticationPrincipal Account a) {
        a.requireAdmin();
        return db.rows("select u.id,u.name,case when u.deleted_at is null then u.mobile else '' end as mobile,u.role,u.status,u.resident_type,u.rejection_reason,u.created_at,u.deleted_at,f.label as flat_label from ap_user u left join ap_flat f on f.tenant_id=u.tenant_id and f.id=u.flat_id where u.tenant_id=? order by u.created_at desc",a.tenantId());
    }
    @GetMapping("/members/{id}") Object member(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return auth.member(a,id);
    }
    @GetMapping("/invites/current") Object inviteStatus(@AuthenticationPrincipal Account a) {
        return auth.inviteStatus(a);
    }
    @PostMapping("/invites") Object invite(@AuthenticationPrincipal Account a,@Valid @RequestBody Requests.InviteRotate request) {
        return auth.invite(a,request);
    }
    @PutMapping("/invites/current/expiry") Object inviteExpiry(@AuthenticationPrincipal Account a,@Valid @RequestBody Requests.InviteExpiry request) {
        return auth.inviteExpiry(a,request.expiry());
    }
    @PostMapping("/invites/current/revoke") Object revokeInvite(@AuthenticationPrincipal Account a) {
        return auth.inviteActivation(a,false);
    }
    @PostMapping("/invites/current/activate") Object activateInvite(@AuthenticationPrincipal Account a) {
        return auth.inviteActivation(a,true);
    }
    @PostMapping("/members/{id}/approve") Object approveMember(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Requests.ClaimReview review) {
        auth.approve(a,id,review.verified());
        return Map.of("ok",true);
    }
    @PostMapping("/members/{id}/reject") Object rejectMember(@AuthenticationPrincipal Account a,@PathVariable UUID id,@Valid @RequestBody Requests.Reject r) {
        auth.reject(a,id,r.reason());
        return Map.of("ok",true);
    }
    @PostMapping("/members/{id}/treasurer") Object treasurer(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        auth.grantTreasurer(a,id);
        return Map.of("ok",true);
    }
    @GetMapping("/billing/rules") Object rules(@AuthenticationPrincipal Account a) {
        return finance.rules(a);
    }
    @PostMapping("/billing/rules") Object rule(@AuthenticationPrincipal Account a,@Valid @RequestBody Requests.Rule r) {
        return finance.setRule(a,r);
    }
    @PostMapping("/billing/generate") Object generate(@AuthenticationPrincipal Account a,@RequestParam String month) {
        return finance.generate(a,YearMonth.parse(month));
    }
    @GetMapping("/billing/preview") Object billingPreview(@AuthenticationPrincipal Account a,@RequestParam String month) {
        return finance.billingPreview(a,YearMonth.parse(month));
    }
    @GetMapping("/billing/reminders/preview") Object reminderPreview(@AuthenticationPrincipal Account a,@RequestParam String month) {
        return finance.reminderPreview(a,YearMonth.parse(month));
    }
    @PostMapping("/billing/reminders/send") Object sendReminders(@AuthenticationPrincipal Account a,@RequestParam String month,@Valid @RequestBody Requests.Reminder r) {
        RateGate.enforce(gate.count("maintenance-reminder:"+a.id()),10);
        return finance.sendReminders(a,YearMonth.parse(month),r.requestKey());
    }
    @GetMapping("/bills") Object bills(@AuthenticationPrincipal Account a,@RequestParam String month) {
        return finance.bills(a,YearMonth.parse(month));
    }
    @GetMapping("/bills/{id}") Object bill(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return finance.bill(a,id);
    }
    @GetMapping("/payments") Object payments(@AuthenticationPrincipal Account a,@RequestParam(required=false) UUID flatId) {
        return finance.payments(a,flatId);
    }
    @GetMapping("/payments/{id}") Object payment(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return finance.payment(a,id);
    }
    @PostMapping("/payments") Object submit(@AuthenticationPrincipal Account a,@Valid @RequestBody Requests.Payment r) {
        return finance.submit(a,r);
    }
    @PostMapping("/payments/approve") Object approve(@AuthenticationPrincipal Account a,@Valid @RequestBody Requests.Review r) {
        return finance.approve(a,r);
    }
    @PostMapping("/payments/{id}/reject") Object reject(@AuthenticationPrincipal Account a,@PathVariable UUID id,@Valid @RequestBody Requests.Reject r) {
        finance.reject(a,id,r.reason());
        return Map.of("ok",true);
    }
    @GetMapping("/receipts/{id}") Object receipt(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return finance.receipt(a,id);
    }
    @GetMapping("/expenses") Object expenses(@AuthenticationPrincipal Account a,@RequestParam String month) {
        return finance.expenses(a,YearMonth.parse(month));
    }
    @PostMapping("/expenses") Object expense(@AuthenticationPrincipal Account a,@Valid @RequestBody Requests.Expense r) {
        return finance.expense(a,r);
    }
    @GetMapping("/reports/monthly") Object report(@AuthenticationPrincipal Account a,@RequestParam String month) {
        return finance.summary(a,YearMonth.parse(month));
    }
    @GetMapping("/notices") Object notices(@AuthenticationPrincipal Account a) {
        return noticeService.list(a);
    }
    @GetMapping("/notices/{id}") Object notice(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return noticeService.get(a,id);
    }
    @GetMapping("/notices/{id}/preview") Object noticePreview(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return noticeService.preview(a,id);
    }
    @PostMapping("/notices") Object createNotice(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> r) {
        if(!r.containsKey("requestKey"))r.put("requestKey",UUID.randomUUID().toString());
        return noticeService.save(a,null,r);
    }
    @PostMapping("/notices/{id}") Object editNotice(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> r) {
        return noticeService.save(a,id,r);
    }
    @PostMapping("/notices/{id}/publish") Object publish(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return noticeService.publish(a,id);
    }
    @PostMapping("/notices/{id}/schedule") Object schedule(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> r) {
        return noticeService.schedule(a,id,r);
    }
    @PostMapping("/notices/{id}/read") Object noticeRead(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> r) {
        return noticeService.read(a,id,com.rapolus.apartmentpilotai.operations.V.bool(r,"acknowledge",false));
    }
    @PostMapping("/notices/{id}/pin") Object noticePin(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> r) {
        return noticeService.pin(a,id,com.rapolus.apartmentpilotai.operations.V.bool(r,"pinned",true));
    }
    @GetMapping("/notifications") Object inbox(@AuthenticationPrincipal Account a) {
        return db.rows("select id,title,body,target,created_at,read_at from ap_inbox where tenant_id=? and recipient_id=? order by created_at desc limit 100",a.tenantId(),a.id());
    }
    @GetMapping("/notifications/{id}") Object notification(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return db.one("select id,title,body,target,created_at,read_at from ap_inbox where tenant_id=? and recipient_id=? and id=?",a.tenantId(),a.id(),id);
    }
    @PostMapping("/notifications/{id}/read") Object read(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        if(db.update("update ap_inbox set read_at=coalesce(read_at,now()) where id=? and tenant_id=? and recipient_id=?",id,a.tenantId(),a.id())!=1)throw ApiError.missing();
        return Map.of("ok",true);
    }
    // Rows with no person as actor: provider operations, login-protection notices and scheduled automation.
    private static final String AUDIT_COLUMNS="select a.id,a.action,a.record_id,a.before_value,a.after_value,a.created_at,coalesce(u.name,case when a.actor_id is not null then null when a.action like 'PROVIDER%' or a.action in ('REFERRAL_REFUND_REVIEW','FIRST_MONTH_WAIVED') then 'Provider' when a.action='LOGIN_LOCKED' then 'Security' when a.action='ACCOUNT_DELETION_REQUESTED' then 'Web request' when a.action='ACCOUNT_DELETED' then 'Operator' else 'Automation' end) as actor from ap_audit a left join ap_user u on u.id=a.actor_id and u.tenant_id=a.tenant_id where a.tenant_id=?";
    /**
     * No parameters: the newest 100 rows exactly as before. Paging (any parameter given): newest first with an id tie-break,
     * {@code before=<createdAt>,<id>} taken from the last row of the previous page, {@code limit} 1-200 (larger values are
     * capped), {@code action} an exact action name.
     */
    @GetMapping("/audit") Object audit(@AuthenticationPrincipal Account a,@RequestParam(required=false) String before,@RequestParam(required=false) Integer limit,@RequestParam(required=false) String action) {
        a.requireStaff();
        if(before==null&&limit==null&&action==null)return db.rows(AUDIT_COLUMNS+" order by a.created_at desc limit 100",a.tenantId());
        int size=limit==null?100:Math.min(limit,200);
        if(size<1)throw new IllegalArgumentException("limit must be between 1 and 200.");
        if(action!=null&&!action.matches("[A-Z_]{1,60}"))throw new IllegalArgumentException("Invalid action.");
        java.sql.Timestamp beforeAt=null;
        UUID beforeId=null;
        if(before!=null) {
            String[] parts=before.split(",",-1);
            try {
                if(parts.length!=2)throw new IllegalArgumentException();
                beforeAt=java.sql.Timestamp.from(java.time.Instant.parse(parts[0].trim()));
                beforeId=UUID.fromString(parts[1].trim());
            } catch(RuntimeException e) {
                throw new IllegalArgumentException("before must be <createdAt>,<id> from the last row of the previous page.");
            }
        }
        return db.rows(AUDIT_COLUMNS+" and (?::text is null or a.action=?) and (?::timestamptz is null or (a.created_at,a.id)<(?::timestamptz,?::uuid)) order by a.created_at desc,a.id desc limit ?",a.tenantId(),action,action,beforeAt,beforeAt,beforeId,size);
    }
    @GetMapping("/audit/{id}") Object auditDetail(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        a.requireStaff();
        return db.one(AUDIT_COLUMNS+" and a.id=?",a.tenantId(),id);
    }
}
