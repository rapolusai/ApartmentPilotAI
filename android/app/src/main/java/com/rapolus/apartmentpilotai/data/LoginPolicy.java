package com.rapolus.apartmentpilotai.data;

/** Pure rules for native validation and account-bound session recovery. */
public final class LoginPolicy {
    private LoginPolicy() {}
    public static String routeForRole(String role) {
        if("ADMIN".equals(role))return "admin-login";
        if("TREASURER".equals(role))return "treasurer-login";
        return "resident-login";
    }
    public static String validationMessage(String mobile,String pin) {
        if(mobile==null||!mobile.matches("[6-9][0-9]{9}"))return "Enter a valid 10-digit mobile number.";
        if(pin==null||!pin.matches("[0-9]{4,6}"))return "Enter your PIN with 4 to 6 digits.";
        return "";
    }
    public static boolean canResume(String route,String expectedAccount,String signedInAccount) {
        if(route==null||route.isEmpty()||expectedAccount==null||expectedAccount.isEmpty())return false;
        if(!expectedAccount.equals(signedInAccount))return false;
        switch(route) {
            case "login": case "admin-login": case "treasurer-login": case "resident-login":
            case "welcome": case "session-expired": return false;
            default: return true;
        }
    }
}
