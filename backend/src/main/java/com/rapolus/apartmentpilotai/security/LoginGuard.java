package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.store.Db;
import java.time.Instant;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
/**
 * Failed-PIN counting with exponential backoff. Tier A keys mobile + client IP, tier B keys the mobile alone, and one IP may
 * fail at most N times per 15-minute window across all mobiles. Each attempt is reserved (counted as a failure) under a row
 * lock before the PIN is checked, so parallel requests cannot all slip through; a correct PIN then clears the counters.
 * While either tier is locked a request is refused before BCrypt and changes nothing, so spamming cannot extend a lock.
 * Unknown mobiles take exactly the same path. Every step commits on its own because the login transaction rolls back on 401.
 */
@Service public class LoginGuard {
    private final Db db;
    private final boolean enabled;
    private final int deviceFree,deviceCap,accountFree,accountCap,ipMax,decayHours;
    public LoginGuard(Db db,@Value("${app.login-guard.enabled:true}")boolean enabled,@Value("${app.login-guard.device-free-failures:5}")int deviceFree,@Value("${app.login-guard.device-cap-minutes:60}")int deviceCap,@Value("${app.login-guard.account-free-failures:20}")int accountFree,@Value("${app.login-guard.account-cap-minutes:15}")int accountCap,@Value("${app.login-guard.ip-failures-per-15-minutes:30}")int ipMax,@Value("${app.login-guard.decay-hours:24}")int decayHours) {
        this.db=db;
        this.enabled=enabled;
        this.deviceFree=deviceFree;
        this.deviceCap=deviceCap;
        this.accountFree=accountFree;
        this.accountCap=accountCap;
        this.ipMax=ipMax;
        this.decayHours=decayHours;
    }
    public boolean enabled() {
        return enabled;
    }
    public static String deviceKey(String mobile,String ip) {
        return Tokens.digest("login-guard:device:"+mobile+":"+ip);
    }
    public static String accountKey(String mobile) {
        return Tokens.digest("login-guard:account:"+mobile);
    }
    static String ipKey(String ip) {
        return Tokens.digest("login-fail-ip:"+ip);
    }
    /** Lock length after the given failure count: none below the free allowance, then 1, 2, 4, 8... minutes up to the cap. */
    public static long lockMinutes(int failures,int free,int cap) {
        if(failures<free)return 0;
        return Math.min(cap,1L<<Math.min(failures-free,20));
    }
    static String lockMessage(long seconds) {
        long minutes=Math.max(1,(seconds+59)/60);
        return "Too many incorrect PIN attempts. Try again in "+minutes+(minutes==1?" minute.":" minutes.");
    }
    private long remainingSeconds(String key) {
        // Upsert takes the row lock, so concurrent attempts for the same key queue here and see each other's counts.
        var row=db.one("insert into ap_login_guard(key_hash) values(?) on conflict(key_hash) do update set key_hash=excluded.key_hash returning coalesce(ceil(greatest(0,extract(epoch from locked_until-now()))),0)::bigint as wait_seconds",key);
        return ((Number)row.get("waitSeconds")).longValue();
    }
    private void addFailure(String key,int free,int cap) {
        // A key with no failure for decay-hours starts again from zero, and a new episode may alert again.
        var row=db.one("update ap_login_guard set failures=case when last_failure_at<now()-make_interval(hours=>?) then 1 else failures+1 end,notified_at=case when last_failure_at<now()-make_interval(hours=>?) then null else notified_at end,last_failure_at=now() where key_hash=? returning failures",decayHours,decayHours,key);
        long minutes=lockMinutes(((Number)row.get("failures")).intValue(),free,cap);
        if(minutes>0)db.update("update ap_login_guard set locked_until=now()+make_interval(mins=>?) where key_hash=?",(int)minutes,key);
    }
    /** Before the PIN check: refuse (429 + Retry-After) while locked, otherwise count this attempt as a failure in advance. */
    @Transactional(propagation=Propagation.REQUIRES_NEW) public void reserve(String mobile,String ip) {
        String device=deviceKey(mobile,ip),account=accountKey(mobile);
        long wait=Math.max(remainingSeconds(device),remainingSeconds(account));
        if(wait>0)throw ApiError.tooMany(lockMessage(wait),wait);
        long now=Instant.now().getEpochSecond();
        // Conditional upsert: once the window holds ipMax failures no further attempt is admitted, even under concurrency.
        if(db.find("insert into ap_rate_limit(bucket_key,window_start,uses) values(?,?,1) on conflict(bucket_key,window_start) do update set uses=ap_rate_limit.uses+1 where ap_rate_limit.uses<? returning uses",ipKey(ip),now/900,ipMax).isEmpty())throw ApiError.tooMany(lockMessage(900-now%900),900-now%900);
        addFailure(device,deviceFree,deviceCap);
        addFailure(account,accountFree,accountCap);
    }
    /** After a correct PIN: the reserved attempt was not a failure, so both tiers and the IP count are released. */
    @Transactional(propagation=Propagation.REQUIRES_NEW) public void succeeded(String mobile,String ip) {
        db.update("delete from ap_login_guard where key_hash in (?,?)",deviceKey(mobile,ip),accountKey(mobile));
        db.update("update ap_rate_limit set uses=uses-1 where bucket_key=? and window_start=? and uses>0",ipKey(ip),Instant.now().getEpochSecond()/900);
    }
    /** After an incorrect PIN: the failure was already reserved. When tier B has just locked a real account, tell its holder once. */
    @Transactional(propagation=Propagation.REQUIRES_NEW) public void failed(String mobile) {
        if(db.update("update ap_login_guard set notified_at=now() where key_hash=? and notified_at is null and failures>=?",accountKey(mobile),accountFree)!=1)return;
        var user=db.find("select id,tenant_id from ap_user where mobile=?",mobile);
        if(user.isEmpty())return;
        UUID id=(UUID)user.get().get("id"),tenant=(UUID)user.get().get("tenantId");
        db.notify(tenant,id,"LOGIN_LOCKED:"+UUID.randomUUID(),"Sign-in paused","Several incorrect PIN attempts were made on your account. Sign-in is paused for a few minutes. If this was not you, tell your apartment Admin.","home");
        db.audit(tenant,null,"LOGIN_LOCKED",id,null,"Several incorrect PIN attempts; sign-in paused for up to "+accountCap+" minutes");
    }
}
