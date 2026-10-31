package com.rapolus.apartmentpilotai.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.ToDoubleFunction;

/** Pure text rules for generated reports: Indian money format, amount in words, month labels and word wrapping. */
public final class ReportText {
    private ReportText() {}

    /** Parses a server amount; anything unreadable is treated as no amount rather than failing the export. */
    public static BigDecimal amount(Object value) {
        if(value==null)return BigDecimal.ZERO;
        try {
            return new BigDecimal(String.valueOf(value).trim());
        } catch(NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /** 124500 -> "₹1,24,500.00"; -2500 -> "−₹2,500.00" (Indian digit grouping, two decimals). */
    public static String money(BigDecimal value) {
        return (value.signum()<0?"−":"")+"₹"+plain(value);
    }

    /** Indian grouping without the symbol, for table columns: 124500 -> "1,24,500.00". */
    public static String plain(BigDecimal value) {
        String digits=value.abs().setScale(2,RoundingMode.HALF_UP).toPlainString();
        String whole=digits.substring(0,digits.indexOf('.')),paise=digits.substring(digits.indexOf('.'));
        StringBuilder out=new StringBuilder();
        int n=whole.length();
        for(int i=0;i<n;i++) {
            int fromEnd=n-i;
            out.append(whole.charAt(i));
            if(fromEnd>1&&(fromEnd==4||(fromEnd>4&&(fromEnd-4)%2==0)))out.append(',');
        }
        return out+paise;
    }

    private static final String[] ONES={"","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen"};
    private static final String[] TENS={"","","twenty","thirty","forty","fifty","sixty","seventy","eighty","ninety"};

    /** 3000 -> "Three thousand rupees only"; 1250.50 -> "One thousand two hundred fifty rupees and fifty paise only". */
    public static String words(BigDecimal value) {
        BigDecimal v=value.abs().setScale(2,RoundingMode.HALF_UP);
        long rupees=v.longValue();
        int paise=v.remainder(BigDecimal.ONE).movePointRight(2).intValue();
        String text=(rupees==0?"zero":below(rupees))+(rupees==1?" rupee":" rupees")+(paise>0?" and "+below(paise)+" paise":"")+" only";
        return Character.toUpperCase(text.charAt(0))+text.substring(1);
    }

    private static String below(long n) {
        StringBuilder out=new StringBuilder();
        long[] units={10000000L,100000L,1000L,100L};
        String[] names={"crore","lakh","thousand","hundred"};
        for(int i=0;i<units.length;i++) {
            if(n>=units[i]) {
                append(out,below(n/units[i])+" "+names[i]);
                n%=units[i];
            }
        }
        if(n>=20) {
            append(out,TENS[(int)(n/10)]+(n%10>0?"-"+ONES[(int)(n%10)]:""));
        } else if(n>0) append(out,ONES[(int)n]);
        return out.toString();
    }

    private static void append(StringBuilder out,String part) {
        if(out.length()>0)out.append(' ');
        out.append(part);
    }

    /** "2026-10" or "2026-10-01" -> "October 2026"; anything else unchanged. */
    public static String month(String value) {
        if(value==null)return "";
        try {
            return YearMonth.parse(value.length()>=7?value.substring(0,7):value).format(DateTimeFormatter.ofPattern("MMMM uuuu",Locale.ENGLISH));
        } catch(RuntimeException e) {
            return value;
        }
    }

    /** "PAYMENT_REVERSAL" -> "Payment reversal". */
    public static String label(String code) {
        if(code==null||code.isEmpty())return "";
        if(code.equalsIgnoreCase("UPI"))return "UPI";
        String t=code.replaceAll("([a-z])([A-Z])","$1 $2").replace('_',' ').toLowerCase(Locale.ROOT).trim();
        return Character.toUpperCase(t.charAt(0))+t.substring(1);
    }

    /**
     * Breaks text into lines no wider than {@code width}, at spaces where possible. A single word wider than the
     * line is split by characters so nothing runs past the column or gets cut off.
     */
    public static List<String> wrap(String text,double width,ToDoubleFunction<String> measure) {
        List<String> lines=new ArrayList<>();
        for(String paragraph:(text==null?"":text).split("\n",-1)) {
            String line="";
            for(String word:paragraph.trim().split("\\s+")) {
                if(word.isEmpty())continue;
                String candidate=line.isEmpty()?word:line+" "+word;
                if(measure.applyAsDouble(candidate)<=width) {
                    line=candidate;
                    continue;
                }
                if(!line.isEmpty())lines.add(line);
                line=word;
                while(measure.applyAsDouble(line)>width&&line.length()>1) {
                    int cut=line.length()-1;
                    while(cut>1&&measure.applyAsDouble(line.substring(0,cut))>width)cut--;
                    lines.add(line.substring(0,cut));
                    line=line.substring(cut);
                }
            }
            lines.add(line);
        }
        return lines;
    }
}
