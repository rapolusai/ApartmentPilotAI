package com.rapolus.apartmentpilotai.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Date and date-time form fields: people pick and read "25 Oct 2026, 5:00 PM",
 * while the value sent to the server keeps its existing format (YYYY-MM-DD or IST YYYY-MM-DDTHH:mm).
 */
public final class DateField {
    private DateField() {}
    private static final DateTimeFormatter DAY=DateTimeFormatter.ofPattern("d MMM uuuu",Locale.ENGLISH);
    private static final DateTimeFormatter TIME=DateTimeFormatter.ofPattern("d MMM uuuu, h:mm a",Locale.ENGLISH);
    private static final DateTimeFormatter LOCAL_TIME=DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm",Locale.ROOT);
    /** Format hint the label carries for typing; it is no longer shown once a picker fills the field. */
    public static boolean hasTime(String label) {
        return label!=null&&label.contains("THH:mm");
    }
    /** "Start · IST YYYY-MM-DDTHH:mm" -> "Start · IST"; "AMC expiry · optional YYYY-MM-DD" -> "AMC expiry · optional". */
    public static String label(String label) {
        if(label==null)return null;
        String l=label.replace("YYYY-MM-DDTHH:mm","").replace("YYYY-MM-DD","");
        l=l.replaceAll("\\s+"," ").replaceAll("(\\s*·\\s*)+"," · ").trim();
        if(l.endsWith("·"))l=l.substring(0,l.length()-1).trim();
        if(l.startsWith("·"))l=l.substring(1).trim();
        return l;
    }
    /** Readable text for a stored value; anything unparsable is shown unchanged so nothing is hidden. */
    public static String display(String value,boolean time) {
        if(value==null||value.trim().isEmpty())return "";
        String v=value.trim();
        try {
            if(time&&v.length()>=16)return LocalDateTime.parse(v.substring(0,16),LOCAL_TIME).format(TIME);
            return LocalDate.parse(v.length()>10?v.substring(0,10):v).format(DAY);
        } catch(RuntimeException unparsable) {
            return v;
        }
    }
    /** Server value for a picked date, and time when the field has one. */
    public static String value(int year,int month,int day,int hour,int minute,boolean time) {
        LocalDate d=LocalDate.of(year,month,day);
        return time?d.atTime(hour,minute).format(LOCAL_TIME):d.toString();
    }
}
