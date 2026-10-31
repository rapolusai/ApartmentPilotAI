package com.rapolus.apartmentpilotai.security;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
final class LoginGuardTest {
    @Test void deviceTierBacksOffAfterFiveFreeFailuresUpToOneHour() {
        assertEquals(0,LoginGuard.lockMinutes(4,5,60));
        assertEquals(1,LoginGuard.lockMinutes(5,5,60));
        assertEquals(2,LoginGuard.lockMinutes(6,5,60));
        assertEquals(4,LoginGuard.lockMinutes(7,5,60));
        assertEquals(32,LoginGuard.lockMinutes(10,5,60));
        assertEquals(60,LoginGuard.lockMinutes(11,5,60));
        assertEquals(60,LoginGuard.lockMinutes(500,5,60));
    }
    @Test void accountTierBacksOffAfterTwentyFreeFailuresUpToFifteenMinutes() {
        assertEquals(0,LoginGuard.lockMinutes(19,20,15));
        assertEquals(1,LoginGuard.lockMinutes(20,20,15));
        assertEquals(8,LoginGuard.lockMinutes(23,20,15));
        assertEquals(15,LoginGuard.lockMinutes(24,20,15));
    }
    @Test void keysAreDigestsThatNeverContainTheMobileOrAddress() {
        String device=LoginGuard.deviceKey("9876543210","10.0.0.1"),account=LoginGuard.accountKey("9876543210");
        assertEquals(64,device.length());
        assertNotEquals(device,account);
        assertNotEquals(device,LoginGuard.deviceKey("9876543210","10.0.0.2"));
        assertFalse(device.contains("9876543210")||account.contains("9876543210"));
    }
    @Test void lockMessageRoundsUpToWholeMinutes() {
        assertEquals("Too many incorrect PIN attempts. Try again in 1 minute.",LoginGuard.lockMessage(5));
        assertEquals("Too many incorrect PIN attempts. Try again in 2 minutes.",LoginGuard.lockMessage(61));
    }
}
