package com.rapolus.apartmentpilotai.data;

/** Pure client validation; server validation and authentication remain authoritative. */
public final class RegistrationPolicy {
    private RegistrationPolicy() {}
    public static String person(String name,String mobile,String pin,boolean consent,boolean admin) {
        if(!consent)return admin?"Confirm you are authorised to manage this apartment.":"Confirm your agreement to join.";
        if(name==null||name.trim().isEmpty()||name.trim().length()>80)return "Enter your name (up to 80 characters).";
        return LoginPolicy.validationMessage(mobile,pin);
    }
    public static String apartment(String name,String address,String pinCode,String flats) {
        if(name==null||name.trim().isEmpty()||name.trim().length()>80)return "Enter an apartment name (up to 80 characters).";
        if(address==null||address.trim().isEmpty()||address.trim().length()>300)return "Enter the address (up to 300 characters).";
        if(pinCode==null||!pinCode.matches("[0-9]{6}"))return "Enter a 6-digit PIN code.";
        try {int count=Integer.parseInt(flats);if(count<5||count>50)return "Enter between 5 and 50 flats.";}
        catch(Exception invalid) {return "Enter between 5 and 50 flats.";}
        return "";
    }
    public static String apartmentDetails(String name,String city,String address,String pinCode) {
        if(name==null||name.trim().isEmpty()||name.trim().length()>80)return "Enter an apartment name (up to 80 characters).";
        if(city==null||city.trim().isEmpty()||city.trim().length()>80)return "Enter the city (up to 80 characters).";
        if(address==null||address.trim().isEmpty()||address.trim().length()>300)return "Enter the address (up to 300 characters).";
        if(pinCode==null||!pinCode.trim().matches("[0-9]{6}"))return "Enter a 6-digit PIN code.";
        return "";
    }
}
