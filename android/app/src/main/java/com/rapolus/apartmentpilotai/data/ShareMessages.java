package com.rapolus.apartmentpilotai.data;

/** Builds the plain-text messages shared from the invite and referral screens. */
public final class ShareMessages {
    /** Release package id. Fixed so debug builds (".debug" suffix) still share the public listing. */
    public static final String PLAY_PACKAGE="com.rapolus.apartmentpilotai";
    public static final String PLAY_STORE_URL="https://play.google.com/store/apps/details?id="+PLAY_PACKAGE;
    static final String ATTRIBUTION="Powered By @Rapolu`s";

    private ShareMessages() {}

    /** Message an Admin sends to flat owners and tenants with the resident invite code. */
    public static String residentInvite(String apartmentName,String code,String validUntil) {
        String apartment=blank(apartmentName)?"your apartment":apartmentName.trim();
        StringBuilder text=new StringBuilder();
        text.append("You are invited to join ").append(apartment).append(" on ApartmentCare.\n\n");
        text.append("Invite code: ").append(code).append('\n');
        if(!blank(validUntil)&&!"—".equals(validUntil))text.append("Valid until: ").append(validUntil).append('\n');
        text.append("\nHow to join:\n");
        text.append("1. Download ApartmentCare from Google Play:\n").append(PLAY_STORE_URL).append('\n');
        text.append("2. Open the app and tap \"Join with invite\".\n");
        text.append("3. Enter the invite code above and tap \"Find apartment\".\n");
        text.append("4. Enter your name and mobile number, choose Owner or Tenant, and create a PIN.\n");
        text.append("5. Choose your flat and tap \"Request to join\".\n");
        text.append("6. After the Admin approves your request, log in as Resident with your mobile number and PIN.\n\n");
        text.append("Flat access needs Admin approval. One account per flat.\n");
        text.append(ATTRIBUTION);
        return text.toString();
    }

    /** Message an Admin sends to another apartment with the first-month referral code. */
    public static String apartmentReferral(String code) {
        StringBuilder text=new StringBuilder();
        text.append("Try ApartmentCare for your apartment. My referral code: ").append(code).append("\n\n");
        text.append("How to use it:\n");
        text.append("1. Download ApartmentCare from Google Play:\n").append(PLAY_STORE_URL).append('\n');
        text.append("2. Open the app, tap \"New apartment\" and register your apartment as Admin.\n");
        text.append("3. Go to Subscription & billing, tap \"I was referred\" and enter the code above within your first month.\n\n");
        text.append("Two new independently verified apartment activations qualify the three apartments for their first month only. Terms apply.\n");
        text.append(ATTRIBUTION);
        return text.toString();
    }

    private static boolean blank(String value) {
        return value==null||value.trim().isEmpty();
    }
}
