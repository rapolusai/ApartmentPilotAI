package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.security.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
/**
 * ApartmentCare team (platform super user) endpoints. Read-only across apartments. Operator tokens are checked by
 * OperatorService against their own session table, so apartment tokens are refused here and operator tokens are refused
 * by every apartment endpoint. Creating or pausing an operator needs the server-side provider key.
 */
@RestController @RequestMapping("/api/v1") public class OperatorController {
    private final OperatorService operators;
    private final LoginGuard guard;
    private final RateGate gate;
    public OperatorController(OperatorService operators,LoginGuard guard,RateGate gate) {
        this.operators=operators;
        this.guard=guard;
        this.gate=gate;
    }
    @PostMapping("/operator/login") Object login(@Valid @RequestBody Requests.OperatorLogin r,HttpServletRequest req,HttpServletResponse res) {
        res.setHeader("Cache-Control","no-store");
        String ip=req.getRemoteAddr(),key="operator:"+r.mobile();
        RateGate.enforce(gate.count("operator-login-ip:"+ip),30);
        if(!guard.enabled()) {
            RateGate.enforce(gate.count("operator-login-mobile:"+r.mobile()),10);
            return operators.login(r.mobile(),r.pin());
        }
        // Same lockout as apartment logins, in its own key space so the two never lock each other.
        guard.reserve(key,ip);
        try {
            var session=operators.login(r.mobile(),r.pin());
            guard.succeeded(key,ip);
            return session;
        } catch(ApiError e) {
            if(e.status()==HttpStatus.UNAUTHORIZED)guard.failed(key);
            throw e;
        }
    }
    @PostMapping("/operator/logout") Object logout(@RequestHeader(value="Authorization",required=false)String token) {
        operators.logout(token);
        return Map.of("signedOut",true);
    }
    @GetMapping("/operator/dashboard") Object dashboard(@RequestHeader(value="Authorization",required=false)String token,HttpServletResponse res) {
        var operator=operators.authenticate(token);
        res.setHeader("Cache-Control","no-store");
        return operators.dashboard(operator);
    }
    @GetMapping("/provider/operators") Object list(@RequestHeader(value="X-Provider-Key",required=false)String key) {
        return operators.list(key);
    }
    @PostMapping("/provider/operators") Object save(@RequestHeader(value="X-Provider-Key",required=false)String key,@Valid @RequestBody Requests.OperatorSave r) {
        return operators.upsert(key,r.name().trim(),r.mobile(),r.pin(),r.active()==null||r.active());
    }
}
