import com.rapolus.apartmentpilotai.domain.OperationsRules;
import com.rapolus.apartmentpilotai.domain.OperationsRules.Reservation;
import java.time.*;
import java.util.*;
/** Executes real production pure rules. Does not emulate Spring, JDBC, Android or payments. */
public class OperationsChecks {
  static int assertions;
  static void check(boolean value, String title) { assertions++; if (!value) throw new AssertionError(title); }
  static void reject(Runnable action, String title) {
    assertions++;
    try { action.run(); throw new AssertionError("Expected rejection: " + title); }
    catch (IllegalArgumentException expected) { }
  }
  static Instant t(int minute) { return Instant.parse("2026-09-14T00:00:00Z").plusSeconds(60L * minute); }
  static Reservation slot(int start, int end, int units) { return new Reservation(t(start),t(end),units); }
  public static void main(String[] args) {
    check(!OperationsRules.overlaps(t(0),t(10),t(10),t(20)), "Touching bookings do not overlap");
    check(OperationsRules.overlaps(t(0),t(10),t(9),t(20)), "One minute overlap");
    check(OperationsRules.overlaps(t(0),t(10),t(1),t(2)), "Contained reservation");
    check(OperationsRules.peak(t(0),t(60),List.of())==0,"Empty capacity window");
    check(OperationsRules.peak(t(10),t(20),List.of(slot(0,10,3),slot(20,30,2)))==0,"Outside intervals excluded");
    check(OperationsRules.peak(t(0),t(60),List.of(slot(0,10,3),slot(10,20,2)))==3,"Use peak, not sum of disjoint bookings");
    check(OperationsRules.peak(t(0),t(60),List.of(slot(0,20,3),slot(10,30,2)))==5,"Concurrent quantities add");
    check(OperationsRules.peak(t(10),t(20),List.of(slot(0,30,2),slot(15,25,3)))==5,"Clip to requested range");
    reject(()->OperationsRules.peak(t(10),t(10),List.of()),"Zero duration query");
    reject(()->slot(10,10,1),"Zero duration slot"); reject(()->slot(0,10,0),"Nonpositive capacity");
    OperationsRules.capacity(5,2,3);check(true,"Exact capacity allowed");
    reject(()->OperationsRules.capacity(5,3,3),"Overlapping capacity rejected");
    reject(()->OperationsRules.capacity(0,1,0),"Invalid resource capacity");
    reject(()->OperationsRules.capacity(5,0,0),"Invalid requested capacity");
    reject(()->OperationsRules.capacity(5,1,-1),"Invalid existing occupancy");
    reject(()->OperationsRules.window(t(-1),t(30),t(0)),"Past request start");
    reject(()->OperationsRules.window(t(1),t(1),t(0)),"Zero duration request");
    reject(()->OperationsRules.window(t(1),t(1).plus(Duration.ofDays(7)).plusSeconds(1),t(0)),"Maximum event duration");
    OperationsRules.window(t(0),t(0).plus(Duration.ofDays(7)),t(0));check(true,"Seven days inclusive maximum");
    int[][] prices={{5,99},{10,99},{11,149},{30,149},{31,249},{50,249}};
    for(int[] p:prices)check(OperationsRules.planPrice(p[0])==p[1],"Tier boundary "+p[0]);
    reject(()->OperationsRules.planPrice(4),"Below supported flat count");reject(()->OperationsRules.planPrice(51),"Above supported flat count");
    LocalDate begin=LocalDate.of(2026,1,31),end=begin.plusMonths(1);
    check(end.equals(LocalDate.of(2026,2,28)),"First month anniversary clamps short month");
    check(OperationsRules.withinFirstMonth(begin,end,begin),"First day eligible");
    check(OperationsRules.withinFirstMonth(begin,end,end.minusDays(1)),"Last eligible day");
    check(!OperationsRules.withinFirstMonth(begin,end,end),"Next billing period not free");
    check(!OperationsRules.withinFirstMonth(begin,end,begin.minusDays(1)),"No pre-activation reward");
    check(OperationsRules.next(begin,"MONTHLY").equals(end),"Monthly schedule");
    check(OperationsRules.next(begin,"QUARTERLY").equals(LocalDate.of(2026,4,30)),"Quarterly schedule");
    check(OperationsRules.next(begin,"HALF_YEARLY").equals(LocalDate.of(2026,7,31)),"Six month schedule");
    check(OperationsRules.next(LocalDate.of(2024,2,29),"YEARLY").equals(LocalDate.of(2025,2,28)),"Leap year schedule");
    reject(()->OperationsRules.next(begin,"DAILY"),"Unsupported schedule");
    check(OperationsRules.reminderDays("15, 5, 5, 28").equals(new TreeSet<>(List.of(5,15,28))),"Sort and deduplicate reminders");
    check(OperationsRules.reminderDays("").isEmpty(),"Disable reminder schedule");
    reject(()->OperationsRules.reminderDays("0,3"),"Day zero");reject(()->OperationsRules.reminderDays("29"),"Unsafe short-month day");
    reject(()->OperationsRules.reminderDays("a"),"Non-numeric day");
    OperationsRules.ticketTransition("OPEN","IN_PROGRESS",true);check(true,"Staff progress transition");
    OperationsRules.ticketTransition("IN_PROGRESS","RESOLVED",true);check(true,"Staff resolution");
    OperationsRules.ticketTransition("RESOLVED","CLOSED",false);check(true,"Reporter accepts resolution");
    OperationsRules.ticketTransition("RESOLVED","OPEN",false);check(true,"Reporter reopens unresolved issue");
    reject(()->OperationsRules.ticketTransition("OPEN","CLOSED",true),"Cannot skip resolution");
    reject(()->OperationsRules.ticketTransition("OPEN","RESOLVED",false),"Resident cannot perform staff resolution");
    reject(()->OperationsRules.ticketTransition("CLOSED","OPEN",false),"Closed ticket reopening staff controlled");
    check(OperationsRules.quiet(LocalTime.of(23,0),LocalTime.of(21,0),LocalTime.of(8,0)),"Overnight quiet hours");
    check(OperationsRules.quiet(LocalTime.of(7,59),LocalTime.of(21,0),LocalTime.of(8,0)),"Overnight morning boundary");
    check(!OperationsRules.quiet(LocalTime.of(8,0),LocalTime.of(21,0),LocalTime.of(8,0)),"Quiet end exclusive");
    check(OperationsRules.quiet(LocalTime.of(13,0),LocalTime.of(12,0),LocalTime.of(14,0)),"Daytime quiet period");
    check(!OperationsRules.quiet(LocalTime.NOON,LocalTime.MIDNIGHT,LocalTime.MIDNIGHT),"Equal quiet times disable quiet hours");
    check(OperationsRules.csv("=1+1").equals("\"'=1+1\""),"Neutralise spreadsheet formula");
    check(OperationsRules.csv("  @SUM(A1)").startsWith("\"'"),"Neutralise leading-whitespace formula");
    check(OperationsRules.csv("a\"b").equals("\"a\"\"b\""),"Escape double quotes");
    check(OperationsRules.csv(null).equals("\"\""),"Null CSV cell");
    Random random=new Random(72020402L);
    for(int trial=0;trial<500;trial++){
      List<Reservation> rows=new ArrayList<>();
      for(int n=0;n<20;n++){int start=random.nextInt(160)-30;rows.add(slot(start,start+random.nextInt(40)+1,random.nextInt(5)+1));}
      int from=random.nextInt(60),to=from+random.nextInt(70)+1,brute=0;
      for(int minute=from;minute<to;minute++){Instant moment=t(minute).plusSeconds(30);int total=0;for(var row:rows)if(!moment.isBefore(row.start())&&moment.isBefore(row.end()))total+=row.units();brute=Math.max(brute,total);}
      check(OperationsRules.peak(t(from),t(to),rows)==brute,"Deterministic occupancy scenario "+trial);
    }
    System.out.println("PASS: " + assertions + " operational domain assertions (including 500 deterministic occupancy scenarios).");
  }
}
