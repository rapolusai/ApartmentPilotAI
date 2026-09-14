package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.*;
import java.util.UUID;
@Component @ConditionalOnProperty(name="app.billing-job-enabled",havingValue="true") public class OperationsJob {
    private final Db db;
    private final AutomationService service;
    private static final Logger LOG=LoggerFactory.getLogger(OperationsJob.class);
    public OperationsJob(Db db,AutomationService service) {
        this.db=db;
        this.service=service;
    }
    @Scheduled(initialDelay=15000,fixedDelay=900000) public void tick() {
        for(var row:db.rows("select distinct on(tenant_id) id,tenant_id from ap_user where role='ADMIN' and status='ACTIVE' order by tenant_id,created_at")) {
            try {
                service.run(new Account((UUID)row.get("id"),(UUID)row.get("tenantId"),null,"System","ADMIN"));
            } catch(RuntimeException e) {
                LOG.error("Apartment automation failed for {} ({})",row.get("tenantId"),e.getClass().getSimpleName());
            }
        }
    }
}
