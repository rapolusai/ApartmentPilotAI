package com.rapolus.apartmentpilotai.finance;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.domain.Rules;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service public class FinanceService {
    public static final ZoneId ZONE=ZoneId.of("Asia/Kolkata");
    private final Db db;
    public FinanceService(Db db) {
        this.db=db;
    }
    public static YearMonth currentMonth() {
        return YearMonth.now(ZONE);
    }
    private BigDecimal balance(UUID tenant,UUID bill) {
        return db.decimal("select b.amount+b.late_fee-coalesce((select sum(p.amount) from ap_payment p where p.tenant_id=b.tenant_id and p.bill_id=b.id and p.status='APPROVED' and not exists(select 1 from ap_payment_reversal rv where rv.payment_id=p.id)),0) from ap_bill b where b.tenant_id=? and b.id=?",tenant,bill);
    }
    @Transactional public Map<String,Object> setRule(Account a,Requests.Rule r) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        YearMonth effective=YearMonth.parse(r.effective());
        String max=(String)db.one("select max(billing_month) as last_month from ap_bill where tenant_id=? and kind='MAINTENANCE'",a.tenantId()).get("lastMonth");
        Rules.effectiveMonth(effective,currentMonth(),max==null?null:YearMonth.from(LocalDate.parse(max)));
        Rules.dueDate(effective,r.billingDay(),r.dueDay());
        BigDecimal amount=Rules.money(r.amount());
        db.update("insert into ap_billing_rule(id,tenant_id,effective_month,amount,billing_day,due_day) values(?,?,?,?,?,?) on conflict(tenant_id,effective_month) do update set amount=excluded.amount,billing_day=excluded.billing_day,due_day=excluded.due_day",UUID.randomUUID(),a.tenantId(),Date.valueOf(effective.atDay(1)),amount,r.billingDay(),r.dueDay());
        db.audit(a.tenantId(),a.id(),"MAINTENANCE_RULE_SAVED",effective,null,amount.toPlainString());
        return Map.of("effective",effective.toString(),"amount",amount);
    }
    public List<Map<String,Object>> rules(Account a) {
        a.requireStaff();
        return db.rows("select effective_month,amount,billing_day,due_day from ap_billing_rule where tenant_id=? order by effective_month desc",a.tenantId());
    }
    @Transactional public Map<String,Object> generate(Account a,YearMonth month) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var rule=db.find("select amount,billing_day,due_day from ap_billing_rule where tenant_id=? and effective_month<=? order by effective_month desc limit 1",a.tenantId(),Date.valueOf(month.atDay(1))).orElseThrow(()->ApiError.conflict("Configure maintenance for this month first."));
        db.update("insert into ap_settings(tenant_id) values(?) on conflict do nothing",a.tenantId());
        var settings=db.one("select * from ap_settings where tenant_id=?",a.tenantId());
        LocalDate due=Rules.dueDate(month,((Number)rule.get("billingDay")).intValue(),((Number)rule.get("dueDay")).intValue());
        int generated=0;
        for(var flat:db.rows("select f.id,f.label,coalesce((select r.amount from ap_rate_override r where r.tenant_id=f.tenant_id and r.flat_id=f.id and r.effective_month<=? order by r.effective_month desc limit 1),?) as rate from ap_flat f where f.tenant_id=? and f.active=true and (f.occupied=true or ?=true) order by f.label",Date.valueOf(month.atDay(1)),rule.get("amount"),a.tenantId(),settings.get("billVacant"))) {
            UUID id=UUID.randomUUID(),fid=(UUID)flat.get("id");
            BigDecimal penalty=Boolean.TRUE.equals(settings.get("lateEnabled"))?(BigDecimal)settings.get("lateFee"):BigDecimal.ZERO;
            int n=db.update("insert into ap_bill(id,tenant_id,flat_id,billing_month,amount,due_date,penalty_amount,penalty_grace) values(?,?,?,?,?,?,?,?) on conflict(tenant_id,flat_id,billing_month,charge_key) do nothing",id,a.tenantId(),fid,Date.valueOf(month.atDay(1)),flat.get("rate"),Date.valueOf(due),penalty,settings.get("graceDays"));
            if(n==1) {
                generated++;
                if(Boolean.TRUE.equals(settings.get("dueNotify")))db.rows("select id from ap_user where tenant_id=? and flat_id=? and status='ACTIVE'",a.tenantId(),fid).forEach(u->db.notify(a.tenantId(),(UUID)u.get("id"),"BILL:"+id,"Maintenance ready",month+" maintenance is ready.","bill:"+id));
            }
        }
        if(generated>0)db.audit(a.tenantId(),a.id(),"BILLS_GENERATED",month,null,Integer.toString(generated));
        return Map.of("created",generated,"month",month.toString());
    }
    public Map<String,Object> billingPreview(Account a,YearMonth month) {
        a.requireStaff();
        var rule=db.find("select amount,billing_day,due_day from ap_billing_rule where tenant_id=? and effective_month<=? order by effective_month desc limit 1",a.tenantId(),Date.valueOf(month.atDay(1)));
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("month",month.toString());
        out.put("existing",db.count("select count(*) from ap_bill where tenant_id=? and billing_month=? and kind='MAINTENANCE'",a.tenantId(),Date.valueOf(month.atDay(1))));
        if(rule.isEmpty()) {
            out.put("configured",false);
            return out;
        }
        boolean billVacant=db.find("select bill_vacant from ap_settings where tenant_id=?",a.tenantId()).map(s->Boolean.TRUE.equals(s.get("billVacant"))).orElse(true);
        var flats=db.rows("select f.id,f.label,coalesce((select r.amount from ap_rate_override r where r.tenant_id=f.tenant_id and r.flat_id=f.id and r.effective_month<=? order by r.effective_month desc limit 1),?) as rate from ap_flat f where f.tenant_id=? and f.active=true and (f.occupied=true or ?=true) order by f.label",Date.valueOf(month.atDay(1)),rule.get().get("amount"),a.tenantId(),billVacant);
        BigDecimal scheduled=flats.stream().map(f->(BigDecimal)f.get("rate")).reduce(BigDecimal.ZERO,BigDecimal::add);
        int billingDay=((Number)rule.get().get("billingDay")).intValue(),dueDay=((Number)rule.get().get("dueDay")).intValue();
        out.put("configured",true);
        out.put("flats",flats.size());
        out.put("baseRate",rule.get().get("amount"));
        out.put("scheduled",scheduled);
        out.put("billingDay",billingDay);
        out.put("dueDay",dueDay);
        out.put("dueDate",Rules.dueDate(month,billingDay,dueDay).toString());
        out.put("billVacant",billVacant);
        return out;
    }
    public Map<String,Object> reminderPreview(Account a,YearMonth month) {
        a.requireStaff();
        var rows=reminderRows(a,month);
        BigDecimal total=rows.stream().map(r->(BigDecimal)r.get("outstanding")).reduce(BigDecimal.ZERO,BigDecimal::add);
        long recipients=rows.stream().mapToLong(r->((Number)r.get("recipientCount")).longValue()).sum();
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("month",month.toString());
        out.put("flatCount",rows.size());
        out.put("recipientCount",recipients);
        out.put("outstanding",total);
        out.put("flats",rows);
        out.put("title","Maintenance reminder");
        out.put("message","Please pay the outstanding amount and submit payment details in the app.");
        out.put("externalDeliveryStatus","NOT_CONFIGURED");
        return out;
    }
    @Transactional public Map<String,Object> sendReminders(Account a,YearMonth month,UUID requestKey) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        boolean enabled=db.find("select reminders from ap_settings where tenant_id=?",a.tenantId()).map(s->Boolean.TRUE.equals(s.get("reminders"))).orElse(true);
        if(!enabled)throw ApiError.conflict("Enable maintenance reminders in notification settings first.");
        var rows=reminderRows(a,month);
        int created=0,eligible=0;
        for(var row:rows)for(var user:db.rows("select id from ap_user where tenant_id=? and flat_id=? and status='ACTIVE'",a.tenantId(),row.get("flatId"))) {
            eligible++;
            created+=db.update("insert into ap_inbox(id,tenant_id,recipient_id,event_key,title,body,target) values(?,?,?,?,?,?,?) on conflict(recipient_id,event_key) do nothing",UUID.randomUUID(),a.tenantId(),user.get("id"),"MANUAL-REMINDER:"+requestKey,"Maintenance reminder · "+month,"Please pay "+row.get("outstanding")+" and submit payment details in the app.","dues");
        }
        if(created>0)db.audit(a.tenantId(),a.id(),"MAINTENANCE_REMINDERS_SENT",requestKey,null,month+" · "+created+" in-app recipients");
        return Map.of("created",created,"alreadySent",eligible-created,"eligible",eligible,"month",month.toString(),"externalDeliveryStatus","NOT_CONFIGURED");
    }
    private List<Map<String,Object>> reminderRows(Account a,YearMonth month) {
        return db.rows("select f.id as flat_id,f.label as flat_label,sum(b.amount+b.late_fee-coalesce((select sum(p.amount) from ap_payment p where p.tenant_id=b.tenant_id and p.bill_id=b.id and p.status='APPROVED' and not exists(select 1 from ap_payment_reversal rv where rv.payment_id=p.id)),0)) as outstanding,count(*) as bill_count,(select count(*) from ap_user u where u.tenant_id=f.tenant_id and u.flat_id=f.id and u.status='ACTIVE') as recipient_count from ap_bill b join ap_flat f on f.id=b.flat_id where b.tenant_id=? and b.billing_month=? and not exists(select 1 from ap_payment p where p.tenant_id=b.tenant_id and p.bill_id=b.id and p.status='PENDING') and b.amount+b.late_fee>coalesce((select sum(p.amount) from ap_payment p where p.tenant_id=b.tenant_id and p.bill_id=b.id and p.status='APPROVED' and not exists(select 1 from ap_payment_reversal rv where rv.payment_id=p.id)),0) group by f.id,f.label,f.tenant_id order by f.label",a.tenantId(),Date.valueOf(month.atDay(1)));
    }
    public List<Map<String,Object>> bills(Account a,YearMonth month) {
        String filter=a.staff()?"":" and b.flat_id=?";
        Object[] args=a.staff()?new Object[] {
            a.tenantId(),Date.valueOf(month.atDay(1))
        }
        :new Object[] {
            a.tenantId(),Date.valueOf(month.atDay(1)),a.flatId()
        };
        return db.rows("select b.id,b.flat_id,f.label as flat_label,b.billing_month,b.amount+b.late_fee as amount,b.amount as base_amount,b.late_fee,b.kind,b.title,b.due_date,coalesce((select sum(p.amount) from ap_payment p where p.tenant_id=b.tenant_id and p.bill_id=b.id and p.status='APPROVED' and not exists(select 1 from ap_payment_reversal rv where rv.payment_id=p.id)),0) as paid,exists(select 1 from ap_payment p where p.tenant_id=b.tenant_id and p.bill_id=b.id and p.status='PENDING') as in_review from ap_bill b join ap_flat f on f.id=b.flat_id where b.tenant_id=? and b.billing_month=?"+filter+" order by f.label",args);
    }
    public Map<String,Object> bill(Account a,UUID id) {
        var b=ownedBill(a,id);
        b.put("paid",((BigDecimal)b.get("amount")).subtract(balance(a.tenantId(),id)));
        b.put("inReview",db.count("select count(*) from ap_payment where tenant_id=? and bill_id=? and status='PENDING'",a.tenantId(),id)>0);
        return b;
    }
    private Map<String,Object> ownedBill(Account a,UUID id) {
        var b=db.one("select b.id,b.flat_id,f.label as flat_label,b.amount+b.late_fee as amount,b.amount as base_amount,b.late_fee,b.kind,b.title,b.billing_month,b.due_date from ap_bill b join ap_flat f on f.id=b.flat_id where b.tenant_id=? and b.id=?",a.tenantId(),id);
        if(!a.staff()&&!Objects.equals(a.flatId(),b.get("flatId")))throw ApiError.forbidden();
        return b;
    }
    @Transactional public Map<String,Object> submit(Account a,Requests.Payment r) {
        db.lockTenant(a.tenantId());
        var b=ownedBill(a,r.billId());
        var old=db.find("select * from ap_payment where tenant_id=? and submitted_by=? and request_key=?",a.tenantId(),a.id(),r.requestKey());
        String ref=r.reference()==null||r.reference().isBlank()?null:r.reference().trim().toUpperCase(Locale.ROOT);
        if(old.isPresent()) {
            var p=old.get();
            if(!p.get("billId").equals(r.billId())||((BigDecimal)p.get("amount")).compareTo(r.amount())!=0||!p.get("mode").equals(r.mode())||!Objects.equals(p.get("reference"),ref)||!p.get("paidOn").equals(r.paidOn().toString()))throw ApiError.conflict("This request key was already used with different payment details.");
            return Map.of("id",p.get("id"),"status",p.get("status"));
        }
        Rules.verifyPayment(r.amount(),balance(a.tenantId(),r.billId()),r.paidOn(),LocalDate.now(ZONE));
        checkOpening(a.tenantId(),r.paidOn());
        if(!r.mode().equals("CASH")&&ref==null)throw new IllegalArgumentException("Enter the transaction or cheque reference.");
        if(db.count("select count(*) from ap_payment where tenant_id=? and bill_id=? and status='PENDING'",a.tenantId(),r.billId())>0)throw ApiError.conflict("This bill already has a payment under review.");
        UUID id=UUID.randomUUID();
        db.update("insert into ap_payment(id,tenant_id,bill_id,amount,mode,reference,paid_on,note,submitted_by,request_key) values(?,?,?,?,?,?,?,?,?,?)",id,a.tenantId(),r.billId(),Rules.money(r.amount()),r.mode(),ref,Date.valueOf(r.paidOn()),r.note()==null?"":r.note().trim(),a.id(),r.requestKey());
        db.audit(a.tenantId(),a.id(),"PAYMENT_SUBMITTED",id,null,"Pending "+r.amount());
        db.notifyStaff(a.tenantId(),"PAYMENT:"+id,"Payment to review",b.get("flatLabel")+" · "+r.amount(),"payment:"+id);
        return Map.of("id",id,"status","PENDING");
    }
    public List<Map<String,Object>> payments(Account a) {
        Object[] args=a.staff()?new Object[] {
            a.tenantId()
        }
        :new Object[] {
            a.tenantId(),a.flatId()
        };
        return db.rows("select p.id,p.bill_id,f.label as flat_label,b.billing_month,p.amount,p.mode,p.reference,p.paid_on,p.note,p.status,p.reason,p.receipt_number,p.reviewed_at,exists(select 1 from ap_payment_reversal rv where rv.payment_id=p.id) as reversed from ap_payment p join ap_bill b on b.id=p.bill_id join ap_flat f on f.id=b.flat_id where p.tenant_id=?"+(a.staff()?"":" and b.flat_id=?")+" order by p.submitted_at desc",args);
    }
    @Transactional public Map<String,Object> approve(Account a,Requests.Review r) {
        a.requireStaff();
        if(!r.verified())throw new IllegalArgumentException("Confirm you verified every selected payment.");
        db.lockTenant(a.tenantId());
        List<UUID> ids=r.paymentIds().stream().distinct().sorted().toList();
        if(ids.size()!=r.paymentIds().size())throw new IllegalArgumentException("Duplicate payment selection.");
        List<Map<String,Object>> candidates=new ArrayList<>();
        Map<UUID,BigDecimal> totals=new HashMap<>();
        for(UUID id:ids) {
            var p=db.one("select * from ap_payment where tenant_id=? and id=? for update",a.tenantId(),id);
            if(p.get("status").equals("REJECTED"))throw ApiError.conflict("A selected payment was rejected. Refresh the list.");
            if(p.get("status").equals("APPROVED"))continue;
            // Idempotent retry, never posts another receipt.
            if(!p.get("mode").equals("CASH")&&db.count("select count(*) from ap_settings where tenant_id=? and proof_required=true",a.tenantId())>0&&db.count("select count(*) from ap_file where tenant_id=? and parent_kind='PAYMENT' and parent_id=?",a.tenantId(),id)==0)throw ApiError.conflict("A selected payment requires proof before approval.");
            checkOpening(a.tenantId(),LocalDate.parse(p.get("paidOn").toString()));
            UUID bill=(UUID)p.get("billId");
            totals.merge(bill,(BigDecimal)p.get("amount"),BigDecimal::add);
            candidates.add(p);
        }
        for(var e:totals.entrySet())if(e.getValue().compareTo(balance(a.tenantId(),e.getKey()))>0)throw ApiError.conflict("The selected total exceeds a bill balance. No payments were approved.");
        for(var p:candidates) {
            UUID id=(UUID)p.get("id");
            String receipt="AP-"+id.toString().toUpperCase(Locale.ROOT);
            db.update("update ap_payment set status='APPROVED',reviewed_by=?,reviewed_at=now(),receipt_number=? where tenant_id=? and id=?",a.id(),receipt,a.tenantId(),id);
            db.audit(a.tenantId(),a.id(),"PAYMENT_APPROVED",id,"Pending","Approved "+p.get("amount"));
            db.notify(a.tenantId(),(UUID)p.get("submittedBy"),"RECEIPT:"+id,"Payment approved","Your maintenance receipt is ready.","receipt:"+id);
            db.rows("select u.id from ap_user u join ap_bill b on b.tenant_id=u.tenant_id and b.flat_id=u.flat_id where b.id=? and u.tenant_id=? and u.status='ACTIVE'",p.get("billId"),a.tenantId()).forEach(u->db.notify(a.tenantId(),(UUID)u.get("id"),"RECEIPT:"+id,"Payment approved","Your maintenance receipt is ready.","receipt:"+id));
        }
        return Map.of("approved",candidates.size(),"alreadyApproved",ids.size()-candidates.size());
    }
    @Transactional public void reject(Account a,UUID id,String reason) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var p=db.one("select submitted_by from ap_payment where tenant_id=? and id=? and status='PENDING'",a.tenantId(),id);
        db.update("update ap_payment set status='REJECTED',reviewed_by=?,reviewed_at=now(),reason=? where tenant_id=? and id=?",a.id(),reason,a.tenantId(),id);
        db.audit(a.tenantId(),a.id(),"PAYMENT_REJECTED",id,"Pending",reason);
        db.notify(a.tenantId(),(UUID)p.get("submittedBy"),"REJECTED:"+id,"Payment needs correction",reason,"payment:"+id);
    }
    public Map<String,Object> receipt(Account a,UUID id) {
        var p=db.one("select p.id,p.bill_id,p.amount,p.mode,p.paid_on,p.receipt_number,p.reviewed_at,b.flat_id,f.label as flat_label,b.billing_month,t.name as apartment_name,u.name as approved_by,exists(select 1 from ap_payment_reversal rv where rv.payment_id=p.id) as reversed from ap_payment p join ap_bill b on b.id=p.bill_id join ap_flat f on f.id=b.flat_id join ap_tenant t on t.id=p.tenant_id join ap_user u on u.id=p.reviewed_by where p.tenant_id=? and p.id=? and p.status='APPROVED'",a.tenantId(),id);
        if(!a.staff()&&!Objects.equals(a.flatId(),p.get("flatId")))throw ApiError.forbidden();
        return p;
    }
    @Transactional public Map<String,Object> expense(Account a,Requests.Expense r) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        if(r.paid())checkOpening(a.tenantId(),r.paidOn());
        if(r.paidOn().isAfter(LocalDate.now(ZONE)))throw new IllegalArgumentException("Expense date cannot be in the future.");
        var old=db.find("select id,title,category,amount,paid_on,paid,public from ap_expense where tenant_id=? and created_by=? and request_key=?",a.tenantId(),a.id(),r.requestKey());
        if(old.isPresent()) {
            var e=old.get();
            if(!e.get("title").equals(r.title().trim())||!e.get("category").equals(r.category())||((BigDecimal)e.get("amount")).compareTo(r.amount())!=0||!e.get("paidOn").equals(r.paidOn().toString())||!e.get("paid").equals(r.paid())||!e.get("public").equals(r.visibleToResidents()))throw ApiError.conflict("This request key was used with different expense details.");
            return Map.of("id",e.get("id"));
        }
        UUID id=UUID.randomUUID();
        db.update("insert into ap_expense(id,tenant_id,title,category,amount,paid_on,paid,public,created_by,request_key) values(?,?,?,?,?,?,?,?,?,?)",id,a.tenantId(),r.title().trim(),r.category(),Rules.money(r.amount()),Date.valueOf(r.paidOn()),r.paid(),r.visibleToResidents(),a.id(),r.requestKey());
        db.audit(a.tenantId(),a.id(),"EXPENSE_CREATED",id,null,r.paid()?"Paid "+r.amount():"Draft "+r.amount());
        return Map.of("id",id);
    }
    public List<Map<String,Object>> expenses(Account a,YearMonth month) {
        return db.rows("select id,title,category,amount,paid_on,paid,reversed_on,public,mode,notes from ap_expense where tenant_id=? and paid_on>=? and paid_on<?"+(a.staff()?"":" and public=true and paid=true and coalesce((select expenses_visible from ap_settings where tenant_id=ap_expense.tenant_id),true)")+" order by paid_on desc",a.tenantId(),Date.valueOf(month.atDay(1)),Date.valueOf(month.plusMonths(1).atDay(1)));
    }
    @Transactional(readOnly=true,isolation=org.springframework.transaction.annotation.Isolation.REPEATABLE_READ) public Map<String,Object> summary(Account a,YearMonth month) {
        Date start=Date.valueOf(month.atDay(1)),end=Date.valueOf(month.plusMonths(1).atDay(1));
        UUID t=a.tenantId();
        if(!a.staff()&&db.count("select count(*) from ap_settings where tenant_id=? and coalesce(expenses_visible,true)=true",t)==0)throw ApiError.forbidden();
        var m=db.one("select coalesce(sum(amount+late_fee),0) as billed,count(*) as bill_count from ap_bill where tenant_id=? and billing_month=?",t,start);
        Map<String,Object> out=new LinkedHashMap<>(m);
        BigDecimal allocated=db.decimal("select coalesce(sum(p.amount),0) from ap_payment p join ap_bill b on b.id=p.bill_id where p.tenant_id=? and p.status='APPROVED' and b.billing_month=? and not exists(select 1 from ap_payment_reversal r where r.payment_id=p.id)",t,start);
        BigDecimal received=sumDate("ap_payment","paid_on","amount"," and status='APPROVED'",t,start,end);
        BigDecimal spent=sumDate("ap_expense","paid_on","amount"," and paid=true",t,start,end);
        BigDecimal other=sumDate("ap_income","received_on","amount","",t,start,end);
        BigDecimal expenseReturns=sumDate("ap_expense","reversed_on","amount","",t,start,end);
        BigDecimal refunds=sumDate("ap_payment_reversal","reversed_on","amount","",t,start,end);
        BigDecimal initial=db.decimal("select coalesce((select opening_amount from ap_settings where tenant_id=? and opening_date<=?),0)",t,start);
        BigDecimal beforeIn=before("ap_payment","paid_on"," and status='APPROVED'",t,start).add(before("ap_income","received_on","",t,start)).add(before("ap_expense","reversed_on","",t,start));
        BigDecimal beforeOut=before("ap_expense","paid_on"," and paid=true",t,start).add(before("ap_payment_reversal","reversed_on","",t,start));
        BigDecimal opening=initial.add(beforeIn).subtract(beforeOut),closing=opening.add(received).add(other).add(expenseReturns).subtract(spent).subtract(refunds);
        out.put("collectedForBills",allocated);
        out.put("outstanding",((BigDecimal)m.get("billed")).subtract(allocated));
        out.put("received",received);
        out.put("spent",spent);
        out.put("otherIncome",other);
        out.put("expenseReturns",expenseReturns);
        out.put("refunds",refunds);
        out.put("opening",opening);
        out.put("closing",closing);
        out.put("balance",closing);
        out.put("month",month.toString());
        if(a.staff())out.put("pendingPayments",db.count("select count(*) from ap_payment where tenant_id=? and status='PENDING'",t));
        return out;
    }
    private void checkOpening(UUID tenant,LocalDate date) {
        if(db.count("select count(*) from ap_settings where tenant_id=? and opening_date>?",tenant,Date.valueOf(date))>0)throw new IllegalArgumentException("Payment date must not precede the reconciled opening balance date.");
    }
    // These identifiers are internal constants, never request parameters.
    private BigDecimal sumDate(String table,String date,String amount,String predicate,UUID tenant,Date start,Date end) {
        return db.decimal("select coalesce(sum("+amount+"),0) from "+table+" where tenant_id=? and "+date+">=? and "+date+"<?"+predicate,tenant,start,end);
    }
    private BigDecimal before(String table,String date,String predicate,UUID tenant,Date start) {
        return db.decimal("select coalesce(sum(amount),0) from "+table+" where tenant_id=? and "+date+"<?"+predicate,tenant,start);
    }
}
