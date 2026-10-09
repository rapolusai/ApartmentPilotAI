package com.rapolus.apartmentpilotai;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapolus.apartmentpilotai.operations.AutomationService;
import com.rapolus.apartmentpilotai.security.*;
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
 * HTTP tests against a real PostgreSQL database. Runs only when APT_TEST_DB_URL points at a disposable database
 * (CI provides one); Flyway migrates it from empty. Never point this at a database whose data matters.
 */
@EnabledIfEnvironmentVariable(named="APT_TEST_DB_URL",matches=".+")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={"app.local-registration-enabled=true","app.billing-job-enabled=false","app.cleanup-enabled=false"})
class ApiIntegrationTest {
    @DynamicPropertySource static void database(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",()->System.getenv("APT_TEST_DB_URL"));
        r.add("spring.datasource.username",()->Optional.ofNullable(System.getenv("APT_TEST_DB_USER")).orElse("postgres"));
        r.add("spring.datasource.password",()->Optional.ofNullable(System.getenv("APT_TEST_DB_PASSWORD")).orElse(""));
        // Throwaway per-run key so invite codes can be issued; never a real deployment key.
        byte[] inviteKey=new byte[32];
        new java.security.SecureRandom().nextBytes(inviteKey);
        r.add("app.invite-display-key",()->Base64.getEncoder().encodeToString(inviteKey));
    }
    private static final ObjectMapper JSON=new ObjectMapper();
    private static final HttpClient HTTP=HttpClient.newHttpClient();
    // Each run uses its own forwarded client address so per-IP registration limits do not carry over between runs.
    private static final String CLIENT_IP="10.77."+ThreadLocalRandom.current().nextInt(256)+"."+ThreadLocalRandom.current().nextInt(256);
    @LocalServerPort int port;
    @Autowired Db db;
    @Autowired AutomationService automation;
    record Res(int status,JsonNode body) {
    }
    private Res call(String method,String path,Object body,String token) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/v1"+path)).header("Accept","application/json").header("X-Forwarded-For",CLIENT_IP);
        if(token!=null)b.header("Authorization","Bearer "+token);
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
    private Admin register() throws Exception {
        var r=call("POST","/auth/register",Map.of("name","Synthetic Admin","mobile",mobile(),"pin","482915","apartmentName","IT "+key().substring(0,8),"city","Test","flats",5),null);
        assertEquals(200,r.status(),r.body().toString());
        return new Admin(r.body().get("token").asText(),UUID.fromString(r.body().get("account").get("id").asText()),UUID.fromString(r.body().get("account").get("tenantId").asText()));
    }
    private String resident(Admin admin,String flat) throws Exception {
        String code=call("POST","/invites",Map.of("requestKey",key()),admin.token()).body().get("code").asText();
        String phone=mobile();
        assertEquals(200,call("POST","/auth/join",Map.of("invite",code,"flatLabel",flat,"name","Synthetic Resident","mobile",phone,"pin","739104","residentType","OWNER"),null).status());
        String id=db.one("select id from ap_user where mobile=?",phone).get("id").toString();
        assertEquals(200,call("POST","/members/"+id+"/approve",Map.of("verified",true),admin.token()).status());
        var login=call("POST","/auth/login",Map.of("mobile",phone,"pin","739104","role","RESIDENT"),null);
        assertEquals(200,login.status());
        return login.body().get("token").asText();
    }
    @Test void freshDatabaseMigratesThroughV12() {
        assertEquals("12",db.one("select max(version::int)::text as v from flyway_schema_history where success").get("v"));
    }
    @Test void staleTokenDoesNotBlockPublicRoutesButStillExpiresProtectedOnes() throws Exception {
        String stale="not-a-real-session-token";
        assertEquals(200,call("GET","/status",null,stale).status());
        var me=call("GET","/me",null,stale);
        assertEquals(401,me.status());
        assertEquals("Sign in again. Your session has expired or is invalid.",me.body().get("message").asText());
        assertEquals(401,call("GET","/me",null,null).status());
        var admin=register();
        assertEquals(200,call("GET","/me",null,admin.token()).status());
        assertEquals(200,call("POST","/logout",null,admin.token()).status());
        assertEquals(401,call("GET","/me",null,admin.token()).status());
    }
    @Test void tenantsCannotReadEachOthersRecords() throws Exception {
        var a=register();
        var b=register();
        String month=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
        assertEquals(200,call("POST","/billing/rules",Map.of("amount",1500,"effective",month,"billingDay",1,"dueDay",10),a.token()).status());
        assertEquals(200,call("POST","/billing/generate?month="+month,null,a.token()).status());
        String bill=call("GET","/bills?month="+month,null,a.token()).body().get(0).get("id").asText();
        assertEquals(200,call("GET","/bills/"+bill,null,a.token()).status());
        assertEquals(404,call("GET","/bills/"+bill,null,b.token()).status());
        assertEquals(0,call("GET","/bills?month="+month,null,b.token()).body().size());
    }
    @Test void paymentSubmitApproveAndReceipt() throws Exception {
        var admin=register();
        String token=resident(admin,"A-101");
        String month=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
        call("POST","/billing/rules",Map.of("amount",2000,"effective",month,"billingDay",1,"dueDay",10),admin.token());
        call("POST","/billing/generate?month="+month,null,admin.token());
        var bills=call("GET","/bills?month="+month,null,token).body();
        assertEquals(1,bills.size(),"Resident sees only their own flat bill");
        String payKey=key();
        Map<String,Object> payment=Map.of("billId",bills.get(0).get("id").asText(),"amount",2000,"mode","UPI","reference","UTR"+payKey.substring(0,8),"paidOn",LocalDate.now(ZoneId.of("Asia/Kolkata")).toString(),"requestKey",payKey);
        var submitted=call("POST","/payments",payment,token);
        assertEquals(200,submitted.status(),submitted.body().toString());
        assertEquals(submitted.body().get("id"),call("POST","/payments",payment,token).body().get("id"),"Retry with the same key is idempotent");
        String id=submitted.body().get("id").asText();
        assertEquals(403,call("POST","/payments/approve",Map.of("paymentIds",List.of(id),"verified",true),token).status());
        var approved=call("POST","/payments/approve",Map.of("paymentIds",List.of(id),"verified",true),admin.token());
        assertEquals(1,approved.body().get("approved").asInt());
        assertEquals(1,call("POST","/payments/approve",Map.of("paymentIds",List.of(id),"verified",true),admin.token()).body().get("alreadyApproved").asInt());
        assertEquals(200,call("GET","/receipts/"+id,null,token).status());
    }
    @Test void residentMonthlyReportWorksBeforeSettingsRowExists() throws Exception {
        var admin=register();
        String token=resident(admin,"A-102");
        assertEquals(0,db.count("select count(*) from ap_settings where tenant_id=?",admin.tenant()));
        String month=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
        assertEquals(200,call("GET","/reports/monthly?month="+month,null,token).status());
        db.update("insert into ap_settings(tenant_id,expenses_visible) values(?,false)",admin.tenant());
        assertEquals(403,call("GET","/reports/monthly?month="+month,null,token).status(),"Hiding transparency still denies residents");
    }
    @Test void writesThatUsedPositionalInsertsStillPersist() throws Exception {
        var admin=register();
        String t=admin.token();
        var recurring=call("POST","/ops/recurring",Map.of("title","Lift AMC","category","Lift","frequency","MONTHLY","nextOn",LocalDate.now().plusDays(3).toString(),"amount",900,"requestKey",key()),t);
        assertEquals(200,recurring.status(),recurring.body().toString());
        var ticket=call("POST","/ops/tickets",Map.of("kind","ISSUE","title","Water leak","category","Plumbing","scope","COMMON","description","Leak near lift","priority","NORMAL","requestKey",key()),t);
        assertEquals(200,ticket.status(),ticket.body().toString());
        assertTrue(call("GET","/ops/tickets/"+ticket.body().get("id").asText(),null,t).body().get("following").asBoolean());
        var notice=call("POST","/notices",Map.of("title","Water cut","body","Tomorrow 10-12","publish",true),t);
        assertEquals(200,notice.status(),notice.body().toString());
        assertEquals(1,db.count("select count(*) from ap_notice_recipient where notice_id=?",UUID.fromString(notice.body().get("id").asText())));
        var hall=call("POST","/ops/resources",Map.of("name","Hall","kind","SPACE","capacity",1,"requestKey",key()),t).body().get("id").asText();
        var parking=call("POST","/ops/resources",Map.of("name","P1","kind","CAR","capacity",1,"conflicts",List.of(hall),"requestKey",key()),t);
        assertEquals(200,parking.status(),parking.body().toString());
        assertEquals(2,db.count("select count(*) from ap_resource_conflict where tenant_id=?",admin.tenant()));
        Instant start=Instant.now().plus(Duration.ofDays(2)).truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        var booking=call("POST","/ops/bookings",Map.of("acceptRules",true,"start",start.toString(),"end",start.plus(Duration.ofHours(3)).toString(),"items",List.of(Map.of("resourceId",hall,"quantity",1)),"title","Birthday","eventType","BIRTHDAY","guests",20,"requestKey",key()),t);
        assertEquals(200,booking.status(),booking.body().toString());
        assertEquals(1,call("GET","/ops/bookings/"+booking.body().get("id").asText(),null,t).body().get("items").size());
    }
    @Test void scheduledAutomationAuditsOnlyWhenSomethingChanged() throws Exception {
        var admin=register();
        var system=new Account(admin.id(),admin.tenant(),null,"System","ADMIN");
        automation.run(system,true);
        automation.run(system,true);
        assertEquals(0,db.count("select count(*) from ap_audit where tenant_id=? and action='AUTOMATION_RUN'",admin.tenant()));
        assertEquals(200,call("POST","/ops/automation/run",null,admin.token()).status());
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='AUTOMATION_RUN'",admin.tenant()),"A person's manual run is still audited");
    }
    @Test void cleanupRemovesOnlyExpiredSessionsAndOldRateWindows() throws Exception {
        var admin=register();
        String expired=Tokens.digest(key());
        db.update("insert into ap_session(token_hash,user_id,expires_at) values(?,?,now()-interval '1 hour')",expired,admin.id());
        String oldWindow=Tokens.digest(key());
        db.update("insert into ap_rate_limit(bucket_key,window_start,uses) values(?,?,1)",oldWindow,Instant.now().getEpochSecond()/900-200);
        new SessionCleanupJob(db).purge();
        assertEquals(0,db.count("select count(*) from ap_session where token_hash=?",expired));
        assertEquals(0,db.count("select count(*) from ap_rate_limit where bucket_key=?",oldWindow));
        assertEquals(200,call("GET","/me",null,admin.token()).status(),"Live sessions survive cleanup");
    }
}
