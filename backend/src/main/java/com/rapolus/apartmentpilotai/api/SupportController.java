package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.operations.SupportService;
import com.rapolus.apartmentpilotai.security.Account;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/ops/support") public class SupportController {
    private final SupportService support;
    public SupportController(SupportService support) {this.support=support;}
    @GetMapping Object list(@AuthenticationPrincipal Account a) {return support.mine(a);}
    @PostMapping Object create(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {return support.create(a,p);}
}
