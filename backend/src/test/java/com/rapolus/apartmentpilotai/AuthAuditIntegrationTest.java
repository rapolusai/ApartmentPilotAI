package com.rapolus.apartmentpilotai;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.operations.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
/**
 * HTTP tests for PIN-login protection, audit attribution/paging and the APAI5 auth fixes, against a real PostgreSQL database.
 * Same harness as ApiIntegrationTest: runs only when APT_TEST_DB_URL points at a disposable database.
 */
@EnabledIfEnvironmentVariable(named="APT_TEST_DB_URL",matches=".+")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={"app.local-registration-enabled=true","app.billing-job-enabled=false","app.cleanup-enabled=false"})
class AuthAuditIntegrationTest {
    // Throwaway per-run provider key, so provider verification can be exercised; never a real deployment key.
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
    private static final String PIN="482915";
    @LocalServerPort int port;
    @Autowired Db db;
    @Autowired AutomationService automation;
    @Autowired CommunityService community;
    @Autowired SubscriptionService subscriptions;
    @Autowired PasswordEncoder encoder;
    record Res(int status,JsonNode body,HttpHeaders headers) {
    }
    private static String ip() {
        var r=ThreadLocalRandom.current();
        return "10."+(100+r.nextInt(100))+"."+r.nextInt(256)+"."+r.nextInt(256);
    }
    private Res call(String method,String path,Object body,String token) throws Exception {
        return call(method,path,body,token,ip());
    }
    private Res call(String method,String path,Object body,String token,String ip) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/v1"+path)).header("Accept","application/json").header("X-Forwarded-For",ip);
        if(token!=null)b.header("Authorization","Bearer "+token);
        if(body!=null)b.header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        else b.method(method,HttpRequest.BodyPublishers.noBody());
        var r=HTTP.send(b.build(),HttpResponse.BodyHandlers.ofString());
        return new Res(r.statusCode(),r.body().isEmpty()?JSON.createObjectNode():JSON.readTree(r.body()),r.headers());
    }
    private static String mobile() {
        return "9"+String.format("%09d",ThreadLocalRandom.current().nextLong(1_000_000_000L));
    }
    private static String key() {
        return UUID.randomUUID().toString();
    }
    private record Admin(String token,UUID id,UUID tenant,String mobile) {
    }
    private Admin register() throws Exception {
        String phone=mobile();
        var r=call("POST","/auth/register",Map.of("name","Synthetic Admin","mobile",phone,"pin",PIN,"apartmentName","IT "+key().substring(0,8),"city","Test","flats",5),null);
        assertEquals(200,r.status(),r.body().toString());
        return new Admin(r.body().get("token").asText(),UUID.fromString(r.body().get("account").get("id").asText()),UUID.fromString(r.body().get("account").get("tenantId").asText()),phone);
    }
    private String invite(Admin admin) throws Exception {
        return call("POST","/invites",Map.of("requestKey",key()),admin.token()).body().get("code").asText();
    }
    private Res join(String code,String flat,String phone,String pin) throws Exception {
        return call("POST","/auth/join",Map.of("invite",code,"flatLabel",flat,"name","Synthetic Resident","mobile",phone,"pin",pin,"residentType","OWNER"),null);
    }
    private Res login(String phone,String pin,String role,String ip) throws Exception {
        return call("POST","/auth/login",Map.of("mobile",phone,"pin",pin,"role",role),null,ip);
    }
    private record Member(UUID id,String mobile) {
    }
    private Member resident(Admin admin,String flat) throws Exception {
        String phone=mobile();
        assertEquals(200,join(invite(admin),flat,phone,"739104").status());
        UUID id=(UUID)db.one("select id from ap_user where mobile=?",phone).get("id");
        assertEquals(200,call("POST","/members/"+id+"/approve",Map.of("verified",true),admin.token()).status());
        return new Member(id,phone);
    }
    private List<Integer> parallel(int n,Callable<Integer> task) throws Exception {
        var pool=Executors.newFixedThreadPool(n);
        try {
            List<Future<Integer>> futures=new ArrayList<>();
            for(int i=0; i<n; i++)futures.add(pool.submit(task));
            List<Integer> out=new ArrayList<>();
            for(var f:futures)out.add(f.get(120,TimeUnit.SECONDS));
            return out;
        } finally {
            pool.shutdownNow();
        }
    }
    private Map<String,Object> guardRow(String keyHash) {
        return db.find("select failures,locked_until from ap_login_guard where key_hash=?",keyHash).orElse(null);
    }
    private void expireLocks(String... keys) {
        for(String k:keys)db.update("update ap_login_guard set locked_until=now()-interval '1 second' where key_hash=?",k);
    }

    // ---- Migration ----
    @Test void freshDatabaseMigratesThroughV12AndAV10SchemaUpgrades() {
        assertEquals("12",db.one("select max(version::int)::text as v from flyway_schema_history where success").get("v"));
        String schema="apai5_upgrade_"+key().substring(0,8).replace("-","");
        String url=System.getenv("APT_TEST_DB_URL"),user=Optional.ofNullable(System.getenv("APT_TEST_DB_USER")).orElse("postgres"),password=Optional.ofNullable(System.getenv("APT_TEST_DB_PASSWORD")).orElse("");
        try {
            Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema).createSchemas(true).target("10").load().migrate();
            assertEquals("10",Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema).load().info().current().getVersion().getVersion());
            Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema).load().migrate();
            assertEquals("12",Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema).load().info().current().getVersion().getVersion());
        } finally {
            db.update("drop schema if exists "+schema+" cascade");
        }
    }

    // ---- PIN login protection ----
    @Test void deviceTierLocksAfterFiveFailuresAndRequestsDuringTheLockChangeNothing() throws Exception {
        var admin=register();
        String ip1=ip(),ip2=ip(),device=LoginGuard.deviceKey(admin.mobile(),ip1);
        for(int i=0; i<5; i++) {
            var wrong=login(admin.mobile(),"905172","ADMIN",ip1);
            assertEquals(401,wrong.status());
            assertEquals("Incorrect mobile, PIN or login type.",wrong.body().get("message").asText());
        }
        var locked=login(admin.mobile(),"905172","ADMIN",ip1);
        assertEquals(429,locked.status());
        assertTrue(locked.body().get("message").asText().startsWith("Too many incorrect PIN attempts. Try again in "),locked.body().toString());
        assertTrue(Long.parseLong(locked.headers().firstValue("Retry-After").orElseThrow())>0);
        var before=guardRow(device);
        assertEquals(5,((Number)before.get("failures")).intValue());
        assertEquals(429,login(admin.mobile(),"905172","ADMIN",ip1).status());
        assertEquals(429,login(admin.mobile(),PIN,"ADMIN",ip1).status(),"A correct PIN during the lock is still refused");
        assertEquals(before,guardRow(device),"Requests during a lock add no failure and do not extend it");
        assertEquals(200,login(admin.mobile(),PIN,"ADMIN",ip2).status(),"The holder on another network keeps access");
        expireLocks(device);
        var after=login(admin.mobile(),PIN,"ADMIN",ip1);
        assertEquals(200,after.status());
        assertFalse(after.body().get("pinChangeRequired").asBoolean());
        assertNull(guardRow(device),"A correct PIN clears the counters");
        assertNull(guardRow(LoginGuard.accountKey(admin.mobile())));
    }
    @Test void accountTierLocksEveryAddressAfterTwentyFailuresAndAlertsOncePerEpisode() throws Exception {
        var admin=register();
        String account=LoginGuard.accountKey(admin.mobile());
        List<String> ips=new ArrayList<>();
        for(int n=0; n<4; n++) {
            String address=ip();
            ips.add(address);
            for(int i=0; i<5; i++)assertEquals(401,login(admin.mobile(),"905172","ADMIN",address).status());
        }
        assertEquals(20,((Number)guardRow(account).get("failures")).intValue());
        assertEquals(429,login(admin.mobile(),PIN,"ADMIN",ip()).status(),"Tier B locks the account from every address");
        assertEquals(1,db.count("select count(*) from ap_inbox where recipient_id=? and event_key like 'LOGIN_LOCKED:%'",admin.id()));
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='LOGIN_LOCKED' and actor_id is null and record_id=?",admin.tenant(),admin.id().toString()));
        expireLocks(account);
        assertEquals(401,login(admin.mobile(),"905172","ADMIN",ip()).status());
        assertEquals(429,login(admin.mobile(),PIN,"ADMIN",ip()).status(),"The next failure re-locks with a longer backoff");
        assertEquals(1,db.count("select count(*) from ap_inbox where recipient_id=? and event_key like 'LOGIN_LOCKED:%'",admin.id()),"One notice per lock episode");
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='LOGIN_LOCKED'",admin.tenant()));
        var audit=call("GET","/audit?action=LOGIN_LOCKED",null,admin.token());
        assertEquals("Security",audit.body().get(0).get("actor").asText());
        expireLocks(account);
        assertEquals(200,login(admin.mobile(),PIN,"ADMIN",ip()).status());
        assertNull(guardRow(account));
    }
    @Test void unknownMobileGetsTheSameAnswersAsAWrongPin() throws Exception {
        var admin=register();
        String known=ip(),unknown=ip(),stranger=mobile();
        List<String> knownAnswers=new ArrayList<>(),unknownAnswers=new ArrayList<>();
        for(int i=0; i<6; i++) {
            var k=login(admin.mobile(),"905172","ADMIN",known);
            var u=login(stranger,"905172","ADMIN",unknown);
            knownAnswers.add(k.status()+" "+k.body().get("message").asText().replaceAll("[0-9]+ minutes?","N"));
            unknownAnswers.add(u.status()+" "+u.body().get("message").asText().replaceAll("[0-9]+ minutes?","N"));
        }
        assertEquals(knownAnswers,unknownAnswers);
        assertEquals(429,login(stranger,"905172","ADMIN",unknown).status());
    }
    @Test void parallelWrongAttemptsCannotSlipPastTheLock() throws Exception {
        var admin=register();
        String address=ip();
        var statuses=parallel(30,()->login(admin.mobile(),"905172","ADMIN",address).status());
        assertEquals(5,Collections.frequency(statuses,401),statuses.toString());
        assertEquals(25,Collections.frequency(statuses,429),statuses.toString());
        assertEquals(5,((Number)guardRow(LoginGuard.deviceKey(admin.mobile(),address)).get("failures")).intValue());
    }
    @Test void oneAddressMayFailOnlyThirtyTimesAcrossManyMobiles() throws Exception {
        String address=ip();
        var statuses=parallel(30,()->login(mobile(),"905172","RESIDENT",address).status());
        assertEquals(30,Collections.frequency(statuses,401),statuses.toString());
        var blocked=login(mobile(),"905172","RESIDENT",address);
        assertEquals(429,blocked.status());
        assertTrue(blocked.headers().firstValue("Retry-After").isPresent());
    }
    @Test void newPinsMustBeSixUnpredictableDigitsOnEveryPath() throws Exception {
        var shortPin=call("POST","/auth/register",Map.of("name","Synthetic Admin","mobile",mobile(),"pin","4826","apartmentName","IT Weak","city","Test","flats",5),null);
        assertEquals(400,shortPin.status());
        assertTrue(shortPin.body().get("message").asText().contains(PinPolicy.LENGTH_MESSAGE),shortPin.body().toString());
        var weak=call("POST","/auth/register",Map.of("name","Synthetic Admin","mobile",mobile(),"pin","123456","apartmentName","IT Weak","city","Test","flats",5),null);
        assertEquals(400,weak.status());
        assertEquals(PinPolicy.WEAK_MESSAGE,weak.body().get("message").asText());
        var admin=register();
        String code=invite(admin);
        assertEquals(400,join(code,"A-101",mobile(),"1234").status());
        var weakJoin=join(code,"A-101",mobile(),"111111");
        assertEquals(400,weakJoin.status());
        assertEquals(PinPolicy.WEAK_MESSAGE,weakJoin.body().get("message").asText());
        var weakChange=call("POST","/ops/change-pin",Map.of("currentPin",PIN,"newPin","654321"),admin.token());
        assertEquals(400,weakChange.status());
        assertEquals(PinPolicy.WEAK_MESSAGE,weakChange.body().get("message").asText());
    }
    @Test void legacyFourDigitPinStillSignsInAndIsFlaggedForChange() throws Exception {
        var admin=register();
        db.update("update ap_user set pin_hash=? where id=?",encoder.encode("4826"),admin.id());
        var legacy=login(admin.mobile(),"4826","ADMIN",ip());
        assertEquals(200,legacy.status(),legacy.body().toString());
        assertTrue(legacy.body().get("pinChangeRequired").asBoolean());
    }
    @Test void cleanupPurgesOnlyIdleLoginGuardRows() throws Exception {
        String idle=Tokens.digest(key()),recent=Tokens.digest(key()),lockedOld=Tokens.digest(key());
        db.update("insert into ap_login_guard(key_hash,failures,last_failure_at) values(?,3,now()-interval '25 hours')",idle);
        db.update("insert into ap_login_guard(key_hash,failures,last_failure_at) values(?,3,now()-interval '1 hour')",recent);
        db.update("insert into ap_login_guard(key_hash,failures,last_failure_at,locked_until) values(?,30,now()-interval '25 hours',now()+interval '5 minutes')",lockedOld);
        new SessionCleanupJob(db).purge();
        assertNull(guardRow(idle));
        assertNotNull(guardRow(recent));
        assertNotNull(guardRow(lockedOld),"An active lock is never purged");
    }

    // ---- Audit attribution and paging ----
    @Test void scheduledAutomationIsAttributedToAutomationNotTheAdmin() throws Exception {
        var admin=register();
        String month=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
        assertEquals(200,call("POST","/billing/rules",Map.of("amount",1500,"effective",month,"billingDay",1,"dueDay",10),admin.token()).status());
        automation.run(Account.automation(admin.id(),admin.tenant()),true);
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='BILLS_GENERATED' and actor_id is null",admin.tenant()));
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='AUTOMATION_RUN' and actor_id is null",admin.tenant()));
        var run=call("GET","/audit?action=AUTOMATION_RUN",null,admin.token()).body().get(0);
        assertEquals("Automation",run.get("actor").asText());
        assertEquals("Automation",call("GET","/audit/"+run.get("id").asText(),null,admin.token()).body().get("actor").asText());
        assertEquals(200,call("POST","/ops/automation/run",null,admin.token()).status());
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='AUTOMATION_RUN' and actor_id=?",admin.tenant(),admin.id()),"A person's manual run still names them");
        assertEquals("Synthetic Admin",call("GET","/audit?action=AUTOMATION_RUN&limit=1",null,admin.token()).body().get(0).get("actor").asText());
    }
    @Test void auditPagingIsOptionalCappedTenantScopedAndLeavesTheDefaultUnchanged() throws Exception {
        var admin=register();
        var other=register();
        for(int i=0; i<5; i++)assertEquals(200,call("POST","/notices",Map.of("title","Notice "+i,"body","Body","publish",false),admin.token()).status());
        var legacy=db.rows("select a.id,a.action,a.record_id,a.before_value,a.after_value,a.created_at,u.name as actor from ap_audit a left join ap_user u on u.id=a.actor_id and u.tenant_id=a.tenant_id where a.tenant_id=? order by a.created_at desc limit 100",admin.tenant());
        var plain=call("GET","/audit",null,admin.token());
        assertEquals(JSON.valueToTree(legacy),plain.body(),"No parameters: same rows and fields as before");
        assertEquals(6,plain.body().size());
        List<String> seen=new ArrayList<>();
        String cursor=null;
        for(int page=0; page<3; page++) {
            var rows=call("GET","/audit?limit=2"+(cursor==null?"":"&before="+java.net.URLEncoder.encode(cursor,"UTF-8")),null,admin.token()).body();
            assertEquals(2,rows.size());
            rows.forEach(r->seen.add(r.get("id").asText()));
            var last=rows.get(1);
            cursor=last.get("createdAt").asText()+","+last.get("id").asText();
        }
        assertEquals(6,new HashSet<>(seen).size(),"Pages neither overlap nor skip");
        assertEquals(0,call("GET","/audit?limit=2&before="+java.net.URLEncoder.encode(cursor,"UTF-8"),null,admin.token()).body().size());
        var notices=call("GET","/audit?action=NOTICE_SAVED",null,admin.token()).body();
        assertEquals(5,notices.size());
        notices.forEach(n->assertEquals("NOTICE_SAVED",n.get("action").asText()));
        var created=call("GET","/audit?action=APARTMENT_CREATED&limit=500",null,admin.token());
        assertEquals(200,created.status(),"limit above 200 is capped, not refused");
        assertEquals(1,created.body().size(),"Another tenant's rows never appear");
        assertEquals(1,call("GET","/audit?action=APARTMENT_CREATED",null,other.token()).body().size());
        assertEquals(400,call("GET","/audit?limit=0",null,admin.token()).status());
        assertEquals(400,call("GET","/audit?limit=abc",null,admin.token()).status());
        assertEquals(400,call("GET","/audit?before=yesterday",null,admin.token()).status());
        assertEquals(400,call("GET","/audit?action=notice_saved",null,admin.token()).status());
    }

    // ---- BUG-A-03 ----
    @Test void pinningAndUnpinningANoticeIsAudited() throws Exception {
        var admin=register();
        String id=call("POST","/notices",Map.of("title","Water cut","body","Tomorrow","publish",false),admin.token()).body().get("id").asText();
        assertEquals(200,call("POST","/notices/"+id+"/pin",Map.of("pinned",true),admin.token()).status());
        assertEquals(200,call("POST","/notices/"+id+"/pin",Map.of("pinned",true),admin.token()).status());
        assertEquals(200,call("POST","/notices/"+id+"/pin",Map.of("pinned",false),admin.token()).status());
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='NOTICE_PINNED' and record_id=?",admin.tenant(),id));
        assertEquals(1,db.count("select count(*) from ap_audit where tenant_id=? and action='NOTICE_UNPINNED' and record_id=?",admin.tenant(),id));
    }

    // ---- BUG-A-04 ----
    @Test void anEditRequestKeyIsBoundToTheRecordInItsUrl() throws Exception {
        var admin=register();
        String t=admin.token();
        var flats=db.rows("select id,label from ap_flat where tenant_id=? order by label",admin.tenant());
        String k=key();
        Map<String,Object> flatBody=Map.of("label",flats.get(0).get("label").toString(),"block","A","floor",3,"occupied",true,"active",true,"requestKey",k);
        assertEquals(200,call("POST","/ops/flats/"+flats.get(0).get("id"),flatBody,t).status());
        assertEquals(flats.get(0).get("id").toString(),call("POST","/ops/flats/"+flats.get(0).get("id"),flatBody,t).body().get("id").asText(),"Same record: an idempotent retry");
        var replay=call("POST","/ops/flats/"+flats.get(1).get("id"),flatBody,t);
        assertEquals(409,replay.status(),replay.body().toString());
        assertEquals("Request key was already used for different details.",replay.body().get("message").asText());
        String c1=call("POST","/ops/contacts",Map.of("kind","CONTACT","name","Plumber One","category","Plumbing","requestKey",key()),t).body().get("id").asText();
        String c2=call("POST","/ops/contacts",Map.of("kind","CONTACT","name","Plumber Two","category","Plumbing","requestKey",key()),t).body().get("id").asText();
        Map<String,Object> contactEdit=Map.of("kind","CONTACT","name","Renamed","category","Plumbing","requestKey",key());
        assertEquals(200,call("POST","/ops/contacts/"+c1,contactEdit,t).status());
        assertEquals(409,call("POST","/ops/contacts/"+c2,contactEdit,t).status());
        assertEquals("Plumber Two",db.one("select name from ap_contact where id=?",UUID.fromString(c2)).get("name"));
        String today=LocalDate.now(ZoneId.of("Asia/Kolkata")).toString();
        String m1=call("POST","/ops/committee",Map.of("name","A","title","Secretary","startOn",today,"requestKey",key()),t).body().get("id").asText();
        String m2=call("POST","/ops/committee",Map.of("name","B","title","President","startOn",today,"requestKey",key()),t).body().get("id").asText();
        Map<String,Object> committeeEdit=Map.of("name","C","title","Treasurer","startOn",today,"requestKey",key());
        assertEquals(200,call("POST","/ops/committee/"+m1,committeeEdit,t).status());
        assertEquals(409,call("POST","/ops/committee/"+m2,committeeEdit,t).status());
    }

    // ---- BUG-A-07 ----
    @Test void aRejectedApplicantCanResubmitForTheCorrectFlat() throws Exception {
        var admin=register();
        resident(admin,"A-104");
        String code=invite(admin),phone=mobile(),pin="739104";
        assertEquals(200,join(code,"A-102",phone,pin).status());
        UUID id=(UUID)db.one("select id from ap_user where mobile=?",phone).get("id");
        assertEquals(200,call("POST","/members/"+id+"/reject",Map.of("reason","Wrong flat chosen"),admin.token()).status());
        assertEquals(409,join(code,"A-103",phone,"905172").status(),"Only the applicant (same PIN) can resubmit");
        var taken=join(code,"A-104",phone,pin);
        assertEquals(409,taken.status());
        assertTrue(taken.body().get("message").asText().startsWith("This flat already has a resident"),taken.body().toString());
        var corrected=join(code,"A-103",phone,pin);
        assertEquals(200,corrected.status(),corrected.body().toString());
        assertEquals("PENDING",corrected.body().get("status").asText());
        assertEquals("A-103",corrected.body().get("flatLabel").asText());
        var audit=db.one("select before_value,after_value from ap_audit where tenant_id=? and action='RESIDENT_RESUBMITTED' and actor_id=?",admin.tenant(),id);
        assertEquals("Rejected · A-102",audit.get("beforeValue"));
        assertEquals("Pending · A-103",audit.get("afterValue"));
        assertEquals(409,join(code,"A-105",phone,pin).status(),"A pending request keeps its flat");
        assertEquals(200,call("POST","/members/"+id+"/approve",Map.of("verified",true),admin.token()).status());
        var signedIn=login(phone,pin,"RESIDENT",ip());
        assertEquals(200,signedIn.status());
        assertEquals(db.one("select id from ap_flat where tenant_id=? and label='A-103'",admin.tenant()).get("id").toString(),signedIn.body().get("account").get("flatId").asText());
    }

    // ---- G-24 ----
    @Test void theLastActiveAdminCannotBeDemotedOrDisabled() throws Exception {
        var admin=register();
        var second=resident(admin,"A-101");
        assertEquals(200,call("POST","/ops/members/"+second.id()+"/role",Map.of("role","ADMIN"),admin.token()).status());
        assertEquals(200,call("POST","/ops/members/"+second.id()+"/role",Map.of("role","RESIDENT"),admin.token()).status(),"Another Admin remains, so demotion is allowed");
        // The second Admin acted with a role read before the first demotion committed (the two-Admin race).
        var stale=new Account(second.id(),admin.tenant(),null,"Synthetic Resident","ADMIN");
        var demote=assertThrows(ApiError.class,()->community.changeRole(stale,admin.id(),new HashMap<>(Map.of("role","TREASURER"))));
        assertEquals(HttpStatus.CONFLICT,demote.status());
        assertEquals("This is the last active Admin of the apartment. Make another member Admin first.",demote.getMessage());
        assertEquals(HttpStatus.CONFLICT,assertThrows(ApiError.class,()->community.endAccess(stale,admin.id(),new HashMap<>(Map.of("reason","Left")))).status());
        assertEquals(1,db.count("select count(*) from ap_user where tenant_id=? and role='ADMIN' and status='ACTIVE'",admin.tenant()));
    }

    // ---- G-25 ----
    @Test void providerVerificationCountsATreasurerWithAFlat() throws Exception {
        var admin=register();
        assertEquals(200,call("POST","/ops/subscription/install",null,admin.token()).status());
        Map<String,Object> verified=Map.of("verifiedBy","QA operator","identityAndApartmentVerified",true);
        assertEquals(HttpStatus.CONFLICT,assertThrows(ApiError.class,()->subscriptions.verify(PROVIDER_KEY,admin.tenant(),verified)).status(),"An Admin alone is not enough");
        var member=resident(admin,"A-101");
        assertEquals(200,call("POST","/members/"+member.id()+"/treasurer",null,admin.token()).status());
        assertEquals(Map.of("verified",true),subscriptions.verify(PROVIDER_KEY,admin.tenant(),verified));
    }

    // ---- BUG-B-01 ----
    @Test void frameworkClientErrorsKeepTheirStatusInsteadOfSigningTheUserOut() throws Exception {
        var admin=register();
        var bills=call("GET","/bills",null,admin.token());
        assertEquals(400,bills.status(),bills.body().toString());
        assertEquals("month is required.",bills.body().get("message").asText());
        assertEquals(400,call("POST","/billing/generate",null,admin.token()).status());
        assertEquals(400,call("GET","/ops/files?kind=TICKET",null,admin.token()).status());
        assertEquals(400,call("GET","/payments?flatId=not-a-uuid",null,admin.token()).status());
        var method=call("DELETE","/me",null,admin.token());
        assertEquals(405,method.status());
        assertTrue(method.body().has("message"));
        var missing=call("GET","/no-such-route",null,admin.token());
        assertEquals(404,missing.status());
        assertTrue(missing.body().has("message"));
        assertEquals(401,call("GET","/bills",null,null).status(),"Authentication is still enforced first");
        assertEquals(200,call("GET","/me",null,admin.token()).status(),"The session is untouched");
    }
}
