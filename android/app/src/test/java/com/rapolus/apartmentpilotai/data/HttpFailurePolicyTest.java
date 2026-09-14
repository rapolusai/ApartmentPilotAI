package com.rapolus.apartmentpilotai.data;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public final class HttpFailurePolicyTest {
    @Test public void authenticatedFailuresUseDedicatedStates() {
        assertEquals(HttpFailurePolicy.Destination.SESSION_EXPIRED,HttpFailurePolicy.destination(401,true));
        assertEquals(HttpFailurePolicy.Destination.ACCESS_DENIED,HttpFailurePolicy.destination(403,true));
        assertEquals(HttpFailurePolicy.Destination.NOT_FOUND,HttpFailurePolicy.destination(404,true));
        assertEquals(HttpFailurePolicy.Destination.ERROR,HttpFailurePolicy.destination(503,true));
        assertEquals(HttpFailurePolicy.Destination.OFFLINE,HttpFailurePolicy.destination(0,true));
    }

    @Test public void authenticationAndValidationFailuresStayInline() {
        assertEquals(HttpFailurePolicy.Destination.INLINE,HttpFailurePolicy.destination(401,false));
        assertEquals(HttpFailurePolicy.Destination.INLINE,HttpFailurePolicy.destination(403,false));
        assertEquals(HttpFailurePolicy.Destination.INLINE,HttpFailurePolicy.destination(404,false));
        assertEquals(HttpFailurePolicy.Destination.INLINE,HttpFailurePolicy.destination(400,true));
        assertEquals(HttpFailurePolicy.Destination.INLINE,HttpFailurePolicy.destination(409,true));
    }
}
