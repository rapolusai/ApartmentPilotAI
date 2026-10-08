package com.rapolus.apartmentpilotai.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoginPolicyTest {
    @Test public void RecoveryReturnsToTheChosenLoginType() {
        assertEquals("admin-login",LoginPolicy.routeForRole("ADMIN"));
        assertEquals("treasurer-login",LoginPolicy.routeForRole("TREASURER"));
        assertEquals("resident-login",LoginPolicy.routeForRole("RESIDENT"));
        assertEquals("resident-login",LoginPolicy.routeForRole(null));
    }
    @Test public void validMobileAndPinLengthsAreAccepted() {
        for(String pin:new String[]{"1234","12345","123456"})assertEquals("",LoginPolicy.validationMessage("9876543210",pin));
    }
    @Test public void invalidMobileIsRejectedBeforeHttp() {
        for(String mobile:new String[]{"","1234567890","987654321","98765432100","98765abc10"})assertFalse(LoginPolicy.validationMessage(mobile,"1234").isEmpty());
        assertFalse(LoginPolicy.validationMessage(null,"1234").isEmpty());
    }
    @Test public void invalidPinIsRejectedBeforeHttp() {
        for(String pin:new String[]{"","123","1234567","12a4"," 1234"})assertFalse(LoginPolicy.validationMessage("9876543210",pin).isEmpty());
        assertFalse(LoginPolicy.validationMessage("9876543210",null).isEmpty());
    }
    @Test public void onlyTheSameAccountCanResumeItsPrivateDestination() {
        assertTrue(LoginPolicy.canResume("bill","account-a","account-a"));
        assertFalse(LoginPolicy.canResume("bill","account-a","account-b"));
        assertFalse(LoginPolicy.canResume("bill","","account-a"));
        assertFalse(LoginPolicy.canResume("bill",null,"account-a"));
    }
    @Test public void AuthenticationPagesNeverResumeAfterLogin() {
        for(String route:new String[]{"","welcome","login","admin-login","treasurer-login","resident-login","session-expired"})assertFalse(LoginPolicy.canResume(route,"account-a","account-a"));
        assertFalse(LoginPolicy.canResume(null,"account-a","account-a"));
    }
}
