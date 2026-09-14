package com.rapolus.apartmentpilotai.operations;
import java.util.*;
import java.math.*;
import java.time.*;
import com.rapolus.apartmentpilotai.domain.Rules;
/** Explicit, bounded parsing for JSON commands. Tenant IDs and privileges never come from these maps. */ public final class V {
    private V() {
    }
    public static String text(Map<String,Object> m,String k,int max) {
        String s=opt(m,k,max);
        if(s.isBlank())throw new IllegalArgumentException(k+" is required.");
        return s;
    }
    public static String opt(Map<String,Object> m,String k,int max) {
        Object value=m.get(k);
        if(value==null)return "";
        if(!(value instanceof String))throw new IllegalArgumentException(k+" must be text.");
        String s=((String)value).trim();
        if(s.length()>max)throw new IllegalArgumentException(k+" is too long.");
        return s;
    }
    public static boolean bool(Map<String,Object> m,String k,boolean fallback) {
        Object v=m.get(k);
        if(v==null)return fallback;
        if(!(v instanceof Boolean))throw new IllegalArgumentException(k+" must be true or false.");
        return (Boolean)v;
    }
    public static int num(Map<String,Object> m,String k,int min,int max) {
        try {
            int n=new BigDecimal(String.valueOf(m.get(k))).intValueExact();
            if(n<min||n>max)throw new ArithmeticException();
            return n;
        } catch(RuntimeException e) {
            throw new IllegalArgumentException(k+" must be between "+min+" and "+max+".");
        }
    }
    public static UUID id(Map<String,Object> m,String k) {
        try {
            return UUID.fromString(text(m,k,36));
        } catch(RuntimeException e) {
            throw new IllegalArgumentException("Choose a valid "+k+".");
        }
    }
    public static UUID optionalId(Map<String,Object> m,String k) {
        return opt(m,k,36).isBlank()?null:id(m,k);
    }
    public static BigDecimal money(Map<String,Object> m,String k) {
        try {
            return Rules.money(new BigDecimal(String.valueOf(m.get(k))));
        } catch(RuntimeException e) {
            throw new IllegalArgumentException(k+" must be a positive amount with at most two decimals.");
        }
    }
    public static BigDecimal zeroMoney(Map<String,Object> m,String k) {
        try {
            BigDecimal v=new BigDecimal(String.valueOf(m.getOrDefault(k,0)));
            if(v.signum()<0||v.scale()>2||v.compareTo(new BigDecimal("9999999999.99"))>0)throw new ArithmeticException();
            return v.setScale(2);
        } catch(RuntimeException e) {
            throw new IllegalArgumentException("Invalid "+k+".");
        }
    }
    public static LocalDate date(Map<String,Object> m,String k) {
        try {
            return LocalDate.parse(text(m,k,10));
        } catch(RuntimeException e) {
            throw new IllegalArgumentException(k+" must be a valid date (YYYY-MM-DD).");
        }
    }
    public static LocalDate optionalDate(Map<String,Object> m,String k) {
        return opt(m,k,10).isBlank()?null:date(m,k);
    }
    public static Instant time(Map<String,Object> m,String k) {
        try {
            return Instant.parse(text(m,k,40));
        } catch(RuntimeException e) {
            throw new IllegalArgumentException(k+" must include its timezone (ISO instant).");
        }
    }
    public static Instant optionalTime(Map<String,Object> m,String k) {
        return opt(m,k,40).isBlank()?null:time(m,k);
    }
    public static String choice(Map<String,Object> m,String k,String...choices) {
        String v=text(m,k,60);
        if(!Set.of(choices).contains(v))throw new IllegalArgumentException("Invalid "+k+".");
        return v;
    }
    public static List<Map<String,Object>> items(Map<String,Object> m,String k,int max) {
        Object value=m.get(k);
        if(!(value instanceof List<?> list)||list.isEmpty()||list.size()>max)throw new IllegalArgumentException("Select 1–"+max+" "+k+".");
        List<Map<String,Object>> out=new ArrayList<>();
        for(Object v:list) {
            if(!(v instanceof Map<?,?> map))throw new IllegalArgumentException("Invalid "+k+".");
            Map<String,Object> item=new LinkedHashMap<>();
            map.forEach((a,b)->item.put(String.valueOf(a),b));
            out.add(item);
        }
        return out;
    }
    public static java.sql.Date sql(LocalDate d) {
        return d==null?null:java.sql.Date.valueOf(d);
    }
    public static java.sql.Timestamp sql(Instant d) {
        return d==null?null:java.sql.Timestamp.from(d);
    }
    public static LocalDate today() {
        return LocalDate.now(ZoneId.of("Asia/Kolkata"));
    }
    public static UUID uid(Object o) {
        return UUID.fromString(String.valueOf(o));
    }
}
