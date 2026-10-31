package com.rapolus.apartmentpilotai.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Server timestamps are UTC instants; residents read dates and times in India Standard Time. */
public final class DateText {
    private DateText() {}
    public static final ZoneId IST=ZoneId.of("Asia/Kolkata");
    private static final Pattern INSTANT=Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(?::\\d{2}(?:\\.\\d{1,9})?)?Z");
    // Locale.ROOT has no month names on Android ("M10"), so use English explicitly.
    private static final DateTimeFormatter DAY=DateTimeFormatter.ofPattern("d MMM uuuu",Locale.ENGLISH);
    private static final DateTimeFormatter TIME=DateTimeFormatter.ofPattern("d MMM uuuu, h:mm a",Locale.ENGLISH);

    public static String format(Instant instant) {
        return instant.atZone(IST).format(TIME)+" IST";
    }
    /** Replaces every UTC instant inside the text with its IST date and time; other text is unchanged. */
    public static String humanize(String text) {
        if(text==null||text.indexOf('T')<0)return text;
        Matcher m=INSTANT.matcher(text);
        if(!m.find())return text;
        StringBuffer out=new StringBuffer();
        do {
            String replacement;
            try {replacement=format(Instant.parse(m.group()));}
            catch(RuntimeException unparsable) {replacement=m.group();}
            m.appendReplacement(out,Matcher.quoteReplacement(replacement));
        } while(m.find());
        m.appendTail(out);
        return out.toString();
    }
    /** Calendar date in IST for an instant, or a plain YYYY-MM-DD date, e.g. "9 Oct 2026". */
    public static String day(String value) {
        if(value==null||value.isEmpty())return value==null?"":value;
        try {
            if(INSTANT.matcher(value).matches())return Instant.parse(value).atZone(IST).format(DAY);
            return LocalDate.parse(value.substring(0,10)).format(DAY);
        } catch(RuntimeException unparsable) {
            return value;
        }
    }
}
