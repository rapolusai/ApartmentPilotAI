package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.operations.SubscriptionService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/** Operator endpoints. The provider key is never present in Android source or an apartment account. */ @RestController @RequestMapping("/api/v1/provider") public class ProviderController {
    private final SubscriptionService subscriptions;
    private final com.rapolus.apartmentpilotai.security.AuthService auth;
    public ProviderController(SubscriptionService subscriptions,com.rapolus.apartmentpilotai.security.AuthService auth) {
        this.subscriptions=subscriptions;
        this.auth=auth;
    }
    /** Support: new PIN for an apartment Admin who forgot theirs, after checking who they are by phone or email. */
    @PostMapping("/users/pin") Object resetPin(@RequestHeader(value="X-Provider-Key",required=false)String key,@jakarta.validation.Valid @RequestBody Requests.SupportPinReset r) {
        subscriptions.provider(key);
        auth.supportResetPin(r.mobile(),r.pin());
        return Map.of("ok",true);
    }
    @PostMapping("/apartments/{id}/verify") Object verify(@RequestHeader(value="X-Provider-Key",required=false)String key,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return subscriptions.verify(key,id,p);
    }
    @PostMapping("/invoices/{id}/settle") Object settle(@RequestHeader(value="X-Provider-Key",required=false)String key,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return subscriptions.settle(key,id,p);
    }
}
