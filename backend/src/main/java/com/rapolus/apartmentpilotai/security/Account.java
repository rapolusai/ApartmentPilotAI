package com.rapolus.apartmentpilotai.security;
import java.util.UUID;
import com.rapolus.apartmentpilotai.api.ApiError;
public record Account(UUID id,UUID tenantId,UUID flatId,String name,String role) {
    public boolean staff() {
        return role.equals("ADMIN")||role.equals("TREASURER");
    }
    public void requireStaff() {
        if(!staff())throw ApiError.forbidden();
    }
    public void requireAdmin() {
        if(!role.equals("ADMIN"))throw ApiError.forbidden();
    }
    // Scheduled jobs act with an Admin's tenant authority, but no person performed the action.
    // Identity check is by reference to this constant, so a user named the same is never treated as the system.
    private static final String SYSTEM_NAME=new String("System automation");
    public static Account system(UUID adminId,UUID tenantId) {
        return new Account(adminId,tenantId,null,SYSTEM_NAME,"ADMIN");
    }
    @SuppressWarnings("StringEquality") public boolean system() {
        return name==SYSTEM_NAME;
    }
    /** Audit actor: null for scheduled automation (shown as "System automation"). */
    public UUID auditActor() {
        return system()?null:id;
    }
}
