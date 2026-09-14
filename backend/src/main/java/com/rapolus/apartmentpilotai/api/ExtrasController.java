package com.rapolus.apartmentpilotai.api;
import com.rapolus.apartmentpilotai.operations.ExtrasService;
import com.rapolus.apartmentpilotai.security.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.*;
@RestController @RequestMapping("/api/v1/ops") public class ExtrasController {
    private final ExtrasService extras;
    private final RateGate gate;
    public ExtrasController(ExtrasService e,RateGate g) {
        extras=e;
        gate=g;
    }
    @GetMapping("/polls") Object polls(@AuthenticationPrincipal Account a) {
        return extras.polls(a);
    }
    @GetMapping("/polls/{id}") Object poll(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return extras.poll(a,id);
    }
    @PostMapping("/polls") Object poll(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return extras.createPoll(a,p);
    }
    @PostMapping("/polls/{id}/vote") Object vote(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return extras.vote(a,id,p);
    }
    @PostMapping("/polls/{id}/close") Object close(@AuthenticationPrincipal Account a,@PathVariable UUID id) {
        return extras.close(a,id);
    }
    @GetMapping("/vehicles") Object vehicles(@AuthenticationPrincipal Account a) {
        return extras.vehicles(a);
    }
    @PostMapping("/vehicles") Object vehicle(@AuthenticationPrincipal Account a,@RequestBody Map<String,Object> p) {
        return extras.saveVehicle(a,null,p);
    }
    @PostMapping("/vehicles/{id}") Object vehicle(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        return extras.saveVehicle(a,id,p);
    }
    @PostMapping("/vehicles/{id}/contact") Object contact(@AuthenticationPrincipal Account a,@PathVariable UUID id,@RequestBody Map<String,Object> p) {
        RateGate.enforce(gate.count("vehicle-contact:"+a.id()),5);
        return extras.contactOwner(a,id,p);
    }
}
