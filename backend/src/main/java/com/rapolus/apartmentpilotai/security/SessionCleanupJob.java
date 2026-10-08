package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.store.Db;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/** Removes expired sessions and rate-limit windows that can no longer affect a decision, so both tables stay bounded. */
@Component @ConditionalOnProperty(name="app.cleanup-enabled",havingValue="true",matchIfMissing=true) public class SessionCleanupJob {
    private static final Logger LOG=LoggerFactory.getLogger(SessionCleanupJob.class);
    // Rate windows are 15 minutes (RateGate); one day of history is kept for investigation.
    static final long RETAINED_WINDOWS=96;
    private final Db db;
    public SessionCleanupJob(Db db) {
        this.db=db;
    }
    @Scheduled(initialDelay=60000,fixedDelay=3600000) public void purge() {
        try {
            int sessions=db.update("delete from ap_session where expires_at<now()");
            int windows=db.update("delete from ap_rate_limit where window_start<?",Instant.now().getEpochSecond()/900-RETAINED_WINDOWS);
            if(sessions+windows>0)LOG.info("Purged {} expired sessions and {} old rate-limit windows",sessions,windows);
        } catch(RuntimeException e) {
            LOG.error("Session cleanup failed ({})",e.getClass().getSimpleName());
        }
    }
}
