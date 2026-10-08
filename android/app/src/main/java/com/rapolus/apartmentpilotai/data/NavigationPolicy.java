package com.rapolus.apartmentpilotai.data;

/** Native tab selection follows the frozen screen manifest, including deep links. */
public final class NavigationPolicy {
    public enum Group { HOME, MONEY, ISSUES, COMMUNITY, MORE }
    private NavigationPolicy() {}
    public static Group group(String route,String record) {
        if(route==null)return Group.MORE;
        if(route.equals("attachments")) {
            if(record!=null&&(record.startsWith("PAYMENT:")||record.startsWith("EXPENSE:")))return Group.MONEY;
            if(record!=null&&record.startsWith("TICKET:"))return Group.ISSUES;
            return Group.COMMUNITY;
        }
        switch(route) {
            case "home": return Group.HOME;
            case "apartment", "apartment-edit", "blocks", "block-form", "flats", "flat", "flat-form", "residents", "resident", "join-requests", "join-review", "join-reject", "invite", "invite-settings", "handover", "roles", "role-form", "member-form", "committee-form", "committee-handover": return Group.COMMUNITY;
            case "community", "notices", "notice", "notice-form", "notice-attachment", "vendors", "vendor", "vendor-form", "contacts", "contact", "contact-form", "documents", "document", "document-form", "events", "event-types", "event-form", "event-availability", "event-review", "event-submitted", "event", "booking-requests", "booking-review", "booking-alternative", "event-alternative", "booking-reject", "event-cancel", "event-complete", "event-calendar", "parking-availability", "resources", "resource", "resource-form", "resource-blocks", "resource-block-form", "parking-share", "booking-rules", "booking-rules-edit", "members", "member", "committee", "committee-member", "notice-templates", "notice-preview", "notice-delivery", "polls", "poll-form", "poll", "vehicles", "vehicle", "vehicle-form", "vehicle-contact": return Group.COMMUNITY;
            case "money", "dues", "bill", "payee", "mark-paid", "payment-status", "approvals", "payment", "payment-reverse", "proof", "bulk-approval", "bulk-review", "payment-reject", "receipt", "history", "outstanding", "reminder-preview", "billing-settings", "bill-preview", "expenses", "expense", "expense-reverse", "expense-proof", "expense-form", "recurring", "recurring-detail", "recurring-form", "income", "income-form", "rate-history", "rate-overrides", "rate-override-form", "contributions", "contribution-form", "contribution-preview", "contribution", "opening-dues", "opening-due-form": return Group.MONEY;
            case "reports", "expense-report", "collection-report", "transparency", "export-report", "cashbook": return Group.MONEY;
            case "issues", "issue-categories", "issue-form", "similar-issues", "issue", "affected", "issue-assign", "issue-update", "issue-comments", "issue-photo", "complaints", "complaint-form", "complaint", "complaint-update", "services", "service", "service-form", "service-complete", "service-history", "complaint-photo": return Group.ISSUES;
            case "payments": return Group.MONEY;
            case "ticket", "ticket-update": return Group.ISSUES;
            case "directory", "directory-detail", "member-record", "booking", "bookings", "availability": return Group.COMMUNITY;
            default: return Group.MORE;
        }
    }
}
