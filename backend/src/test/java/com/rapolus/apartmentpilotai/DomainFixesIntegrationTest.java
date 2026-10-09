package com.rapolus.apartmentpilotai;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapolus.apartmentpilotai.store.Db;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
/**
 * HTTP tests for the BACKEND_HARDENING_05 domain fixes against a real, disposable PostgreSQL database
 * (same harness as ApiIntegrationTest). Runs only when APT_TEST_DB_URL is set.
 */
@EnabledIfEnvironmentVariable(named="APT_TEST_DB_URL",matches=".+")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={"app.local-registration-enabled=true","app.billing-job-enabled=false","app.cleanup-enabled=false"})
class DomainFixesIntegrationTest {
    // Throwaway per-run provider key, never a real deployment key.
    private static final String PROVIDER_KEY=UUID.randomUUID()+"-"+UUID.randomUUID();
    @DynamicPropertySource static void database(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",()->System.getenv("APT_TEST_DB_URL"));
        r.add("spring.datasource.username",()->Optional.ofNullable(System.getenv("APT_TEST_DB_USER")).orElse("postgres"));
        r.add("spring.datasource.password",()->Optional.ofNullable(System.getenv("APT_TEST_DB_PASSWORD")).orElse(""));
        byte[] inviteKey=new byte[32];
        new java.security.SecureRandom().nextBytes(inviteKey);
        r.add("app.invite-display-key",()->Base64.getEncoder().encodeToString(inviteKey));
        r.add("app.provider-key",()->PROVIDER_KEY);
    }
    private static final ObjectMapper JSON=new ObjectMapper();
    private static final HttpClient HTTP=HttpClient.newHttpClient();
    // A fresh forwarded address per test instance, so per-IP registration limits never carry between tests or runs.
    private final String CLIENT_IP="10.78."+ThreadLocalRandom.current().nextInt(256)+"."+ThreadLocalRandom.current().nextInt(256);
    private static final String MONTH=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
    private static final String TODAY=LocalDate.now(ZoneId.of("Asia/Kolkata")).toString();
    @LocalServerPort int port;
    @Autowired Db db;
    record Res(int status,JsonNode body) {
    }
    private Res call(String method,String path,Object body,String token) throws Exception {
        return call(method,path,body,token,Map.of());
    }
    private Res call(String method,String path,Object body,String token,Map<String,String> headers) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/v1"+path)).header("Accept","application/json").header("X-Forwarded-For",CLIENT_IP);
        if(token!=null)b.header("Authorization","Bearer "+token);
        headers.forEach(b::header);
        if(body!=null)b.header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        else b.method(method,HttpRequest.BodyPublishers.noBody());
        var r=HTTP.send(b.build(),HttpResponse.BodyHandlers.ofString());
        return new Res(r.statusCode(),r.body().isEmpty()?JSON.createObjectNode():JSON.readTree(r.body()));
    }
    private static String mobile() {
        return "9"+String.format("%09d",ThreadLocalRandom.current().nextLong(1_000_000_000L));
    }
    private static String key() {
        return UUID.randomUUID().toString();
    }
    private record Admin(String token,UUID id,UUID tenant) {
    }
    private record Member(String token,UUID id,String flat) {
    }
    private Admin register() throws Exception {
        var r=call("POST","/auth/register",Map.of("name","Synthetic Admin","mobile",mobile(),"pin","482915","apartmentName","DF "+key().substring(0,8),"city","Test","flats",5),null);
        assertEquals(200,r.status(),r.body().toString());
        return new Admin(r.body().get("token").asText(),UUID.fromString(r.body().get("account").get("id").asText()),UUID.fromString(r.body().get("account").get("tenantId").asText()));
    }
    private Member member(Admin admin,String flat,boolean treasurer) throws Exception {
        String code=call("POST","/invites",Map.of("requestKey",key()),admin.token()).body().get("code").asText();
        String phone=mobile();
        assertEquals(200,call("POST","/auth/join",Map.of("invite",code,"flatLabel",flat,"name","Synthetic "+flat,"mobile",phone,"pin","739104","residentType","OWNER"),null).status());
        UUID id=(UUID)db.one("select id from ap_user where mobile=?",phone).get("id");
        assertEquals(200,call("POST","/members/"+id+"/approve",Map.of("verified",true),admin.token()).status());
        if(treasurer)assertEquals(200,call("POST","/members/"+id+"/treasurer",null,admin.token()).status());
        var login=call("POST","/auth/login",Map.of("mobile",phone,"pin","739104","role",treasurer?"TREASURER":"RESIDENT"),null);
        assertEquals(200,login.status(),login.body().toString());
        return new Member(login.body().get("token").asText(),id,flat);
    }
    private Map<String,String> bills(Admin admin) throws Exception {
        assertEquals(200,call("POST","/billing/rules",Map.of("amount",2000,"effective",MONTH,"billingDay",1,"dueDay",10),admin.token()).status());
        assertEquals(200,call("POST","/billing/generate?month="+MONTH,null,admin.token()).status());
        Map<String,String> out=new HashMap<>();
        call("GET","/bills?month="+MONTH,null,admin.token()).body().forEach(b->out.put(b.get("flatLabel").asText(),b.get("id").asText()));
        return out;
    }
    private String pay(String bill,int amount,String token) throws Exception {
        var r=call("POST","/payments",Map.of("billId",bill,"amount",amount,"mode","CASH","paidOn",TODAY,"note","synthetic","requestKey",key()),token);
        assertEquals(200,r.status(),r.body().toString());
        return r.body().get("id").asText();
    }
    private Map<String,UUID> flats(Admin admin) throws Exception {
        Map<String,UUID> out=new HashMap<>();
        call("GET","/ops/flats",null,admin.token()).body().forEach(f->out.put(f.get("label").asText(),UUID.fromString(f.get("id").asText())));
        return out;
    }
    private long audits(Admin admin,String action) {
        return db.count("select count(*) from ap_audit where tenant_id=? and action=?",admin.tenant(),action);
    }
    private static Map<String,Object> body(Object...kv) {
        Map<String,Object> m=new LinkedHashMap<>();
        for(int i=0; i<kv.length; i+=2)m.put((String)kv[i],kv[i+1]);
        return m;
    }
    @Test void submitterCannotApproveOwnPaymentAndWholeBatchFails() throws Exception {
        var admin=register();
        var resident=member(admin,"A-101",false);
        var treasurer=member(admin,"A-104",true);
        var bills=bills(admin);
        String residentPayment=pay(bills.get("A-101"),500,resident.token());
        String adminPayment=pay(bills.get("A-102"),500,admin.token());
        String treasurerPayment=pay(bills.get("A-103"),500,treasurer.token());
        var self=call("POST","/payments/approve",Map.of("paymentIds",List.of(adminPayment),"verified",true),admin.token());
        assertEquals(409,self.status());
        assertTrue(self.body().get("message").asText().contains("Another Admin or Treasurer must approve it"));
        var batch=call("POST","/payments/approve",Map.of("paymentIds",List.of(residentPayment,adminPayment),"verified",true),admin.token());
        assertEquals(409,batch.status(),"One self-submitted payment fails the whole batch");
        assertEquals(0,db.count("select count(*) from ap_payment where tenant_id=? and status='APPROVED'",admin.tenant()),"Nothing was partially approved");
        assertEquals(0,audits(admin,"PAYMENT_APPROVED"));
        assertEquals(409,call("POST","/payments/approve",Map.of("paymentIds",List.of(treasurerPayment),"verified",true),treasurer.token()).status(),"Treasurer cannot approve their own payment either");
        var ok=call("POST","/payments/approve",Map.of("paymentIds",List.of(residentPayment,adminPayment),"verified",true),treasurer.token());
        assertEquals(200,ok.status(),ok.body().toString());
        assertEquals(2,ok.body().get("approved").asInt());
        assertEquals(1,call("POST","/payments/approve",Map.of("paymentIds",List.of(treasurerPayment),"verified",true),admin.token()).body().get("approved").asInt(),"Admin approves the Treasurer's payment");
        var retry=call("POST","/payments/approve",Map.of("paymentIds",List.of(adminPayment),"verified",true),admin.token());
        assertEquals(200,retry.status(),"Retry of an already approved payment by its submitter is unchanged");
        assertEquals(1,retry.body().get("alreadyApproved").asInt());
        // The submitter may still reject (withdraw) their own pending entry.
        String second=pay(bills.get("A-105"),100,admin.token());
        assertEquals(200,call("POST","/payments/"+second+"/reject",Map.of("reason","Entered by mistake"),admin.token()).status());
        // Database guard (V12) refuses a self-approval that bypasses the service.
        String third=pay(bills.get("A-105"),100,admin.token());
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,()->db.update("update ap_payment set status='APPROVED',reviewed_by=submitted_by,reviewed_at=now() where id=?",UUID.fromString(third)));
    }
    @Test void privateParkingOwnerDetailsAreHiddenFromOtherResidents() throws Exception {
        var admin=register();
        var other=member(admin,"A-101",false);
        var owner=member(admin,"A-102",false);
        var treasurer=member(admin,"A-103",true);
        UUID ownerFlat=flats(admin).get("A-102");
        var created=call("POST","/ops/resources",Map.of("name","P-A102","kind","CAR","capacity",1,"privateFlatId",ownerFlat.toString(),"requestKey",key()),admin.token());
        assertEquals(200,created.status(),created.body().toString());
        Instant from=Instant.now().plus(Duration.ofDays(1)).truncatedTo(java.time.temporal.ChronoUnit.HOURS),until=from.plus(Duration.ofDays(2));
        assertEquals(200,call("POST","/ops/resources/"+created.body().get("id").asText()+"/release",Map.of("consent",true,"start",from.toString(),"end",until.toString()),owner.token()).status());
        JsonNode seen=call("GET","/ops/resources",null,other.token()).body().get(0);
        assertTrue(seen.get("privateSpace").asBoolean());
        for(String hidden:List.of("privateFlatId","ownerConsent","availableFrom","availableUntil"))assertFalse(seen.has(hidden),hidden+" must not reach another flat's resident");
        JsonNode own=call("GET","/ops/resources",null,owner.token()).body().get(0);
        assertEquals(ownerFlat.toString(),own.get("privateFlatId").asText());
        assertTrue(own.get("ownerConsent").asBoolean());
        assertTrue(call("GET","/ops/resources",null,admin.token()).body().get(0).has("privateFlatId"));
        assertTrue(call("GET","/ops/resources",null,treasurer.token()).body().get(0).has("availableFrom"));
        var availability=call("GET","/ops/availability?start="+from.plus(Duration.ofHours(1))+"&end="+from.plus(Duration.ofHours(2)),null,other.token()).body().get(0);
        assertEquals(1,availability.get("available").asInt(),"Hiding details does not change availability");
        assertFalse(availability.has("privateFlatId"));
        assertFalse(availability.has("availableUntil"));
    }
    @Test void billsCannotBeGeneratedForFutureMonths() throws Exception {
        var admin=register();
        assertEquals(200,call("POST","/billing/rules",Map.of("amount",1500,"effective",MONTH,"billingDay",1,"dueDay",10),admin.token()).status());
        String next=YearMonth.parse(MONTH).plusMonths(1).toString();
        var future=call("POST","/billing/generate?month="+next,null,admin.token());
        assertEquals(400,future.status());
        assertTrue(future.body().get("message").asText().contains("current month"));
        assertEquals(0,db.count("select count(*) from ap_bill where tenant_id=?",admin.tenant()));
        assertEquals(200,call("POST","/billing/generate?month="+MONTH,null,admin.token()).status());
    }
    @Test void openingCashAmountIsRequired() throws Exception {
        var admin=register();
        String first=YearMonth.parse(MONTH).atDay(1).toString();
        assertEquals(400,call("POST","/ops/opening",Map.of("date",first),admin.token()).status());
        assertEquals(0,db.count("select count(*) from ap_settings where tenant_id=? and opening_date is not null",admin.tenant()),"Nothing is locked by the rejected request");
        assertEquals(200,call("POST","/ops/opening",Map.of("date",first,"amount",5000),admin.token()).status());
        assertEquals(0,new java.math.BigDecimal("5000.00").compareTo(db.decimal("select opening_amount from ap_settings where tenant_id=?",admin.tenant())));
    }
    @Test void ticketUpdateKeepsVendorAndEtaUnlessExplicitlyChanged() throws Exception {
        var admin=register();
        String vendor=call("POST","/ops/contacts",Map.of("kind","VENDOR","name","Lift Co","category","Lift","phone","9876543210","requestKey",key()),admin.token()).body().get("id").asText();
        String ticket=call("POST","/ops/tickets",Map.of("kind","ISSUE","title","Lift stuck","category","Lift","scope","COMMON","description","Stuck at 3","priority","NORMAL","requestKey",key()),admin.token()).body().get("id").asText();
        String eta=Instant.now().plus(Duration.ofDays(3)).truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString();
        assertEquals(200,call("POST","/ops/tickets/"+ticket+"/status",Map.of("status","ASSIGNED","vendorId",vendor,"eta",eta,"note","assigned","requestKey",key()),admin.token()).status());
        assertEquals(200,call("POST","/ops/tickets/"+ticket+"/status",Map.of("status","IN_PROGRESS","note","work","requestKey",key()),admin.token()).status());
        JsonNode t=call("GET","/ops/tickets/"+ticket,null,admin.token()).body();
        assertEquals(vendor,t.get("vendorId").asText(),"Omitted vendorId keeps the assignment");
        assertFalse(t.get("eta").isNull(),"Omitted eta keeps the ETA");
        assertEquals(200,call("POST","/ops/tickets/"+ticket+"/status",body("status","IN_PROGRESS","note","eta unknown","eta",null,"requestKey",key()),admin.token()).status());
        t=call("GET","/ops/tickets/"+ticket,null,admin.token()).body();
        assertTrue(t.get("eta").isNull(),"Explicit null clears the ETA");
        assertEquals(vendor,t.get("vendorId").asText());
    }
    @Test void residentsGet404ForInactiveService() throws Exception {
        var admin=register();
        var resident=member(admin,"A-101",false);
        String id=call("POST","/ops/services",Map.of("title","Pump AMC","category","Water","frequency","MONTHLY","nextOn",LocalDate.now().plusDays(10).toString(),"active",false,"requestKey",key()),admin.token()).body().get("id").asText();
        assertEquals(404,call("GET","/ops/services/"+id,null,resident.token()).status());
        assertEquals(200,call("GET","/ops/services/"+id,null,admin.token()).status());
    }
    @Test void vehicleOwnerSeesOwnDeactivatedVehicleOnly() throws Exception {
        var admin=register();
        var owner=member(admin,"A-101",false);
        var other=member(admin,"A-102",false);
        String id=call("POST","/ops/vehicles",Map.of("registration","AP28GH4321","kind","CAR","requestKey",key()),owner.token()).body().get("id").asText();
        assertEquals(200,call("POST","/ops/vehicles/"+id,Map.of("registration","AP28GH4321","kind","CAR","active",false,"requestKey",key()),owner.token()).status());
        JsonNode mine=call("GET","/ops/vehicles",null,owner.token()).body();
        assertEquals(1,mine.size());
        assertFalse(mine.get(0).get("active").asBoolean(),"Listed and marked inactive");
        assertEquals(0,call("GET","/ops/vehicles",null,other.token()).body().size());
    }
    @Test void selectedNoticeAudienceIsCaseInsensitive() throws Exception {
        var admin=register();
        var resident=member(admin,"A-101",false);
        var other=member(admin,"A-102",false);
        var notice=call("POST","/notices",Map.of("title","Water","body","Tank cleaning","audience","SELECTED","audienceValue"," a-101 ,A-103","publish",true),admin.token());
        assertEquals(200,notice.status(),notice.body().toString());
        UUID id=UUID.fromString(notice.body().get("id").asText());
        assertEquals("A-101,A-103",db.one("select audience_value from ap_notice where id=?",id).get("audienceValue"));
        assertEquals(1,db.count("select count(*) from ap_notice_recipient where notice_id=? and user_id=?",id,resident.id()));
        assertEquals(0,db.count("select count(*) from ap_notice_recipient where notice_id=? and user_id=?",id,other.id()));
        assertEquals(400,call("POST","/notices",Map.of("title","x","body","y","audience","SELECTED","audienceValue","z-999"),admin.token()).status());
    }
    @Test void repeatedProviderVerificationIsIdempotent() throws Exception {
        var admin=register();
        member(admin,"A-101",false);
        assertEquals(200,call("POST","/ops/subscription/install",null,admin.token()).status());
        Map<String,String> provider=Map.of("X-Provider-Key",PROVIDER_KEY);
        var first=call("POST","/provider/apartments/"+admin.tenant()+"/verify",Map.of("verifiedBy","QA operator","identityAndApartmentVerified",true),null,provider);
        assertEquals(200,first.status(),first.body().toString());
        assertFalse(first.body().get("alreadyVerified").asBoolean());
        var before=db.one("select verified_at,verified_by from ap_subscription where tenant_id=?",admin.tenant());
        var second=call("POST","/provider/apartments/"+admin.tenant()+"/verify",Map.of("verifiedBy","QA operator 2","identityAndApartmentVerified",true),null,provider);
        assertEquals(200,second.status());
        assertTrue(second.body().get("alreadyVerified").asBoolean());
        assertEquals("QA operator",second.body().get("verifiedBy").asText());
        assertEquals(before,db.one("select verified_at,verified_by from ap_subscription where tenant_id=?",admin.tenant()));
        assertEquals(1,audits(admin,"PROVIDER_ACTIVATION_VERIFIED"));
        assertEquals(403,call("POST","/provider/apartments/"+admin.tenant()+"/verify",Map.of("verifiedBy","x","identityAndApartmentVerified",true),null).status(),"The provider key is still required");
    }
    @Test void closingClosedPollWritesNoSecondAudit() throws Exception {
        var admin=register();
        String id=call("POST","/ops/polls",Map.of("title","Paint colour","closesAt",Instant.now().plus(Duration.ofDays(2)).toString(),"options",List.of("Blue","Green"),"requestKey",key()),admin.token()).body().get("id").asText();
        var first=call("POST","/ops/polls/"+id+"/close",null,admin.token());
        assertEquals(200,first.status());
        assertFalse(first.body().get("alreadyClosed").asBoolean());
        var second=call("POST","/ops/polls/"+id+"/close",null,admin.token());
        assertEquals(200,second.status());
        assertTrue(second.body().get("alreadyClosed").asBoolean());
        assertEquals(1,audits(admin,"POLL_CLOSED"));
    }
    @Test void contactKindChangeIsSaved() throws Exception {
        var admin=register();
        String id=call("POST","/ops/contacts",Map.of("kind","CONTACT","name","Probe","category","Misc","requestKey",key()),admin.token()).body().get("id").asText();
        assertEquals(200,call("POST","/ops/contacts/"+id,Map.of("kind","VENDOR","name","Probe","category","Misc","requestKey",key()),admin.token()).status());
        assertEquals("VENDOR",db.one("select kind from ap_contact where id=?",UUID.fromString(id)).get("kind"));
        assertEquals(400,call("POST","/ops/contacts/"+id,Map.of("kind","OTHER","name","Probe","category","Misc","requestKey",key()),admin.token()).status());
    }
    @Test void paymentAccountAndPhoneNumbersAreValidated() throws Exception {
        var admin=register();
        String t=admin.token();
        assertEquals(400,call("POST","/ops/settings/payment",Map.of("upi","qabank"),t).status());
        assertEquals(400,call("POST","/ops/settings/payment",Map.of("account","12"),t).status());
        assertEquals(400,call("POST","/ops/settings/payment",Map.of("account","12345678901234567890"),t).status());
        var bad=call("POST","/ops/settings/payment",Map.of("upi","q@1"),t);
        assertEquals(400,bad.status());
        assertTrue(bad.body().get("message").asText().contains("name@bank"));
        assertEquals(200,call("POST","/ops/settings/payment",Map.of("payee","Assoc","upi","assoc.funds@okhdfc","account","123456789012"),t).status());
        // A legacy invalid stored value does not block saving another settings section.
        db.update("update ap_settings set upi='legacy' where tenant_id=?",admin.tenant());
        assertEquals(200,call("POST","/ops/settings/billing",Map.of("graceDays",5),t).status());
        assertEquals(400,call("POST","/ops/contacts",Map.of("kind","VENDOR","name","V","category","Misc","phone","123","requestKey",key()),t).status());
        for(String ok:List.of("9876543210","+91 98765 43210","098765 43210","040-23456789","(080) 2345 6789"))assertEquals(200,call("POST","/ops/contacts",Map.of("kind","VENDOR","name","V "+ok,"category","Misc","phone",ok,"requestKey",key()),t).status(),ok);
        assertEquals(200,call("POST","/ops/contacts",Map.of("kind","VENDOR","name","No phone","category","Misc","phone","","requestKey",key()),t).status(),"Phone stays optional");
    }
}
