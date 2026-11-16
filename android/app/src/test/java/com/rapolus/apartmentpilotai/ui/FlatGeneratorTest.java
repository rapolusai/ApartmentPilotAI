package com.rapolus.apartmentpilotai.ui;
import org.json.JSONArray;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class FlatGeneratorTest {
    private static JSONArray build(List<String> blocks,int floors,int per,String style,String ground) {
        Map<String,int[]> f=new HashMap<>(),p=new HashMap<>();
        for(String b:blocks) {
            f.put(b,new int[]{floors});
            p.put(b,new int[]{per});
        }
        return FlatGenerator.generate(blocks,f,p,style,ground);
    }
    @Test public void tenFloorsOfTenMakeOneHundredFlats() {
        JSONArray g=build(List.of("A"),10,10,"plain","first");
        assertEquals(100,g.length());
        assertEquals("101",g.optJSONObject(0).optString("label"));
        assertEquals("1010",g.optJSONObject(99).optString("label"));
    }
    @Test public void blockStyleMatchesSignupPlaceholders() {
        JSONArray g=build(List.of("A"),2,5,"block","first");
        assertEquals("A-101",g.optJSONObject(0).optString("label"));
        assertEquals("A-205",g.optJSONObject(9).optString("label"));
    }
    @Test public void groundFloorAndSerialStyles() {
        assertEquals("G01",build(List.of("A"),2,2,"plain","ground").optJSONObject(0).optString("label"));
        assertEquals("T3",build(List.of("T"),2,2,"serial","first").optJSONObject(2).optString("label"));
    }
    @Test public void severalBlocksNeverClash() {
        JSONArray g=build(List.of("A","B"),3,4,"plain","first");
        Set<String> labels=new HashSet<>();
        for(int i=0;i<g.length();i++)assertTrue(labels.add(g.optJSONObject(i).optString("label")));
        assertEquals(24,labels.size());
    }
}
