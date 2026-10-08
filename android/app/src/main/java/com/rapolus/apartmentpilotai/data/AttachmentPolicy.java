package com.rapolus.apartmentpilotai.data;

/** Bounded attachment handling shared by preview, open and user-selected saving. */
public final class AttachmentPolicy {
    public static final int MAX_BYTES=5*1024*1024;
    private AttachmentPolicy() {}
    public static String extension(String mime) {
        switch(mime) {
            case "application/pdf":return ".pdf";
            case "image/png":return ".png";
            case "image/jpeg":return ".jpg";
            default:throw new IllegalArgumentException("Unsupported attachment type.");
        }
    }
    public static String name(String value,String mime) {
        String clean=value==null?"":value.replaceAll("[\\\\/\\p{Cntrl}]","_").trim();
        if(clean.isEmpty()||clean.equals(".")||clean.equals(".."))clean="attachment";
        if(clean.length()>100)clean=clean.substring(0,100);
        String ext=extension(mime);
        if(!clean.toLowerCase(java.util.Locale.ROOT).endsWith(ext))clean+=ext;
        return clean;
    }
    public static void validate(byte[] bytes,String mime) {
        extension(mime);
        if(bytes==null||bytes.length<8||bytes.length>MAX_BYTES)throw new IllegalArgumentException("Attachment is empty or exceeds 5 MB.");
        boolean png=(bytes[0]&255)==137&&bytes[1]==80&&bytes[2]==78&&bytes[3]==71&&bytes[4]==13&&bytes[5]==10&&bytes[6]==26&&bytes[7]==10;
        boolean jpeg=(bytes[0]&255)==255&&(bytes[1]&255)==216&&(bytes[2]&255)==255;
        boolean pdf=bytes[0]==37&&bytes[1]==80&&bytes[2]==68&&bytes[3]==70&&bytes[4]==45;
        if(!(mime.equals("image/png")&&png||mime.equals("image/jpeg")&&jpeg||mime.equals("application/pdf")&&pdf))throw new IllegalArgumentException("Attachment contents do not match its type.");
    }
    public static int sample(int width,int height) {
        if(width<=0||height<=0)throw new IllegalArgumentException("Image cannot be decoded.");
        int sample=1;
        while(width/sample>1200||height/sample>1600)sample*=2;
        return sample;
    }
}
