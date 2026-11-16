package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.operations.SetupService;
import com.rapolus.apartmentpilotai.security.Account;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
/** Guided setup: progress, step completion (server-verified) and one-time tips. */
@RestController @RequestMapping("/api/v1/setup") public class SetupController {
    private final SetupService setup;
    public SetupController(SetupService setup) {
        this.setup=setup;
    }
    @GetMapping Object state(@AuthenticationPrincipal Account a) {
        return setup.state(a);
    }
    @PostMapping("/steps/{key}/complete") Object complete(@AuthenticationPrincipal Account a,@PathVariable String key) {
        return setup.complete(a,key);
    }
    @PostMapping("/tips/{key}/seen") Object tip(@AuthenticationPrincipal Account a,@PathVariable String key) {
        return setup.tipSeen(a,key);
    }
    @GetMapping("/members") Object members(@AuthenticationPrincipal Account a) {
        return setup.members(a);
    }
}
