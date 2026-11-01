package com.rapolus.apartmentpilotai.ui;
import static org.junit.Assert.*;
import org.junit.Test;
public class RiskyLabelTest {
    @Test public void destructiveActionsAreRisky() {
        for(String s:new String[]{"Delete my account","Reject","Reject payment","Sign out","Remove block","End old access","Revoke","Cancel booking","Reverse payment"})assertTrue(s,Ui.risky(s));
    }
    @Test public void ordinaryLabelsAndFiltersAreNot() {
        for(String s:new String[]{"Rejected","Approve resident","Save","Add expense","Pending","Ends soon","Endorse",""})assertFalse(s,Ui.risky(s));
        assertFalse(Ui.risky(null));
    }
}
