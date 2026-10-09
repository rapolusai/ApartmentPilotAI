package com.rapolus.apartmentpilotai.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class TextScaleTest {
    @Test public void phonesKeepReferenceSizes() {
        assertEquals(11f,TextScale.size(11,411),0f);
        assertEquals(36f,TextScale.size(36,599),0f);
    }
    @Test public void tabletsGetLargerSmallText() {
        assertEquals(13f,TextScale.size(11,800),0f);
        assertEquals(14f,TextScale.size(12,600),0f);
        assertEquals(16f,TextScale.size(15,800),0f);
        assertEquals(25f,TextScale.size(25,800),0f);
    }
}
