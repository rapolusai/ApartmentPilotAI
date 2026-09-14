package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.api.ApiError;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
@Service public class RateGate {
    private final Db db;
    public RateGate(Db db) {
        this.db=db;
    }
    // Independent commit: failed logins MUST still count toward the limit.
    @Transactional(propagation=Propagation.REQUIRES_NEW) public int count(String key) {
        long bucket=Instant.now().getEpochSecond()/900;
        return ((Number)db.one("insert into ap_rate_limit(bucket_key,window_start,uses) values(?,?,1) on conflict(bucket_key,window_start) do update set uses=ap_rate_limit.uses+1 returning uses",Tokens.digest(key),bucket).get("uses")).intValue();
    }
    public static void enforce(int used,int max) {
        if(used>max)throw new ApiError(HttpStatus.TOO_MANY_REQUESTS,"Too many attempts. Wait 15 minutes before retrying.");
    }
}
