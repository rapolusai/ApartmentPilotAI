package com.rapolus.apartmentpilotai.data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

/** Pure rules for the ApartmentCare team dashboard: labels, search and sort order. */
public final class TeamDashboard {
    private TeamDashboard() {}
    public enum Sort { NAME, REVENUE, LAST_SIGN_IN, STATUS }

    public static String statusLabel(String state) {
        if("ACTIVE".equals(state))return "Active";
        if("TRIAL".equals(state))return "Trial";
        return "Payment due";
    }
    /** Apartments that need attention first: payment due, then trial, then active. */
    public static int statusRank(String state) {
        if("ACTIVE".equals(state))return 2;
        if("TRIAL".equals(state))return 1;
        return 0;
    }
    /** Case-insensitive match on any field; a search of digits also matches inside a mobile number. */
    public static boolean matches(String query,String... values) {
        if(query==null||query.trim().isEmpty())return true;
        String q=query.trim().toLowerCase(Locale.ROOT);
        for(String v:values)if(v!=null&&v.toLowerCase(Locale.ROOT).contains(q))return true;
        return false;
    }
    /** "Today", "Yesterday", "5 days ago", or "Not recorded" when nobody has signed in since sign-in times were kept. */
    public static String lastSeen(String instant,Instant now) {
        if(instant==null||instant.isEmpty()||"null".equals(instant))return "Not recorded";
        try {
            long days=Duration.between(Instant.parse(instant),now).toDays();
            if(days<=0)return "Today";
            if(days==1)return "Yesterday";
            return days+" days ago";
        } catch(RuntimeException unparsable) {
            return "Not recorded";
        }
    }
    /** Sort order between two rows; ties fall back to the apartment name. */
    public static int compare(Sort sort,String nameA,String nameB,BigDecimal revenueA,BigDecimal revenueB,String lastA,String lastB,String stateA,String stateB) {
        int byName=String.CASE_INSENSITIVE_ORDER.compare(nameA==null?"":nameA,nameB==null?"":nameB);
        int result;
        switch(sort) {
            case REVENUE: result=revenueB.compareTo(revenueA); break;
            // ISO-8601 instants sort as text; never signed in goes last.
            case LAST_SIGN_IN: result=empty(lastA)&&empty(lastB)?0:empty(lastA)?1:empty(lastB)?-1:lastB.compareTo(lastA); break;
            case STATUS: result=Integer.compare(statusRank(stateA),statusRank(stateB)); break;
            default: result=0;
        }
        return result!=0?result:byName;
    }
    private static boolean empty(String v) {
        return v==null||v.isEmpty()||"null".equals(v);
    }
    /** Phone numbers are shown as 98480 12345 for reading aloud; the dialer gets the plain digits. */
    public static String spacedMobile(String mobile) {
        if(mobile==null||!mobile.matches("[0-9]{10}"))return mobile==null||"null".equals(mobile)?"No active Admin":mobile;
        return mobile.substring(0,5)+" "+mobile.substring(5);
    }
}
