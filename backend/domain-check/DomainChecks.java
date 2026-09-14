import com.rapolus.apartmentpilotai.domain.Rules;
import com.rapolus.apartmentpilotai.security.Tokens;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
/** Dependency-free executable checks; NOT a substitute for Spring/PostgreSQL integration tests. */
public class DomainChecks {
 static int checks=0;
 static void eq(Object a,Object b){checks++;if(!Objects.equals(a,b))throw new AssertionError(a+" != "+b);}
 static void rejected(Runnable r){checks++;try{r.run();throw new AssertionError("Expected validation failure");}catch(IllegalArgumentException expected){}}
 public static void main(String[] args){
  eq(Rules.money(new BigDecimal("2000")),new BigDecimal("2000.00"));
  eq(Rules.money(new BigDecimal("0.10")),new BigDecimal("0.10"));
  rejected(()->Rules.money(null));rejected(()->Rules.money(BigDecimal.ZERO));
  rejected(()->Rules.money(new BigDecimal("-0.01")));rejected(()->Rules.money(new BigDecimal("2.999")));
  rejected(()->Rules.money(new BigDecimal("100000000")));
  eq(Rules.outstanding(new BigDecimal("2000.00"),List.of(new BigDecimal("500"))),new BigDecimal("1500.00"));
  eq(Rules.outstanding(new BigDecimal("0.30"),List.of(new BigDecimal("0.10"),new BigDecimal("0.20"))),new BigDecimal("0.00"));
  rejected(()->Rules.outstanding(new BigDecimal("2000.00"),List.of(new BigDecimal("2001"))));
  LocalDate today=LocalDate.of(2026,9,13);
  rejected(()->Rules.verifyPayment(new BigDecimal("2001"),new BigDecimal("2000"),today,today));
  rejected(()->Rules.verifyPayment(BigDecimal.ONE,BigDecimal.TEN,today.plusDays(1),today));
  rejected(()->Rules.verifyPayment(BigDecimal.ONE,BigDecimal.TEN,null,today));
  eq(Rules.dueDate(YearMonth.of(2027,2),1,28),LocalDate.of(2027,2,28));
  rejected(()->Rules.dueDate(YearMonth.of(2026,9),10,5));rejected(()->Rules.dueDate(YearMonth.of(2026,9),1,31));
  rejected(()->Rules.effectiveMonth(YearMonth.of(2026,8),YearMonth.of(2026,9),null));
  rejected(()->Rules.effectiveMonth(YearMonth.of(2026,9),YearMonth.of(2026,9),YearMonth.of(2026,9)));
  Rules.effectiveMonth(YearMonth.of(2026,10),YearMonth.of(2026,9),YearMonth.of(2026,9));checks++;
  eq(Tokens.digest("abc"),"ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
  Set<String> tokens=new HashSet<>();for(int i=0;i<1000;i++)tokens.add(Tokens.create());eq(tokens.size(),1000);
  System.out.println("PASS: "+checks+" dependency-free domain/token checks.");
 }
}
