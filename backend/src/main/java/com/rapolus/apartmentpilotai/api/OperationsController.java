package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.operations.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.time.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/ops") public class OperationsController {
    private final CommunityService community;
    private final AccountingService accounting;
    private final TicketService tickets;
    private final BookingService bookings;
    private final FileService files;
    private final SubscriptionService subscriptions;
    private final AutomationService automation;
    private final RateGate gate;
    private final Db db;
    public OperationsController(CommunityService community,AccountingService accounting,TicketService tickets,BookingService bookings,FileService files,SubscriptionService subscriptions,AutomationService automation,RateGate gate,Db db) {
        this.community=community;
        this.accounting=accounting;
        this.tickets=tickets;
        this.bookings=bookings;
        this.files=files;
        this.subscriptions=subscriptions;
        this.automation=automation;
        this.gate=gate;
        this.db=db;
    }
    @GetMapping("/apartment") Object apartment(@AuthenticationPrincipal Account a) {
        return community.apartment(a);
    }
    @PostMapping("/apartment") Object apartment(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return community.saveApartment(a,p);
    }
    @GetMapping("/flats") Object flats(@AuthenticationPrincipal Account a) {
        return community.flats(a);
    }
    @PostMapping("/flats") Object flat(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return community.saveFlat(a,null,p);
    }
    @PostMapping("/flats/{id}") Object flat(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return community.saveFlat(a,id,p);
    }
    @GetMapping("/directory") Object directory(@AuthenticationPrincipal Account a) {
        return community.directory(a);
    }
    @PostMapping("/directory") Object directory(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return community.memberRecord(a,null,p);
    }
    @PostMapping("/directory/{id}") Object directory(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return community.memberRecord(a,id,p);
    }
    @PostMapping("/members/{id}/role") Object role(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return community.changeRole(a,id,p);
    }
    @PostMapping("/members/{id}/end-access") Object end(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return community.endAccess(a,id,p);
    }
    @PostMapping("/profile") Object profile(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return community.profile(a,p);
    }
    @PostMapping("/change-pin") Object pin(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        RateGate.enforce(gate.count("change-pin:"+a.id()),5);
        return community.changePin(a,p);
    }
    @GetMapping("/committee") Object committee(@AuthenticationPrincipal Account a) {
        return community.committee(a);
    }
    @PostMapping("/committee") Object committee(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return community.saveCommittee(a,null,p);
    }
    @PostMapping("/committee/{id}") Object committee(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return community.saveCommittee(a,id,p);
    }
    @GetMapping("/contacts") Object contacts(@AuthenticationPrincipal Account a,@RequestParam(defaultValue="CONTACT")String kind) {
        return community.contacts(a,kind);
    }
    @PostMapping("/contacts") Object contact(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return community.saveContact(a,null,p);
    }
    @PostMapping("/contacts/{id}") Object contact(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return community.saveContact(a,id,p);
    }
    @GetMapping("/settings") Object settings(@AuthenticationPrincipal Account a) {
        return accounting.settings(a);
    }
    @PostMapping("/settings") Object settings(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return accounting.saveSettings(a,p);
    }
    @PostMapping("/opening") Object opening(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return accounting.opening(a,p);
    }
    @GetMapping("/rate-overrides") Object overrides(@AuthenticationPrincipal Account a) {
        return accounting.overrides(a);
    }
    @PostMapping("/rate-overrides") Object override(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return accounting.override(a,p);
    }
    @PostMapping("/charges") Object charge(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return accounting.charge(a,p);
    }
    @PostMapping("/charges/preview") Object previewCharge(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return accounting.previewCharge(a,p);
    }
    @GetMapping("/income") Object income(@AuthenticationPrincipal Account a) {
        return accounting.income(a);
    }
    @PostMapping("/income") Object income(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return accounting.income(a,p);
    }
    @PostMapping("/expenses") Object createExpense(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return accounting.createExpense(a,p);
    }
    @GetMapping("/expenses/{id}") Object expense(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return accounting.expense(a,id);
    }
    @PostMapping("/expenses/{id}") Object expense(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return accounting.editExpense(a,id,p);
    }
    @PostMapping("/expenses/{id}/reverse") Object reverseExpense(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return accounting.reverseExpense(a,id,p);
    }
    @PostMapping("/payments/{id}/reverse") Object reversePayment(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return accounting.reversePayment(a,id,p);
    }
    @GetMapping("/recurring") Object recurring(@AuthenticationPrincipal Account a) {
        return accounting.recurring(a);
    }
    @PostMapping("/recurring") Object recurring(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return accounting.recurring(a,null,p);
    }
    @PostMapping("/recurring/{id}") Object recurring(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return accounting.recurring(a,id,p);
    }
    @GetMapping("/cashbook") Object cashbook(@AuthenticationPrincipal Account a,@RequestParam String month) {
        return accounting.cashbook(a,YearMonth.parse(month));
    }
    @GetMapping("/tickets") Object tickets(@AuthenticationPrincipal Account a,@RequestParam(defaultValue="ISSUE")String kind) {
        return tickets.list(a,kind);
    }
    @GetMapping("/tickets/{id}") Object ticket(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return tickets.get(a,id);
    }
    @GetMapping("/tickets/{id}/affected") Object affected(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return tickets.affected(a,id);
    }
    @PostMapping("/tickets") Object ticket(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return tickets.create(a,p);
    }
    @PostMapping("/tickets/{id}/status") Object ticket(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return tickets.update(a,id,p);
    }
    @PostMapping("/tickets/{id}/comments") Object comment(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return tickets.comment(a,id,p);
    }
    @PostMapping("/tickets/{id}/follow") Object follow(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return tickets.follow(a,id);
    }
    @GetMapping("/services") Object services(@AuthenticationPrincipal Account a) {
        return tickets.services(a);
    }
    @GetMapping("/services/{id}") Object service(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return tickets.service(a,id);
    }
    @PostMapping("/services") Object service(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return tickets.saveService(a,null,p);
    }
    @PostMapping("/services/{id}") Object service(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return tickets.saveService(a,id,p);
    }
    @PostMapping("/services/{id}/complete") Object complete(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return tickets.completeService(a,id,p);
    }
    @GetMapping("/documents") Object documents(@AuthenticationPrincipal Account a) {
        return files.documents(a);
    }
    @GetMapping("/documents/{id}") Object document(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return files.document(a,id);
    }
    @PostMapping("/documents") Object document(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return files.saveDocument(a,null,p);
    }
    @PostMapping("/documents/{id}") Object document(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return files.saveDocument(a,id,p);
    }
    @GetMapping("/files") Object files(@AuthenticationPrincipal Account a,@RequestParam String kind,@RequestParam UUID parentId) {
        return files.list(a,kind,parentId);
    }
    @PostMapping("/files") Object file(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        RateGate.enforce(gate.count("file:"+a.id()),30);
        return files.upload(a,p);
    }
    @GetMapping("/files/{id}") Object file(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return files.content(a,id);
    }
    @GetMapping("/resources") Object resources(@AuthenticationPrincipal Account a) {
        return bookings.resources(a);
    }
    @PostMapping("/resources") Object resource(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return bookings.saveResource(a,null,p);
    }
    @PostMapping("/resources/{id}") Object resource(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return bookings.saveResource(a,id,p);
    }
    @PostMapping("/resources/{id}/release") Object release(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return bookings.release(a,id,p);
    }
    @PostMapping("/resources/{id}/blocks") Object block(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return bookings.block(a,id,p);
    }
    @DeleteMapping("/resource-blocks/{id}") Object unblock(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return bookings.removeBlock(a,id);
    }
    @GetMapping("/availability") Object availability(@AuthenticationPrincipal Account a,@RequestParam String start,@RequestParam String end) {
        return bookings.availability(a,Instant.parse(start),Instant.parse(end));
    }
    @GetMapping("/bookings") Object bookings(@AuthenticationPrincipal Account a) {
        return bookings.list(a);
    }
    @GetMapping("/bookings/{id}") Object booking(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return bookings.get(a,id);
    }
    @PostMapping("/bookings") Object booking(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return bookings.create(a,p);
    }
    @PostMapping("/bookings/{id}/decision") Object decision(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return bookings.decide(a,id,p);
    }
    @GetMapping("/subscription") Object subscription(@AuthenticationPrincipal Account a) {
        return subscriptions.summary(a);
    }
    @PostMapping("/subscription/install") Object install(@AuthenticationPrincipal Account a) {
        return subscriptions.installed(a);
    }
    @PostMapping("/referrals/claim") Object claim(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return subscriptions.claim(a,p);
    }
    @PostMapping("/automation/run") Object run(@AuthenticationPrincipal Account a) {
        return automation.run(a);
    }
    @PostMapping("/notifications/read-all") Object readAll(@AuthenticationPrincipal Account a) {
        db.update("update ap_inbox set read_at=coalesce(read_at,now()) where tenant_id=? and recipient_id=?",a.tenantId(),a.id());
        return Map.of("ok",true);
    }
    @GetMapping("/tasks") Object tasks(@AuthenticationPrincipal Account a) {
        a.requireStaff();
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("payments",db.count("select count(*) from ap_payment where tenant_id=? and status='PENDING'",a.tenantId()));
        out.put("issues",db.count("select count(*) from ap_ticket where tenant_id=? and status not in ('CLOSED','RESOLVED')",a.tenantId()));
        out.put("expenseDrafts",db.count("select count(*) from ap_expense where tenant_id=? and paid=false",a.tenantId()));
        if(a.role().equals("ADMIN")) {
            out.put("joinRequests",db.count("select count(*) from ap_user where tenant_id=? and status='PENDING'",a.tenantId()));
            out.put("bookings",db.count("select count(*) from ap_booking where tenant_id=? and status in ('PENDING','ALTERNATIVE')",a.tenantId()));
        }
        return out;
    }
}
