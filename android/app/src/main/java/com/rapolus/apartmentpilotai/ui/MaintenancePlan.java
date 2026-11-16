package com.rapolus.apartmentpilotai.ui;
import java.math.BigDecimal;
import java.util.*;
/**
 * Maintenance by flat size, worked out on the device and saved with existing features: the most common size's
 * amount becomes the apartment's monthly amount, and every other flat gets a flat-specific rate.
 */
final class MaintenancePlan {
    private MaintenancePlan() {
    }
    /** "A-101" → "01", "1203" → "03": the flat's position on its floor. Labels without a floor number keep their own key. */
    static String position(String label) {
        if(label==null)return "";
        java.util.regex.Matcher m=java.util.regex.Pattern.compile("([0-9]+)$").matcher(label);
        if(!m.find()||m.group(1).length()<3)return label;
        return m.group(1).substring(m.group(1).length()-2);
    }
    /** Index of the size most flats have; ties go to the lower index. */
    static int baseSize(int[] sizeOfFlat,int sizes) {
        int[] count=new int[sizes];
        for(int s:sizeOfFlat)if(s>=0&&s<sizes)count[s]++;
        int best=0;
        for(int i=1; i<sizes; i++)if(count[i]>count[best])best=i;
        return best;
    }
    /** Flats (by index) that need their own rate because their size's amount differs from the base amount. */
    static List<Integer> overrides(int[] sizeOfFlat,BigDecimal[] amounts,int base) {
        List<Integer> out=new ArrayList<>();
        for(int i=0; i<sizeOfFlat.length; i++)if(amounts[sizeOfFlat[i]].compareTo(amounts[base])!=0)out.add(i);
        return out;
    }
    static BigDecimal total(int[] sizeOfFlat,BigDecimal[] amounts) {
        BigDecimal t=BigDecimal.ZERO;
        for(int s:sizeOfFlat)t=t.add(amounts[s]);
        return t;
    }
}
