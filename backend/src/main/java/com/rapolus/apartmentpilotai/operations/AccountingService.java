package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.*;
import java.math.*;
@Service public class AccountingService {
    private final Db db;
    private final Commands c;
    public AccountingService(Db db,Commands c) {
        this.db=db;
        this.c=c;
    }
    public Map<String,Object> settings(Account a) {
        var s=c.settings(a);
        if(!a.staff())return Map.of("payee",s.get("payee"),"upi",s.get("upi"),"bank",s.get("bank"),"account",s.get("account"),"ifsc",s.get("ifsc"),"bookingRules",s.get("bookingRules"),"expensesVisible",s.get("expensesVisible"),"proofRequired",s.get("proofRequired"));
        return s;
    }
    @Transactional public Object saveSettings(Account a,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        c.settings(a);
        String days=V.opt(p,"reminderDays",80);
        OperationsRules.reminderDays(days);
        String qs=V.text(p,"quietStart",5),qe=V.text(p,"quietEnd",5);
        try {
            LocalTime.parse(qs);
            LocalTime.parse(qe);
        } catch(Exception e) {
            throw new IllegalArgumentException("Quiet hours use HH:mm.");
        }
        String ifsc=V.opt(p,"ifsc",11).toUpperCase(Locale.ROOT);
        if(!ifsc.isBlank()&&!ifsc.matches("[A-Z]{4}0[A-Z0-9]{6}"))throw new IllegalArgumentException("Check the IFSC.");
        db.update("update ap_settings set payee=?,upi=?,bank=?,account=?,ifsc=?,bill_vacant=?,late_enabled=?,late_fee=?,grace_days=?,due_notify=?,reminders=?,reminder_days=?,expenses_visible=?,proof_required=?,quiet_start=?,quiet_end=?,booking_rules=? where tenant_id=?",V.opt(p,"payee",100),V.opt(p,"upi",100),V.opt(p,"bank",100),V.opt(p,"account",30),ifsc,V.bool(p,"billVacant",true),V.bool(p,"lateEnabled",false),V.zeroMoney(p,"lateFee"),V.num(p,"graceDays",0,30),V.bool(p,"dueNotify",true),V.bool(p,"reminders",true),days,V.bool(p,"expensesVisible",true),V.bool(p,"proofRequired",false),qs,qe,V.text(p,"bookingRules",2000),a.tenantId());
        c.audit(a,"SETTINGS_UPDATED",a.tenantId(),null,"Financial, notification and booking controls updated");
        return Map.of("ok",true);
    }
    @Transactional public Object opening(Account a,Map<String,Object> p) {
        a.requireAdmin();
        db.lockTenant(a.tenantId());
        var s=c.settings(a);
        LocalDate date=V.date(p,"date");
        if(date.getDayOfMonth()!=1||date.isAfter(V.today()))throw new IllegalArgumentException("Use the first day of a current or earlier month.");
        if(s.get("openingDate")!=null)throw ApiError.conflict("Opening cash is already set. Record a documented adjustment as income/expense instead.");
        var first=db.one("select min(d) as first_date from(select paid_on d from ap_payment where tenant_id=? and status='APPROVED' union all select paid_on from ap_expense where tenant_id=? and paid=true union all select received_on from ap_income where tenant_id=?) x",a.tenantId(),a.tenantId(),a.tenantId()).get("firstDate");
        if(first!=null&&date.isAfter(LocalDate.parse(first.toString())))throw new IllegalArgumentException("Opening date must not follow existing cash entries.");
        BigDecimal amount=V.zeroMoney(p,"amount");
        db.update("update ap_settings set opening_amount=?,opening_date=? where tenant_id=?",amount,V.sql(date),a.tenantId());
        c.audit(a,"OPENING_CASH_SET",a.tenantId(),null,date+" · "+amount);
        return Map.of("ok",true);
    }
    public List<Map<String,Object>> overrides(Account a) {
        a.requireStaff();
        return db.rows("select r.*,f.label as flat_label from ap_rate_override r join ap_flat f on f.id=r.flat_id where r.tenant_id=? order by r.effective_month desc",a.tenantId());
    }
    @Transactional public Object override(Account a,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"RATE_OVERRIDE",p);
        if(old.isPresent())return Map.of("id",old.get());
        UUID flat=V.id(p,"flatId");
        c.flat(a,flat);
        YearMonth ym=YearMonth.parse(V.text(p,"effective",7));
        var last=db.one("select max(billing_month) as last_month from ap_bill where tenant_id=? and flat_id=? and kind='MAINTENANCE'",a.tenantId(),flat).get("lastMonth");
        Rules.effectiveMonth(ym,YearMonth.now(ZoneId.of("Asia/Kolkata")),last==null?null:YearMonth.from(LocalDate.parse(last.toString())));
        UUID id=UUID.randomUUID();
        db.update("insert into ap_rate_override(id,tenant_id,flat_id,effective_month,amount,reason) values(?,?,?,?,?,?) on conflict(tenant_id,flat_id,effective_month) do update set amount=excluded.amount,reason=excluded.reason",id,a.tenantId(),flat,V.sql(ym.atDay(1)),V.money(p,"amount"),V.text(p,"reason",300));
        id=(UUID)db.one("select id from ap_rate_override where tenant_id=? and flat_id=? and effective_month=?",a.tenantId(),flat,V.sql(ym.atDay(1))).get("id");
        c.remember(a,"RATE_OVERRIDE",p,id);
        c.audit(a,"FLAT_RATE_SAVED",id,null,ym.toString());
        return Map.of("id",id);
    }
    public Map<String,Object> previewCharge(Account a,Map<String,Object> p) {
        a.requireStaff();
        String kind=V.choice(p,"kind","CONTRIBUTION","OPENING_DUE"),title=V.text(p,"title",100);
        LocalDate date=V.date(p,"dueDate");
        if(kind.equals("CONTRIBUTION")&&date.isBefore(V.today()))throw new IllegalArgumentException("Contribution due date cannot be in the past.");
        YearMonth month=YearMonth.parse(V.text(p,"month",7));
        BigDecimal amount=V.money(p,"amount");
        var flats=chargeFlats(a,p,kind);
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("kind",kind);
        out.put("title",title);
        out.put("amount",amount);
        out.put("month",month.toString());
        out.put("dueDate",date.toString());
        out.put("flatCount",flats.size());
        out.put("total",amount.multiply(BigDecimal.valueOf(flats.size())));
        out.put("flatLabels",String.join(", ",flats.stream().map(f->f.get("label").toString()).toList()));
        return out;
    }
    @Transactional public Object charge(Account a,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"SPECIAL_CHARGE",p);
        if(old.isPresent())return Map.of("id",old.get());
        String kind=V.choice(p,"kind","CONTRIBUTION","OPENING_DUE"),title=V.text(p,"title",100);
        LocalDate date=V.date(p,"dueDate");
        if(kind.equals("CONTRIBUTION")&&date.isBefore(V.today()))throw new IllegalArgumentException("Contribution due date cannot be in the past.");
        YearMonth month=YearMonth.parse(V.text(p,"month",7));
        BigDecimal amount=V.money(p,"amount");
        UUID charge=UUID.randomUUID();
        var flats=chargeFlats(a,p,kind);
        for(var f:flats) {
            UUID flat=(UUID)f.get("id");
            if(kind.equals("OPENING_DUE")&&db.count("select count(*) from ap_bill where tenant_id=? and flat_id=? and kind='OPENING_DUE'",a.tenantId(),flat)>0)throw ApiError.conflict("An opening due already exists for this flat.");
            UUID bill=UUID.randomUUID();
            db.update("insert into ap_bill(id,tenant_id,flat_id,billing_month,amount,due_date,kind,charge_key,title) values(?,?,?,?,?,?,?,?,?)",bill,a.tenantId(),flat,V.sql(month.atDay(1)),amount,V.sql(date),kind,charge.toString(),title);
            db.rows("select id from ap_user where tenant_id=? and flat_id=? and status='ACTIVE'",a.tenantId(),flat).forEach(u->db.notify(a.tenantId(),(UUID)u.get("id"),"BILL:"+bill,title,"New apartment charge: "+amount,"bill:"+bill));
        }
        c.remember(a,"SPECIAL_CHARGE",p,charge);
        c.audit(a,kind,charge,null,title+" · "+flats.size()+" flats");
        return Map.of("id",charge,"created",flats.size());
    }
    private List<Map<String,Object>> chargeFlats(Account a,Map<String,Object> p,String kind) {
        UUID only=V.optionalId(p,"flatId");
        String labels=V.opt(p,"flatLabels",500);
        if(only!=null&&!labels.isEmpty())throw new IllegalArgumentException("Choose either one flat or a flat list, not both.");
        List<Map<String,Object>> flats=new ArrayList<>();
        if(only!=null)flats.add(db.find("select id,label from ap_flat where tenant_id=? and id=? and active=true",a.tenantId(),only).orElseThrow(()->new IllegalArgumentException("Choose a valid active flat.")));
        else if(!labels.isEmpty()) {
            LinkedHashSet<String> selected=new LinkedHashSet<>();
            for(String label:labels.split(",")) {
                String clean=label.trim();
                if(clean.isEmpty()||clean.length()>20)throw new IllegalArgumentException("Enter valid comma-separated flat labels.");
                selected.add(clean.toUpperCase(Locale.ROOT));
            }
            if(selected.isEmpty()||selected.size()>50)throw new IllegalArgumentException("Select between 1 and 50 active flats.");
            for(String label:selected)flats.add(db.find("select id,label from ap_flat where tenant_id=? and upper(label)=? and active=true",a.tenantId(),label).orElseThrow(()->new IllegalArgumentException("Choose valid active flats.")));
        } else if(!"SELECTED".equals(V.opt(p,"scope",20)))flats.addAll(db.rows("select id,label from ap_flat where tenant_id=? and active=true order by label",a.tenantId()));
        if(flats.isEmpty())throw new IllegalArgumentException("No active flats selected.");
        if(kind.equals("OPENING_DUE")&&flats.size()!=1)throw new IllegalArgumentException("Opening dues must identify one flat.");
        return flats;
    }
    public List<Map<String,Object>> income(Account a) {
        a.requireStaff();
        return db.rows("select * from ap_income where tenant_id=? order by received_on desc",a.tenantId());
    }
    @Transactional public Object income(Account a,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"INCOME",p);
        if(old.isPresent())return Map.of("id",old.get());
        LocalDate date=V.date(p,"receivedOn");
        if(date.isAfter(V.today()))throw new IllegalArgumentException("Income cannot be future-dated.");
        checkOpening(a,date);
        UUID id=UUID.randomUUID();
        db.update("insert into ap_income(id,tenant_id,title,amount,received_on,mode,created_by) values(?,?,?,?,?,?,?)",id,a.tenantId(),V.text(p,"title",100),V.money(p,"amount"),V.sql(date),V.choice(p,"mode","CASH","UPI","BANK_TRANSFER","CHEQUE"),a.id());
        c.remember(a,"INCOME",p,id);
        c.audit(a,"INCOME_RECORDED",id,null,p.get("title").toString());
        return Map.of("id",id);
    }
    @Transactional public Object createExpense(Account a,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"EXPENSE_CREATE",p);
        if(old.isPresent())return Map.of("id",old.get());
        LocalDate date=V.date(p,"paidOn");
        if(date.isAfter(V.today()))throw new IllegalArgumentException("Expense cannot be future-dated.");
        boolean paid=V.bool(p,"paid",false);
        if(paid)checkOpening(a,date);
        UUID id=UUID.randomUUID();
        db.update("insert into ap_expense(id,tenant_id,title,category,amount,paid_on,paid,public,mode,notes,created_by,request_key) values(?,?,?,?,?,?,?,?,?,?,?,?)",id,a.tenantId(),V.text(p,"title",100),V.text(p,"category",30),V.money(p,"amount"),V.sql(date),paid,V.bool(p,"visibleToResidents",true),V.choice(p,"mode","CASH","UPI","BANK_TRANSFER","CHEQUE"),V.opt(p,"notes",500),a.id(),V.id(p,"requestKey"));
        c.remember(a,"EXPENSE_CREATE",p,id);
        c.audit(a,"EXPENSE_CREATED",id,null,(paid?"Paid · ":"Draft · ")+p.get("title"));
        return Map.of("id",id);
    }
    public Map<String,Object> expense(Account a,UUID id) {
        var e=db.one("select * from ap_expense where tenant_id=? and id=?",a.tenantId(),id);
        if(!a.staff()&&(!Boolean.TRUE.equals(e.get("public"))||!Boolean.TRUE.equals(e.get("paid"))||!Boolean.TRUE.equals(c.settings(a).get("expensesVisible"))))throw ApiError.forbidden();
        return e;
    }
    @Transactional public Object editExpense(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var e=expense(a,id);
        var old=c.previous(a,"EXPENSE_EDIT",p);
        if(old.isPresent())return Map.of("id",old.get());
        if(Boolean.TRUE.equals(e.get("paid")))throw ApiError.conflict("Paid expenses cannot be overwritten. Use a reversal.");
        LocalDate date=V.date(p,"paidOn");
        if(date.isAfter(V.today()))throw new IllegalArgumentException("Expense cannot be future-dated.");
        if(V.bool(p,"paid",false))checkOpening(a,date);
        db.update("update ap_expense set title=?,category=?,amount=?,paid_on=?,paid=?,public=?,mode=?,notes=? where tenant_id=? and id=?",V.text(p,"title",100),V.text(p,"category",30),V.money(p,"amount"),V.sql(date),V.bool(p,"paid",false),V.bool(p,"visibleToResidents",true),V.choice(p,"mode","CASH","UPI","BANK_TRANSFER","CHEQUE"),V.opt(p,"notes",500),a.tenantId(),id);
        c.remember(a,"EXPENSE_EDIT",p,id);
        c.audit(a,"EXPENSE_EDITED",id,"Draft",V.bool(p,"paid",false)?"Paid":"Draft");
        return Map.of("id",id);
    }
    @Transactional public Object reverseExpense(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var e=expense(a,id);
        var old=c.previous(a,"EXPENSE_REVERSE",p);
        if(old.isPresent())return Map.of("id",old.get());
        if(!Boolean.TRUE.equals(e.get("paid"))||e.get("reversedOn")!=null)throw ApiError.conflict("Only an unreversed paid expense can be reversed.");
        if(!V.bool(p,"verified",false))throw new IllegalArgumentException("Confirm the adjustment or returned funds.");
        String reason=V.text(p,"reason",300);
        db.update("update ap_expense set reversed_on=?,reversal_reason=? where tenant_id=? and id=?",V.sql(V.today()),reason,a.tenantId(),id);
        c.remember(a,"EXPENSE_REVERSE",p,id);
        c.audit(a,"EXPENSE_REVERSED",id,e.get("amount").toString(),reason);
        return Map.of("id",id);
    }
    @Transactional public Object reversePayment(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"PAYMENT_REVERSE",p);
        if(old.isPresent())return Map.of("id",old.get());
        var payment=db.one("select * from ap_payment where tenant_id=? and id=? and status='APPROVED'",a.tenantId(),id);
        if(!V.bool(p,"verified",false))throw new IllegalArgumentException("Verify the correction/refund first.");
        String reason=V.text(p,"reason",300);
        if(db.count("select count(*) from ap_payment_reversal where tenant_id=? and payment_id=?",a.tenantId(),id)>0)throw ApiError.conflict("Payment already reversed.");
        db.update("insert into ap_payment_reversal(id,tenant_id,payment_id,reversed_on,amount,reason,created_by) values(?,?,?,?,?,?,?)",UUID.randomUUID(),a.tenantId(),id,V.sql(V.today()),payment.get("amount"),reason,a.id());
        db.notify(a.tenantId(),(UUID)payment.get("submittedBy"),"REVERSAL:"+id,"Payment reversed",reason,"payment:"+id);
        c.remember(a,"PAYMENT_REVERSE",p,id);
        c.audit(a,"PAYMENT_REVERSED",id,payment.get("amount").toString(),reason);
        return Map.of("id",id);
    }
    public List<Map<String,Object>> recurring(Account a) {
        a.requireStaff();
        return db.rows("select * from ap_recurring where tenant_id=? order by next_on",a.tenantId());
    }
    @Transactional public Object recurring(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"RECURRING",p);
        if(old.isPresent())return Map.of("id",old.get());
        String title=V.text(p,"title",100),category=V.text(p,"category",30),frequency=V.choice(p,"frequency","MONTHLY","QUARTERLY","HALF_YEARLY","YEARLY");
        LocalDate next=V.date(p,"nextOn");
        BigDecimal amount=V.money(p,"amount");
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_recurring values(?,?,?,?,?,?,?,?,?)",id,a.tenantId(),title,category,amount,frequency,V.sql(next),V.bool(p,"active",true),a.id());
        } else {
            db.one("select id from ap_recurring where tenant_id=? and id=?",a.tenantId(),id);
            db.update("update ap_recurring set title=?,category=?,amount=?,frequency=?,next_on=?,active=? where tenant_id=? and id=?",title,category,amount,frequency,V.sql(next),V.bool(p,"active",true),a.tenantId(),id);
        }
        c.remember(a,"RECURRING",p,id);
        c.audit(a,"RECURRING_SAVED",id,null,title);
        return Map.of("id",id);
    }
    private void checkOpening(Account a,LocalDate date) {
        if(db.count("select count(*) from ap_settings where tenant_id=? and opening_date>?",a.tenantId(),V.sql(date))>0)throw new IllegalArgumentException("Date must not precede the opening balance.");
    }
    public List<Map<String,Object>> cashbook(Account a,YearMonth month) {
        a.requireStaff();
        var start=V.sql(month.atDay(1));
        var end=V.sql(month.plusMonths(1).atDay(1));
        return db.rows("select * from (select p.id,'PAYMENT' as kind,p.paid_on as date,('Maintenance · '||f.label)::text as title,p.amount from ap_payment p join ap_bill b on b.id=p.bill_id join ap_flat f on f.id=b.flat_id where p.tenant_id=? and p.status='APPROVED' union all select id,'INCOME',received_on,title,amount from ap_income where tenant_id=? union all select id,'EXPENSE',paid_on,title,-amount from ap_expense where tenant_id=? and paid=true union all select id,'EXPENSE_REVERSAL',reversed_on,title,amount from ap_expense where tenant_id=? and reversed_on is not null union all select id,'PAYMENT_REVERSAL',reversed_on,reason,-amount from ap_payment_reversal where tenant_id=?) entries where date>=? and date<? order by date,id",a.tenantId(),a.tenantId(),a.tenantId(),a.tenantId(),a.tenantId(),start,end);
    }
}
