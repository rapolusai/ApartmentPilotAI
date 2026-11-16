package com.rapolus.apartmentpilotai;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapolus.apartmentpilotai.store.Db;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
/** ApartmentCare team (super user) login and dashboard against a real PostgreSQL database (APT_TEST_DB_URL). */
@EnabledIfEnvironmentVariable(named="APT_TEST_DB_URL",matches=".+")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={"app.local-registration-enabled=true","app.billing-job-enabled=false","app.cleanup-enabled=false"})
class OperatorIntegrationTest {
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
    private static final String PIN="482915",OPERATOR_PIN="583920";
    @LocalServerPort int port;
    @Autowired Db db;
    record Res(int status,JsonNode body) {
    }
    private static String ip() {
        var r=ThreadLocalRandom.current();
        return "10."+(100+r.nextInt(100))+"."+r.nextInt(256)+"."+r.nextInt(256);
    }
    private Res call(String method,String path,Object body,String token,String providerKey) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/v1"+path)).header("Accept","application/json").header("X-Forwarded-For",ip());
        if(token!=null)b.header("Authorization","Bearer "+token);
        if(providerKey!=null)b.header("X-Provider-Key",providerKey);
        if(body!=null)b.header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        else b.method(method,HttpRequest.BodyPublishers.noBody());
        var r=HTTP.send(b.build(),HttpResponse.BodyHandlers.ofString());
        return new Res(r.statusCode(),r.body().isEmpty()?JSON.createObjectNode():JSON.readTree(r.body()));
    }
    private Res call(String method,String path,Object body,String token) throws Exception {
        return call(method,path,body,token,null);
    }
    private static String mobile() {
        return "9"+String.format("%09d",ThreadLocalRandom.current().nextLong(1_000_000_000L));
    }
    private record Admin(String token,UUID tenant,String mobile,String apartment) {
    }
    private Admin register() throws Exception {
        String phone=mobile(),apartment="Operator "+UUID.randomUUID().toString().substring(0,8);
        var r=call("POST","/auth/register",Map.of("name","Synthetic Admin","mobile",phone,"pin",PIN,"apartmentName",apartment,"city","Hyderabad","flats",12),null);
        assertEquals(200,r.status(),r.body().toString());
        return new Admin(r.body().get("token").asText(),UUID.fromString(r.body().get("account").get("tenantId").asText()),phone,apartment);
    }
    private String operator() throws Exception {
        String phone=mobile();
        var saved=call("POST","/provider/operators",Map.of("name","Team Member","mobile",phone,"pin",OPERATOR_PIN),null,PROVIDER_KEY);
        assertEquals(200,saved.status(),saved.body().toString());
        var login=call("POST","/operator/login",Map.of("mobile",phone,"pin",OPERATOR_PIN),null);
        assertEquals(200,login.status(),login.body().toString());
        return login.body().get("token").asText();
    }
    private JsonNode row(JsonNode dashboard,UUID tenant) {
        for(var a:dashboard.get("apartments"))if(a.get("id").asText().equals(tenant.toString()))return a;
        fail("Apartment missing from the dashboard");
        return null;
    }

    @Test void onlyTheProviderKeyCreatesSuperUsers() throws Exception {
        assertEquals(403,call("POST","/provider/operators",Map.of("name","X","mobile",mobile(),"pin",OPERATOR_PIN),null).status());
        assertEquals(403,call("POST","/provider/operators",Map.of("name","X","mobile",mobile(),"pin",OPERATOR_PIN),null,"wrong-key").status());
        Admin admin=register();
        // An apartment Admin token is not a provider key either.
        assertEquals(403,call("POST","/provider/operators",Map.of("name","X","mobile",mobile(),"pin",OPERATOR_PIN),admin.token()).status());
        // Weak PINs are refused.
        assertEquals(400,call("POST","/provider/operators",Map.of("name","X","mobile",mobile(),"pin","123456"),null,PROVIDER_KEY).status());
    }

    @Test void operatorAndApartmentTokensNeverCross() throws Exception {
        Admin admin=register();
        String op=operator();
        assertEquals(401,call("GET","/operator/dashboard",null,admin.token()).status());
        assertEquals(401,call("GET","/operator/dashboard",null,null).status());
        assertEquals(401,call("GET","/ops/subscription",null,op).status());
        assertEquals(401,call("GET","/notices",null,op).status());
        assertEquals(200,call("GET","/operator/dashboard",null,op).status());
        // Existing apartment login is unaffected.
        assertEquals(200,call("POST","/auth/login",Map.of("mobile",admin.mobile(),"pin",PIN,"role","ADMIN"),null).status());
    }

    @Test void wrongPinPauseAndLogoutEndAccess() throws Exception {
        String phone=mobile();
        assertEquals(200,call("POST","/provider/operators",Map.of("name","Team Member","mobile",phone,"pin",OPERATOR_PIN),null,PROVIDER_KEY).status());
        assertEquals(401,call("POST","/operator/login",Map.of("mobile",phone,"pin","739104"),null).status());
        String token=call("POST","/operator/login",Map.of("mobile",phone,"pin",OPERATOR_PIN),null).body().get("token").asText();
        assertEquals(200,call("GET","/operator/dashboard",null,token).status());
        assertEquals(200,call("POST","/provider/operators",Map.of("name","Team Member","mobile",phone,"active",false),null,PROVIDER_KEY).status());
        assertEquals(401,call("GET","/operator/dashboard",null,token).status());
        assertEquals(401,call("POST","/operator/login",Map.of("mobile",phone,"pin",OPERATOR_PIN),null).status());
        assertEquals(200,call("POST","/provider/operators",Map.of("name","Team Member","mobile",phone,"active",true),null,PROVIDER_KEY).status());
        String again=call("POST","/operator/login",Map.of("mobile",phone,"pin",OPERATOR_PIN),null).body().get("token").asText();
        assertEquals(200,call("POST","/operator/logout",null,again).status());
        assertEquals(401,call("GET","/operator/dashboard",null,again).status());
        var listed=call("GET","/provider/operators",null,null,PROVIDER_KEY);
        assertEquals(200,listed.status());
        assertFalse(listed.body().toString().contains("pinHash"));
    }

    @Test void dashboardShowsTrialApartmentWithAdminContactAndSignIn() throws Exception {
        Admin admin=register();
        String op=operator();
        var d=call("GET","/operator/dashboard",null,op);
        assertEquals(200,d.status(),d.body().toString());
        var a=row(d.body(),admin.tenant());
        assertEquals(admin.apartment(),a.get("name").asText());
        assertEquals("Synthetic Admin",a.get("adminName").asText());
        assertEquals(admin.mobile(),a.get("adminMobile").asText());
        assertEquals("TRIAL",a.get("subscription").asText());
        assertEquals(12,a.get("flats").asInt());
        assertTrue(a.get("activeRecently").asBoolean(),"sign-up session counts as a sign-in");
        assertEquals(0,a.get("received").decimalValue().signum());
        var totals=d.body().get("totals");
        assertTrue(totals.get("apartments").asInt()>=1);
        assertEquals(totals.get("apartments").asInt(),totals.get("activeSubscriptions").asInt()+totals.get("onTrial").asInt()+totals.get("paymentDue").asInt());
        // A login records last_login_at, which survives the session being purged.
        assertEquals(200,call("POST","/auth/login",Map.of("mobile",admin.mobile(),"pin",PIN,"role","ADMIN"),null).status());
        db.update("delete from ap_session where user_id in (select id from ap_user where tenant_id=?)",admin.tenant());
        assertTrue(row(call("GET","/operator/dashboard",null,op).body(),admin.tenant()).get("activeRecently").asBoolean());
    }

    @Test void paidSubscriptionCountsAsActiveRevenueAndExpectedNextMonth() throws Exception {
        Admin admin=register();
        String op=operator();
        // Move the apartment two months back so its trial is over and a paid cycle is due.
        db.update("update ap_tenant set created_at=now()-interval '45 days',trial_ends_at=now()-interval '15 days' where id=?",admin.tenant());
        assertEquals("PAYMENT_DUE",row(call("GET","/operator/dashboard",null,op).body(),admin.tenant()).get("subscription").asText());
        assertEquals(200,call("GET","/ops/subscription",null,admin.token()).status());
        var invoice=db.one("select id,amount from ap_subscription_invoice where tenant_id=? and starts_on<=current_date and ends_on>current_date",admin.tenant());
        var settled=call("POST","/provider/invoices/"+invoice.get("id")+"/settle",Map.of("reference","OP-"+UUID.randomUUID(),"amount",invoice.get("amount"),"verified",true),null,PROVIDER_KEY);
        assertEquals(200,settled.status(),settled.body().toString());
        var a=row(call("GET","/operator/dashboard",null,op).body(),admin.tenant());
        assertEquals("ACTIVE",a.get("subscription").asText());
        assertEquals(0,a.get("received").decimalValue().compareTo(new java.math.BigDecimal(invoice.get("amount").toString())));
        assertFalse(a.get("paidUntil").isNull());
        // 12 flats is the 11-30 plan; the next cycle always starts within the next calendar month for a monthly anniversary.
        assertEquals(0,a.get("expectedNextMonth").decimalValue().compareTo(new java.math.BigDecimal("149")));
    }
}
