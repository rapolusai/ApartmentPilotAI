package com.rapolus.apartmentpilotai;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rapolus.apartmentpilotai.play.PlayBillingService;
import com.rapolus.apartmentpilotai.play.PlayGateway;
import com.rapolus.apartmentpilotai.store.Db;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
/** Google Play Billing against a real PostgreSQL database, with Google's API replaced by an in-memory fake. */
@EnabledIfEnvironmentVariable(named="APT_TEST_DB_URL",matches=".+")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={"app.local-registration-enabled=true","app.billing-job-enabled=false","app.cleanup-enabled=false","app.play.enabled=true","app.play.notification-secret=test-notification-secret-0123456789"})
class PlayBillingIntegrationTest {
    static final String SECRET="test-notification-secret-0123456789",PRODUCT="apartmentcare_monthly";
    static final Map<String,JsonNode> GOOGLE=new ConcurrentHashMap<>();
    static final Set<String> ACKNOWLEDGED=ConcurrentHashMap.newKeySet();
    @TestConfiguration static class FakeGoogle {
        @Bean @Primary PlayGateway fakePlayGateway() {
            return new PlayGateway() {
                @Override public JsonNode subscription(String token) {
                    return GOOGLE.get(token);
                }
                @Override public void acknowledge(String productId,String token) {
                    ACKNOWLEDGED.add(token);
                }
            };
        }
    }
    @DynamicPropertySource static void database(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",()->System.getenv("APT_TEST_DB_URL"));
        r.add("spring.datasource.username",()->Optional.ofNullable(System.getenv("APT_TEST_DB_USER")).orElse("postgres"));
        r.add("spring.datasource.password",()->Optional.ofNullable(System.getenv("APT_TEST_DB_PASSWORD")).orElse(""));
        byte[] inviteKey=new byte[32];
        new java.security.SecureRandom().nextBytes(inviteKey);
        r.add("app.invite-display-key",()->Base64.getEncoder().encodeToString(inviteKey));
    }
    private static final ObjectMapper JSON=new ObjectMapper();
    private static final HttpClient HTTP=HttpClient.newHttpClient();
    private static final String PIN="482915";
    @LocalServerPort int port;
    @Autowired Db db;
    @Autowired PasswordEncoder encoder;
    record Res(int status,JsonNode body) {
    }
    private static String ip() {
        var r=ThreadLocalRandom.current();
        return "10."+(100+r.nextInt(100))+"."+r.nextInt(256)+"."+r.nextInt(256);
    }
    private Res call(String method,String path,Object body,String token) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/v1"+path)).header("Accept","application/json").header("X-Forwarded-For",ip());
        if(token!=null)b.header("Authorization","Bearer "+token);
        if(body!=null)b.header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        else b.method(method,HttpRequest.BodyPublishers.noBody());
        var r=HTTP.send(b.build(),HttpResponse.BodyHandlers.ofString());
        return new Res(r.statusCode(),r.body().isEmpty()?JSON.createObjectNode():JSON.readTree(r.body()));
    }
    private static String mobile() {
        return "9"+String.format("%09d",ThreadLocalRandom.current().nextLong(1_000_000_000L));
    }
    private record Admin(String token,UUID tenant,String mobile) {
    }
    private Admin register(int flats) throws Exception {
        String phone=mobile();
        var r=call("POST","/auth/register",Map.of("name","Synthetic Admin","mobile",phone,"pin",PIN,"apartmentName","Play "+UUID.randomUUID().toString().substring(0,8),"city","Hyderabad","flats",flats),null);
        assertEquals(200,r.status(),r.body().toString());
        return new Admin(r.body().get("token").asText(),UUID.fromString(r.body().get("account").get("tenantId").asText()),phone);
    }
    private static String purchase(UUID tenant,String state,String basePlan,Instant expiry,boolean ackPending,String linked) {
        String token="play-"+UUID.randomUUID();
        ObjectNode p=JSON.createObjectNode();
        p.put("subscriptionState",state);
        p.put("acknowledgementState",ackPending?"ACKNOWLEDGEMENT_STATE_PENDING":"ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED");
        p.putObject("externalAccountIdentifiers").put("obfuscatedExternalAccountId",PlayBillingService.accountId(tenant));
        if(linked!=null)p.put("linkedPurchaseToken",linked);
        ObjectNode item=p.putArray("lineItems").addObject();
        item.put("productId",PRODUCT);
        item.put("expiryTime",expiry.toString());
        item.putObject("autoRenewingPlan").put("autoRenewEnabled",true);
        item.putObject("offerDetails").put("basePlanId",basePlan);
        item.put("latestSuccessfulOrderId","GPA.0000-"+token.substring(5,13));
        GOOGLE.put(token,p);
        return token;
    }
    private Res notify(String secret,String token) throws Exception {
        String data=Base64.getEncoder().encodeToString(JSON.writeValueAsBytes(Map.of("packageName","com.rapolus.apartmentpilotai","subscriptionNotification",Map.of("notificationType",2,"purchaseToken",token,"subscriptionId",PRODUCT))));
        return call("POST","/play/notifications?secret="+secret,Map.of("message",Map.of("data",data,"messageId","1")),null);
    }
    private void lapse(UUID tenant) {
        db.update("update ap_tenant set created_at=now()-interval '20 days',trial_ends_at=now()-interval '15 days' where id=?",tenant);
    }

    @Test void newApartmentGetsFiveDayTrialAndTheRightPlan() throws Exception {
        Admin admin=register(12);
        var s=call("GET","/ops/subscription/play",null,admin.token());
        assertEquals(200,s.status(),s.body().toString());
        assertTrue(s.body().get("enabled").asBoolean());
        assertEquals("TRIAL",s.body().get("status").asText());
        assertEquals("flats-30",s.body().get("basePlanId").asText());
        assertEquals(149,s.body().get("price").asInt());
        assertEquals(PlayBillingService.accountId(admin.tenant()),s.body().get("accountId").asText());
        assertTrue(s.body().get("canSubscribe").asBoolean());
        long hours=Duration.between(Instant.now(),Instant.parse(s.body().get("trialEndsAt").asText())).toHours();
        assertTrue(hours>=119&&hours<=120,"5-day trial, was "+hours+"h");
    }

    @Test void lockedAdminSubscribesAndTheWholeApartmentReopens() throws Exception {
        Admin admin=register(8);
        lapse(admin.tenant());
        assertEquals(402,call("GET","/notices",null,admin.token()).status());
        assertEquals(200,call("GET","/ops/subscription/play",null,admin.token()).status());
        String token=purchase(admin.tenant(),"SUBSCRIPTION_STATE_ACTIVE","flats-10",Instant.now().plus(Duration.ofDays(30)),true,null);
        var v=call("POST","/ops/subscription/play/verify",Map.of("purchaseToken",token),admin.token());
        assertEquals(200,v.status(),v.body().toString());
        assertEquals("ACTIVE",v.body().get("status").asText());
        assertFalse(v.body().get("canSubscribe").asBoolean());
        assertTrue(v.body().get("purchase").get("boughtByYou").asBoolean());
        assertTrue(ACKNOWLEDGED.contains(token),"purchase acknowledged to Google");
        assertEquals(200,call("GET","/notices",null,admin.token()).status());
        // Verifying the same token again is harmless.
        assertEquals(200,call("POST","/ops/subscription/play/verify",Map.of("purchaseToken",token),admin.token()).status());
    }

    @Test void forgedOrForeignPurchasesNeverCount() throws Exception {
        Admin a=register(8),b=register(8);
        lapse(a.tenant());
        String foreign=purchase(b.tenant(),"SUBSCRIPTION_STATE_ACTIVE","flats-10",Instant.now().plus(Duration.ofDays(30)),true,null);
        assertEquals(409,call("POST","/ops/subscription/play/verify",Map.of("purchaseToken",foreign),a.token()).status());
        assertEquals(400,call("POST","/ops/subscription/play/verify",Map.of("purchaseToken","never-issued-by-google"),a.token()).status());
        String expired=purchase(a.tenant(),"SUBSCRIPTION_STATE_EXPIRED","flats-10",Instant.now().minus(Duration.ofDays(1)),false,null);
        var v=call("POST","/ops/subscription/play/verify",Map.of("purchaseToken",expired),a.token());
        assertEquals(200,v.status());
        assertEquals("PAYMENT_DUE",v.body().get("status").asText());
        assertFalse(ACKNOWLEDGED.contains(expired));
        assertEquals(402,call("GET","/notices",null,a.token()).status());
    }

    @Test void subscriptionStaysWithTheApartmentAfterAdminHandover() throws Exception {
        Admin admin=register(8);
        lapse(admin.tenant());
        String token=purchase(admin.tenant(),"SUBSCRIPTION_STATE_ACTIVE","flats-10",Instant.now().plus(Duration.ofDays(30)),true,null);
        assertEquals(200,call("POST","/ops/subscription/play/verify",Map.of("purchaseToken",token),admin.token()).status());
        // A second Admin takes over; the first one is no longer an Admin.
        String phone=mobile();
        db.update("insert into ap_user(id,tenant_id,name,mobile,pin_hash,role,status) values(?,?,?,?,?,'ADMIN','ACTIVE')",UUID.randomUUID(),admin.tenant(),"New Admin",phone,encoder.encode(PIN));
        db.update("update ap_user set status='DISABLED' where mobile=?",admin.mobile());
        var login=call("POST","/auth/login",Map.of("mobile",phone,"pin",PIN,"role","ADMIN"),null);
        assertEquals(200,login.status(),login.body().toString());
        var s=call("GET","/ops/subscription/play",null,login.body().get("token").asText());
        assertEquals("ACTIVE",s.body().get("status").asText());
        assertFalse(s.body().get("canSubscribe").asBoolean(),"no second charge while the first subscription runs");
        assertFalse(s.body().get("purchase").get("boughtByYou").asBoolean());
        assertEquals("Synthetic Admin",s.body().get("purchase").get("buyerName").asText());
        // A renewal for the former Admin's purchase still keeps the apartment active.
        GOOGLE.put(token,((ObjectNode)GOOGLE.get(token).deepCopy()));
        ((ObjectNode)GOOGLE.get(token).get("lineItems").get(0)).put("expiryTime",Instant.now().plus(Duration.ofDays(60)).toString());
        assertEquals(200,notify(SECRET,token).status());
        assertEquals("ACTIVE",call("GET","/ops/subscription/play",null,login.body().get("token").asText()).body().get("status").asText());
    }

    @Test void googleNotificationsUpdateStateAndNeedTheSecret() throws Exception {
        Admin admin=register(8);
        lapse(admin.tenant());
        String token=purchase(admin.tenant(),"SUBSCRIPTION_STATE_ACTIVE","flats-10",Instant.now().plus(Duration.ofDays(30)),false,null);
        assertEquals(403,notify("wrong-secret",token).status());
        assertEquals(402,call("GET","/notices",null,admin.token()).status(),"a refused notification changes nothing");
        // A purchase the app never reported is matched to its apartment by the account id.
        assertEquals(200,notify(SECRET,token).status());
        assertEquals(200,call("GET","/notices",null,admin.token()).status());
        // Google revokes it (refund or expiry): the apartment locks again.
        ((ObjectNode)GOOGLE.get(token)).put("subscriptionState","SUBSCRIPTION_STATE_EXPIRED");
        ((ObjectNode)GOOGLE.get(token).get("lineItems").get(0)).put("expiryTime",Instant.now().minus(Duration.ofMinutes(1)).toString());
        assertEquals(200,notify(SECRET,token).status());
        assertEquals(402,call("GET","/notices",null,admin.token()).status());
    }

    @Test void upgradeReplacesTheOldPurchase() throws Exception {
        Admin admin=register(8);
        lapse(admin.tenant());
        String first=purchase(admin.tenant(),"SUBSCRIPTION_STATE_ACTIVE","flats-10",Instant.now().plus(Duration.ofDays(30)),true,null);
        assertEquals(200,call("POST","/ops/subscription/play/verify",Map.of("purchaseToken",first),admin.token()).status());
        String upgraded=purchase(admin.tenant(),"SUBSCRIPTION_STATE_ACTIVE","flats-30",Instant.now().plus(Duration.ofDays(30)),true,first);
        var v=call("POST","/ops/subscription/play/verify",Map.of("purchaseToken",upgraded),admin.token());
        assertEquals("flats-30",v.body().get("purchase").get("basePlanId").asText());
        assertEquals(1,db.count("select count(*) from ap_play_purchase where tenant_id=? and replaced_by is not null",admin.tenant()));
    }

    @Test void onlyTheAdminCanSeeOrBuy() throws Exception {
        Admin admin=register(8);
        UUID flat=(UUID)db.one("select id from ap_flat where tenant_id=? order by label limit 1",admin.tenant()).get("id");
        String phone=mobile();
        db.update("insert into ap_user(id,tenant_id,flat_id,name,mobile,pin_hash,role,status) values(?,?,?,?,?,?,'RESIDENT','ACTIVE')",UUID.randomUUID(),admin.tenant(),flat,"Resident",phone,encoder.encode(PIN));
        String resident=call("POST","/auth/login",Map.of("mobile",phone,"pin",PIN,"role","RESIDENT"),null).body().get("token").asText();
        assertEquals(403,call("GET","/ops/subscription/play",null,resident).status());
        String token=purchase(admin.tenant(),"SUBSCRIPTION_STATE_ACTIVE","flats-10",Instant.now().plus(Duration.ofDays(30)),true,null);
        assertEquals(403,call("POST","/ops/subscription/play/verify",Map.of("purchaseToken",token),resident).status());
    }
}
