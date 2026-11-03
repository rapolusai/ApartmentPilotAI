package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.play.PlayBillingService;
import com.rapolus.apartmentpilotai.security.*;
import java.util.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
/** Google Play Billing for the apartment subscription. Admin only; Google's notifications arrive on a secret push URL. */
@RestController @RequestMapping("/api/v1") public class PlayController {
    private final PlayBillingService play;
    private final RateGate gate;
    public PlayController(PlayBillingService play,RateGate gate) {
        this.play=play;
        this.gate=gate;
    }
    @GetMapping("/ops/subscription/play") Object status(@AuthenticationPrincipal Account a) {
        return play.status(a);
    }
    @PostMapping("/ops/subscription/play/verify") Object verify(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        RateGate.enforce(gate.count("play-verify:"+a.tenantId()),30);
        Object token=p==null?null:p.get("purchaseToken");
        return play.verify(a,token==null?null:token.toString());
    }
    @PostMapping("/play/notifications") Object notification(@RequestParam(value="secret",required=false)String secret,@RequestBody(required=false)Map<String,Object> body) {
        play.notification(secret,body);
        return Map.of("received",true);
    }
}
