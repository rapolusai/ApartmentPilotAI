package com.rapolus.apartmentpilotai.play;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.domain.OperationsRules;
import com.rapolus.apartmentpilotai.operations.SubscriptionService;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.security.Tokens;
import com.rapolus.apartmentpilotai.store.Db;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
/**
 * Apartment subscriptions bought through Google Play Billing. The phone only starts the purchase; this server asks Google
 * whether a purchase token is real and active before it counts, acknowledges it, and keeps it current from Google's
 * real-time developer notifications. A purchase belongs to the apartment (matched by an obfuscated account id set at
 * purchase time), so it keeps the whole apartment active after the Admin role changes hands.
 */
@Service public class PlayBillingService {
    private static final Logger LOG=LoggerFactory.getLogger(PlayBillingService.class);
    static final Set<String> COVERING=Set.of("SUBSCRIPTION_STATE_ACTIVE","SUBSCRIPTION_STATE_IN_GRACE_PERIOD","SUBSCRIPTION_STATE_CANCELED");
    private final Db db;
    private final PlayGateway gateway;
    private final SubscriptionService subscriptions;
    private final boolean enabled;
    private final String productId,packageName,notificationSecret;
    private final ObjectMapper json=new ObjectMapper();
    public PlayBillingService(Db db,PlayGateway gateway,SubscriptionService subscriptions,@Value("${app.play.enabled:false}")boolean enabled,@Value("${app.play.product-id:apartmentcare_monthly}")String productId,@Value("${app.play.package-name:com.rapolus.apartmentpilotai}")String packageName,@Value("${app.play.notification-secret:}")String notificationSecret) {
        this.db=db;
        this.gateway=gateway;
        this.subscriptions=subscriptions;
        this.enabled=enabled;
        this.productId=productId;
        this.packageName=packageName;
        this.notificationSecret=notificationSecret;
    }
    /** Opaque per-apartment id sent with the purchase; 64 hex characters, Google's limit for obfuscatedAccountId. */
    public static String accountId(UUID tenant) {
        return Tokens.digest("apartmentcare-play-account:"+tenant);
    }
    /** Base plan for the apartment's size: one monthly base plan per price band, set up in Play Console with these ids. */
    public static String basePlan(int price) {
        switch(price) {
            case 99: return "flats-10";
            case 149: return "flats-30";
            case 249: return "flats-50";
            case 349: return "flats-100";
            case 499: return "flats-200";
            default: return "price-"+price;
        }
    }
    private void requireEnabled() {
        if(!enabled)throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Google Play subscriptions are not switched on yet.");
    }
    private int price(UUID tenant) {
        return OperationsRules.planPrice((int)db.count("select count(*) from ap_flat where tenant_id=?",tenant));
    }
    /** What the Admin's Subscription screen needs to show and to start a purchase. */
    public Map<String,Object> status(Account a) {
        a.requireAdmin();
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("enabled",enabled);
        if(!enabled)return out;
        int price=price(a.tenantId());
        var access=subscriptions.entitlement(a.tenantId());
        out.put("packageName",packageName);
        out.put("productId",productId);
        out.put("basePlanId",basePlan(price));
        out.put("price",price);
        out.put("accountId",accountId(a.tenantId()));
        out.put("status",access.state());
        out.put("trialEndsAt",access.trialEndsAt());
        var current=db.find("select token_hash,buyer_user_id,buyer_name,state,auto_renewing,expiry_at,base_plan_id from ap_play_purchase where tenant_id=? and replaced_by is null and state in ('SUBSCRIPTION_STATE_ACTIVE','SUBSCRIPTION_STATE_IN_GRACE_PERIOD','SUBSCRIPTION_STATE_CANCELED','SUBSCRIPTION_STATE_ON_HOLD','SUBSCRIPTION_STATE_PAUSED') and (expiry_at is null or expiry_at>now()) order by expiry_at desc nulls last limit 1",a.tenantId());
        boolean covering=current.isPresent()&&COVERING.contains(current.get().get("state"));
        out.put("canSubscribe",!covering);
        if(current.isPresent()) {
            var c=current.get();
            Map<String,Object> purchase=new LinkedHashMap<>();
            purchase.put("state",c.get("state"));
            purchase.put("autoRenewing",c.get("autoRenewing"));
            purchase.put("expiryAt",c.get("expiryAt")==null?null:c.get("expiryAt").toString());
            purchase.put("basePlanId",c.get("basePlanId"));
            purchase.put("boughtByYou",a.id().equals(c.get("buyerUserId")));
            purchase.put("buyerName",c.get("buyerName"));
            // A smaller apartment than its plan is fine until renewal; a bigger one should move up a band.
            purchase.put("planMatches",basePlan(price).equals(c.get("basePlanId")));
            out.put("purchase",purchase);
        }
        return out;
    }
    /** The Admin's app sends the token Google returned; it counts only after Google confirms it. */
    public Map<String,Object> verify(Account a,String purchaseToken) {
        a.requireAdmin();
        requireEnabled();
        if(purchaseToken==null||purchaseToken.isBlank()||purchaseToken.length()>2000)throw new IllegalArgumentException("Purchase token is missing.");
        JsonNode p=gateway.subscription(purchaseToken);
        if(p==null)throw new IllegalArgumentException("Google Play does not recognise this purchase.");
        String owner=p.path("externalAccountIdentifiers").path("obfuscatedExternalAccountId").asText("");
        if(!MessageDigest.isEqual(owner.getBytes(StandardCharsets.UTF_8),accountId(a.tenantId()).getBytes(StandardCharsets.UTF_8)))throw ApiError.conflict("This Google Play purchase belongs to a different apartment.");
        String hash=Tokens.digest(purchaseToken);
        var existing=db.find("select tenant_id from ap_play_purchase where token_hash=?",hash);
        if(existing.isPresent()&&!a.tenantId().equals(existing.get().get("tenantId")))throw ApiError.conflict("This Google Play purchase belongs to a different apartment.");
        save(a.tenantId(),purchaseToken,p,a.id(),a.name());
        db.audit(a.tenantId(),a.actorId(),"PLAY_SUBSCRIPTION_VERIFIED",hash.substring(0,16),null,p.path("subscriptionState").asText());
        return status(a);
    }
    /** Stores Google's view of a purchase, links an upgrade or downgrade to the purchase it replaced, and acknowledges it. */
    private void save(UUID tenant,String token,JsonNode p,UUID buyer,String buyerName) {
        String hash=Tokens.digest(token);
        JsonNode item=null;
        for(JsonNode li:p.path("lineItems"))if(productId.equals(li.path("productId").asText())) {
            item=li;
            break;
        }
        if(item==null)throw new IllegalArgumentException("This purchase is not an ApartmentCare subscription.");
        String state=p.path("subscriptionState").asText("SUBSCRIPTION_STATE_UNSPECIFIED");
        String expiryText=item.path("expiryTime").asText("");
        Instant expiry=expiryText.isEmpty()?null:Instant.parse(expiryText);
        boolean renewing=item.path("autoRenewingPlan").path("autoRenewEnabled").asBoolean(false);
        String basePlanId=item.path("offerDetails").path("basePlanId").asText("");
        String order=item.path("latestSuccessfulOrderId").asText(p.path("latestOrderId").asText(""));
        db.update("""
                insert into ap_play_purchase(token_hash,purchase_token,tenant_id,buyer_user_id,buyer_name,product_id,base_plan_id,state,auto_renewing,expiry_at,latest_order_id)
                values(?,?,?,?,?,?,?,?,?,?,?)
                on conflict(token_hash) do update set buyer_user_id=coalesce(ap_play_purchase.buyer_user_id,excluded.buyer_user_id),
                 buyer_name=case when ap_play_purchase.buyer_name='' then excluded.buyer_name else ap_play_purchase.buyer_name end,state=excluded.state,auto_renewing=excluded.auto_renewing,expiry_at=excluded.expiry_at,
                 base_plan_id=excluded.base_plan_id,latest_order_id=excluded.latest_order_id,updated_at=now()""",
                hash,token,tenant,buyer,buyerName==null?"":buyerName,productId,basePlanId,state,renewing,expiry==null?null:java.sql.Timestamp.from(expiry),order.isEmpty()?null:order);
        String linked=p.path("linkedPurchaseToken").asText("");
        if(!linked.isEmpty())db.update("update ap_play_purchase set replaced_by=?,updated_at=now() where token_hash=? and tenant_id=?",hash,Tokens.digest(linked),tenant);
        boolean pendingAck="ACKNOWLEDGEMENT_STATE_PENDING".equals(p.path("acknowledgementState").asText());
        if(pendingAck&&COVERING.contains(state)) {
            gateway.acknowledge(productId,token);
            db.update("update ap_play_purchase set acknowledged=true where token_hash=?",hash);
        } else if(!pendingAck)db.update("update ap_play_purchase set acknowledged=true where token_hash=?",hash);
        LOG.info("play purchase saved tenant={} state={} expiry={}",tenant,state,expiry);
    }
    /**
     * Google's real-time developer notification (Pub/Sub push). The push URL carries a shared secret; the purchase itself
     * is always re-read from Google, so a forged message can only make the server look a token up again.
     */
    public void notification(String secret,Map<String,Object> body) {
        if(!enabled)return;
        if(notificationSecret.length()<24||secret==null||!MessageDigest.isEqual(notificationSecret.getBytes(StandardCharsets.UTF_8),secret.getBytes(StandardCharsets.UTF_8)))throw ApiError.forbidden();
        JsonNode data;
        try {
            Object message=body==null?null:body.get("message");
            Object encoded=message instanceof Map?((Map<?,?>)message).get("data"):null;
            if(encoded==null)return;
            data=json.readTree(Base64.getDecoder().decode(encoded.toString()));
        } catch(Exception unreadable) {
            LOG.warn("play notification unreadable ({})",unreadable.getClass().getSimpleName());
            return;
        }
        if(!packageName.equals(data.path("packageName").asText(packageName)))return;
        String token=data.path("subscriptionNotification").path("purchaseToken").asText("");
        if(token.isEmpty())token=data.path("voidedPurchaseNotification").path("purchaseToken").asText("");
        if(token.isEmpty())return;
        refresh(token);
    }
    /** Re-reads one purchase from Google and stores it. Unknown tokens are matched to an apartment by their account id. */
    void refresh(String token) {
        JsonNode p=gateway.subscription(token);
        if(p==null)return;
        var row=db.find("select tenant_id from ap_play_purchase where token_hash=?",Tokens.digest(token));
        UUID tenant=row.map(r->(UUID)r.get("tenantId")).orElse(null);
        if(tenant==null) {
            String owner=p.path("externalAccountIdentifiers").path("obfuscatedExternalAccountId").asText("");
            if(owner.isEmpty())return;
            for(var t:db.rows("select id from ap_tenant"))if(accountId((UUID)t.get("id")).equals(owner)) {
                tenant=(UUID)t.get("id");
                break;
            }
            if(tenant==null)return;
        }
        try {
            save(tenant,token,p,null,null);
        } catch(IllegalArgumentException otherProduct) {
            LOG.info("play notification ignored: {}",otherProduct.getMessage());
        }
    }
}
