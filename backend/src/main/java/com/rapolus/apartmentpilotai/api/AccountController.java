package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.security.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
/** Account deletion: in-app (signed in, PIN confirmed) and the public web request page required by Google Play. */
@RestController @RequestMapping("/api/v1") public class AccountController {
    private final AccountDeletionService deletion;
    private final RateGate gate;
    public AccountController(AccountDeletionService deletion,RateGate gate) {
        this.deletion=deletion;
        this.gate=gate;
    }
    @GetMapping("/account/deletion") Object preview(@AuthenticationPrincipal Account a) {
        return deletion.preview(a);
    }
    @PostMapping("/account/delete") Object delete(@AuthenticationPrincipal Account a,@Valid @RequestBody Requests.DeleteAccount r) {
        // Wrong PINs are counted so the PIN cannot be guessed from a stolen session.
        RateGate.enforce(gate.count("delete-account:"+a.id()),5);
        return deletion.deleteOwnAccount(a,r.pin());
    }
    @PostMapping("/auth/deletion-requests") Object request(@Valid @RequestBody Requests.DeletionRequest r,HttpServletRequest req) {
        RateGate.enforce(gate.count("deletion-request:"+req.getRemoteAddr()),5);
        return deletion.request(r.mobile(),r.apartmentName(),r.note(),Tokens.digest(req.getRemoteAddr()));
    }
    @GetMapping("/provider/deletion-requests") Object open(@RequestHeader(value="X-Provider-Key",required=false)String key) {
        return deletion.openRequests(key);
    }
    @PostMapping("/provider/deletion-requests/{id}") Object handle(@RequestHeader(value="X-Provider-Key",required=false)String key,@PathVariable UUID id,@Valid @RequestBody Requests.DeletionDecision r) {
        return deletion.execute(key,id,r.approve(),r.outcome());
    }
}
