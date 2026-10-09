package com.rapolus.apartmentpilotai.security;
import java.util.UUID;
import com.rapolus.apartmentpilotai.api.ApiError;
/** Authenticated principal. {@code system} marks the scheduler's account: it keeps the tenant lock and staff checks of the
 * Admin it runs as, but audit rows record no human actor (actor_id NULL, shown as "Automation"). */
public record Account(UUID id,UUID tenantId,UUID flatId,String name,String role,boolean system) {
    public Account(UUID id,UUID tenantId,UUID flatId,String name,String role) {
        this(id,tenantId,flatId,name,role,false);
    }
    public static Account automation(UUID adminId,UUID tenantId) {
        return new Account(adminId,tenantId,null,"Automation","ADMIN",true);
    }
    /** The audit actor: null for scheduled automation so no person is credited with actions they never took. */
    public UUID actorId() {
        return system?null:id;
    }
    public boolean staff() {
        return role.equals("ADMIN")||role.equals("TREASURER");
    }
    public void requireStaff() {
        if(!staff())throw ApiError.forbidden();
    }
    public void requireAdmin() {
        if(!role.equals("ADMIN"))throw ApiError.forbidden();
    }
    /** Alias of {@link #automation} kept for callers that name the scheduler's account "system". */
    public static Account system(UUID adminId,UUID tenantId) {
        return automation(adminId,tenantId);
    }
    /** Alias of {@link #actorId}. */
    public UUID auditActor() {
        return actorId();
    }
}
