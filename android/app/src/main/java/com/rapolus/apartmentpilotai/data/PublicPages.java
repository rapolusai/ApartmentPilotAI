package com.rapolus.apartmentpilotai.data;
/** Addresses of the server's public pages (privacy policy and account deletion), derived from the API base URL. */
public final class PublicPages {
    private PublicPages() {
    }
    /** "http://host:8080/api/v1" or "https://api.example.in/api/v1/" becomes "http://host:8080". */
    public static String root(String apiBase) {
        String s=apiBase==null?"":apiBase.trim();
        while(s.endsWith("/"))s=s.substring(0,s.length()-1);
        if(s.endsWith("/api/v1"))s=s.substring(0,s.length()-"/api/v1".length());
        return s;
    }
    public static String privacy(String apiBase) {
        return root(apiBase)+"/privacy";
    }
    public static String deleteAccount(String apiBase) {
        return root(apiBase)+"/delete-account";
    }
}
