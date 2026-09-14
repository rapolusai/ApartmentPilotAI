package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.operations.SubscriptionService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/** Operator endpoints. The provider key is never present in Android source or an apartment account. */ @RestController @RequestMapping("/api/v1/provider") public class ProviderController {
    private final SubscriptionService subscriptions;
    public ProviderController(SubscriptionService subscriptions) {
        this.subscriptions=subscriptions;
    }
    @PostMapping("/apartments/{id}/verify") Object verify(@RequestHeader(value="X-Provider-Key",required=false)String key,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return subscriptions.verify(key,id,p);
    }
    @PostMapping("/invoices/{id}/settle") Object settle(@RequestHeader(value="X-Provider-Key",required=false)String key,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return subscriptions.settle(key,id,p);
    }
}
