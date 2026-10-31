package com.rapolus.apartmentpilotai.data;

import java.util.Locale;

/** Colour family for a status badge, so problems stand out from neutral states. */
public enum StatusTone {
    WAITING, REVIEW, UPCOMING, DONE, PROBLEM, NEUTRAL;

    public static StatusTone of(String status) {
        String s=status==null?"":status.trim().toLowerCase(Locale.ROOT).replace('_',' ');
        switch(s) {
            case "pending": case "partly paid": case "reversed": return WAITING;
            case "in review": return REVIEW;
            case "next": case "upcoming": return UPCOMING;
            case "paid": case "approved": case "verified": case "resolved": case "completed": return DONE;
            case "rejected": case "overdue": case "failed": case "not approved": return PROBLEM;
            default: return NEUTRAL;
        }
    }
}
