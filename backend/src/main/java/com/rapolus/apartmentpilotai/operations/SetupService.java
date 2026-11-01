package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
/**
 * Guided setup. Progress lives in the database (never only on the phone), so another device or a reinstall
 * resumes at the same step. A step is only marked done after the server re-checks the real configuration.
 */
@Service public class SetupService {
    /** apartment=true: done once per apartment and shared by every Admin. */
    public record Step(String key,String title,String why,boolean apartment) { }
    public record Item(String key,String title,String route,String target,String tip) { }
    static final List<Step> ADMIN=List.of(
        new Step("apartment","Apartment details","Residents see this name and address on every screen.",true),
        new Step("blocks","Blocks / towers","Flats are grouped by block, so residents find their flat quickly.",true),
        new Step("flats","Flat numbers","Residents pick their own flat from this list when they join, so it must match your real door numbers.",true),
        new Step("payment","Payment details","Residents see where to send maintenance money. The app never moves money itself.",true),
        new Step("maintenance","Monthly maintenance","A bill for every flat is made from this amount each month.",true),
        new Step("late_fee","Late fee & reminders","A late fee is optional. Reminders help residents pay on time.",true),
        new Step("opening_balance","Existing balance","Reports start from the money the association has today.",true),
        new Step("old_dues","Old dues","Money a flat already owed before you started using the app.",true),
        new Step("invite","Invite residents","Residents need this code to join your apartment.",true));
    static final List<Step> TREASURER=List.of(
        new Step("terms","Terms & privacy","How ApartmentCare uses your details, and how to delete your account.",false),
        new Step("treasurer_rules","Your role","Two people always handle money, so mistakes and misuse are caught.",false),
        new Step("payment_check","Payment details","Residents send money to these details. Make sure they are correct.",false),
        new Step("late_fee_check","Late fee & reminders","Check the penalty and reminder settings residents will see.",false),
        new Step("recurring","Regular expenses","Monthly costs such as watchman or electricity are recorded automatically.",false));
    static final List<Step> RESIDENT=List.of(
        new Step("terms","Terms & privacy","How ApartmentCare uses your details, and how to delete your account.",false),
        new Step("profile","Your details","Your Admin and neighbours know who lives in your flat.",false),
        new Step("vehicles","Your vehicles","Helps with parking and lets neighbours reach you about a vehicle.",false),
        new Step("how_to_pay","How to pay","See your dues and how to tell the association you have paid.",false),
        new Step("stay_informed","Stay informed","Notices and building issues, all in one place.",false));
    // Payment details are optional: many apartments collect cash and add UPI or a bank account later.
    // Any details that are entered are validated by /ops/settings/payment itself.
    static final Set<String> CONFIRM_ONLY=Set.of("payment","payment_check","late_fee","old_dues","terms","treasurer_rules","late_fee_check","recurring","vehicles","how_to_pay","stay_informed");
    private final Db db;
    public SetupService(Db db) {
        this.db=db;
    }
    public static List<Step> steps(String role) {
        return switch(role) {
            case "ADMIN" -> ADMIN;
            case "TREASURER" -> TREASURER;
            default -> RESIDENT;
        };
    }
    private Set<String> doneKeys(Account a) {
        Set<String> done=new HashSet<>();
        for(var r:db.rows("select step_key,(user_id is null) as shared from ap_setup_step where tenant_id=? and (user_id is null or user_id=?)",a.tenantId(),a.id())) {
            String key=r.get("stepKey").toString();
            // Apartment rows only count for apartment steps; personal rows only for personal steps.
            done.add((Boolean.TRUE.equals(r.get("shared"))?"apt:":"me:")+key);
        }
        return done;
    }
    private static boolean isDone(Set<String> done,Step s) {
        return done.contains((s.apartment()?"apt:":"me:")+s.key());
    }
    /** Fast check used by the request filter: true while any required step is still open. */
    @Transactional(readOnly=true) public boolean requiredOpen(Account a) {
        Set<String> done=doneKeys(a);
        for(Step s:steps(a.role()))if(!isDone(done,s))return true;
        return false;
    }
    @Transactional public Map<String,Object> state(Account a) {
        Set<String> done=doneKeys(a);
        List<Step> list=steps(a.role());
        List<Map<String,Object>> required=new ArrayList<>();
        String current=null;
        int count=0;
        for(Step s:list) {
            boolean ok=isDone(done,s);
            if(ok)count++;
            else if(current==null)current=s.key();
            Map<String,Object> m=new LinkedHashMap<>();
            m.put("key",s.key());
            m.put("title",s.title());
            m.put("why",s.why());
            m.put("done",ok);
            required.add(m);
        }
        List<Map<String,Object>> readiness=a.role().equals("ADMIN")&&current==null&&!done.contains("apt:readiness")?readiness(a):List.of();
        int readyDone=(int)readiness.stream().filter(r->Boolean.TRUE.equals(r.get("done"))).count();
        if(a.role().equals("ADMIN")&&current==null&&!done.contains("apt:readiness")&&readyDone==readiness.size()) {
            // Everything is ready: remember it so the guidance never comes back, even if data changes later.
            db.update("insert into ap_setup_step(tenant_id,user_id,step_key,completed_by) values(?,null,'readiness',?) on conflict do nothing",a.tenantId(),a.id());
            readiness=List.of();
        }
        int total=list.size()+readiness.size(),finished=count+readyDone;
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("role",a.role());
        out.put("steps",required);
        out.put("total",list.size());
        out.put("done",count);
        out.put("percent",list.isEmpty()?100:count*100/list.size());
        out.put("current",current);
        out.put("requiredComplete",current==null);
        out.put("readiness",readiness);
        out.put("overallPercent",total==0?100:finished*100/total);
        boolean complete=current==null&&readiness.stream().allMatch(r->Boolean.TRUE.equals(r.get("done")));
        out.put("setupComplete",complete);
        Map<String,Object> next=null;
        if(!complete)for(var r:readiness)if(!Boolean.TRUE.equals(r.get("done"))) {
            next=r;
            break;
        }
        out.put("next",next);
        List<String> tips=new ArrayList<>();
        for(String d:done)if(d.startsWith("me:tip:"))tips.add(d.substring(7));
        out.put("tipsSeen",tips);
        return out;
    }
    /** Admin checklist after the wizard. Never blocks anyone: it only depends on other people acting. */
    List<Map<String,Object>> readiness(Account a) {
        UUID t=a.tenantId();
        long flats=db.count("select count(*) from ap_flat where tenant_id=? and active=true",t);
        long joined=db.count("select count(distinct flat_id) from ap_user where tenant_id=? and status='ACTIVE' and flat_id is not null",t);
        long pending=db.count("select count(*) from ap_user where tenant_id=? and status='PENDING'",t);
        List<Map<String,Object>> out=new ArrayList<>();
        out.add(item("join",pending>0?pending+(pending==1?" family is":" families are")+" waiting for approval":"Approve families who ask to join",joined+" of "+flats+" flats joined",pending==0&&joined>0,"join-requests","","Open a request, check the person and flat, then approve."));
        out.add(item("treasurer","Choose a Treasurer","The Treasurer checks payments and records expenses",db.count("select count(*) from ap_user where tenant_id=? and status='ACTIVE' and role='TREASURER'",t)>0,"roles","","Tap a member, then change their role to Treasurer."));
        out.add(item("second_admin","Add a second Admin","The person who records a payment can never approve it",db.count("select count(*) from ap_user where tenant_id=? and status='ACTIVE' and role='ADMIN'",t)>1,"roles","","Tap a trusted member, then change their role to Admin."));
        out.add(item("welcome","Post a welcome notice","Tell everyone the apartment now uses ApartmentCare",db.count("select count(*) from ap_notice where tenant_id=? and status='PUBLISHED'",t)>0,"notices","Create notice","Tap here to write your first notice."));
        out.add(item("subscription","Check your subscription","See your plan and the referral offer",db.count("select count(*) from ap_subscription where tenant_id=?",t)>0,"subscription","",""));
        return out;
    }
    private static Map<String,Object> item(String key,String title,String detail,boolean done,String route,String target,String tip) {
        Map<String,Object> m=new LinkedHashMap<>();
        m.put("key",key);
        m.put("title",title);
        m.put("detail",detail);
        m.put("done",done);
        m.put("route",route);
        m.put("target",target);
        m.put("tip",tip);
        return m;
    }
    @Transactional public Map<String,Object> complete(Account a,String key) {
        List<Step> list=steps(a.role());
        Step step=list.stream().filter(s->s.key().equals(key)).findFirst().orElseThrow(()->new IllegalArgumentException("This setup step is not part of your setup."));
        db.lockTenant(a.tenantId());
        Set<String> done=doneKeys(a);
        if(isDone(done,step))return state(a);
        for(Step s:list) {
            if(s==step)break;
            if(!isDone(done,s))throw ApiError.conflict("Finish \""+s.title()+"\" first.");
        }
        verify(a,step);
        if(step.apartment())db.update("insert into ap_setup_step(tenant_id,user_id,step_key,completed_by) values(?,null,?,?) on conflict do nothing",a.tenantId(),key,a.id());
        else db.update("insert into ap_setup_step(tenant_id,user_id,step_key,completed_by) values(?,?,?,?) on conflict do nothing",a.tenantId(),a.id(),key,a.id());
        db.audit(a.tenantId(),a.id(),"SETUP_STEP_DONE",a.tenantId(),null,key);
        return state(a);
    }
    /** Server-side proof that the step's configuration really exists. Confirm-only steps need no data. */
    void verify(Account a,Step step) {
        if(CONFIRM_ONLY.contains(step.key()))return;
        UUID t=a.tenantId();
        switch(step.key()) {
            case "apartment" -> {
                var ap=db.one("select name,city,address,pincode from ap_tenant where id=?",t);
                if(blank(ap.get("name"))||blank(ap.get("city")))throw new IllegalArgumentException("Enter the apartment name and city.");
                if(blank(ap.get("address")))throw new IllegalArgumentException("Enter the apartment address.");
                if(ap.get("pincode")==null||!ap.get("pincode").toString().matches("[0-9]{6}"))throw new IllegalArgumentException("Enter a 6-digit PIN code.");
            }
            case "blocks" -> {
                if(db.count("select count(*) from ap_block where tenant_id=?",t)<1)throw new IllegalArgumentException("Add at least one block.");
            }
            case "flats" -> {
                long n=db.count("select count(*) from ap_flat where tenant_id=? and active=true",t);
                if(n<5||n>200)throw new IllegalArgumentException("An apartment needs between 5 and 200 flats.");
            }
            case "maintenance" -> {
                if(db.count("select count(*) from ap_billing_rule where tenant_id=?",t)<1)throw new IllegalArgumentException("Save the monthly maintenance amount first.");
            }
            case "opening_balance" -> {
                if(db.count("select count(*) from ap_settings where tenant_id=? and opening_date is not null",t)<1)throw new IllegalArgumentException("Save the existing balance first, or choose Starting from ₹0.");
            }
            case "invite" -> {
                if(db.count("select count(*) from ap_invite where tenant_id=? and active=true and expires_at>now()",t)<1)throw new IllegalArgumentException("Create an invite code first.");
            }
            case "profile" -> {
                if(blank(db.one("select name from ap_user where id=? and tenant_id=?",a.id(),t).get("name")))throw new IllegalArgumentException("Enter your name.");
            }
            default -> throw new IllegalArgumentException("Unknown setup step.");
        }
    }
    static boolean paymentReady(Map<String,Object> s) {
        return !blank(s.get("payee"))&&(!blank(s.get("upi"))||(!blank(s.get("account"))&&!blank(s.get("ifsc"))));
    }
    private static boolean blank(Object o) {
        return o==null||o.toString().isBlank();
    }
    @Transactional public Map<String,Object> tipSeen(Account a,String tip) {
        if(!tip.matches("[a-z0-9_-]{1,30}"))throw new IllegalArgumentException("Unknown tip.");
        db.update("insert into ap_setup_step(tenant_id,user_id,step_key,completed_by) values(?,?,?,?) on conflict do nothing",a.tenantId(),a.id(),"tip:"+tip,a.id());
        return Map.of("ok",true);
    }
    /** Admin view: where every flat stands, so the Admin knows whom to follow up with. */
    @Transactional(readOnly=true) public List<Map<String,Object>> members(Account a) {
        a.requireAdmin();
        int total=RESIDENT.size();
        var rows=db.rows("select f.id as flat_id,f.label,f.block,u.id as user_id,u.name,u.status,(select count(*) from ap_setup_step s where s.user_id=u.id and s.step_key in ('terms','profile','vehicles','how_to_pay','stay_informed'))::int as steps_done from ap_flat f left join ap_user u on u.tenant_id=f.tenant_id and u.flat_id=f.id and u.status in ('ACTIVE','PENDING') where f.tenant_id=? and f.active=true order by f.block,f.label",a.tenantId());
        for(var r:rows) {
            Object status=r.get("status");
            int stepsDone=r.get("stepsDone")==null?0:((Number)r.get("stepsDone")).intValue();
            String state=status==null?"NOT_JOINED":"PENDING".equals(status)?"WAITING_APPROVAL":stepsDone>=total?"COMPLETE":"IN_PROGRESS";
            r.put("state",state);
            r.put("stepsTotal",total);
            r.put("stepsDone",Math.min(stepsDone,total));
        }
        return rows;
    }
    /** While setup is open, only what the wizard itself needs is reachable. */
    public static boolean allowedDuringSetup(String role,String method,String path) {
        String p=path.startsWith("/api/v1")?path.substring(7):path;
        if(p.equals("/me")||p.equals("/logout")||p.startsWith("/setup")||p.equals("/status"))return true;
        if(p.equals("/ops/profile")||p.equals("/ops/change-pin"))return true;
        // Account deletion must always be reachable (Play policy), even before setup is finished.
        if(p.equals("/account/deletion")||p.equals("/account/delete"))return true;
        boolean get="GET".equals(method);
        if(get&&(p.equals("/ops/settings")||p.equals("/ops/apartment")||p.startsWith("/notifications")))return true;
        return switch(role) {
            case "ADMIN" -> p.equals("/ops/apartment")||p.startsWith("/ops/blocks")||p.startsWith("/ops/flats")||p.startsWith("/ops/settings")||p.equals("/flats")
                ||p.startsWith("/billing/rules")||p.equals("/ops/opening")||p.startsWith("/ops/charges")||p.startsWith("/invites")
                ||p.startsWith("/ops/rate-overrides")||p.startsWith("/ops/subscription")||p.equals("/ops/referrals/claim");
            case "TREASURER" -> p.startsWith("/ops/settings")||p.startsWith("/ops/recurring")||p.startsWith("/ops/contacts")
                ||(get&&(p.startsWith("/payments")||p.startsWith("/expenses")||p.startsWith("/reports")||p.startsWith("/billing/rules")));
            default -> p.startsWith("/ops/vehicles")
                ||(get&&(p.startsWith("/bills")||p.startsWith("/notices")||p.startsWith("/ops/tickets")||p.equals("/ops/flats")));
        };
    }
}
