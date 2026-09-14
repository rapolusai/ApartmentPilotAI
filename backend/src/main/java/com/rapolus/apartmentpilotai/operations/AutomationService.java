package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.domain.OperationsRules;
import com.rapolus.apartmentpilotai.finance.FinanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
@Service public class AutomationService {
    private final Db db;
    private final Commands c;
    private final FinanceService finance;
    private final NoticeService notices;
    public AutomationService(Db db,Commands c,FinanceService finance,NoticeService notices) {
        this.db=db;
        this.c=c;
        this.finance=finance;
        this.notices=notices;
    }
    @Transactional public Map<String,Object> run(Account a) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        LocalDate today=V.today();
        YearMonth month=YearMonth.from(today);
        var s=c.settings(a);
        int drafts=0,reminded=0,fees=0,bills=0,published=0;
        var rule=db.find("select billing_day from ap_billing_rule where tenant_id=? and effective_month<=? order by effective_month desc limit 1",a.tenantId(),V.sql(month.atDay(1)));
        if(rule.isPresent()&&((Number)rule.get().get("billingDay")).intValue()<=today.getDayOfMonth())bills=((Number)finance.generate(a,month).get("created")).intValue();
        for(var r:db.rows("select * from ap_recurring where tenant_id=? and active=true and next_on<=?",a.tenantId(),V.sql(today))) {
            LocalDate next=LocalDate.parse(r.get("nextOn").toString());
            int cycles=0;
            while(!next.isAfter(today)&&cycles++<12) {
                int inserted=db.update("insert into ap_expense(id,tenant_id,title,category,amount,paid_on,paid,public,created_by,request_key,recurring_id,recurring_on) values(?,?,?,?,?,?,false,true,?,?,?,?) on conflict do nothing",UUID.randomUUID(),a.tenantId(),r.get("title"),r.get("category"),r.get("amount"),V.sql(next),a.id(),UUID.randomUUID(),r.get("id"),V.sql(next));
                drafts+=inserted;
                next=OperationsRules.next(next,r.get("frequency").toString());
            }
            db.update("update ap_recurring set next_on=? where tenant_id=? and id=?",V.sql(next),a.tenantId(),r.get("id"));
        }
        // Penalty settings are snapped on each bill. Later configuration changes never rewrite old bills.
        for(var b:db.rows("select b.id,b.penalty_amount from ap_bill b where b.tenant_id=? and b.penalty_amount>0 and b.late_applied_at is null and b.due_date+b.penalty_grace<? and not exists(select 1 from ap_payment p where p.bill_id=b.id and p.status='PENDING') and b.amount>coalesce((select sum(p.amount) from ap_payment p where p.bill_id=b.id and p.status='APPROVED' and not exists(select 1 from ap_payment_reversal r where r.payment_id=p.id)),0)",a.tenantId(),V.sql(today))) {
            fees+=db.update("update ap_bill set late_fee=penalty_amount,late_applied_at=now() where id=? and late_applied_at is null",b.get("id"));
            c.audit(a,"LATE_FEE_APPLIED",(UUID)b.get("id"),null,b.get("penaltyAmount").toString());
        }
        if(!OperationsRules.quiet(LocalTime.now(ZoneId.of("Asia/Kolkata")),LocalTime.parse(s.get("quietStart").toString()),LocalTime.parse(s.get("quietEnd").toString()))&&Boolean.TRUE.equals(s.get("reminders"))&&OperationsRules.reminderDays(s.get("reminderDays").toString()).contains(today.getDayOfMonth())) {
            for(var b:db.rows("select b.id,b.flat_id,b.title from ap_bill b where b.tenant_id=? and b.billing_month<=? and not exists(select 1 from ap_payment p where p.bill_id=b.id and p.status='PENDING') and b.amount+b.late_fee>coalesce((select sum(p.amount) from ap_payment p where p.bill_id=b.id and p.status='APPROVED' and not exists(select 1 from ap_payment_reversal r where r.payment_id=p.id)),0)",a.tenantId(),V.sql(month.atDay(1))))for(var u:db.rows("select id from ap_user where tenant_id=? and flat_id=? and status='ACTIVE'",a.tenantId(),b.get("flatId"))) {
                db.notify(a.tenantId(),(UUID)u.get("id"),"REMINDER:"+b.get("id")+":"+today,"Maintenance reminder",b.get("title")+" is still outstanding.","bill:"+b.get("id"));
                reminded++;
            }
        }
        for(var n:db.rows("select id from ap_notice where tenant_id=? and status='DRAFT' and schedule_confirmed=true and scheduled_at is not null and scheduled_at<=now()",a.tenantId())) {
            notices.publish(a,(UUID)n.get("id"));
            published++;
        }
        for(var svc:db.rows("select id,title,next_on from ap_service where tenant_id=? and active=true and next_on<=?",a.tenantId(),V.sql(today.plusDays(5))))db.notifyStaff(a.tenantId(),"SERVICE-DUE:"+svc.get("id")+":"+svc.get("nextOn"),"Service due",svc.get("title")+" · "+svc.get("nextOn"),"service:"+svc.get("id"));
        int completed=0;
        for(var booking:db.rows("select id,created_by,title from ap_booking where tenant_id=? and status='APPROVED' and ends_at<=now()",a.tenantId())) {
            db.update("update ap_booking set status='COMPLETED',revision=revision+1 where id=?",booking.get("id"));
            db.update("insert into ap_booking_event(id,tenant_id,booking_id,actor_id,status,note) values(?,?,?,?,'COMPLETED','Reservation window ended')",UUID.randomUUID(),a.tenantId(),booking.get("id"),a.id());
            db.notify(a.tenantId(),(UUID)booking.get("createdBy"),"BOOKING-END:"+booking.get("id"),"Event reservation ended",booking.get("title").toString(),"booking:"+booking.get("id"));
            completed++;
        }
        var result=Map.<String,Object>of("billsCreated",bills,"expenseDraftsCreated",drafts,"lateFeesApplied",fees,"reminderRecipientsConsidered",reminded,"noticesPublished",published,"bookingsCompleted",completed);
        c.audit(a,"AUTOMATION_RUN",a.tenantId(),null,result.toString());
        return result;
    }
}
