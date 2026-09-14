package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.domain.OperationsRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;
import java.time.*;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
/** Subscription ledger only: no client claim is accepted as payment or independently verified activation. */ @Service public class SubscriptionService {
    private final Db db;
    private final String providerKey;
    public SubscriptionService(Db db,@Value("${app.provider-key:}")String key) {
        this.db=db;
        this.providerKey=key;
    }
    public void provider(String supplied) {
        if(providerKey.length()<32||supplied==null||!MessageDigest.isEqual(providerKey.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8)))throw new ApiError(org.springframework.http.HttpStatus.FORBIDDEN,"Provider verification is not configured or authorised.");
    }
    private int price(UUID tenant) {
        return OperationsRules.planPrice((int)db.count("select count(*) from ap_flat where tenant_id=?",tenant));
    }
    private Map<String,Object> ensure(UUID tenant) {
        db.lockTenant(tenant);
        var tenantRow=db.one("select created_at from ap_tenant where id=?",tenant);
        LocalDate start=Instant.parse(tenantRow.get("createdAt").toString()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        db.update("insert into ap_subscription(tenant_id,activated_at,first_end,referral_code) values(?,?,?,?) on conflict(tenant_id) do nothing",tenant,V.sql(start),V.sql(start.plusMonths(1)),"AP-"+Tokens.create().substring(0,14).toUpperCase(Locale.ROOT));
        var s=db.one("select * from ap_subscription where tenant_id=?",tenant);
        LocalDate cursor=start;
        // Billing cycles are anchored to the original anniversary, including short months.
        for(int cycle=0; cycle<120&&!cursor.isAfter(V.today()); cycle++) {
            LocalDate next=start.plusMonths(cycle+1L);
            db.update("insert into ap_subscription_invoice(id,tenant_id,starts_on,ends_on,amount) values(?,?,?,?,?) on conflict(tenant_id,starts_on) do nothing",UUID.randomUUID(),tenant,V.sql(cursor),V.sql(next),BigDecimal.valueOf(price(tenant)));
            cursor=next;
        }
        return s;
    }
    @Transactional public Map<String,Object> summary(Account a) {
        a.requireAdmin();
        var s=ensure(a.tenantId());
        int count=(int)db.count("select count(*) from ap_flat where tenant_id=?",a.tenantId());
        var invoices=db.rows("select * from ap_subscription_invoice where tenant_id=? order by starts_on desc",a.tenantId());
        Map<String,Object> out=new LinkedHashMap<>(s);
        out.put("flats",count);
        out.put("price",price(a.tenantId()));
        out.put("category",count<=10?"A":count<=30?"B":"C");
        out.put("invoices",invoices);
        out.put("status",db.count("select count(*) from ap_subscription_invoice where tenant_id=? and starts_on<=? and ends_on>? and credit+paid=amount",a.tenantId(),V.sql(V.today()),V.sql(V.today()))>0?"ACTIVE":"PAYMENT_DUE");
        out.put("referrals",db.rows("select r.id,r.created_at,r.rewarded,t.name as apartment_name,(s.installed_at is not null) as installed,(s.verified_at is not null) as verified from ap_referral r join ap_tenant t on t.id=r.referred_id left join ap_subscription s on s.tenant_id=r.referred_id where r.referrer_id=? order by r.created_at",a.tenantId()));
        out.put("checkoutConfigured",false);
        out.put("automaticDebit",false);
        out.put("firstMonthOnly",true);
        return out;
    }
    @Transactional public Object installed(Account a) {
        a.requireAdmin();
        ensure(a.tenantId());
        db.update("update ap_subscription set installed_at=coalesce(installed_at,now()) where tenant_id=?",a.tenantId());
        return Map.of("recorded",true,"qualified",false,"message","App registration alone does not qualify a referral.");
    }
    @Transactional public Object claim(Account a,Map<String,Object> p) {
        a.requireAdmin();
        promotionLock();
        String code=V.text(p,"code",24).toUpperCase(Locale.ROOT);
        var referrer=db.one("select tenant_id from ap_subscription where referral_code=?",code);
        UUID source=(UUID)referrer.get("tenantId");
        if(source.equals(a.tenantId()))throw ApiError.conflict("You cannot refer your own apartment.");
        lockPair(source,a.tenantId());
        var s=ensure(a.tenantId());
        ensure(source);
        LocalDate begin=LocalDate.parse(s.get("activatedAt").toString()),end=LocalDate.parse(s.get("firstEnd").toString());
        if(!OperationsRules.withinFirstMonth(begin,end,V.today()))throw ApiError.conflict("The first-month referral window has ended.");
        var previous=db.find("select referrer_id,id from ap_referral where referred_id=?",a.tenantId());
        if(previous.isPresent()) {
            if(previous.get().get("referrerId").equals(source))return Map.of("id",previous.get().get("id"));
            throw ApiError.conflict("This apartment already has a referrer.");
        }
        // Unique referred_id gives one parent per apartment. Walk ancestors to reject cycles.
        UUID cursor=source;
        Set<UUID> seen=new HashSet<>();
        while(cursor!=null) {
            if(cursor.equals(a.tenantId())||!seen.add(cursor))throw ApiError.conflict("Circular referrals are not permitted.");
            var parent=db.find("select referrer_id from ap_referral where referred_id=?",cursor);
            cursor=parent.isEmpty()?null:(UUID)parent.get().get("referrerId");
        }
        UUID id=UUID.randomUUID();
        db.update("insert into ap_referral(id,referrer_id,referred_id) values(?,?,?)",id,source,a.tenantId());
        db.audit(a.tenantId(),a.id(),"REFERRAL_LINKED",id,null,"First month only");
        award(source);
        return Map.of("id",id);
    }
    private void lockPair(UUID a,UUID b) {
        if(a.compareTo(b)<0) {
            db.lockTenant(a);
            db.lockTenant(b);
        } else {
            db.lockTenant(b);
            db.lockTenant(a);
        }
    }
    private void promotionLock() {
        db.rows("select pg_advisory_xact_lock(72020402)");
    }
    @Transactional public Object verify(String key,UUID tenant,Map<String,Object> p) {
        provider(key);
        promotionLock();
        var parent=db.find("select referrer_id from ap_referral where referred_id=?",tenant);
        UUID root=parent.isEmpty()?tenant:(UUID)parent.get().get("referrerId");
        // Single promotion lock serialises cross-tenant rewards and prevents referral/activation races.
        var s=ensure(tenant);
        if(s.get("installedAt")==null)throw ApiError.conflict("The apartment Admin must first register this app installation.");
        if(db.count("select count(*) from ap_user where tenant_id=? and role='RESIDENT' and status='ACTIVE'",tenant)<1)throw ApiError.conflict("At least one approved resident is required for activation review.");
        String verifier=V.text(p,"verifiedBy",80);
        if(!V.bool(p,"identityAndApartmentVerified",false))throw new IllegalArgumentException("Independent apartment and authorised-admin verification is required.");
        db.update("update ap_subscription set verified_at=coalesce(verified_at,now()),verified_by=? where tenant_id=?",verifier,tenant);
        db.audit(tenant,null,"PROVIDER_ACTIVATION_VERIFIED",tenant,null,verifier);
        award(root);
        award(tenant);
        return Map.of("verified",true);
    }
    private boolean eligible(UUID tenant) {
        var s=ensure(tenant);
        return OperationsRules.withinFirstMonth(LocalDate.parse(s.get("activatedAt").toString()),LocalDate.parse(s.get("firstEnd").toString()),V.today());
    }
    private void award(UUID root) {
        if(db.count("select count(*) from ap_referral_award where referrer_id=?",root)>0||!eligible(root))return;
        var rootState=db.one("select verified_at,installed_at from ap_subscription where tenant_id=?",root);
        if(rootState.get("verifiedAt")==null||rootState.get("installedAt")==null)return;
        var children=db.rows("select r.id,r.referred_id from ap_referral r join ap_subscription s on s.tenant_id=r.referred_id where r.referrer_id=? and s.verified_at is not null and s.installed_at is not null order by r.created_at,r.id",root);
        if(children.size()<2)return;
        List<Map<String,Object>> valid=new ArrayList<>();
        for(var child:children)if(eligible((UUID)child.get("referredId")))valid.add(child);
        if(valid.size()<2)return;
        // Exactly one reward per first invoice. Further referrals never extend the period.
        db.update("insert into ap_referral_award(referrer_id,first_child,second_child) values(?,?,?)",root,valid.get(0).get("referredId"),valid.get(1).get("referredId"));
        waive(root);
        for(var child:valid.subList(0,2)) {
            waive((UUID)child.get("referredId"));
            db.update("update ap_referral set rewarded=true where id=?",child.get("id"));
        }
    }
    private void waive(UUID tenant) {
        var s=ensure(tenant);
        var i=db.one("select id,amount,paid,credit from ap_subscription_invoice where tenant_id=? and starts_on=?",tenant,V.sql(LocalDate.parse(s.get("activatedAt").toString())));
        if(((BigDecimal)i.get("credit")).compareTo((BigDecimal)i.get("amount"))==0)return;
        if(((BigDecimal)i.get("paid")).signum()>0) {
            db.update("insert into ap_subscription_refund_review(invoice_id,tenant_id,reason) values(?,?,?) on conflict do nothing",i.get("id"),tenant,"First-month reward earned after payment; verify refund manually; no future-month credit");
            db.audit(tenant,null,"REFERRAL_REFUND_REVIEW",i.get("id"),null,"Already paid: provider refund review needed, no automatic extra free month");
            return;
        }
        db.update("update ap_subscription_invoice set credit=amount,status='WAIVED' where id=? and paid=0",i.get("id"));
        db.audit(tenant,null,"FIRST_MONTH_WAIVED",i.get("id"),null,"Referral reward; future invoices unchanged");
    }
    @Transactional public Object settle(String key,UUID invoice,Map<String,Object> p) {
        provider(key);
        var target=db.one("select tenant_id from ap_subscription_invoice where id=?",invoice);
        UUID tenant=(UUID)target.get("tenantId");
        db.lockTenant(tenant);
        String ref=V.text(p,"reference",100);
        var amount=V.money(p,"amount");
        var old=db.find("select id,invoice_id,amount from ap_provider_payment where provider_reference=?",ref);
        if(old.isPresent()) {
            if(!old.get().get("invoiceId").equals(invoice)||((BigDecimal)old.get().get("amount")).compareTo(amount)!=0)throw ApiError.conflict("Provider reference was already used.");
            return Map.of("id",old.get().get("id"));
        }
        if(!V.bool(p,"verified",false))throw new IllegalArgumentException("Provider payment verification is required.");
        var i=db.one("select * from ap_subscription_invoice where tenant_id=? and id=?",tenant,invoice);
        BigDecimal due=((BigDecimal)i.get("amount")).subtract((BigDecimal)i.get("credit")).subtract((BigDecimal)i.get("paid"));
        if(amount.compareTo(due)!=0)throw ApiError.conflict("Payment must match the remaining invoice amount.");
        UUID id=UUID.randomUUID();
        db.update("insert into ap_provider_payment(id,tenant_id,invoice_id,provider_reference,amount) values(?,?,?,?,?)",id,tenant,invoice,ref,amount);
        db.update("update ap_subscription_invoice set paid=paid+?,status='PAID' where id=?",amount,invoice);
        db.audit(tenant,null,"PROVIDER_PAYMENT_VERIFIED",invoice,null,ref);
        return Map.of("id",id,"status","PAID");
    }
}
