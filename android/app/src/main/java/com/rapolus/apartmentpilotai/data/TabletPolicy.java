package com.rapolus.apartmentpilotai.data;

/** Pure rules for the tablet list + detail layout. Phones never show the side list. */
public final class TabletPolicy {
    private TabletPolicy() {}
    /** Width at which a sibling list fits beside a detail page (Android's tablet breakpoint). */
    public static final int SPLIT_MIN_WIDTH_DP=600;
    public static boolean split(int screenWidthDp) {
        return screenWidthDp>=SPLIT_MIN_WIDTH_DP;
    }
    /**
     * Server list shown beside a Wave 1 detail page, or null when the page has no side list.
     * Each path is one the signed-in role can already read on the phone layout.
     */
    public static String sideListPath(String page,boolean staff,boolean admin) {
        if(page==null)return null;
        switch(page) {
            case "flat": return admin?"/ops/flats":null;
            case "join-review": case "member": return admin?"/members":null;
            case "payment": return staff?"/payments":null;
            case "ticket": return "/ops/tickets?kind=ISSUE";
            case "notice": return "/notices";
            default: return null;
        }
    }
    /** Heading for the side list. */
    public static String sideListTitle(String page) {
        switch(page) {
            case "flat": return "Flats";
            case "join-review": case "member": return "Residents";
            case "payment": return "Payments";
            case "ticket": return "Issues & requests";
            case "notice": return "Notices";
            default: return "";
        }
    }
}
