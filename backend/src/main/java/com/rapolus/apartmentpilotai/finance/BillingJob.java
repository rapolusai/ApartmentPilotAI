package com.rapolus.apartmentpilotai.finance;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.security.Account;
import java.time.*;
import java.sql.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component @ConditionalOnProperty(name="app.billing-job-enabled",havingValue="true") public class BillingJob {
    private static final Logger LOG=LoggerFactory.getLogger(BillingJob.class);
    private final Db db;
    private final FinanceService service;
    public BillingJob(Db db,FinanceService service) {
        this.db=db;
        this.service=service;
    }
    // Starts after five seconds, then catches up hourly. Safe after downtime; unique bills prevent repeats.
    @Scheduled(initialDelay=5000,fixedDelay=3600000) public void generate() {
        YearMonth month=FinanceService.currentMonth();
        int day=LocalDate.now(FinanceService.ZONE).getDayOfMonth();
        for(var t:db.rows("select t.id,u.id as user_id,u.name,r.billing_day from ap_tenant t join lateral(select id,name from ap_user where tenant_id=t.id and role='ADMIN' and status='ACTIVE' order by created_at limit 1) u on true join lateral(select billing_day from ap_billing_rule where tenant_id=t.id and effective_month<=? order by effective_month desc limit 1) r on true",Date.valueOf(month.atDay(1)))) {
            if(((Number)t.get("billingDay")).intValue()>day)continue;
            try {
                service.generate(new Account((UUID)t.get("userId"),(UUID)t.get("id"),null,"System","ADMIN"),month);
            } catch(RuntimeException e) {
                LOG.error("Monthly billing failed for tenant {}: {}",t.get("id"),e.getClass().getSimpleName());
            }
        }
    }
}
