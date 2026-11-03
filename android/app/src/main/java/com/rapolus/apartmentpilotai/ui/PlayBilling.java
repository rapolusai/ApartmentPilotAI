package com.rapolus.apartmentpilotai.ui;
import android.app.Activity;
import android.content.Context;
import com.android.billingclient.api.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Google Play Billing for the apartment subscription. The app only starts the purchase and hands Google's purchase token
 * to the server; the server checks it with Google, acknowledges it and decides access. Nothing here unlocks anything.
 * One client lives for the whole process so a purchase that finishes after the screen was rebuilt still reaches the
 * screen that is showing now.
 */
final class PlayBilling {
    /** What the Subscription screen does with the results. Always called on the main thread. */
    interface Listener {
        void purchased(String purchaseToken);
        void pending();
        void message(String text);
    }
    private static PlayBilling instance;
    private final BillingClient client;
    private Listener listener;
    private Activity activity;
    private PlayBilling(Context app) {
        client=BillingClient.newBuilder(app).setListener(this::onPurchases)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()).build();
    }
    static synchronized PlayBilling get(Activity activity,Listener listener) {
        if(instance==null)instance=new PlayBilling(activity.getApplicationContext());
        instance.activity=activity;
        instance.listener=listener;
        return instance;
    }
    private void ui(Runnable r) {
        Activity a=activity;
        if(a!=null&&!a.isFinishing())a.runOnUiThread(r);
    }
    private void say(String text) {
        ui(()->{ if(listener!=null)listener.message(text); });
    }
    private void connected(Runnable next) {
        if(client.isReady()) {
            next.run();
            return;
        }
        client.startConnection(new BillingClientStateListener() {
            @Override public void onBillingSetupFinished(BillingResult result) {
                if(result.getResponseCode()==BillingClient.BillingResponseCode.OK)next.run();
                else say("Google Play is not available on this device right now. "+describe(result));
            }
            @Override public void onBillingServiceDisconnected() {
                // The next action reconnects.
            }
        });
    }
    /**
     * Opens Google's purchase sheet for the apartment's base plan. With an old purchase token (the same Google account
     * bought a different plan) the plan is changed instead, charging the prorated difference now.
     */
    void subscribe(String productId,String basePlanId,String accountId,boolean changePlan) {
        connected(()->{
            QueryProductDetailsParams query=QueryProductDetailsParams.newBuilder().setProductList(Collections.singletonList(
                QueryProductDetailsParams.Product.newBuilder().setProductId(productId).setProductType(BillingClient.ProductType.SUBS).build())).build();
            client.queryProductDetailsAsync(query,(result,details)->{
                if(result.getResponseCode()!=BillingClient.BillingResponseCode.OK||details==null||details.isEmpty()) {
                    say("This plan is not available on Google Play yet. "+describe(result));
                    return;
                }
                ProductDetails product=details.get(0);
                String offerToken=null;
                List<ProductDetails.SubscriptionOfferDetails> offers=product.getSubscriptionOfferDetails();
                // The base plan itself, not a promotional offer: the free trial is run by the server, not by Play.
                if(offers!=null)for(ProductDetails.SubscriptionOfferDetails o:offers)if(basePlanId.equals(o.getBasePlanId())&&o.getOfferId()==null)offerToken=o.getOfferToken();
                if(offerToken==null) {
                    say("The plan for your apartment size ("+basePlanId+") is not set up on Google Play yet.");
                    return;
                }
                String token=offerToken;
                if(!changePlan) {
                    launch(product,token,accountId,null);
                    return;
                }
                currentPurchase(productId,old->{
                    if(old==null)say("Only the Google account that bought the subscription can change its plan.");
                    else launch(product,token,accountId,old.getPurchaseToken());
                });
            });
        });
    }
    private void launch(ProductDetails product,String offerToken,String accountId,String oldToken) {
        List<BillingFlowParams.ProductDetailsParams> items=new ArrayList<>();
        items.add(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(product).setOfferToken(offerToken).build());
        BillingFlowParams.Builder flow=BillingFlowParams.newBuilder().setProductDetailsParamsList(items).setObfuscatedAccountId(accountId);
        if(oldToken!=null)flow.setSubscriptionUpdateParams(BillingFlowParams.SubscriptionUpdateParams.newBuilder().setOldPurchaseToken(oldToken)
            .setSubscriptionReplacementMode(BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_PRORATED_PRICE).build());
        ui(()->{
            Activity a=activity;
            if(a==null)return;
            BillingResult result=client.launchBillingFlow(a,flow.build());
            if(result.getResponseCode()!=BillingClient.BillingResponseCode.OK)say("Google Play could not open the purchase. "+describe(result));
        });
    }
    private interface Found {
        void found(Purchase purchase);
    }
    private void currentPurchase(String productId,Found found) {
        client.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),(result,purchases)->{
            Purchase match=null;
            if(result.getResponseCode()==BillingClient.BillingResponseCode.OK&&purchases!=null)
                for(Purchase p:purchases)if(p.getProducts().contains(productId)&&p.getPurchaseState()==Purchase.PurchaseState.PURCHASED)match=p;
            found.found(match);
        });
    }
    /** Sends this Google account's existing subscription to the server again, e.g. after a lost connection or a new phone. */
    void restore(String productId) {
        connected(()->currentPurchase(productId,p->{
            if(p==null)say("No ApartmentCare subscription was found for the Google account on this phone.");
            else ui(()->{ if(listener!=null)listener.purchased(p.getPurchaseToken()); });
        }));
    }
    private void onPurchases(BillingResult result,List<Purchase> purchases) {
        int code=result.getResponseCode();
        if(code==BillingClient.BillingResponseCode.USER_CANCELED)return;
        if(code!=BillingClient.BillingResponseCode.OK||purchases==null) {
            say("The purchase did not complete. "+describe(result));
            return;
        }
        for(Purchase p:purchases) {
            if(p.getPurchaseState()==Purchase.PurchaseState.PURCHASED)ui(()->{ if(listener!=null)listener.purchased(p.getPurchaseToken()); });
            else if(p.getPurchaseState()==Purchase.PurchaseState.PENDING)ui(()->{ if(listener!=null)listener.pending(); });
        }
    }
    private static String describe(BillingResult result) {
        if(result==null)return "";
        switch(result.getResponseCode()) {
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE: return "Sign in to Google Play with a Google account and try again.";
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
            case BillingClient.BillingResponseCode.NETWORK_ERROR: return "Check the internet connection and try again.";
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED: return "This Google account already has the subscription. Tap Restore purchase.";
            default: return "(code "+result.getResponseCode()+")";
        }
    }
}
