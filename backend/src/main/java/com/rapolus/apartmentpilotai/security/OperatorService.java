package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.domain.OperationsRules;
import com.rapolus.apartmentpilotai.operations.SubscriptionService;
import com.rapolus.apartmentpilotai.operations.V;
import com.rapolus.apartmentpilotai.store.Db;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Platform super users ("ApartmentCare team"): a read-only view across every apartment. Operators are kept apart from
 * apartment logins: their own table, their own sessions, and their tokens are accepted only by /api/v1/operator/*.
 * Accounts are created, reset and disabled only with the server-side provider key, never from the app.
 */
@Service public class OperatorService {
    private static final Logger LOG=LoggerFactory.getLogger(OperatorService.class);
    private static final ZoneId IST=ZoneId.of("Asia/Kolkata");
    static final int ACTIVE_DAYS=30;
    private static final class Dummy {
        static final String HASH=new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12).encode(Tokens.create());
    }
    private final Db db;
    private final PasswordEncoder encoder;
    private final SubscriptionService subscriptions;
    private final int hours;
    public OperatorService(Db db,PasswordEncoder encoder,SubscriptionService subscriptions,@Value("${app.session-hours:12}")int hours) {
        this.db=db;
        this.encoder=encoder;
        this.subscriptions=subscriptions;
        this.hours=hours;
    }
    public record Operator(UUID id,String name,String mobile) {
    }
    @Transactional public Map<String,Object> login(String mobile,String pin) {
        var row=db.find("select id,name,mobile,pin_hash,status from ap_operator where mobile=?",mobile);
        boolean matches=encoder.matches(pin,row.map(r->r.get("pinHash").toString()).orElse(Dummy.HASH));
        if(row.isEmpty()||!matches||!"ACTIVE".equals(row.get().get("status")))throw new ApiError(HttpStatus.UNAUTHORIZED,"Incorrect mobile or PIN.");
        UUID id=(UUID)row.get().get("id");
        String raw=Tokens.create();
        Instant expires=Instant.now().plus(Duration.ofHours(hours));
        db.update("insert into ap_operator_session(token_hash,operator_id,expires_at) values(?,?,?)",Tokens.digest(raw),id,java.sql.Timestamp.from(expires));
        db.update("update ap_operator set last_login_at=now() where id=?",id);
        LOG.info("operator sign-in operator={}",id);
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("token",raw);
        out.put("expiresAt",expires.toString());
        out.put("operator",Map.of("id",id,"name",row.get().get("name")));
        return out;
    }
    /** The operator behind a bearer header, or 401. Apartment tokens are not in this table and are refused. */
    @Transactional(readOnly=true) public Operator authenticate(String header) {
        if(header==null||!header.startsWith("Bearer "))throw unauthorised();
        return db.find("select o.id,o.name,o.mobile from ap_operator_session s join ap_operator o on o.id=s.operator_id where s.token_hash=? and s.expires_at>now() and o.status='ACTIVE'",Tokens.digest(header.substring(7)))
                .map(r->new Operator((UUID)r.get("id"),r.get("name").toString(),r.get("mobile").toString())).orElseThrow(OperatorService::unauthorised);
    }
    private static ApiError unauthorised() {
        return new ApiError(HttpStatus.UNAUTHORIZED,"Sign in again. Your session has expired or is invalid.");
    }
    public void logout(String header) {
        if(header!=null&&header.startsWith("Bearer "))db.update("delete from ap_operator_session where token_hash=?",Tokens.digest(header.substring(7)));
    }
    /** Provider key only: create an operator, or update the name, PIN or status of an existing one (matched by mobile). */
    @Transactional public Map<String,Object> upsert(String providerKey,String name,String mobile,String pin,boolean active) {
        subscriptions.provider(providerKey);
        var existing=db.find("select id from ap_operator where mobile=?",mobile);
        UUID id;
        if(existing.isEmpty()) {
            if(pin==null)throw new IllegalArgumentException("A 6-digit PIN is required for a new super user.");
            PinPolicy.requireNew(pin);
            id=UUID.randomUUID();
            db.update("insert into ap_operator(id,name,mobile,pin_hash,status) values(?,?,?,?,?)",id,name,mobile,encoder.encode(pin),active?"ACTIVE":"DISABLED");
        } else {
            id=(UUID)existing.get().get("id");
            if(pin!=null) {
                PinPolicy.requireNew(pin);
                db.update("update ap_operator set pin_hash=? where id=?",encoder.encode(pin),id);
            }
            db.update("update ap_operator set name=?,status=? where id=?",name,active?"ACTIVE":"DISABLED",id);
            // A PIN reset or a pause ends every open session at once.
            if(pin!=null||!active)db.update("delete from ap_operator_session where operator_id=?",id);
        }
        LOG.info("operator saved operator={} active={}",id,active);
        return Map.of("id",id,"name",name,"mobile",mobile,"active",active);
    }
    @Transactional(readOnly=true) public List<Map<String,Object>> list(String providerKey) {
        subscriptions.provider(providerKey);
        return db.rows("select id,name,mobile,status,last_login_at,created_at from ap_operator order by created_at");
    }
    /** Totals and one row per apartment, in a single read. */
    @Transactional(readOnly=true) public Map<String,Object> dashboard(Operator operator) {
        LocalDate today=V.today();
        LocalDate monthStart=today.withDayOfMonth(1),nextMonth=monthStart.plusMonths(1),nextMonthEnd=nextMonth.plusMonths(1).minusDays(1);
        Instant monthStartInstant=monthStart.atStartOfDay(IST).toInstant(),activeSince=Instant.now().minus(Duration.ofDays(ACTIVE_DAYS));
        var rows=db.rows("""
                select t.id,t.name,t.city,t.created_at,
                 (select count(*) from ap_flat f where f.tenant_id=t.id) as flats,
                 (select count(*) from ap_user u where u.tenant_id=t.id and u.status='ACTIVE') as members,
                 a.name as admin_name,a.mobile as admin_mobile,
                 greatest((select max(u.last_login_at) from ap_user u where u.tenant_id=t.id),
                          (select max(s.created_at) from ap_session s join ap_user u on u.id=s.user_id where u.tenant_id=t.id)) as last_login_at,
                 coalesce((select sum(p.amount) from ap_provider_payment p where p.tenant_id=t.id),0) as received,
                 coalesce((select sum(p.amount) from ap_provider_payment p where p.tenant_id=t.id and p.verified_at>=?),0) as received_this_month,
                 (select max(i.ends_on) from ap_subscription_invoice i left join ap_subscription s on s.tenant_id=i.tenant_id
                   where i.tenant_id=t.id and i.paid>0 and i.credit+i.paid=i.amount and (s.activated_at is null or i.starts_on<>s.activated_at)) as paid_until
                from ap_tenant t
                left join lateral (select u.name,u.mobile from ap_user u where u.tenant_id=t.id and u.role='ADMIN' and u.status='ACTIVE' order by u.created_at limit 1) a on true
                order by t.created_at desc""",V.sql(monthStartInstant));
        var invoices=db.rows("select tenant_id,amount,credit,paid from ap_subscription_invoice where starts_on>=? and starts_on<=?",V.sql(nextMonth),V.sql(nextMonthEnd));
        Map<Object,BigDecimal> openNextMonth=new HashMap<>();
        for(var i:invoices)openNextMonth.merge(i.get("tenantId"),((BigDecimal)i.get("amount")).subtract((BigDecimal)i.get("credit")).subtract((BigDecimal)i.get("paid")),BigDecimal::add);
        int active=0,trial=0,due=0,signedIn=0;
        BigDecimal received=BigDecimal.ZERO,receivedThisMonth=BigDecimal.ZERO,expected=BigDecimal.ZERO;
        List<Map<String,Object>> apartments=new ArrayList<>();
        for(var r:rows) {
            UUID tenant=(UUID)r.get("id");
            var access=subscriptions.entitlement(tenant);
            switch(access.state()) {
                case "ACTIVE"->active++;
                case "TRIAL"->trial++;
                default->due++;
            }
            Object last=r.get("lastLoginAt");
            Instant lastLogin=last==null?null:Instant.parse(last.toString());
            if(lastLogin!=null&&lastLogin.isAfter(activeSince))signedIn++;
            LocalDate start=Instant.parse(r.get("createdAt").toString()).atZone(IST).toLocalDate();
            BigDecimal next=expectedNextMonth(start,nextMonth,nextMonthEnd,((Number)r.get("flats")).intValue(),openNextMonth.get(tenant));
            BigDecimal paid=(BigDecimal)r.get("received"),paidThisMonth=(BigDecimal)r.get("receivedThisMonth");
            received=received.add(paid);
            receivedThisMonth=receivedThisMonth.add(paidThisMonth);
            expected=expected.add(next);
            Map<String,Object> row=new LinkedHashMap<>();
            row.put("id",tenant);
            row.put("name",r.get("name"));
            row.put("city",r.get("city"));
            row.put("adminName",r.get("adminName"));
            row.put("adminMobile",r.get("adminMobile"));
            row.put("flats",r.get("flats"));
            row.put("members",r.get("members"));
            row.put("subscription",access.state());
            row.put("trialEndsAt",access.trialEndsAt());
            row.put("paidUntil",r.get("paidUntil")==null?null:r.get("paidUntil").toString());
            row.put("received",paid);
            row.put("receivedThisMonth",paidThisMonth);
            row.put("expectedNextMonth",next);
            row.put("lastLoginAt",lastLogin==null?null:lastLogin.toString());
            row.put("activeRecently",lastLogin!=null&&lastLogin.isAfter(activeSince));
            row.put("joinedOn",start.toString());
            apartments.add(row);
        }
        Map<String,Object> totals=new LinkedHashMap<>();
        totals.put("apartments",rows.size());
        totals.put("signedInLast30Days",signedIn);
        totals.put("activeSubscriptions",active);
        totals.put("onTrial",trial);
        totals.put("paymentDue",due);
        totals.put("receivedTotal",received);
        totals.put("receivedThisMonth",receivedThisMonth);
        totals.put("expectedNextMonth",expected);
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("generatedAt",Instant.now().toString());
        out.put("activeWindowDays",ACTIVE_DAYS);
        out.put("nextMonth",nextMonth.toString().substring(0,7));
        out.put("viewer",operator.name());
        out.put("totals",totals);
        out.put("apartments",apartments);
        return out;
    }
    /**
     * Subscription amount still to be paid for the billing cycle that starts next calendar month. Cycles are anchored to the
     * registration date (SubscriptionService.ensure); the first cycle is the free trial. An invoice that already exists wins
     * (it carries credits and part payments); otherwise the current plan price for the flat count is used.
     */
    static BigDecimal expectedNextMonth(LocalDate start,LocalDate nextMonth,LocalDate nextMonthEnd,int flats,BigDecimal openInvoice) {
        if(openInvoice!=null)return openInvoice.max(BigDecimal.ZERO);
        for(int k=1; k<=1200; k++) {
            LocalDate cycle=start.plusMonths(k);
            if(cycle.isAfter(nextMonthEnd))return BigDecimal.ZERO;
            if(!cycle.isBefore(nextMonth)) {
                try {
                    return BigDecimal.valueOf(OperationsRules.planPrice(flats));
                } catch(IllegalArgumentException outsidePlans) {
                    return BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
}
