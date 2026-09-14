package com.rapolus.apartmentpilotai.domain;
import java.time.*;
import java.util.*;
/** Pure domain rules shared by service code and executable tests. Intervals are [start,end). */ public final class OperationsRules {
    private OperationsRules() {
    }
    public record Reservation(Instant start,Instant end,int units) {
        public Reservation {
            if(start==null||end==null||!end.isAfter(start)||units<1)throw new IllegalArgumentException("Invalid reservation.");
        }
    }
    public static void window(Instant start,Instant end,Instant now) {
        if(start==null||end==null||!end.isAfter(start))throw new IllegalArgumentException("End time must follow start time.");
        if(start.isBefore(now))throw new IllegalArgumentException("Choose a future start time.");
        if(Duration.between(start,end).compareTo(Duration.ofDays(7))>0)throw new IllegalArgumentException("A reservation can last at most seven days.");
    }
    public static boolean overlaps(Instant a,Instant b,Instant c,Instant d) {
        return a.isBefore(d)&&c.isBefore(b);
    }
    public static int peak(Instant start,Instant end,List<Reservation> slots) {
        if(!end.isAfter(start))throw new IllegalArgumentException("Invalid time range.");
        TreeMap<Instant,Integer> changes=new TreeMap<>();
        for(Reservation r:slots)if(overlaps(start,end,r.start,r.end)) {
            Instant s=r.start.isAfter(start)?r.start:start,e=r.end.isBefore(end)?r.end:end;
            changes.merge(s,r.units,Integer::sum);
            changes.merge(e,-r.units,Integer::sum);
        }
        int count=0,max=0;
        for(int d:changes.values()) {
            count+=d;
            max=Math.max(max,count);
        }
        return max;
    }
    public static void capacity(int capacity,int requested,int peak) {
        if(capacity<1||requested<1||requested>capacity||peak<0||peak+requested>capacity)throw new IllegalArgumentException("Space is no longer available for the requested period.");
    }
    public static LocalDate next(LocalDate date,String frequency) {
        return switch(frequency) {
            case "MONTHLY"->date.plusMonths(1);
            case "QUARTERLY"->date.plusMonths(3);
            case "HALF_YEARLY"->date.plusMonths(6);
            case "YEARLY"->date.plusYears(1);
            default->throw new IllegalArgumentException("Choose a supported frequency.");
        };
    }
    public static void ticketTransition(String from,String to,boolean staff) {
        if(from.equals(to))return;
        Set<String> allowed=switch(from) {
            case "OPEN"->Set.of("ASSIGNED","IN_PROGRESS","RESOLVED");
            case "ASSIGNED"->Set.of("IN_PROGRESS","RESOLVED");
            case "IN_PROGRESS"->Set.of("RESOLVED");
            case "RESOLVED"->Set.of("CLOSED","OPEN");
            case "CLOSED"->Set.of("OPEN");
            default->Set.of();
        };
        if(!allowed.contains(to)||!staff&&!(from.equals("RESOLVED")&&(to.equals("CLOSED")||to.equals("OPEN"))))throw new IllegalArgumentException("That issue status change is not permitted.");
    }
    public static int planPrice(int units) {
        if(units<5||units>50)throw new IllegalArgumentException("This release supports 5–50 flats.");
        return units<=10?99:units<=30?149:249;
    }
    public static boolean withinFirstMonth(LocalDate start,LocalDate end,LocalDate today) {
        return !today.isBefore(start)&&today.isBefore(end);
    }
    public static SortedSet<Integer> reminderDays(String input) {
        SortedSet<Integer> days=new TreeSet<>();
        if(input==null||input.isBlank())return days;
        for(String p:input.split(",")) {
            int n;
            try {
                n=Integer.parseInt(p.trim());
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Reminder days must be comma-separated numbers.");
            }
            if(n<1||n>28)throw new IllegalArgumentException("Reminder days must be 1–28.");
            days.add(n);
        }
        return days;
    }
    public static String csv(String input) {
        String s=input==null?"":input;
        String stripped=s.stripLeading();
        if(!stripped.isEmpty()&&"=+-@\t\r\n".indexOf(stripped.charAt(0))>=0)s="'"+s;
        return "\""+s.replace("\"","\"\"")+"\"";
    }
    public static boolean quiet(LocalTime now,LocalTime start,LocalTime end) {
        if(start.equals(end))return false;
        return start.isBefore(end)?!now.isBefore(start)&&now.isBefore(end):!now.isBefore(start)||now.isBefore(end);
    }
}
