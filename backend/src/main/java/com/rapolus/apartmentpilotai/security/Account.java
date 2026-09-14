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
}
