package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.finance.FinanceService;
import com.rapolus.apartmentpilotai.store.Db;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.time.YearMonth;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
@RestController @RequestMapping("/api/v1") public class ApiController {
    private final com.rapolus.apartmentpilotai.operations.NoticeService noticeService;
    private final AuthService auth;
    private final FinanceService finance;
    private final Db db;
    private final RateGate gate;
    private final boolean local;
    public ApiController(com.rapolus.apartmentpilotai.operations.NoticeService noticeService,AuthService auth,FinanceService finance,Db db,RateGate gate,@Value("${app.local-registration-enabled:false}")boolean local) {
        this.noticeService=noticeService;
        this.auth=auth;
        this.finance=finance;
        this.db=db;
        this.gate=gate;
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
        RateGate.enforce(gate.count("login-ip:"+req.getRemoteAddr()),100);
        RateGate.enforce(gate.count("login-mobile:"+r.mobile()),10);
        return auth.login(r);
    }
    @GetMapping("/auth/invites/{code}") Object previewInvite(@PathVariable String code,HttpServletRequest req) {
        RateGate.enforce(gate.count("invite-lookup:"+req.getRemoteAddr()),30);
        return auth.previewInvite(code);
    }
    @PostMapping("/auth/join") Object join(@Valid @RequestBody Requests.Join r,HttpServletRequest req) {
        RateGate.enforce(gate.count("join:"+req.getRemoteAddr()),20);
        return auth.join(r);
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
        return db.rows("select u.id,u.name,u.mobile,u.role,u.status,f.label as flat_label from ap_user u left join ap_flat f on f.id=u.flat_id where u.tenant_id=? order by u.status desc,u.name",a.tenantId());
    }
    @PostMapping("/invites") Object invite(@AuthenticationPrincipal Account a) {
        return auth.invite(a);
    }
    @PostMapping("/members/{id}/approve") Object approveMember(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        auth.approve(a,id);
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
    @GetMapping("/bills") Object bills(@AuthenticationPrincipal Account a,@RequestParam String month) {
        return finance.bills(a,YearMonth.parse(month));
    }
    @GetMapping("/bills/{id}") Object bill(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return finance.bill(a,id);
    }
    @GetMapping("/payments") Object payments(@AuthenticationPrincipal Account a) {
        return finance.payments(a);
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
    @GetMapping("/audit") Object audit(@AuthenticationPrincipal Account a) {
        a.requireStaff();
        return db.rows("select action,record_id,before_value,after_value,created_at from ap_audit where tenant_id=? order by created_at desc limit 100",a.tenantId());
    }
}
