package com.rapolus.apartmentpilotai.ui;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.Test;
public class MaintenancePlanTest {
    @Test public void positionIsTheFlatsPlaceOnItsFloor() {
        assertEquals("01",MaintenancePlan.position("A-101"));
        assertEquals("06",MaintenancePlan.position("506"));
        assertEquals("03",MaintenancePlan.position("A-1003"));
        assertEquals("A1",MaintenancePlan.position("A1"));
        assertEquals("Shop",MaintenancePlan.position("Shop"));
    }
    @Test public void theMostCommonSizeBecomesTheApartmentAmount() {
        int[] sizeOf= {0,1,1,1,0};
        assertEquals(1,MaintenancePlan.baseSize(sizeOf,2));
        assertEquals(0,MaintenancePlan.baseSize(new int[] {0,1},2));
    }
    @Test public void onlyFlatsWithADifferentAmountGetTheirOwnRate() {
        int[] sizeOf= {0,1,1,2};
        BigDecimal[] amounts= {new BigDecimal("2500"),new BigDecimal("3200"),new BigDecimal("3200.00")};
        assertEquals(List.of(0),MaintenancePlan.overrides(sizeOf,amounts,1));
        assertEquals(0,new BigDecimal("12100").compareTo(MaintenancePlan.total(sizeOf,amounts)));
    }
}
