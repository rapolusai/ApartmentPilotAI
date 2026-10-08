package com.rapolus.apartmentpilotai.data;
import org.junit.Test;
import static org.junit.Assert.*;
public class LocalEndpointTest {
    @Test public void phoneUsesUsbReverseAddressWithoutAdminSetup() {
        assertEquals("http://127.0.0.1:8080/api/v1",LocalEndpoint.defaultUrl("OPPO/CPH2573/production","CPH2573","qcom"));
    }
    @Test public void phoneAndTabletEmulatorsUseHostAlias() {
        assertEquals("http://10.0.2.2:8080/api/v1",LocalEndpoint.defaultUrl("google/sdk_gphone64/emu","sdk_gphone64_x86_64","ranchu"));
        assertEquals("http://10.0.2.2:8080/api/v1",LocalEndpoint.defaultUrl("generic/sdk/test","Pixel Tablet","goldfish"));
    }
    @Test public void requestLogsRemoveInviteCodesAndQueryValues() {
        assertEquals("/auth/invites/{code}",ApiClient.tracePath("/auth/invites/APT-SECRET?pin=1234"));
        assertEquals("/members/{id}",ApiClient.tracePath("/members/12345678-1234-1234-1234-123456789012?token=secret"));
    }
}
