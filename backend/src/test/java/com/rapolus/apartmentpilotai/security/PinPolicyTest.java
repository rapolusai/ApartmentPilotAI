package com.rapolus.apartmentpilotai.security;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
final class PinPolicyTest {
    @Test void acceptsUnpredictableSixDigitPins() {
        for(String pin:new String[]{"482915","739104","205871","918273"})assertEquals(pin,PinPolicy.requireNew(pin));
    }
    @Test void rejectsWrongLengthWithTheLengthMessage() {
        for(String pin:new String[]{"1234","48291","4829150","48a915",null}) {
            assertEquals(PinPolicy.LENGTH_MESSAGE,assertThrows(IllegalArgumentException.class,()->PinPolicy.requireNew(pin)).getMessage());
        }
    }
    @Test void rejectsRepeatsRunsAndCommonPatterns() {
        for(String pin:new String[]{"000000","111111","123456","234567","654321","987654","121212","112233","123123"}) {
            assertEquals(PinPolicy.WEAK_MESSAGE,assertThrows(IllegalArgumentException.class,()->PinPolicy.requireNew(pin)).getMessage(),pin);
        }
    }
    @Test void loginFlagsShortOrWeakPinsForChange() {
        assertTrue(PinPolicy.changeRequired("4826"));
        assertTrue(PinPolicy.changeRequired("48261"));
        assertTrue(PinPolicy.changeRequired("123456"));
        assertFalse(PinPolicy.changeRequired("482915"));
    }
}
