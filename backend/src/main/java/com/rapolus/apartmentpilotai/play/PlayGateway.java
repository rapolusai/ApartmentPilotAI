package com.rapolus.apartmentpilotai.play;
import com.fasterxml.jackson.databind.JsonNode;
/** Google Play Developer API calls the server needs. Replaced by a fake in tests. */
public interface PlayGateway {
    /** purchases.subscriptionsv2.get: the current state of a subscription purchase, or null when Google does not know the token. */
    JsonNode subscription(String purchaseToken);
    /** purchases.subscriptions.acknowledge: required within 3 days of purchase or Google refunds it. */
    void acknowledge(String productId,String purchaseToken);
}
