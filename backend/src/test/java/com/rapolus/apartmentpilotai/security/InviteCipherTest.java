package com.rapolus.apartmentpilotai.security;

import com.rapolus.apartmentpilotai.api.ApiError;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class InviteCipherTest {
    private static final String KEY=Base64.getEncoder().encodeToString(new byte[32]);

    @Test void roundTripBindsTheCodeToTenantAndInvite() {
        InviteCipher cipher=new InviteCipher(KEY);
        UUID tenant=UUID.randomUUID(),invite=UUID.randomUUID();
        String encrypted=cipher.encrypt(tenant,invite,"APT-123456");
        assertNotEquals("APT-123456",encrypted);
        assertEquals("APT-123456",cipher.decrypt(tenant,invite,encrypted));
        assertThrows(ApiError.class,()->cipher.decrypt(UUID.randomUUID(),invite,encrypted));
        assertThrows(ApiError.class,()->cipher.decrypt(tenant,UUID.randomUUID(),encrypted));
    }

    @Test void missingOrInvalidKeyFailsClosed() {
        InviteCipher missing=new InviteCipher("");
        assertFalse(missing.configured());
        assertThrows(ApiError.class,()->missing.encrypt(UUID.randomUUID(),UUID.randomUUID(),"APT-123456"));
        assertThrows(IllegalStateException.class,()->new InviteCipher("not base64"));
        assertThrows(IllegalStateException.class,()->new InviteCipher(Base64.getEncoder().encodeToString(new byte[16])));
    }
}
