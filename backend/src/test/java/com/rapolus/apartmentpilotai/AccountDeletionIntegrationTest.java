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
 * Account deletion (Google Play policy) against a real PostgreSQL database. Same harness as ApiIntegrationTest:
 * runs only when APT_TEST_DB_URL points at a disposable database.
 */
@EnabledIfEnvironmentVariable(named="APT_TEST_DB_URL",matches=".+")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={"app.local-registration-enabled=true","app.billing-job-enabled=false","app.cleanup-enabled=false","app.support-email=privacy@example.test"})
class AccountDeletionIntegrationTest {
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
    private static final String PIN="482915",RESIDENT_PIN="739104";
    @LocalServerPort int port;
    @Autowired Db db;
    record Res(int status,JsonNode body) {
    }
    private static String ip() {
        var r=ThreadLocalRandom.current();
        return "10."+(100+r.nextInt(100))+"."+r.nextInt(256)+"."+r.nextInt(256);
    }
    private Res call(String method,String path,Object body,String token) throws Exception {
        return call(method,path,body,token,ip(),null);
    }
    private Res call(String method,String path,Object body,String token,String ip,String providerKey) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/v1"+path)).header("Accept","application/json").header("X-Forwarded-For",ip);
        if(token!=null)b.header("Authorization","Bearer "+token);
        if(providerKey!=null)b.header("X-Provider-Key",providerKey);
        if(body!=null)b.header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        else b.method(method,HttpRequest.BodyPublishers.noBody());
        var r=HTTP.send(b.build(),HttpResponse.BodyHandlers.ofString());
        return new Res(r.statusCode(),r.body().isEmpty()?JSON.createObjectNode():JSON.readTree(r.body()));
    }
    private Res provider(String method,String path,Object body) throws Exception {
        return call(method,path,body,null,ip(),PROVIDER_KEY);
    }
    private static String mobile() {
        return "9"+String.format("%09d",ThreadLocalRandom.current().nextLong(1_000_000_000L));
    }
    private static String key() {
        return UUID.randomUUID().toString();
    }
    private record Admin(String token,UUID id,UUID tenant,String mobile,String apartment) {
    }
    private Admin register() throws Exception {
        String phone=mobile(),apartment="Deletion "+key().substring(0,8);
        var r=call("POST","/auth/register",Map.of("name","Synthetic Admin","mobile",phone,"pin",PIN,"apartmentName",apartment,"city","Test","flats",5),null);
        assertEquals(200,r.status(),r.body().toString());
        return new Admin(r.body().get("token").asText(),UUID.fromString(r.body().get("account").get("id").asText()),UUID.fromString(r.body().get("account").get("tenantId").asText()),phone,apartment);
    }
    private Res join(Admin admin,String flat,String phone) throws Exception {
        String code=call("POST","/invites",Map.of("requestKey",key()),admin.token()).body().get("code").asText();
        return call("POST","/auth/join",Map.of("invite",code,"flatLabel",flat,"name","Synthetic Resident","mobile",phone,"pin",RESIDENT_PIN,"residentType","OWNER"),null);
    }
    private record Member(UUID id,String mobile,String token) {
    }
    private Member resident(Admin admin,String flat) throws Exception {
        String phone=mobile();
        var joined=join(admin,flat,phone);
        assertEquals(200,joined.status(),joined.body().toString());
        UUID id=(UUID)db.one("select id from ap_user where mobile=?",phone).get("id");
        assertEquals(200,call("POST","/members/"+id+"/approve",Map.of("verified",true),admin.token()).status());
        var login=call("POST","/auth/login",Map.of("mobile",phone,"pin",RESIDENT_PIN,"role","RESIDENT"),null);
        assertEquals(200,login.status(),login.body().toString());
        return new Member(id,phone,login.body().get("token").asText());
    }
    private String approvedPayment(Admin admin,Member m) throws Exception {
        String month=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
        assertEquals(200,call("POST","/billing/rules",Map.of("amount",2000,"effective",month,"billingDay",1,"dueDay",10),admin.token()).status());
        assertEquals(200,call("POST","/billing/generate?month="+month,null,admin.token()).status());
        var bills=call("GET","/bills?month="+month,null,m.token()).body();
        assertEquals(1,bills.size());
        String payKey=key();
        var submitted=call("POST","/payments",Map.of("billId",bills.get(0).get("id").asText(),"amount",2000,"mode","UPI","reference","UTR"+payKey.substring(0,8),"paidOn",LocalDate.now(ZoneId.of("Asia/Kolkata")).toString(),"requestKey",payKey),m.token());
        assertEquals(200,submitted.status(),submitted.body().toString());
        String id=submitted.body().get("id").asText();
        assertEquals(1,call("POST","/payments/approve",Map.of("paymentIds",List.of(id),"verified",true),admin.token()).body().get("approved").asInt());
        return id;
    }

    @Test void migrationV13AddsDeletionColumnsWithoutTouchingExistingRows() {
        assertEquals("13",db.one("select max(version::int)::text as v from flyway_schema_history where success").get("v"));
        assertEquals(1,db.count("select count(*) from information_schema.columns where table_name='ap_user' and column_name='deleted_at'"));
        assertEquals(1,db.count("select count(*) from information_schema.tables where table_name='ap_deletion_request'"));
    }

    @Test void residentDeletesOwnAccountKeepingFinancialRecordsAndReleasingTheFlat() throws Exception {
        Admin admin=register();
        Member m=resident(admin,"A-101");
        String payment=approvedPayment(admin,m);
        var preview=call("GET","/account/deletion",null,m.token());
        assertEquals(200,preview.status());
        assertFalse(preview.body().get("lastAdmin").asBoolean());
        assertEquals("A-101",preview.body().get("flatLabel").asText());
        // Wrong PIN changes nothing.
        assertEquals(400,call("POST","/account/delete",Map.of("pin","000000"),m.token()).status());
        assertEquals("Synthetic Resident",db.one("select name from ap_user where id=?",m.id()).get("name"));
        var deleted=call("POST","/account/delete",Map.of("pin",RESIDENT_PIN),m.token());
        assertEquals(200,deleted.status(),deleted.body().toString());
        assertTrue(deleted.body().get("deleted").asBoolean());
        // Signed out everywhere, and the mobile can no longer sign in.
        assertEquals(401,call("GET","/me",null,m.token()).status());
        assertEquals(401,call("POST","/auth/login",Map.of("mobile",m.mobile(),"pin",RESIDENT_PIN,"role","RESIDENT"),null).status());
        var row=db.one("select name,mobile,email,status,deleted_at,flat_id from ap_user where id=?",m.id());
        assertEquals("Former resident · A-101",row.get("name"));
        assertEquals("deleted:"+m.id().toString().replace("-",""),row.get("mobile"));
        assertEquals("DISABLED",row.get("status"));
        assertNotNull(row.get("deletedAt"));
        assertNotNull(row.get("flatId"),"The flat reference stays so the payment history still reads by flat");
        // Approved money is untouched and shows the anonymised name to staff.
        assertEquals("APPROVED",db.one("select status from ap_payment where id=?::uuid",payment).get("status"));
        assertEquals(200,call("GET","/payments/"+payment,null,admin.token()).status());
        assertEquals(200,call("GET","/receipts/"+payment,null,admin.token()).status());
        String audit=call("GET","/audit",null,admin.token()).body().toString();
        assertTrue(audit.contains("Former resident · A-101"),audit);
        assertFalse(audit.contains("Synthetic Resident"),audit);
        // The Admin's member list shows the anonymised name and no mobile for the deleted account.
        var members=call("GET","/members",null,admin.token()).body();
        boolean listed=false;
        for(JsonNode x:members)if(x.get("id").asText().equals(m.id().toString())) {
            listed=true;
            assertEquals("",x.get("mobile").asText());
            assertEquals("Former resident · A-101",x.get("name").asText());
        }
        assertTrue(listed);
        // Audit entry credited to the person, and the Admin is told.
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='ACCOUNT_DELETED' and actor_id=? and record_id=?",admin.tenant(),m.id(),m.id().toString()));
        assertEquals(1,db.count("select count(*) from ap_inbox where tenant_id=? and recipient_id=? and event_key=?",admin.tenant(),admin.id(),"ACCOUNT_DELETED:"+m.id()));
        // The flat is free again and the same mobile may register again.
        var again=join(admin,"A-101",m.mobile());
        assertEquals(200,again.status(),again.body().toString());
        assertEquals("PENDING",again.body().get("status").asText());
    }

    @Test void lastAdminCannotDeleteUntilAnotherAdminExists() throws Exception {
        Admin admin=register();
        assertTrue(call("GET","/account/deletion",null,admin.token()).body().get("lastAdmin").asBoolean());
        var blocked=call("POST","/account/delete",Map.of("pin",PIN),admin.token());
        assertEquals(409,blocked.status(),blocked.body().toString());
        assertEquals(200,call("GET","/me",null,admin.token()).status(),"Nothing was revoked");
        Member m=resident(admin,"A-102");
        assertEquals(200,call("POST","/ops/members/"+m.id()+"/role",Map.of("role","ADMIN"),admin.token()).status());
        var deleted=call("POST","/account/delete",Map.of("pin",PIN),admin.token());
        assertEquals(200,deleted.status(),deleted.body().toString());
        assertEquals(1,db.count("select count(*) from ap_user where tenant_id=? and role='ADMIN' and status='ACTIVE'",admin.tenant()));
        assertEquals("Former resident",db.one("select name from ap_user where id=?",admin.id()).get("name"));
    }

    @Test void webRequestIsRecordedThenExecutedByTheOperatorOnly() throws Exception {
        Admin admin=register();
        Member m=resident(admin,"A-103");
        var ask=call("POST","/auth/deletion-requests",Map.of("mobile",m.mobile(),"apartmentName",admin.apartment().toLowerCase(Locale.ROOT),"note","Moved out"),null);
        assertEquals(200,ask.status(),ask.body().toString());
        assertTrue(ask.body().get("received").asBoolean());
        // Nothing changed yet; the Admin is told; the reply for an unknown person looks the same.
        assertEquals(200,call("GET","/me",null,m.token()).status());
        assertEquals(1,db.count("select count(*) from ap_inbox where tenant_id=? and recipient_id=? and event_key like 'DELETION_REQUEST:%'",admin.tenant(),admin.id()));
        var unknown=call("POST","/auth/deletion-requests",Map.of("mobile",mobile(),"apartmentName","Nowhere"),null);
        assertEquals(200,unknown.status());
        assertEquals(ask.body().get("message"),unknown.body().get("message"));
        // Operator key required.
        assertEquals(403,call("GET","/provider/deletion-requests",null,null,ip(),"wrong-key-"+key()).status());
        assertEquals(403,call("GET","/provider/deletion-requests",null,admin.token()).status());
        var open=provider("GET","/provider/deletion-requests",null).body();
        JsonNode mine=null;
        for(JsonNode x:open)if(x.get("mobile").asText().equals(m.mobile()))mine=x;
        assertNotNull(mine);
        var done=provider("POST","/provider/deletion-requests/"+mine.get("id").asText(),Map.of("approve",true,"outcome","Called back and confirmed"));
        assertEquals(200,done.status(),done.body().toString());
        assertEquals("DONE",done.body().get("status").asText());
        assertEquals(401,call("GET","/me",null,m.token()).status());
        assertEquals("Former resident · A-103",db.one("select name from ap_user where id=?",m.id()).get("name"));
        assertEquals("DONE",db.one("select status from ap_deletion_request where id=?::uuid",mine.get("id").asText()).get("status"));
        // A handled request cannot be executed twice.
        assertEquals(404,provider("POST","/provider/deletion-requests/"+mine.get("id").asText(),Map.of("approve",true)).status());
    }

    @Test void publicPagesServeWithoutSignIn() throws Exception {
        for(String path:List.of("/privacy","/delete-account")) {
            var r=HTTP.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path)).GET().build(),HttpResponse.BodyHandlers.ofString());
            assertEquals(200,r.statusCode(),path);
            assertTrue(r.headers().firstValue("Content-Type").orElse("").startsWith("text/html"),path);
            assertTrue(r.body().contains("privacy@example.test"),path+" shows the configured contact");
            assertTrue(r.body().contains("Powered By @Rapolu`s"),path);
            assertFalse(r.body().contains("{{CONTACT}}"),path);
        }
    }
}
