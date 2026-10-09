package com.rapolus.apartmentpilotai.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class TabletPolicyTest {
    @Test public void splitOnlyFromTabletWidth() {
        assertFalse(TabletPolicy.split(411));
        assertFalse(TabletPolicy.split(599));
        assertTrue(TabletPolicy.split(600));
        assertTrue(TabletPolicy.split(777));
        assertTrue(TabletPolicy.split(1164));
    }
    @Test public void sideListsFollowExistingRoleAccess() {
        assertEquals("/ops/flats",TabletPolicy.sideListPath("flat",true,true));
        assertNull(TabletPolicy.sideListPath("flat",true,false));
        assertEquals("/members",TabletPolicy.sideListPath("join-review",true,true));
        assertNull(TabletPolicy.sideListPath("join-review",true,false));
        assertEquals("/members",TabletPolicy.sideListPath("member",true,true));
        assertNull(TabletPolicy.sideListPath("member",false,false));
        assertEquals("/payments",TabletPolicy.sideListPath("payment",true,false));
        assertNull(TabletPolicy.sideListPath("payment",false,false));
        assertEquals("/ops/tickets?kind=ISSUE",TabletPolicy.sideListPath("ticket",false,false));
        assertEquals("/notices",TabletPolicy.sideListPath("notice",false,false));
    }
    @Test public void listAndFormPagesHaveNoSideList() {
        for(String page:new String[]{"home","flats","payments","flat-form","notice-form","more",null})assertNull(TabletPolicy.sideListPath(page,true,true));
    }
}
