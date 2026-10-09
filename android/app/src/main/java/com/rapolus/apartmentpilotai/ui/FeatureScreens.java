package com.rapolus.apartmentpilotai.ui;
import android.content.*;
import android.net.Uri;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatDelegate;
import com.rapolus.apartmentpilotai.R;
import com.rapolus.apartmentpilotai.BuildConfig;
import org.json.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
/** Connected native operational screens. All writes are server-validated; unavailable providers are never simulated. */ public final class FeatureScreens {
    private final FeatureHost h;
    private final Ui u;
    private final PaymentScreens paymentScreens;
    private final FinanceReportScreens reportScreens;
    private final AttachmentScreens attachmentScreens;
    private LinearLayout b;
    private String id="";
    private static final int TEXT=InputType.TYPE_CLASS_TEXT,NUM=InputType.TYPE_CLASS_NUMBER,MONEY=InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL;
    private static final String[] FREQUENCIES= {
        "MONTHLY","QUARTERLY","HALF_YEARLY","YEARLY"
    };
    private static final String[] CATEGORIES= {
        "Water","Power","Lift","Plumbing","Cleaning","Security","Parking","Generator","CCTV","Gate / intercom","Pest control","Civil / leaks","Internet","Other"
    };
    public FeatureScreens(FeatureHost host) {
        h=host;
        u=h.components();
        paymentScreens=new PaymentScreens(h);
        reportScreens=new FinanceReportScreens(h);
        attachmentScreens=new AttachmentScreens(h);
    }
    public boolean render(String route,String record) {
        b=h.body();
        id=record;
        if(paymentScreens.render(route,record))return true;
        if(reportScreens.render(route))return true;
        if(attachmentScreens.render(route,record))return true;
        switch(route) {
            case "community":community();
            break;
            case "more":more();
            break;
            case "money":money();
            break;
            case "profile":profile();
            break;
            case "profile-edit":profileEdit();
            break;
            case "change-pin":pin();
            break;
            case "apartment":apartment();
            break;
            case "apartment-edit":apartmentEdit();
            break;
            case "blocks":blocks();
            break;
            case "block-form":blockForm();
            break;
            case "flats":flats();
            break;
            case "flat":flat();
            break;
            case "flat-form":flatForm();
            break;
            case "directory-detail":directoryDetail();
            break;
            case "directory":case "residents":directory();
            break;
            case "member-record-form":memberRecord();
            break;
            case "committee":committee();
            break;
            case "committee-form":committeeForm();
            break;
            case "roles":roles();
            break;
            case "role-form":roleForm();
            break;
            case "handover":handover();
            break;
            case "join-requests":joinRequests();
            break;
            case "join-review":joinReview();
            break;
            case "join-reject":joinReject();
            break;
            case "invite-settings":inviteSettings();
            break;
            case "vendors":contacts("VENDOR");
            break;
            case "contacts":contacts("CONTACT");
            break;
            case "vendor":contact("VENDOR");
            break;
            case "contact":contact("CONTACT");
            break;
            case "vendor-form":contactForm("VENDOR");
            break;
            case "contact-form":contactForm("CONTACT");
            break;
            case "issues":tickets("ISSUE");
            break;
            case "complaints":tickets("COMPLAINT");
            break;
            case "issue-categories":issueCategories();
            break;
            case "issue-form":ticketForm("ISSUE");
            break;
            case "complaint-form":ticketForm("COMPLAINT");
            break;
            case "ticket":case "issue":case "complaint":ticket();
            break;
            case "affected":affected();
            break;
            case "ticket-update":ticketUpdate();
            break;
            case "services":services();
            break;
            case "service":service();
            break;
            case "service-form":serviceForm();
            break;
            case "service-complete":serviceComplete();
            break;
            case "events":case "bookings":bookings();
            break;
            case "event-form":bookingForm(false);
            break;
            case "booking-alternative":bookingForm(true);
            break;
            case "booking":case "event":booking();
            break;
            case "availability":case "parking-availability":availability();
            break;
            case "resources":resources();
            break;
            case "resource-form":resourceForm();
            break;
            case "resource":resource();
            break;
            case "resource-block-form":resourceBlock();
            break;
            case "parking-share":parkingShare();
            break;
            case "notices":notices();
            break;
            case "notice":notice();
            break;
            case "notice-form":noticeForm();
            break;
            case "notice-templates":noticeTemplates();
            break;
            case "notice-preview":noticePreview();
            break;
            case "documents":documents();
            break;
            case "document":document();
            break;
            case "document-form":documentForm();
            break;
            case "attachments":attachments();
            break;
            case "settings":case "finance-settings":settings();
            break;
            case "payment-settings":settingsForm("payment");
            break;
            case "notification-settings":case "reminder-settings":settingsForm("reminders");
            break;
            case "billing-options":settingsForm("billing");
            break;
            case "verification-settings":settingsForm("verification");
            break;
            case "booking-policy":settingsForm("booking");
            break;
            case "opening-balance":opening();
            break;
            case "rate-overrides":overrides();
            break;
            case "rate-override-form":overrideForm();
            break;
            case "contributions":contributions();
            break;
            case "contribution-form":charge("CONTRIBUTION");
            break;
            case "contribution-preview":contributionPreview();
            break;
            case "opening-dues":charge("OPENING_DUE");
            break;
            case "income":income();
            break;
            case "income-form":incomeForm();
            break;
            case "expense-form":newExpense();
            break;
            case "expense":expense();
            break;
            case "expense-edit":expenseEdit();
            break;
            case "expense-reverse":reverse(false);
            break;
            case "payment-reverse":reverse(true);
            break;
            case "recurring":recurring();
            break;
            case "recurring-detail":recurringDetail();
            break;
            case "recurring-form":recurringForm();
            break;
            case "cashbook":cashbook();
            break;
            case "reports":reports();
            break;
            case "transparency":transparency();
            break;
            case "payee":payee();
            break;
            case "tasks":case "approval-inbox":tasks();
            break;
            case "automation":automation();
            break;
            case "subscription":case "referrals":subscription();
            break;
            case "referral-claim":referralClaim();
            break;
            case "polls":polls();
            break;
            case "poll-form":pollForm();
            break;
            case "poll":poll();
            break;
            case "vehicles":vehicles();
            break;
            case "vehicle-form":vehicleForm();
            break;
            case "vehicle":vehicle();
            break;
            case "event-calendar":eventCalendar();
            break;
            case "appearance":appearance();
            break;
            case "privacy":privacy();
            break;
            case "help":help();
            break;
            case "permissions":permissions();
            break;
            case "audit-detail":auditDetail();
            break;
            case "payment-help":paymentHelp();
            break;
            case "support":support();
            break;
            case "subscription-plans":subscriptionPlans();
            break;
            case "referral-offer":referralOffer();
            break;
            case "referral-rules":referralRules();
            break;
            case "status":case "pending-module":status();
            break;
            default:return false;
        }
        return true;
    }
    private void title(String title) {
        h.pageTitle(title);
    }
    private void go(String page) {
        h.openPage(page,"");
    }
    private void go(String page,String id) {
        h.openPage(page,id);
    }
    private void get(String path,FeatureHost.Reply reply) {
        h.requestApi("GET",path,null,reply);
    }
    private JSONObject obj(Object...args) {
        JSONObject j=new JSONObject();
        try {
            for(int i=0; i<args.length; i+=2)j.put(args[i].toString(),args[i+1]);
            return j;
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private JSONObject command(JSONObject body) {
        try {
            body.put("requestKey",h.commandKey());
            return body;
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private void write(String path,JSONObject body,FeatureHost.Reply done) {
        h.requestApi("POST",path,command(body),value-> {
            h.resetCommandKey();
            done.done(value);
        }
        );
    }
    private void safe(Runnable fn) {
        try {
            fn.run();
        } catch(Exception e) {
            h.problem(e.getMessage()==null?"Check the input.":e.getMessage());
        }
    }
    private void submit(String label,Runnable action) {
        u.button(b,label,R.drawable.ic_check,true,()->safe(action));
    }
    private void compactInviteButton(com.google.android.material.button.MaterialButton button) {
        button.setTextSize(11.5f);
        button.setMinHeight(u.dp(41));
        button.setCornerRadius(u.dp(13));
        button.setIconSize(u.dp(16));
    }
    private void row(LinearLayout parent,String label,String detail,int icon,String route) {
        u.iconRow(parent,label,detail,icon,()->go(route));
    }
    private void row(LinearLayout parent,String label,String detail,int icon,String route,String record) {
        u.iconRow(parent,label,detail,icon,()->go(route,record));
    }
    private LinearLayout card() {
        return u.card(b);
    }
    private String val(JSONObject j,String key) {
        return j.isNull(key)?"":j.optString(key);
    }
    private BigDecimal amount(Ui.Fields f,String key) {
        return new BigDecimal(f.get(key));
    }
    private BigDecimal expenseAmount(Ui.Fields f) {
        try {
            BigDecimal value=new BigDecimal(f.get("amount").trim());
            if(value.signum()<=0)throw new IllegalArgumentException("Enter an amount greater than zero.");
            return value;
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid expense amount.");
        }
    }
    private void validateExpense(Ui.Fields f) {
        String description=f.get("title").trim();
        if(description.isEmpty())throw new IllegalArgumentException("Enter an expense description.");
        if(description.length()>100)throw new IllegalArgumentException("Keep the expense description to 100 characters or fewer.");
        expenseAmount(f);
        try {
            LocalDate date=LocalDate.parse(f.get("paidOn").trim());
            if(date.isAfter(LocalDate.now(ZoneId.of("Asia/Kolkata"))))throw new IllegalArgumentException("Expense date cannot be in the future.");
        } catch(java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Choose a valid expense date.");
        }
    }
    private JSONObject find(JSONArray rows,String target)throws JSONException {
        for(int i=0; i<rows.length(); i++)if(target.equals(rows.getJSONObject(i).optString("id")))return rows.getJSONObject(i);
        return new JSONObject();
    }
    /** Server enum to sentence case for display: "IN_PROGRESS" -> "In progress". */
    private String friendly(String t) {
        if(t.equalsIgnoreCase("UPI")||t.equalsIgnoreCase("CCTV")||t.equalsIgnoreCase("AMC"))return t.toUpperCase(Locale.ROOT);
        // Block names are short codes such as "A"; keep them as stored.
        if(t.length()<=2)return t.toUpperCase(Locale.ROOT);
        String s=t.toLowerCase(Locale.ROOT).replace('_',' ');
        return s.isEmpty()?s:Character.toUpperCase(s.charAt(0))+s.substring(1);
    }
    private void details(JSONObject j,String...keys) {
        LinearLayout c=card();
        for(int i=0; i<keys.length; i+=2)if(j.has(keys[i]))u.kv(c,keys[i+1],j.isNull(keys[i])?"—":j.optString(keys[i]));
    }
    private void choices(Ui.Fields f,String key,String label,JSONArray rows,String display,boolean optional,String selected)throws JSONException {
        ArrayList<String> labels=new ArrayList<>(),values=new ArrayList<>();
        if(optional) {
            labels.add("None");
            values.add("");
        }
        for(int i=0; i<rows.length(); i++) {
            JSONObject r=rows.getJSONObject(i);
            labels.add(r.optString(display,r.optString("name")));
            values.add(r.optString("id"));
        }
        f.select(key,label,labels.toArray(new String[0]),values.toArray(new String[0]));
        if(selected!=null)f.restore(obj(key,selected));
    }
    /** Same categories as one-off expenses so reports group recurring drafts correctly; keeps a legacy value. */
    private void expenseCategory(Ui.Fields f,String selected) {
        List<String> values=new ArrayList<>(Arrays.asList("Security","Housekeeping","Electricity","Water","Lift","Repairs","Generator","Other"));
        String current=selected==null||selected.trim().isEmpty()?"Security":selected.trim();
        boolean known=false;
        for(String v:values)if(v.equalsIgnoreCase(current)) {
            current=v;
            known=true;
        }
        if(!known)values.add(current);
        pick(f,"category","Category",values.toArray(new String[0]),current);
    }
    private void pick(Ui.Fields f,String key,String label,String[] values,String selected) {
        String[] labels=Arrays.stream(values).map(this::friendly).toArray(String[]::new);
        f.select(key,label,labels,values);
        if(selected!=null)f.restore(obj(key,selected));
    }
    private static String today() {
        return LocalDate.now(ZoneId.of("Asia/Kolkata")).toString();
    }
    private String serviceStatus(JSONObject service) {
        if(!service.optBoolean("active",true))return "Paused";
        try {
            return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(ZoneId.of("Asia/Kolkata")),LocalDate.parse(service.optString("nextOn")))<7?"Due soon":"Scheduled";
        } catch(RuntimeException e) {
            return "Scheduled";
        }
    }
    private static String timeDefault() {
        return LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(2)+"T17:00";
    }
    private String instant(String text) {
        return LocalDateTime.parse(text).atZone(ZoneId.of("Asia/Kolkata")).toInstant().toString();
    }
    private String local(String text) {
        return text.trim().isEmpty()?"":LocalDateTime.ofInstant(Instant.parse(text),ZoneId.of("Asia/Kolkata")).toString();
    }
    private String enc(String text) {
        try {
            return URLEncoder.encode(text,"UTF-8");
        } catch(java.io.UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
    private void fileLink(String kind,String record) {
        u.button(b,"Attachments",R.drawable.ic_copy,false,()->go("attachments",kind+":"+record));
    }
    private void community() {
        title("Community");
        LinearLayout c=card();
        row(c,"Members","Resident directory",R.drawable.ic_users,"directory");
        row(c,"Committee","Apartment team",R.drawable.ic_shield,"committee");
        row(c,"Notices","Updates & acknowledgements",R.drawable.ic_notice,"notices");
        row(c,"Events & parking","Requests & reservations",R.drawable.ic_calendar,"events");
        row(c,"Contacts","Important numbers",R.drawable.ic_user,"contacts");
        row(c,"Vendors","Service providers",R.drawable.ic_tools,"vendors");
        row(c,"Documents","Shared apartment files",R.drawable.ic_copy,"documents");
        row(c,"Polls","One vote per flat",R.drawable.ic_check,"polls");
        row(c,"Vehicles","Parking & owner contact",R.drawable.ic_car,"vehicles");
        if(h.isAdmin())row(c,"Apartment","Flats & access",R.drawable.ic_building,"apartment");
    }
    private void more() {
        title("More");
        LinearLayout c=card();
        row(c,h.client().account().optString("name"),friendly(h.client().account().optString("role")),R.drawable.ic_user,"profile");
        row(c,"Appearance","Light & dark",R.drawable.ic_moon,"appearance");
        row(c,"Payment history","Declarations & receipts",R.drawable.ic_receipt,"history");
        if(h.isStaff()) {
            row(c,"Approvals","Tasks needing review",R.drawable.ic_check,"tasks");
            row(c,"Financial controls","Account, reminders & rules",R.drawable.ic_settings,"settings");
            row(c,"Automation","Scheduled tasks",R.drawable.ic_clock,"automation");
            row(c,"Activity","Audit trail",R.drawable.ic_clock,"audit");
        }
        if(h.isAdmin())row(c,"Subscription & billing","Plan & first-month referrals",R.drawable.ic_wallet,"subscription");
        row(c,"Help","Contacts & privacy",R.drawable.ic_tools,"help");
        if(BuildConfig.DEBUG)row(c,"Local server",h.client().base(),R.drawable.ic_settings,"connection");
        u.button(b,"Sign out",R.drawable.ic_logout,false,()->u.confirm("Sign out?","Your records stay on the server.","Sign out",()->h.requestApi("POST","/logout",obj(),r->h.exitSession())));
    }
    private void money() {
        title("Money");
        h.monthControl();
        LinearLayout c=card();
        row(c,"Maintenance","Monthly bills",R.drawable.ic_receipt,"dues");
        row(c,"Payments","Verify declarations",R.drawable.ic_check,"approvals");
        row(c,"Expenses","Paid & draft entries",R.drawable.ic_expense,"expenses");
        row(c,"Recurring expenses","Automatic drafts",R.drawable.ic_clock,"recurring");
        row(c,"One-time contributions","Repairs & collections",R.drawable.ic_plus,"contributions");
        row(c,"Opening dues","Previous flat balances",R.drawable.ic_wallet,"opening-dues");
        row(c,"Other income","Non-maintenance receipts",R.drawable.ic_plus,"income");
        row(c,"Cashbook","Cash movement",R.drawable.ic_report,"cashbook");
        row(c,"Reports","Monthly financial summary",R.drawable.ic_report,"reports");
        row(c,"Maintenance amount","Rate & effective month",R.drawable.ic_settings,"billing-settings");
        row(c,"Flat-specific rates","Agreed exceptions",R.drawable.ic_home,"rate-overrides");
        row(c,"Financial controls","Payment account & rules",R.drawable.ic_settings,"settings");
    }
    private void profile() {
        title("My profile");
        get("/me",r-> {
            JSONObject j=(JSONObject)r;
            details(j,"name","Name","apartmentName","Apartment","mobile","Mobile","role","Role");
            u.button(b,"Edit profile",R.drawable.ic_user,true,()->go("profile-edit"));
            u.button(b,"Change PIN",R.drawable.ic_shield,false,()->go("change-pin"));
        }
        );
    }
    private void profileEdit() {
        title("Edit profile");
        Ui.Fields f=h.newFields();
        f.field("name","Name",h.client().account().optString("name"),TEXT);
        f.field("email","Email · optional","",TEXT);
        submit("Save",()->write("/ops/profile",obj("name",f.get("name"),"email",f.get("email")),r-> {
            h.client().account().put("name",f.get("name"));
            h.client().updateAccount(h.client().account());
            go("profile");
        }
        ));
    }
    private void pin() {
        title("Change PIN");
        Ui.Fields f=h.newFields();
        f.field("currentPin","Current PIN","",NUM);
        f.field("newPin","New PIN · 6 digits","",NUM);
        f.field("confirmPin","Confirm PIN","",NUM);
        u.note(b,"Changing your PIN signs out all sessions.");
        submit("Change PIN",()-> {
            if(!f.get("newPin").equals(f.get("confirmPin")))throw new IllegalArgumentException("PINs do not match.");
            h.requestApi("POST","/ops/change-pin",obj("currentPin",f.get("currentPin"),"newPin",f.get("newPin")),r->h.exitSession());
        }
        );
    }
    private void apartment() {
        title("Apartment");
        get("/ops/apartment",r-> {
            JSONObject a=(JSONObject)r;
            u.hero(b,a.optString("city"),a.optString("name"),a.optString("address"));
            LinearLayout c=card();
            row(c,"Apartment details","Name & address",R.drawable.ic_building,"apartment-edit");
            row(c,"Blocks","Apartment sections",R.drawable.ic_building,"blocks");
            row(c,"Flats","Flat records",R.drawable.ic_home,"flats");
            row(c,"Residents & approvals","Account access",R.drawable.ic_users,"members");
            row(c,"Invite residents","Share a join code",R.drawable.ic_plus,"invite");
            row(c,"Team roles","Permissions",R.drawable.ic_shield,"roles");
            row(c,"Resident handover","End old flat access",R.drawable.ic_users,"handover");
        }
        );
    }
    private void apartmentEdit() {
        title("Apartment profile");
        get("/ops/apartment",r-> {
            JSONObject a=(JSONObject)r;
            Ui.Fields f=h.newFields();
            f.field("name","Apartment name",val(a,"name"),TEXT);f.limit("name",80);
            f.field("city","City",val(a,"city"),TEXT);f.limit("city",80);
            f.field("address","Address",val(a,"address"),TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);f.limit("address",300);
            f.field("pincode","PIN code",val(a,"pincode"),NUM);f.limit("pincode",6);
            submit("Save",()-> {
                String error=com.rapolus.apartmentpilotai.data.RegistrationPolicy.apartmentDetails(f.get("name"),f.get("city"),f.get("address"),f.get("pincode"));
                if(!error.isEmpty())throw new IllegalArgumentException(error);
                write("/ops/apartment",f.values(),x->go("apartment"));
            });
        }
        );
    }
    private void flats() {
        title("Flats");
        if(h.isAdmin())u.button(b,"Add flat",R.drawable.ic_plus,true,()->go("flat-form"));
        get("/ops/flats",r-> {
            JSONArray arr=(JSONArray)r;
            get("/bills?month="+h.selectedMonth(),billValue-> {
            JSONArray billRows=(JSONArray)billValue;
            Map<String,JSONObject> billsByFlat=new HashMap<>();
            for(int i=0;i<billRows.length();i++){JSONObject bill=billRows.getJSONObject(i);billsByFlat.put(bill.optString("flatId"),bill);}
            u.note(b,Ui.count(arr.length(),"flat","flats"));
            Ui.Fields fields=h.newFields();
            JSONObject draft=h.pageDraft("flat-list");
            EditText search=fields.field("search","Search flat or resident",draft.optString("search"),TEXT);
            LinkedHashSet<String> names=new LinkedHashSet<>();names.add("All");
            for(int i=0;i<arr.length();i++)names.add(arr.getJSONObject(i).optString("block"));
            String[] blocks=names.toArray(new String[0]);
            Spinner filter=new Spinner(h.activity());
            ArrayAdapter<String> adapter=new ArrayAdapter<>(h.activity(),android.R.layout.simple_spinner_item,blocks);adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filter.setAdapter(adapter);filter.setContentDescription("Filter by block");filter.setMinimumHeight(u.dp(48));b.addView(filter);
            int selected=Arrays.asList(blocks).indexOf(draft.optString("block","All"));filter.setSelection(Math.max(selected,0));
            LinearLayout list=u.column();b.addView(list);
            Runnable display=()-> {
                String term=search.getText().toString().trim().toLowerCase(Locale.ROOT),block=blocks[filter.getSelectedItemPosition()];
                h.savePageDraft("flat-list",obj("search",search.getText().toString(),"block",block));
                list.removeAllViews();GridLayout grid=new GridLayout(h.activity());grid.setColumnCount(3);list.addView(grid,new LinearLayout.LayoutParams(-1,-2));int count=0;
                for(int i=0;i<arr.length();i++) {
                    JSONObject f=arr.optJSONObject(i);if(f==null)continue;
                    if(!block.equals("All")&&!block.equals(f.optString("block")))continue;
                    if(!(f.optString("label")+" "+val(f,"name")).toLowerCase(Locale.ROOT).contains(term))continue;
                    count++;
                    String state=!f.optBoolean("active")?"Inactive":f.isNull("memberId")?"Unassigned":f.optString("status").equals("PENDING")?"Pending approval":"Active";
                    JSONObject bill=billsByFlat.get(f.optString("id"));
                    if(f.optBoolean("active")&&bill!=null)state=bill.optBoolean("inReview")?"In review":new BigDecimal(bill.optString("paid","0")).compareTo(new BigDecimal(bill.optString("amount","0")))<0?"Unpaid":"Paid";
                    LinearLayout holder=u.column();com.google.android.material.button.MaterialButton choice=u.button(holder,f.optString("label")+"\n"+state,0,false,()->go("flat",f.optString("id")));
                    choice.setAllCaps(false);choice.setTextSize(11.5f);choice.setMaxLines(2);choice.setEllipsize(android.text.TextUtils.TruncateAt.END);choice.setPadding(u.dp(2),u.dp(2),u.dp(2),u.dp(2));
                    choice.setContentDescription("Open flat "+f.optString("label"));holder.removeView(choice);
                    GridLayout.LayoutParams p=new GridLayout.LayoutParams();p.width=0;p.height=u.dp(82);p.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);p.setMargins(u.dp(3),u.dp(3),u.dp(3),u.dp(3));grid.addView(choice,p);
                }
                if(count==0)u.empty(list,R.drawable.ic_building,arr.length()==0?"No flats configured":"No matching flats","Try another flat, resident or block.");
            };
            search.addTextChangedListener(new android.text.TextWatcher() {
                public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                public void onTextChanged(CharSequence s,int start,int before,int count) {display.run();}
                public void afterTextChanged(android.text.Editable value) {}
            });
            filter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                public void onItemSelected(android.widget.AdapterView<?> parent,android.view.View view,int position,long itemId) {display.run();}
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });display.run();
            });
        }
        );
    }
    private void flat() {
        title("Flat details");
        if(id.isEmpty()) {u.empty(b,R.drawable.ic_building,"No flat selected","Choose a flat from the Flats page.");return;}
        get("/ops/flats/"+id,r-> {
            JSONObject f=(JSONObject)r;
            LinearLayout c=card();c.addView(u.text(f.optString("label"),28,true,R.color.ap_ink));
            u.kv(c,"Status",f.optBoolean("active")?"Active":"Inactive");u.kv(c,"Block",f.optString("block"));u.kv(c,"Floor",f.optString("floor"));u.kv(c,"Occupancy",f.optBoolean("occupied")?"Occupied":"Vacant");
            u.kv(c,"Resident",f.isNull("memberId")?"Not assigned":val(f,"name"));u.kv(c,"Account",f.isNull("memberId")?"Available":friendly(f.optString("status")));
            u.button(b,"Edit flat",R.drawable.ic_settings,false,()->go("flat-form",f.optString("id")));
            u.button(b,"Payment history",R.drawable.ic_receipt,false,()->go("history","flat:"+f.optString("id")));
            if(!f.isNull("memberId")&&f.optString("status").equals("ACTIVE"))u.button(b,"Resident handover",R.drawable.ic_users,false,()->go("handover",f.optString("memberId")));
            get("/bills?month="+h.selectedMonth(),value-> {
                JSONArray bills=(JSONArray)value;
                for(int i=0;i<bills.length();i++) {
                    JSONObject bill=bills.getJSONObject(i);
                    if(bill.optString("flatId").equals(f.optString("id")))u.button(b,"Current dues · "+Ui.money(bill.opt("amount")),R.drawable.ic_wallet,true,()->go("bill",bill.optString("id")));
                }
            });
        });
    }
    private void blocks() {
        title("Blocks");
        u.button(b,"Add block",R.drawable.ic_plus,true,()->go("block-form"));
        get("/ops/blocks",r-> {
            JSONArray rows=(JSONArray)r;
            if(rows.length()==0)u.empty(b,R.drawable.ic_building,"No blocks configured","Add a block before creating its flats.");
            for(int i=0;i<rows.length();i++) {
                JSONObject block=rows.getJSONObject(i);
                row(card(),"Block "+block.optString("name"),Ui.count(block.optInt("flatCount"),"flat","flats"),R.drawable.ic_building,"block-form",block.optString("id"));
            }
        });
    }
    private void blockForm() {
        title("Block details");
        get("/ops/blocks",r-> {
            JSONObject block=find((JSONArray)r,id);
            Ui.Fields f=h.newFields();
            f.field("name","Block name",val(block,"name"),TEXT);
            u.note(b,"Renaming a block updates its flats but does not rename existing flat IDs.");
            submit(id.isEmpty()?"Add block":"Save block",()->write("/ops/blocks"+(id.isEmpty()?"":"/"+id),f.values(),x->go("blocks")));
        });
    }
    private void flatForm() {
        title(id.isEmpty()?"Add flat":"Flat details");
        get("/ops/blocks",blockValue-> {
            JSONArray blockRows=(JSONArray)blockValue;
            String[] blockNames=new String[blockRows.length()];
            for(int i=0;i<blockRows.length();i++)blockNames[i]=blockRows.getJSONObject(i).optString("name");
            get("/ops/flats",r-> {
                JSONObject j=find((JSONArray)r,id);
                Ui.Fields f=h.newFields();
                f.field("label","Flat number",val(j,"label"),TEXT);
                pick(f,"block","Block",blockNames,j.optString("block",blockNames.length==0?"":blockNames[0]));
                f.field("floor","Floor",j.optString("floor","1"),InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                f.check("occupied","Occupied",j.optBoolean("occupied",true));
                f.check("active","Active",j.optBoolean("active",true));
                submit("Save flat",()-> {
                    JSONObject p=f.values();
                    try {
                        p.put("floor",Integer.parseInt(f.get("floor")));
                    } catch(JSONException e) {
                        throw new IllegalArgumentException(e);
                    }
                    write("/ops/flats"+(id.isEmpty()?"":"/"+id),p,x->go("flats"));
                });
            });
        });
    }
    private void directory() {
        title("Members");
        if(h.isAdmin()) {
            u.button(b,"Add member",R.drawable.ic_plus,true,()->go("member-record-form"));
            u.button(b,"Invite residents",R.drawable.ic_plus,false,()->go("invite"));
            u.button(b,"Join requests",R.drawable.ic_users,false,()->go("join-requests"));
        }
        get("/ops/directory",r-> {
            JSONArray arr=(JSONArray)r;
            for(int i=0; i<arr.length(); i++) {
                JSONObject m=arr.getJSONObject(i);
                LinearLayout c=card();
                row(c,m.optString("name"),val(m,"flatLabel")+" · "+friendly(m.optString("status")),R.drawable.ic_user,"directory-detail",m.optString("id"));
                if(h.isAdmin()&&"RECORD".equals(m.optString("source")))u.button(c,"Edit member record",R.drawable.ic_user,false,()->go("member-record-form",m.optString("id")));
            }
        }
        );
    }
    private void memberRecord() {
        title("Member record");
        get("/ops/flats",rs-> {
            JSONArray flats=(JSONArray)rs;
            get("/ops/directory",r-> {
                JSONObject j=find((JSONArray)r,id);
                Ui.Fields f=h.newFields();
                choices(f,"flatId","Flat",flats,"label",false,val(j,"flatId"));
                f.field("name","Name",val(j,"name"),TEXT);
                f.field("mobile","Mobile · optional",val(j,"mobile"),InputType.TYPE_CLASS_PHONE);
                pick(f,"residentType","Member type",new String[] {
                    "OWNER","TENANT"
                }
                ,j.optString("residentType","OWNER"));
                f.check("active","Active record",true);
                u.note(b,"Directory record only. The member joins with an invite and Admin approval.");
                submit("Save member",()->write("/ops/directory"+(id.isEmpty()?"":"/"+id),f.values(),x->go("directory")));
            }
            );
        }
        );
    }
    private void committee() {
        title("Committee");
        if(h.isAdmin())u.button(b,"Add committee member",R.drawable.ic_plus,true,()->go("committee-form"));
        get("/ops/committee",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_users,"Committee not added","Apartment team appears here.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject m=arr.getJSONObject(i);
                LinearLayout c=card();
                u.iconRow(c,m.optString("name"),m.optString("title")+" · "+val(m,"flatLabel"),R.drawable.ic_shield,h.isAdmin()?()->go("committee-form",m.optString("id")):null);
                u.kv(c,"Term",m.optString("startOn")+" — "+val(m,"endOn"));
            }
        }
        );
    }
    private void committeeForm() {
        title("Committee role");
        get("/ops/flats",rs-> {
            JSONArray flats=(JSONArray)rs;
            get("/ops/committee",r-> {
                JSONObject j=find((JSONArray)r,id);
                Ui.Fields f=h.newFields();
                f.field("name","Member name",val(j,"name"),TEXT);
                f.field("title","Committee title",j.optString("title","Secretary"),TEXT);
                choices(f,"flatId","Flat · optional",flats,"label",true,val(j,"flatId"));
                f.field("startOn","Term begins · YYYY-MM-DD",j.optString("startOn",today()),TEXT);
                f.field("endOn","Term ends · optional",val(j,"endOn"),TEXT);
                f.check("public","Visible to residents",j.optBoolean("public",true));
                f.check("active","Active committee role",j.optBoolean("active",true));
                u.note(b,"Committee titles do not grant app permissions.");
                submit("Save role",()->write("/ops/committee"+(id.isEmpty()?"":"/"+id),f.values(),x->go("committee")));
            }
            );
        }
        );
    }
    private void roles() {
        title("Team access");
        get("/members",r-> {
            JSONArray arr=(JSONArray)r;
            for(int i=0; i<arr.length(); i++) {
                JSONObject m=arr.getJSONObject(i);
                if(!"ACTIVE".equals(m.optString("status")))continue;
                row(card(),m.optString("name"),friendly(m.optString("role")),R.drawable.ic_shield,"role-form",m.optString("id"));
            }
        }
        );
    }
    private void roleForm() {
        title("App permissions");
        Ui.Fields f=h.newFields();
        pick(f,"role","Role",new String[] {
            "RESIDENT","TREASURER","ADMIN"
        }
        ,"RESIDENT");
        u.note(b,"Changing access revokes existing sessions. Admin can manage the full apartment.");
        submit("Change role",()->u.confirm("Change permissions?","This changes financial and administrative access.","Confirm",()->write("/ops/members/"+id+"/role",f.values(),r->go("roles"))));
    }
    private void handover() {
        title("Resident handover");
        get("/members",r-> {
            JSONArray arr=(JSONArray)r;
            Ui.Fields f=h.newFields();
            choices(f,"member","End account access",arr,"name",false,id);
            f.field("reason","Reason","Resident moved out",TEXT);
            u.note(b,"Existing bills and receipts stay with the flat. Invite and verify the next resident afterward.");
            submit("End old access",()->u.confirm("Revoke old access?","The selected account will be disabled and signed out.","End access",()->write("/ops/members/"+f.get("member")+"/end-access",obj("reason",f.get("reason")),x->go("members"))));
        }
        );
    }
    private void joinRequests() {
        title("Join requests");
        String selected=h.pageDraft("joinRequests").optString("status","PENDING");
        if(!Arrays.asList("PENDING","ACTIVE","REJECTED").contains(selected))selected="PENDING";
        final String status=selected;
        LinearLayout filters=u.row();
        for(String option:new String[] {"PENDING","ACTIVE","REJECTED"}) {
            String label=option.equals("ACTIVE")?"Approved":option.substring(0,1)+option.substring(1).toLowerCase(Locale.ROOT);
            Button button=u.button(filters,label,0,option.equals(status),()-> {
                h.savePageDraft("joinRequests",obj("status",option));
                h.refreshPage();
            });
            button.setSingleLine(true);
            button.setTextSize(11);
            button.setMinWidth(0);
            button.setMinimumWidth(0);
            button.setPadding(u.dp(4),0,u.dp(4),0);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,-2,1);
            params.setMarginEnd(u.dp(6));
            button.setLayoutParams(params);
        }
        b.addView(filters);
        get("/members",value-> {
            JSONArray rows=(JSONArray)value;
            int requestCount=0;
            for(int i=0;i<rows.length();i++) {
                JSONObject member=rows.getJSONObject(i);
                if(val(member,"flatLabel").isEmpty()||!status.equals(member.optString("status")))continue;
                requestCount++;
            }
            if(requestCount==0)u.empty(b,R.drawable.ic_users,"No "+(status.equals("ACTIVE")?"approved":status.toLowerCase(Locale.ROOT))+" requests","Resident requests in this state will appear here.");
            LinearLayout requests=requestCount==0?null:card();
            if(requests!=null)requests.setPadding(0,0,0,0);
            for(int i=0;i<rows.length();i++) {
                JSONObject member=rows.getJSONObject(i);
                if(val(member,"flatLabel").isEmpty()||!status.equals(member.optString("status")))continue;
                if(requests.getChildCount()>0) {
                    android.view.View divider=new android.view.View(h.activity());
                    divider.setBackgroundColor(u.color(R.color.ap_line));
                    requests.addView(divider,new LinearLayout.LayoutParams(-1,u.dp(1)));
                }
                row(requests,member.optString("name"),val(member,"flatLabel")+" · "+friendly(member.optString("residentType")),R.drawable.ic_users,"join-review",member.optString("id"));
            }
        });
    }
    private void joinReview() {
        title("Review resident");
        get("/members/"+id,value-> {
            JSONObject member=(JSONObject)value;
            String requested=val(member,"createdAt");
            member.put("requestedDate",com.rapolus.apartmentpilotai.data.DateText.day(requested));
            member.put("residentTypeLabel","TENANT".equals(member.optString("residentType"))?"Tenant":"OWNER".equals(member.optString("residentType"))?"Owner":member.optString("residentType"));
            String status=friendly(member.optString("status"));
            u.hero(b,val(member,"flatLabel"),member.optString("name"),status.isEmpty()?status:Character.toUpperCase(status.charAt(0))+status.substring(1));
            details(member,"flatLabel","Requested flat","mobile","Mobile","residentTypeLabel","Resident type","requestedDate","Requested");
            if("PENDING".equals(member.optString("status"))) {
                u.note(b,"Approve only after verifying the person and their connection to this flat.");
                LinearLayout actions=u.row();
                Button reject=u.button(actions,"Reject",R.drawable.ic_error,false,()->go("join-reject",id));
                Button approve=u.button(actions,"Approve resident",R.drawable.ic_check,true,()->approveResident(member));
                for(Button button:new Button[] {reject,approve}) {
                    button.setSingleLine(true);
                    button.setTextSize(11);
                    button.setMinWidth(0);
                    button.setMinimumWidth(0);
                    button.setPadding(u.dp(4),0,u.dp(4),0);
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,-2,1);
                    params.setMarginEnd(u.dp(6));
                    button.setLayoutParams(params);
                }
                b.addView(actions);
            } else if(!val(member,"rejectionReason").isEmpty())u.note(b,member.optString("rejectionReason"));
        });
    }
    private void approveResident(JSONObject member) {
        String requestId=member.optString("id");
        LinearLayout box=u.column();
        box.setPadding(u.dp(20),u.dp(8),u.dp(20),0);
        LinearLayout summary=u.card(box);
        u.kv(summary,"Resident",member.optString("name"));
        u.kv(summary,"Flat",val(member,"flatLabel"));
        CheckBox verified=new CheckBox(h.activity());
        verified.setText("I verified this resident and their flat.");
        verified.setTextSize(12);verified.setTextColor(u.color(R.color.ap_sub));
        box.addView(verified);
        h.activity().getLayoutInflater().inflate(R.layout.view_brand_footer,box,true);
        androidx.appcompat.app.AlertDialog dialog=new com.google.android.material.dialog.MaterialAlertDialogBuilder(h.activity())
            .setTitle("Approve flat access").setView(box).setNegativeButton("Cancel",null).setPositiveButton("Approve resident",null).create();
        dialog.setOnShowListener(ignored-> {
            Button submit=dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE);
            submit.setEnabled(false);
            verified.setOnCheckedChangeListener((button,checked)->submit.setEnabled(checked));
            submit.setOnClickListener(button-> {
                if(!verified.isChecked())return;
                submit.setEnabled(false);dialog.dismiss();
                h.requestApi("POST","/members/"+requestId+"/approve",obj("verified",true),x->h.refreshPage());
            });
        });
        dialog.show();
    }
    private void joinReject() {
        title("Reject join request");
        get("/members/"+id,value-> {
            JSONObject member=(JSONObject)value;
            if(!"PENDING".equals(member.optString("status"))) {
                u.empty(b,R.drawable.ic_check,"Request already reviewed","Return to join requests for its current status.");
                return;
            }
            u.hero(b,val(member,"flatLabel"),member.optString("name"),"Explain what the resident should correct.");
            Ui.Fields f=h.newFields();
            f.field("reason","Reason for resident","",TEXT);
            submit("Reject request",()-> {
                String reason=f.get("reason");
                if(reason.isEmpty())throw new IllegalArgumentException("Explain what the resident should correct.");
                if(reason.length()>300)throw new IllegalArgumentException("Keep the reason within 300 characters.");
                h.requestApi("POST","/members/"+id+"/reject",obj("reason",reason),x->go("join-review",id));
            });
        });
    }
    private String inviteDate(JSONObject invite) {
        String day=val(invite,"expiresOn");
        return day.isEmpty()?LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(7).toString():day;
    }
    private void inviteSettings() {
        title("Invite controls");
        if(!h.isAdmin()) {
            u.empty(b,R.drawable.ic_shield,"Admin access required","Only your apartment Admin can change the resident invite.");
            return;
        }
        get("/invites/current",value-> {
            JSONObject invite=(JSONObject)value;
            boolean exists=invite.optBoolean("exists");
            boolean configured=invite.optBoolean("displayConfigured");
            if(!configured)u.state(b,R.drawable.ic_shield,"Invite codes are not set up","Configure the server display key before issuing or activating resident codes.",R.color.ap_amber,R.color.ap_ambg);
            if(exists) {
                Ui.Fields fields=h.newFields();
                fields.field("expiry","Valid until",LocalDate.parse(inviteDate(invite)).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-uuuu",Locale.ROOT)),TEXT);
                LinearLayout hint=u.row();
                hint.setPadding(u.dp(12),u.dp(11),u.dp(12),u.dp(11));
                android.graphics.drawable.GradientDrawable hintBackground=new android.graphics.drawable.GradientDrawable();
                hintBackground.setColor(u.color(R.color.ap_soft));
                hintBackground.setCornerRadius(u.dp(11));
                hint.setBackground(hintBackground);
                ImageView hintIcon=new ImageView(h.activity());
                hintIcon.setImageResource(R.drawable.ic_key);
                hintIcon.setImageTintList(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_pine)));
                hint.addView(hintIcon,new LinearLayout.LayoutParams(u.dp(14),u.dp(14)));
                TextView hintText=u.text("A new code revokes the previous code. Existing approved accounts keep access.",9,false,R.color.ap_sub);
                LinearLayout.LayoutParams hintTextParams=new LinearLayout.LayoutParams(0,-2,1);
                hintTextParams.setMarginStart(u.dp(10));
                hint.addView(hintText,hintTextParams);
                b.addView(hint);
                com.google.android.material.button.MaterialButton save=u.button(b,"Save",R.drawable.ic_check,true,()->safe(()-> {
                    LocalDate date=LocalDate.parse(fields.get("expiry"),java.time.format.DateTimeFormatter.ofPattern("dd-MM-uuuu",Locale.ROOT));
                    LocalDate today=LocalDate.now(ZoneId.of("Asia/Kolkata"));
                    if(date.isBefore(today)||date.isAfter(today.plusYears(1)))throw new IllegalArgumentException("Choose today or a date within one year.");
                    h.requestApi("PUT","/invites/current/expiry",obj("expiry",date.toString()),reply->go("invite"));
                }));
                compactInviteButton(save);
            } else u.note(b,"No current invite. Create a new code before changing its expiry or status.");
            if(!invite.isNull("displayError")&&!invite.optString("displayError").isEmpty())u.note(b,invite.optString("displayError"));
            else if(exists&&!invite.optBoolean("codeAvailable"))u.note(b,"This older code cannot be redisplayed. New code will replace it without changing approved accounts.");
            if(!configured)return;
            LinearLayout actions=u.row();
            com.google.android.material.button.MaterialButton rotate=u.button(actions,"New code",R.drawable.ic_rotate,false,()->u.confirm("Generate new code?","The current code will stop working. Approved accounts keep their access.","Generate",()->h.requestApi("POST","/invites",obj("requestKey",h.commandKey()),reply->{h.resetCommandKey();go("invite");})));
            compactInviteButton(rotate);
            LinearLayout.LayoutParams rotateParams=new LinearLayout.LayoutParams(0,-2,1);
            rotateParams.setMarginEnd(u.dp(7));
            rotateParams.topMargin=u.dp(12);
            rotate.setLayoutParams(rotateParams);
            if(exists) {
                boolean active=invite.optBoolean("active");
                com.google.android.material.button.MaterialButton status=u.button(actions,active?"Revoke":"Activate",active?R.drawable.ic_close:R.drawable.ic_check,false,()->u.confirm(active?"Revoke invite?":"Activate invite?",active?"The code stops working immediately. Approved accounts keep access.":"The existing code works until its saved expiry.",active?"Revoke":"Activate",()->h.requestApi("POST",active?"/invites/current/revoke":"/invites/current/activate",obj(),reply->h.refreshPage())));
                compactInviteButton(status);
                LinearLayout.LayoutParams statusParams=new LinearLayout.LayoutParams(0,-2,1);
                statusParams.topMargin=u.dp(12);
                status.setLayoutParams(statusParams);
                if(active) {
                    status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_redbg)));
                    status.setTextColor(u.color(R.color.ap_red));
                    status.setIconTint(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_red)));
                }
            }
            b.addView(actions);
        });
    }
    private void contacts(String kind) {
        title(kind.equals("VENDOR")?"Vendors":"Contacts");
        String route=kind.equals("VENDOR")?"vendor":"contact";
        if(h.isStaff())u.button(b,"Add "+route,R.drawable.ic_plus,true,()->go(route+"-form"));
        get("/ops/contacts?kind="+kind,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_phone,"No contacts yet","Add verified apartment contacts.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject c=arr.getJSONObject(i);
                row(card(),c.optString("name"),c.optString("category"),R.drawable.ic_user,route,c.optString("id"));
            }
        }
        );
    }
    private void contact(String kind) {
        title(kind.equals("VENDOR")?"Vendor":"Contact");
        get("/ops/contacts?kind="+kind,r-> {
            JSONObject c=find((JSONArray)r,id);
            details(c,"name","Name","category","Service","phone","Phone","amcEnd","AMC ends","notes","Notes");
            String phone=val(c,"phone");
            if(!phone.trim().isEmpty())u.button(b,"Call",R.drawable.ic_phone,true,()->safe(()->h.activity().startActivity(new Intent(Intent.ACTION_DIAL,Uri.fromParts("tel",phone,null)))));
            if(h.isStaff())u.button(b,"Edit",R.drawable.ic_user,false,()->go(kind.equals("VENDOR")?"vendor-form":"contact-form",id));
        }
        );
    }
    private void contactForm(String kind) {
        title(kind.equals("VENDOR")?"Vendor details":"Contact details");
        get("/ops/contacts?kind="+kind,r-> {
            JSONObject j=find((JSONArray)r,id);
            Ui.Fields f=h.newFields();
            f.field("name","Name",val(j,"name"),TEXT);
            f.field("category","Service / role",j.optString("category","Other"),TEXT);
            f.field("phone","Phone",val(j,"phone"),InputType.TYPE_CLASS_PHONE);
            f.field("notes","Notes",val(j,"notes"),TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            f.field("amcEnd","AMC expiry · optional YYYY-MM-DD",val(j,"amcEnd"),TEXT);
            f.check("public","Visible to residents",j.optBoolean("public",true));
            f.check("active","Active",j.optBoolean("active",true));
            submit("Save",()-> {
                JSONObject body=f.values();
                try {
                    body.put("kind",kind);
                } catch(JSONException e) {
                    throw new IllegalArgumentException(e);
                }
                write("/ops/contacts"+(id.isEmpty()?"":"/"+id),body,x->go(kind.equals("VENDOR")?"vendors":"contacts"));
            }
            );
        }
        );
    }
    private int categoryIcon(String category) {
        return switch(category) {
            case "Water","Plumbing"->R.drawable.ic_water;
            case "Lift"->R.drawable.ic_lift;
            case "Parking"->R.drawable.ic_car;
            case "Power","Generator"->R.drawable.ic_power;
            case "Security"->R.drawable.ic_shield;
            default->R.drawable.ic_tools;
        };
    }
    private void tickets(String kind) {
        title(kind.equals("ISSUE")?"Building issues":h.isStaff()?"Tenant complaints":"Private requests");
        com.google.android.material.button.MaterialButton report=u.button(b,"Report "+(kind.equals("ISSUE")?"issue":"request"),R.drawable.ic_plus,true,()->go(kind.equals("ISSUE")?"issue-categories":"complaint-form"));
        report.setTextSize(15);report.setIconSize(u.dp(23));report.setMinHeight(u.dp(54));
        if(kind.equals("ISSUE")) {
            LinearLayout c=card();
            u.gap(b,12);
            u.prominentIconRow(c,"My requests","Private flat issues",R.drawable.ic_user,()->go("complaints"));
            u.prominentIconRow(c,"Scheduled services","Preventive maintenance",R.drawable.ic_clock,()->go("services"));
            u.prominentIconRow(c,"Contacts","Emergency and service contacts",R.drawable.ic_phone,()->go("contacts"));
        }
        Ui.Fields searchFields=h.newFields();
        EditText search=searchFields.field("search",kind.equals("ISSUE")?"Search building issues":"Search requests","",TEXT);
        search.setTextSize(15);search.setMinHeight(u.dp(56));
        LinearLayout filters=u.row();
        b.addView(filters);
        LinearLayout list=u.column();
        u.gap(b,12);
        b.addView(list);
        get("/ops/tickets?kind="+kind,r-> {
            JSONArray arr=(JSONArray)r;
            String[] selected={"Active"};
            String[] options=kind.equals("ISSUE")?new String[]{"Active","Following","Resolved","All"}:new String[]{"Active","Closed","All"};
            ArrayList<com.google.android.material.button.MaterialButton> buttons=new ArrayList<>();
            for(String option:options) {
                com.google.android.material.button.MaterialButton button=u.button(filters,option,0,false,()-> {
                    selected[0]=option;
                    for(com.google.android.material.button.MaterialButton item:buttons) {
                        boolean active=item.getText().toString().equals(option);
                        int ink=u.color(active?R.color.ap_bg:R.color.ap_ink);
                        item.setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(active?R.color.ap_pine:R.color.ap_soft)));
                        item.setTextColor(ink);
                        item.setIconTint(android.content.res.ColorStateList.valueOf(ink));
                    }
                    renderTicketRows(arr,list,search.getText().toString(),selected[0],kind);
                });
                button.setSingleLine(true);
                button.setTextSize(12);
                button.setMinHeight(u.dp(50));
                button.setPadding(u.dp(4),0,u.dp(4),0);
                LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)button.getLayoutParams();
                params.width=0;params.weight=1;params.setMarginEnd(u.dp(8));button.setLayoutParams(params);
                buttons.add(button);
            }
            buttons.get(0).setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_pine)));
            buttons.get(0).setTextColor(u.color(R.color.ap_bg));
            buttons.get(0).setIconTint(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_bg)));
            search.addTextChangedListener(new android.text.TextWatcher() {
                public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                public void onTextChanged(CharSequence s,int start,int before,int count) {renderTicketRows(arr,list,s.toString(),selected[0],kind);}
                public void afterTextChanged(android.text.Editable value) {}
            });
            renderTicketRows(arr,list,search.getText().toString(),selected[0],kind);
        }
        );
    }
    private void renderTicketRows(JSONArray rows,LinearLayout list,String query,String filter,String kind) {
        list.removeAllViews();
        String term=query==null?"":query.trim().toLowerCase(Locale.ROOT);
        int shown=0;
        for(int i=0;i<rows.length();i++) {
            JSONObject ticket=rows.optJSONObject(i);
            if(ticket==null)continue;
            String status=ticket.optString("status","OPEN");
            boolean ended=Set.of("RESOLVED","CLOSED").contains(status);
            if(filter.equals("Active")&&ended)continue;
            if(filter.equals("Closed")&&!ended)continue;
            if(filter.equals("Resolved")&&!ended)continue;
            if(filter.equals("Following")&&!ticket.optBoolean("following"))continue;
            if(!term.isEmpty()&&!ticket.optString("title").toLowerCase(Locale.ROOT).contains(term)&&!ticket.optString("category").toLowerCase(Locale.ROOT).contains(term)&&!ticket.optString("scope").toLowerCase(Locale.ROOT).contains(term)&&!ticket.optString("description").toLowerCase(Locale.ROOT).contains(term))continue;
            String location=kind.equals("COMPLAINT")&&h.isStaff()?ticket.optString("flatLabel","Unknown flat"):ticket.optString("scope");
            String subtitle=friendly(status)+" · "+location+(kind.equals("ISSUE")?" · "+ticket.optInt("affectedCount")+" following":"");
            u.prominentIconRow(u.card(list),ticket.optString("title"),subtitle,categoryIcon(ticket.optString("category")),()->go("ticket",ticket.optString("id")));
            shown++;
        }
        if(shown==0)u.empty(list,R.drawable.ic_tools,term.isEmpty()&&filter.equals("Active")?"All clear":"No matching issues",term.isEmpty()&&filter.equals("Active")?"No active issues have been reported.":"Try another search or filter.");
    }
    private void issueCategories() {
        title("What needs attention?");
        u.note(b,"Immediate danger? Contact emergency services directly; this app is not monitored for emergency response.");
        for(String cat:CATEGORIES)u.iconRow(b,cat,"",categoryIcon(cat),()->go("issue-form",cat));
    }
    private void ticketForm(String kind) {
        title(kind.equals("ISSUE")?"Report issue":"Private request");
        String initialCategory=id.isEmpty()?"Other":id;
        Ui.Fields f=h.newFields();
        pick(f,"category","Category",CATEGORIES,initialCategory);
        f.field("title","Short title","",TEXT);
        f.field("description","What happened?","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        if(kind.equals("ISSUE"))f.field("scope","Affected area","ALL",TEXT);
        pick(f,"priority","Priority",new String[] {
            "NORMAL","HIGH","URGENT"
        }
        ,"NORMAL");
        if(kind.equals("COMPLAINT")&&h.isStaff())get("/ops/flats",r->choices(f,"flatId","Flat",(JSONArray)r,"label",false,""));
        submit("Submit",()-> {
            JSONObject payload=obj("kind",kind,"category",f.get("category"),"title",f.get("title"),"description",f.get("description"),"scope",kind.equals("ISSUE")?f.get("scope"):"PRIVATE","priority",f.get("priority"));
            if(kind.equals("COMPLAINT")&&h.isStaff())try {
                payload.put("flatId",f.get("flatId"));
            } catch(JSONException e) {
                throw new IllegalArgumentException(e);
            }
            write("/ops/tickets",payload,r->go("ticket",((JSONObject)r).getString("id")));
        }
        );
        if(kind.equals("ISSUE"))get("/ops/tickets?kind=ISSUE",r-> {
            JSONArray arr=(JSONArray)r;
            boolean heading=false;
            for(int i=0; i<arr.length(); i++) {
                JSONObject t=arr.getJSONObject(i);
                if(!t.optString("category").equals(initialCategory)||java.util.Arrays.asList("CLOSED","RESOLVED").contains(t.optString("status")))continue;
                if(!heading) {
                    u.heading(b,"Already reported");
                    heading=true;
                }
                row(b,t.optString("title"),"Open instead of reporting twice",R.drawable.ic_tools,"ticket",t.optString("id"));
            }
        }
        );
    }
    private void ticket() {
        title("Issue details");
        get("/ops/tickets/"+id,r-> {
            JSONObject t=(JSONObject)r;
            u.hero(b,friendly(t.optString("status")),t.optString("title"),t.optString("category")+" · "+t.optString("scope"));
            LinearLayout c=card();
            c.addView(u.text(t.optString("description"),14,false,R.color.ap_sub));
            u.kv(c,"Priority",friendly(t.optString("priority")));
            u.kv(c,"Vendor",val(t,"vendorName"));
            u.kv(c,"Expected update",val(t,"eta"));
            if("ISSUE".equals(t.optString("kind"))) {
                u.kv(c,"Following",String.valueOf(t.optInt("affectedCount")));
                if(h.isStaff())u.button(b,"View affected flats",R.drawable.ic_users,false,()->go("affected",id));
                else if(!t.optBoolean("following")&&!java.util.Arrays.asList("RESOLVED","CLOSED").contains(t.optString("status")))u.button(b,"I'm affected",R.drawable.ic_users,true,()->write("/ops/tickets/"+id+"/follow",obj(),x->h.refreshPage()));
            }
            if(h.isStaff()||"RESOLVED".equals(t.optString("status")))u.button(b,"Update status",R.drawable.ic_check,true,()->go("ticket-update",id));
            fileLink("TICKET",id);
            u.heading(b,"Updates");
            JSONArray events=t.optJSONArray("events");
            if(events!=null)for(int i=0; i<events.length(); i++) {
                JSONObject e=events.getJSONObject(i);
                LinearLayout ev=card();
                u.kv(ev,e.optString("actor"),friendly(val(e,"status")));
                ev.addView(u.text(e.optString("message"),13,false,R.color.ap_sub));
                u.note(ev,e.optString("createdAt"));
            }
            Ui.Fields f=h.newFields();
            f.field("message","Add update","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            submit("Post update",()->write("/ops/tickets/"+id+"/comments",f.values(),x->h.refreshPage()));
        }
        );
    }
    private void affected() {
        title("Affected flats");
        get("/ops/tickets/"+id,ticketValue-> {
            JSONObject ticket=(JSONObject)ticketValue;
            get("/ops/tickets/"+id+"/affected",rowsValue-> {
                JSONArray rows=(JSONArray)rowsValue;
                u.hero(b,Ui.count(rows.length(),"flat","flats"),ticket.optString("title"),ticket.optString("scope"));
                if(rows.length()==0)u.empty(b,R.drawable.ic_tools,"No affected flats yet","Residents can follow an active shared-area issue.");
                for(int i=0;i<rows.length();i++) {
                    JSONObject affectedRow=rows.getJSONObject(i);
                    row(card(),affectedRow.optString("flatLabel"),affectedRow.optString("name")+" · Block "+affectedRow.optString("block"),R.drawable.ic_home,"directory-detail",affectedRow.optString("userId"));
                }
            });
        });
    }
    private void ticketUpdate() {
        title("Update issue");
        get("/ops/tickets/"+id,r-> {
            JSONObject t=(JSONObject)r;
            get("/ops/contacts?kind=VENDOR",rs-> {
                Ui.Fields f=h.newFields();
                pick(f,"status","Status",h.isStaff()?new String[] {
                    "OPEN","ASSIGNED","IN_PROGRESS","RESOLVED","CLOSED"
                }
                :new String[] {
                    "CLOSED","OPEN"
                }
                ,h.isStaff()?t.optString("status"):"CLOSED");
                choices(f,"vendorId","Assigned vendor",(JSONArray)rs,"name",true,val(t,"vendorId"));
                f.field("eta","Expected restoration · IST YYYY-MM-DDTHH:mm",local(val(t,"eta")),TEXT);
                f.field("note","Update / reason","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                if(h.isStaff()&&"ISSUE".equals(t.optString("kind")))f.check("notifyAll","Notify the whole apartment",false);
                submit("Save update",()-> {
                    JSONObject p=f.values();
                    try {
                        p.put("eta",f.get("eta").trim().isEmpty()?"":instant(f.get("eta")));
                    } catch(JSONException e) {
                        throw new IllegalArgumentException(e);
                    }
                    write("/ops/tickets/"+id+"/status",p,x->go("ticket",id));
                }
                );
            }
            );
        }
        );
    }
    private void services() {
        title("Service schedule");
        if(h.isStaff())u.button(b,"Schedule service",R.drawable.ic_plus,true,()->go("service-form"));
        get("/ops/services",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_lift,"No schedules yet","Set reminders for lift service, cleaning and equipment checks.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject s=arr.getJSONObject(i);
                row(card(),s.optString("title"),s.optString("nextOn")+" · "+friendly(s.optString("frequency"))+" · "+serviceStatus(s),categoryIcon(s.optString("category")),"service",s.optString("id"));
            }
        }
        );
    }
    private void service() {
        title("Scheduled service");
        get("/ops/services/"+id,r-> {
            JSONObject s=(JSONObject)r;
            details(s,"title","Service","vendorName","Vendor","nextOn","Next due","frequency","Frequency");
            u.kv(b,"Schedule status",s.optBoolean("active",true)?"Active":"Paused");
            if(h.isStaff()) {
                u.button(b,"Complete service",R.drawable.ic_check,true,()->go("service-complete",id));
                u.button(b,"Edit schedule",R.drawable.ic_calendar,false,()->go("service-form",id));
            }
            u.heading(b,"Service history");
            JSONArray visits=s.optJSONArray("visits");
            if(visits!=null)for(int i=0; i<visits.length(); i++) {
                JSONObject v=visits.getJSONObject(i);
                LinearLayout c=card();
                u.kv(c,"Completed",v.optString("completedOn"));
                c.addView(u.text(v.optString("note"),13,false,R.color.ap_sub));
                if(v.has("cost"))u.kv(c,"Cost",Ui.money(v.opt("cost")));
                if(!val(v,"expenseId").trim().isEmpty())u.button(c,"View expense",R.drawable.ic_expense,false,()->go("expense",v.optString("expenseId")));
            }
        }
        );
    }
    private void serviceForm() {
        title("Schedule service");
        get("/ops/contacts?kind=VENDOR",rs-> {
            JSONArray vendors=(JSONArray)rs;
            FeatureHost.Reply build=r-> {
                JSONObject j=(JSONObject)r;
                Ui.Fields f=h.newFields();
                f.field("title","Service name",val(j,"title"),TEXT);
                pick(f,"category","Category",CATEGORIES,j.optString("category","Lift"));
                choices(f,"vendorId","Vendor · optional",vendors,"name",true,val(j,"vendorId"));
                pick(f,"frequency","Repeat",FREQUENCIES,j.optString("frequency","MONTHLY"));
                f.field("nextOn","Next due · YYYY-MM-DD",j.optString("nextOn",today()),TEXT);
                f.check("active","Active schedule",j.optBoolean("active",true));
                submit("Save schedule",()->write("/ops/services"+(id.isEmpty()?"":"/"+id),f.values(),x->go("services")));
            };
            if(id.isEmpty())build.done(new JSONObject());
            else get("/ops/services/"+id,build);
        }
        );
    }
    private void serviceComplete() {
        title("Complete service");
        get("/ops/services/"+id,r-> {
            JSONObject s=(JSONObject)r;
            Ui.Fields f=h.newFields();
            u.note(b,s.optString("title")+" · scheduled "+s.optString("nextOn"));
            f.field("completedOn","Completed on · YYYY-MM-DD",today(),TEXT);
            f.field("cost","Cost · ₹", "0",MONEY);
            f.field("note","Work completed","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            f.check("paid","Vendor payment completed",false);
            u.note(b,"With a cost: creates one expense. Unchecked payments remain drafts.");
            submit("Complete",()-> {
                LocalDate completed;
                try {
                    completed=LocalDate.parse(f.get("completedOn").trim());
                } catch(RuntimeException e) {
                    throw new IllegalArgumentException("Enter a valid completion date.");
                }
                if(completed.isAfter(LocalDate.now(ZoneId.of("Asia/Kolkata"))))throw new IllegalArgumentException("Completion date cannot be in the future.");
                BigDecimal cost=amount(f,"cost");
                if(cost.signum()<0)throw new IllegalArgumentException("Service cost cannot be negative.");
                if(f.get("note").trim().isEmpty())throw new IllegalArgumentException("Describe the work completed.");
                write("/ops/services/"+id+"/complete",obj("scheduledOn",s.optString("nextOn"),"completedOn",completed.toString(),"cost",cost,"note",f.get("note").trim(),"paid",f.checked("paid")),x->go("service",id));
            });
        }
        );
    }
    private void bookings() {
        title("Events & bookings");
        u.button(b,"New event",R.drawable.ic_calendar,true,()->go("event-form"));
        LinearLayout links=card();
        row(links,"Event calendar","Approved reservation dates",R.drawable.ic_calendar,"event-calendar");
        row(links,"Check availability","Spaces & guest parking",R.drawable.ic_car,"availability");
        row(links,"Spaces & parking","Configured resources",R.drawable.ic_home,"resources");
        row(links,"Booking rules","Review apartment booking rules",R.drawable.ic_shield,"booking-policy");
        Ui.Fields searchFields=h.newFields();
        EditText search=searchFields.field("search","Search events or spaces",h.pageDraft("booking-list").optString("search"),TEXT);
        LinearLayout filters=u.row();
        b.addView(filters);
        LinearLayout list=u.column();
        b.addView(list);
        get("/ops/bookings",r-> {
            JSONArray arr=(JSONArray)r;
            String initial=h.pageDraft("booking-list").optString("filter","Upcoming");
            String[] selected={initial};
            String[] options={"Upcoming","Mine","Past","All"};
            ArrayList<com.google.android.material.button.MaterialButton> buttons=new ArrayList<>();
            Runnable render=()->renderBookingRows(arr,list,search.getText().toString(),selected[0]);
            for(String option:options) {
                com.google.android.material.button.MaterialButton button=u.button(filters,option,0,false,()-> {
                    selected[0]=option;
                    h.savePageDraft("booking-list",obj("search",search.getText().toString(),"filter",option));
                    for(com.google.android.material.button.MaterialButton item:buttons) {
                        boolean active=item.getText().toString().equals(option);
                        int ink=u.color(active?R.color.ap_bg:R.color.ap_ink);
                        item.setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(active?R.color.ap_pine:R.color.ap_soft)));
                        item.setTextColor(ink);
                        item.setIconTint(android.content.res.ColorStateList.valueOf(ink));
                    }
                    render.run();
                });
                button.setSingleLine(true);
                button.setTextSize(10);
                button.setPadding(u.dp(2),0,u.dp(2),0);
                LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)button.getLayoutParams();
                params.width=0;params.weight=1;params.setMarginEnd(u.dp(4));button.setLayoutParams(params);
                buttons.add(button);
            }
            if(!Arrays.asList(options).contains(initial))selected[0]="Upcoming";
            for(com.google.android.material.button.MaterialButton item:buttons) {
                boolean active=item.getText().toString().equals(selected[0]);
                int ink=u.color(active?R.color.ap_bg:R.color.ap_ink);
                item.setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(active?R.color.ap_pine:R.color.ap_soft)));
                item.setTextColor(ink);
                item.setIconTint(android.content.res.ColorStateList.valueOf(ink));
            }
            search.addTextChangedListener(new android.text.TextWatcher() {
                public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                public void onTextChanged(CharSequence s,int start,int before,int count) {
                    h.savePageDraft("booking-list",obj("search",s.toString(),"filter",selected[0]));
                    render.run();
                }
                public void afterTextChanged(android.text.Editable value) {}
            });
            render.run();
        }
        );
    }
    private void renderBookingRows(JSONArray rows,LinearLayout list,String query,String filter) {
        list.removeAllViews();
        String term=query==null?"":query.trim().toLowerCase(Locale.ROOT);
        String account=h.client().account().optString("id");
        String flat=h.client().account().optString("flatId");
        int shown=0;
        for(int i=0;i<rows.length();i++) {
            JSONObject event=rows.optJSONObject(i);
            if(event==null)continue;
            String status=event.optString("status","PENDING");
            boolean past=Set.of("CANCELLED","REJECTED","COMPLETED").contains(status);
            if(filter.equals("Upcoming")&&past)continue;
            if(filter.equals("Past")&&!past)continue;
            if(filter.equals("Mine")&&!account.equals(event.optString("createdBy"))&&!flat.equals(event.optString("flatId")))continue;
            if(!term.isEmpty()&&!event.optString("title").toLowerCase(Locale.ROOT).contains(term)&&!event.optString("eventType").toLowerCase(Locale.ROOT).contains(term)&&!event.optString("flatLabel").toLowerCase(Locale.ROOT).contains(term)&&!event.optString("notes").toLowerCase(Locale.ROOT).contains(term))continue;
            String timing=com.rapolus.apartmentpilotai.data.DateText.humanize(event.optString("startsAt"));
            row(u.card(list),event.optString("title"),friendly(status)+" · "+timing,R.drawable.ic_calendar,"booking",event.optString("id"));
            shown++;
        }
        if(shown==0)u.empty(list,R.drawable.ic_calendar,term.isEmpty()&&filter.equals("Upcoming")?"No upcoming events":"No matching events",term.isEmpty()&&filter.equals("Upcoming")?"Request a space for your next gathering.":"Try another search or filter.");
    }
    private void booking() {
        title("Event details");
        get("/ops/bookings/"+id,r-> {
            JSONObject e=(JSONObject)r;
            u.hero(b,friendly(e.optString("status")),e.optString("title"),e.optString("startsAt"));
            details(e,"eventType","Event","flatLabel","Flat","guests","Guests","notes","Notes","decisionNote","Decision");
            LinearLayout timing=card();
            u.kv(timing,"Start",e.optString("startsAt"));
            u.kv(timing,"End",e.optString("endsAt"));
            JSONArray items=e.optJSONArray("items");
            if(items!=null)for(int i=0; i<items.length(); i++) {
                JSONObject v=items.getJSONObject(i);
                u.kv(timing,(v.optBoolean("offered")?"Alternative: ":"")+v.optString("name"),v.optString("quantity"));
            }
            String s=e.optString("status");
            if("ALTERNATIVE".equals(s)) {
                u.kv(timing,"Proposed start",val(e,"offeredStart"));
                u.kv(timing,"Proposed end",val(e,"offeredEnd"));
                if(h.client().account().optString("id").equals(e.optString("createdBy")))u.button(b,"Accept alternative",R.drawable.ic_check,true,()->decision(e,"ACCEPT","I accept the offered spaces and times."));
            }
            if(h.isAdmin()&&"PENDING".equals(s)) {
                u.button(b,"Approve booking",R.drawable.ic_check,true,()->decision(e,"APPROVE","Approved"));
                u.button(b,"Offer alternative",R.drawable.ic_calendar,false,()->go("booking-alternative",id));
            }
            if(h.isAdmin()&&java.util.Arrays.asList("PENDING","ALTERNATIVE").contains(s)) {
                Ui.Fields reason=new Ui.Fields(u,b);
                reason.field("reason","Reason for rejection","",TEXT);
                u.button(b,"Reject",R.drawable.ic_error,false,()->decision(e,"REJECT",reason.get("reason")));
            }
            if(java.util.Arrays.asList("PENDING","APPROVED","ALTERNATIVE").contains(s)) {
                Ui.Fields cancel=new Ui.Fields(u,b);
                cancel.field("reason","Cancellation reason","",TEXT);
                u.button(b,"Cancel booking",R.drawable.ic_error,false,()->decision(e,"CANCEL",cancel.get("reason")));
            }
            if(h.isAdmin()&&"APPROVED".equals(s))u.button(b,"Complete event",R.drawable.ic_check,false,()->decision(e,"COMPLETE","Event ended"));
            u.heading(b,"Activity");
            JSONArray events=e.optJSONArray("events");
            if(events!=null)for(int i=0; i<events.length(); i++) {
                JSONObject v=events.getJSONObject(i);
                LinearLayout c=card();
                u.kv(c,friendly(v.optString("status")),v.optString("createdAt"));
                u.note(c,v.optString("note"));
            }
        }
        );
    }
    private void decision(JSONObject e,String action,String note) {
        u.confirm(friendly(action)+" booking?",action.equals("APPROVE")||action.equals("ACCEPT")?"Availability is checked again. A pending request does not reserve space.":note,"Confirm",()->write("/ops/bookings/"+id+"/decision",obj("action",action,"note",note,"revision",e.optInt("revision")),r->h.refreshPage()));
    }
    private void bookingForm(boolean offer) {
        title(offer?"Offer alternative":"Request an event");
        get("/ops/resources",rs-> {
            JSONArray resources=(JSONArray)rs;
            FeatureHost.Reply build=r-> {
                JSONObject existing=(JSONObject)r;
                Ui.Fields f=h.newFields();
                if(!offer) {
                    f.field("title","Event title","",TEXT);
                    pick(f,"eventType","Event type",new String[] {
                        "BIRTHDAY","MARRIAGE","FAMILY","FESTIVAL","MEETING","OTHER"
                    }
                    ,"BIRTHDAY");
                    f.field("guests","Expected guests","10",NUM);
                }
                f.field("start","Start · IST YYYY-MM-DDTHH:mm",offer?local(existing.optString("startsAt")):timeDefault(),TEXT);
                f.field("end","End incl. cleanup · IST YYYY-MM-DDTHH:mm",offer?local(existing.optString("endsAt")):timeDefault().replace("17:00","21:00"),TEXT);
                u.heading(b,"Spaces & guest parking");
                if(resources.length()==0)u.note(b,"Ask the Admin to configure common spaces first.");
                for(int i=0; i<resources.length(); i++) {
                    JSONObject resource=resources.getJSONObject(i);
                    if(!resource.optBoolean("bookable"))continue;
                    String selected="0";
                    JSONArray selectedItems=existing.optJSONArray("items");
                    if(selectedItems!=null)for(int n=0; n<selectedItems.length(); n++) {
                        JSONObject line=selectedItems.getJSONObject(n);
                        if(line.optString("resourceId").equals(resource.optString("id"))&&!line.optBoolean("offered"))selected=line.optString("quantity");
                    }
                    f.field("qty:"+resource.optString("id"),resource.optString("name")+" · "+friendly(resource.optString("kind"))+" (0–"+resource.optInt("capacity")+")",selected,NUM);
                }
                f.field("notes",offer?"Explain the alternative":"Notes · optional","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                if(!offer) {
                    f.check("acceptRules","I accept the apartment rules",false);
                    u.button(b,"Booking rules",R.drawable.ic_shield,false,()->get("/ops/settings",x->u.confirm("Booking rules",((JSONObject)x).optString("bookingRules"),"Close",()-> {
                    }
                    )));
                }
                submit(offer?"Send alternative":"Review & submit",()-> {
                    JSONArray items=new JSONArray();
                    for(int i=0; i<resources.length(); i++) {
                        JSONObject resource=resources.optJSONObject(i);
                        String value=f.get("qty:"+resource.optString("id"));
                        int quantity=value.trim().isEmpty()?0:Integer.parseInt(value);
                        if(quantity>0)items.put(obj("resourceId",resource.optString("id"),"quantity",quantity));
                    }
                    if(items.length()==0)throw new IllegalArgumentException("Choose at least one space or parking quantity.");
                    JSONObject payload=offer?obj("action","OFFER","revision",existing.optInt("revision"),"note",f.get("notes")):obj("title",f.get("title"),"eventType",f.get("eventType"),"guests",Integer.parseInt(f.get("guests")),"notes",f.get("notes"),"acceptRules",f.checked("acceptRules"));
                    try {
                        payload.put("start",instant(f.get("start")));
                        payload.put("end",instant(f.get("end")));
                        payload.put("items",items);
                    } catch(JSONException e) {
                        throw new IllegalArgumentException(e);
                    }
                    u.confirm(offer?"Send alternative?":"Submit event request?",f.get("start")+" to "+f.get("end")+" IST\n"+items.length()+" resource selections. No booking fee.\n"+(offer?"The requester must accept.":"Admin approval is required."),"Submit",()->write(offer?"/ops/bookings/"+id+"/decision":"/ops/bookings",payload,x-> {
                        String bookingId=((JSONObject)x).optString("id",id);
                        if(offer)go("booking",bookingId);
                        else h.openPageWithBackTarget("booking",bookingId,"events");
                    }));
                }
                );
            };
            if(offer)get("/ops/bookings/"+id,build);
            else build.done(new JSONObject());
        }
        );
    }
    private void availability() {
        title("Availability");
        Ui.Fields f=h.newFields();
        f.field("start","Start · IST YYYY-MM-DDTHH:mm",timeDefault(),TEXT);
        f.field("end","End · IST YYYY-MM-DDTHH:mm",timeDefault().replace("17:00","21:00"),TEXT);
        LinearLayout results=u.column();
        submit("Check spaces",()-> {
            String start=instant(f.get("start")),end=instant(f.get("end"));
            get("/ops/availability?start="+enc(start)+"&end="+enc(end),r-> {
                results.removeAllViews();
                JSONArray arr=(JSONArray)r;
                for(int i=0; i<arr.length(); i++) {
                    JSONObject x=arr.getJSONObject(i);
                    LinearLayout c=u.card(results);
                    u.kv(c,x.optString("name"),x.optInt("available")+" / "+x.optInt("capacity")+" available");
                    u.note(c,friendly(x.optString("kind")));
                }
            }
            );
        }
        );
        b.addView(results);
        u.note(b,"Reservation availability, not physical vehicle detection. Rechecked at approval.");
    }
    private void resources() {
        title("Spaces & parking");
        if(h.isAdmin())u.button(b,"Add space",R.drawable.ic_plus,true,()->go("resource-form"));
        get("/ops/resources",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_calendar,"No spaces configured","Admin can add a terrace, event area and guest parking.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                row(card(),x.optString("name"),friendly(x.optString("kind"))+" · "+x.optInt("capacity")+" capacity",x.optString("kind").equals("SPACE")?R.drawable.ic_home:R.drawable.ic_car,"resource",x.optString("id"));
            }
        }
        );
    }
    private void resource() {
        title("Space details");
        get("/ops/resources",r-> {
            JSONObject x=find((JSONArray)r,id);
            details(x,"name","Space","kind","Type","capacity","Capacity","bookable","Bookable","ownerConsent","Owner released");
            if(h.isAdmin()) {
                u.button(b,"Edit space",R.drawable.ic_settings,true,()->go("resource-form",id));
                u.button(b,"Block dates",R.drawable.ic_calendar,false,()->go("resource-block-form",id));
            }
            String flat=h.client().account().optString("flatId","");
            if(!flat.trim().isEmpty()&&flat.equals(x.optString("privateFlatId")))u.button(b,"Share my parking",R.drawable.ic_car,true,()->go("parking-share",id));
            JSONArray blocks=x.optJSONArray("blocks");
            if(blocks!=null)for(int i=0; i<blocks.length(); i++) {
                JSONObject v=blocks.getJSONObject(i);
                LinearLayout c=card();
                u.kv(c,"Unavailable",v.optString("startsAt")+" — "+v.optString("endsAt"));
                u.note(c,v.optString("reason"));
                if(h.isAdmin())u.button(c,"Remove block",R.drawable.ic_error,false,()->u.confirm("Restore availability?","Approved reservations are not removed.","Remove block",()->h.requestApi("DELETE","/ops/resource-blocks/"+v.optString("id"),null,z->h.refreshPage())));
            }
        }
        );
    }
    private void resourceForm() {
        title("Configure space");
        get("/ops/resources",rs-> {
            JSONArray resources=(JSONArray)rs;
            JSONObject j=find(resources,id);
            get("/ops/flats",r-> {
                Ui.Fields f=h.newFields();
                f.field("name","Name",val(j,"name"),TEXT);
                pick(f,"kind","Type",new String[] {
                    "SPACE","CAR","BIKE"
                }
                ,j.optString("kind","SPACE"));
                f.field("capacity","Capacity · simultaneous units",j.optString("capacity","1"),NUM);
                f.check("bookable","Bookable",j.optBoolean("bookable",true));
                choices(f,"privateFlatId","Private flat · otherwise common",(JSONArray)r,"label",true,val(j,"privateFlatId"));
                u.note(b,"Private bays need an explicit release from their assigned flat account. Exits and access routes must not be configured as bookable spaces.");
                u.heading(b,"Overlapping physical spaces");
                JSONArray linked=j.optJSONArray("conflicts");
                for(int i=0; i<resources.length(); i++) {
                    JSONObject resource=resources.getJSONObject(i);
                    if(resource.optString("id").equals(id))continue;
                    boolean selected=false;
                    if(linked!=null)for(int n=0; n<linked.length(); n++)selected|=resource.optString("id").equals(linked.getJSONObject(n).optString("otherId"));
                    f.check("conflict:"+resource.optString("id"),resource.optString("name"),selected);
                }
                submit("Save space",()-> {
                    JSONObject p=f.values();
                    JSONArray conflicts=new JSONArray();
                    for(int i=0; i<resources.length(); i++) {
                        JSONObject resource=resources.optJSONObject(i);
                        if(f.checked("conflict:"+resource.optString("id")))conflicts.put(resource.optString("id"));
                    }
                    try {
                        p.put("capacity",Integer.parseInt(f.get("capacity")));
                        p.put("conflicts",conflicts);
                    } catch(JSONException e) {
                        throw new IllegalArgumentException(e);
                    }
                    write("/ops/resources"+(id.isEmpty()?"":"/"+id),p,z->go("resources"));
                }
                );
            }
            );
        }
        );
    }
    private void resourceBlock() {
        title("Unavailable dates");
        Ui.Fields f=h.newFields();
        f.field("start","Start · IST YYYY-MM-DDTHH:mm",timeDefault(),TEXT);
        f.field("end","End · IST YYYY-MM-DDTHH:mm",timeDefault().replace("17:00","21:00"),TEXT);
        f.field("reason","Reason","Scheduled work",TEXT);
        submit("Block dates",()->write("/ops/resources/"+id+"/blocks",obj("start",instant(f.get("start")),"end",instant(f.get("end")),"reason",f.get("reason")),r->go("resource",id)));
    }
    private void parkingShare() {
        title("Share my parking");
        Ui.Fields f=h.newFields();
        f.field("start","Available from · IST YYYY-MM-DDTHH:mm",timeDefault(),TEXT);
        f.field("end","Available until · IST YYYY-MM-DDTHH:mm",timeDefault().replace("17:00","21:00"),TEXT);
        f.check("consent","I release my assigned space for this period",false);
        submit("Save availability",()->write("/ops/resources/"+id+"/release",obj("start",instant(f.get("start")),"end",instant(f.get("end")),"consent",f.checked("consent")),r->go("resource",id)));
    }
    private void notices() {
        title("Notices");
        if(h.isStaff())u.button(b,"Create notice",R.drawable.ic_notice,true,()->go("notice-templates"));
        Ui.Fields searchFields=h.newFields();
        EditText search=searchFields.field("search","Search notices","",TEXT);
        LinearLayout filters=u.row();
        b.addView(filters);
        LinearLayout list=u.column();
        b.addView(list);
        get("/notices",r-> {
            JSONArray rows=(JSONArray)r;
            String[] selected={"All"};
            String[] options=h.isStaff()?new String[]{"All","Pinned","Drafts","Scheduled"}:new String[]{"All","Pinned"};
            ArrayList<com.google.android.material.button.MaterialButton> buttons=new ArrayList<>();
            for(String option:options) {
                com.google.android.material.button.MaterialButton button=u.button(filters,option,0,false,()-> {
                    selected[0]=option;
                    for(com.google.android.material.button.MaterialButton item:buttons) {
                        boolean active=item.getText().toString().equals(option);
                        int ink=u.color(active?R.color.ap_bg:R.color.ap_ink);
                        item.setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(active?R.color.ap_pine:R.color.ap_soft)));
                        item.setTextColor(ink);
                        item.setIconTint(android.content.res.ColorStateList.valueOf(ink));
                    }
                    renderNoticeRows(rows,list,search.getText().toString(),selected[0]);
                });
                button.setSingleLine(true);
                button.setTextSize(11);
                button.setPadding(u.dp(4),0,u.dp(4),0);
                LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)button.getLayoutParams();
                params.width=0;params.weight=1;params.setMarginEnd(u.dp(6));button.setLayoutParams(params);
                buttons.add(button);
            }
            if(!buttons.isEmpty()) {
                buttons.get(0).setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_pine)));
                buttons.get(0).setTextColor(u.color(R.color.ap_bg));
                buttons.get(0).setIconTint(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_bg)));
            }
            search.addTextChangedListener(new android.text.TextWatcher() {
                public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                public void onTextChanged(CharSequence s,int start,int before,int count) {renderNoticeRows(rows,list,s.toString(),selected[0]);}
                public void afterTextChanged(android.text.Editable value) {}
            });
            renderNoticeRows(rows,list,search.getText().toString(),selected[0]);
        }
        );
    }
    private void renderNoticeRows(JSONArray rows,LinearLayout list,String query,String filter) {
        list.removeAllViews();
        String term=query==null?"":query.trim().toLowerCase(Locale.ROOT);
        int shown=0;
        for(int i=0;i<rows.length();i++) {
            JSONObject n=rows.optJSONObject(i);
            if(n==null)continue;
            String status=n.optString("status","DRAFT");
            boolean scheduled=n.optBoolean("scheduleConfirmed")||status.equals("SCHEDULED");
            if(filter.equals("Pinned")&&!n.optBoolean("pinned"))continue;
            if(filter.equals("Drafts")&&(!status.equals("DRAFT")||scheduled))continue;
            if(filter.equals("Scheduled")&&!scheduled)continue;
            if(!term.isEmpty()&&!n.optString("title").toLowerCase(Locale.ROOT).contains(term)&&!n.optString("body").toLowerCase(Locale.ROOT).contains(term))continue;
            String statusLabel=scheduled?"Scheduled":friendly(status);
            LinearLayout card=u.card(list);
            u.iconRow(card,(n.optBoolean("pinned")?"★ ":"")+n.optString("title"),friendly(n.optString("audience"))+" · "+statusLabel,R.drawable.ic_notice,()->go("notice",n.optString("id")));
            String body=n.optString("body").trim();
            if(!body.isEmpty())u.note(card,body.length()>100?body.substring(0,100)+"…":body);
            shown++;
        }
        if(shown==0)u.empty(list,R.drawable.ic_notice,term.isEmpty()&&filter.equals("All")?"No notices yet":"No matching notices",term.isEmpty()&&filter.equals("All")?"Updates from your apartment team.":"Try another search or filter.");
    }
    private void notice() {
        title("Apartment notice");
        get("/notices/"+id,r-> {
            JSONObject n=(JSONObject)r;
            LinearLayout c=card();
            c.addView(u.text(n.optString("title"),24,true,R.color.ap_ink));
            u.note(c,friendly(n.optString("audience"))+" · "+friendly(n.optString("status")));
            c.addView(u.text(n.optString("body"),15,false,R.color.ap_sub));
            fileLink("NOTICE",id);
            if(h.isStaff()) {
                if(n.optString("status").equals("DRAFT")) {
                    u.button(b,"Edit draft",R.drawable.ic_settings,false,()->go("notice-form",id));
                    if(!n.optBoolean("scheduleConfirmed"))u.button(b,"Preview notice",R.drawable.ic_notice,true,()->go("notice-preview",id));
                    else u.note(b,"Scheduled · "+n.optString("scheduledAt")+" · in-app delivery");
                } else u.button(b,n.optBoolean("pinned")?"Unpin":"Pin notice",R.drawable.ic_notice,false,()->h.requestApi("POST","/notices/"+id+"/pin",obj("pinned",!n.optBoolean("pinned")),x->h.refreshPage()));
                if(n.optBoolean("phoneNotify"))u.note(b,"Phone notification requested · phone delivery "+Ui.delivery(n.optString("externalDeliveryStatus")));
                JSONArray recipients=n.optJSONArray("recipients");
                if(recipients!=null) {
                    u.heading(b,"Audience · "+recipients.length());
                    for(int i=0; i<recipients.length(); i++) {
                        JSONObject x=recipients.getJSONObject(i);
                        u.kv(card(),x.optString("name"),!x.isNull("acknowledgedAt")?"Acknowledged":!x.isNull("readAt")?"Read":"Not opened");
                    }
                }
            } else {
                boolean read=!n.isNull("myReadAt")&&n.has("myReadAt"),acknowledged=!n.isNull("myAcknowledgedAt")&&n.has("myAcknowledgedAt");
                if(acknowledged)u.note(b,"✓ You acknowledged this notice on "+n.optString("myAcknowledgedAt")+".");
                else if(read&&!n.optBoolean("acknowledge"))u.note(b,"✓ Read");
                if(!read)u.button(b,"Mark read",R.drawable.ic_check,false,()->h.requestApi("POST","/notices/"+id+"/read",obj(),x->{u.message("Marked read",false);h.refreshPage();}));
                if(n.optBoolean("acknowledge")&&!acknowledged)u.button(b,"Acknowledge",R.drawable.ic_check,true,()->u.confirm("Acknowledge notice?","Confirm you have read this announcement.","Acknowledge",()->h.requestApi("POST","/notices/"+id+"/read",obj("acknowledge",true),x->{u.message("Acknowledged",false);h.refreshPage();})));
            }
        }
        );
    }
    private void noticeTemplates() {
        title("Choose a notice");
        u.button(b,"Water",R.drawable.ic_water,false,()->useNoticeTemplate("Water supply interruption","Water supply will be paused between the announced hours. Please store water in advance.","SERVICE_ALERT","Water"));
        u.button(b,"Power",R.drawable.ic_power,false,()->useNoticeTemplate("Power interruption","Common-area power is temporarily unavailable. Updates will follow here.","SERVICE_ALERT","Power"));
        u.button(b,"Meeting",R.drawable.ic_users,false,()->useNoticeTemplate("Apartment meeting","Please join the apartment meeting at the common area.","GENERAL","Other"));
        u.button(b,"Maintenance",R.drawable.ic_receipt,false,()->useNoticeTemplate("Maintenance reminder","Please pay your outstanding maintenance and submit the payment reference in the app.","MAINTENANCE","Other"));
        u.button(b,"Write a notice",R.drawable.ic_plus,true,()-> {
            h.clearPageDraft("noticeTemplate");
            go("notice-form");
        });
    }
    private void useNoticeTemplate(String title,String body,String type,String category) {
        h.savePageDraft("noticeTemplate",obj("title",title,"body",body,"noticeType",type,"category",category));
        go("notice-form");
    }
    private void noticeForm() {
        title("Send notice");
        FeatureHost.Reply build=r-> {
            JSONObject loaded=(JSONObject)r;
            JSONObject n=id.isEmpty()&&!loaded.has("title")?h.pageDraft("noticeTemplate"):loaded;
            Ui.Fields f=h.newFields();
            f.field("title","Title",val(n,"title"),TEXT);
            f.field("body","Message",val(n,"body"),TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            pick(f,"noticeType","Type",new String[] {"GENERAL","MAINTENANCE","SERVICE_ALERT","EMERGENCY"},n.optString("noticeType","GENERAL"));
            pick(f,"category","Category",CATEGORIES,n.optString("category","Other"));
            pick(f,"audience","Send to",new String[] {
                "ALL","BLOCK","SELECTED","UNPAID"
            }
            ,n.optString("audience","ALL"));
            f.field("audienceValue","Block or selected flat labels · optional",val(n,"audienceValue"),TEXT);
            f.field("scheduledAt","Schedule · IST YYYY-MM-DDTHH:mm · optional",val(n,"scheduledAt").trim().isEmpty()?"":local(val(n,"scheduledAt")),TEXT);
            f.check("acknowledge","Request acknowledgement",n.optBoolean("acknowledge"));
            f.check("pinned","Pin notice",n.optBoolean("pinned"));
            f.check("phoneNotify","Notify on phone · external delivery not configured",n.optBoolean("phoneNotify"));
            u.note(b,"Preview first. Publishing sends the notice to residents' in-app inbox. Phone (SMS/push) delivery is not set up yet.");
            submit("Preview notice",()-> {
                JSONObject body=f.values();
                try {
                    body.put("scheduledAt",f.get("scheduledAt").trim().isEmpty()?JSONObject.NULL:instant(f.get("scheduledAt")));
                } catch(JSONException e) {
                    throw new IllegalArgumentException(e);
                }
                write("/notices"+(id.isEmpty()?"":"/"+id),body,x-> {
                    h.clearPageDraft("noticeTemplate");
                    go("notice-preview",((JSONObject)x).getString("id"));
                });
            }
            );
        };
        if(id.isEmpty())safe(()-> {
            try {
                build.done(obj());
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        );
        else get("/notices/"+id,build);
    }
    private void noticePreview() {
        title("Preview notice");
        get("/notices/"+id+"/preview",r-> {
            JSONObject n=(JSONObject)r;
            LinearLayout cover=card();
            u.kv(cover,"Type",friendly(n.optString("noticeType","GENERAL")));
            cover.addView(u.text(n.optString("title"),24,true,R.color.ap_ink));
            cover.addView(u.text(n.optString("body"),15,false,R.color.ap_sub));
            LinearLayout delivery=card();
            u.kv(delivery,"Audience",friendly(n.optString("audience")));
            u.kv(delivery,"Recipients",n.optInt("recipientCount")+" active accounts");
            u.kv(delivery,"Phone notification",n.optBoolean("phoneNotify")?"Requested · phone delivery "+Ui.delivery(n.optString("externalDeliveryStatus")):"Off");
            u.kv(delivery,"Acknowledgement",n.optBoolean("acknowledge")?"Required":"Not requested");
            u.kv(delivery,"Publish",n.isNull("scheduledAt")?"Now":n.optString("scheduledAt"));
            u.note(b,"Delivery mode: authenticated in-app inbox only. A phone request is not proof of external delivery.");
            u.button(b,"Edit",R.drawable.ic_settings,false,()->go("notice-form",id));
            if(n.isNull("scheduledAt"))submit("Publish",()->u.confirm("Publish notice?","Send this immutable message to the reviewed in-app audience.","Publish",()->h.requestApi("POST","/notices/"+id+"/publish",obj(),x->h.openPageWithBackTarget("notice",id,"notices"))));
            else submit("Schedule",()->u.confirm("Schedule notice?","The local backend will publish it at "+com.rapolus.apartmentpilotai.data.DateText.humanize(n.optString("scheduledAt"))+".","Schedule",()->write("/notices/"+id+"/schedule",obj(),x->h.openPageWithBackTarget("notice",id,"notices"))));
            u.button(b,"Keep draft",R.drawable.ic_report,false,()->go("notices"));
        });
    }
    private void documents() {
        title("Documents");
        if(h.isStaff())u.button(b,"Add document",R.drawable.ic_plus,true,()->go("document-form"));
        JSONObject draft=h.pageDraft("document-list");
        EditText search=h.newFields().field("search","Search documents",draft.optString("search"),TEXT);
        LinearLayout filters=u.row();
        if(h.isStaff())b.addView(filters);
        LinearLayout list=u.column();
        b.addView(list);
        get("/ops/documents",r-> {
            JSONArray a=(JSONArray)r;
            String initial=draft.optString("filter","All");
            String[] selected={initial};
            String[] options=h.isStaff()?new String[]{"All","Public","Private"}:new String[]{"All"};
            ArrayList<com.google.android.material.button.MaterialButton> buttons=new ArrayList<>();
            for(String option:options) {
                com.google.android.material.button.MaterialButton button=u.button(filters,option,0,false,()-> {
                    selected[0]=option;
                    h.savePageDraft("document-list",obj("search",search.getText().toString(),"filter",option));
                    for(com.google.android.material.button.MaterialButton item:buttons) {
                        boolean active=item.getText().toString().equals(option);
                        int ink=u.color(active?R.color.ap_bg:R.color.ap_ink);
                        item.setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(active?R.color.ap_pine:R.color.ap_soft)));
                        item.setTextColor(ink);
                        item.setIconTint(android.content.res.ColorStateList.valueOf(ink));
                    }
                    renderDocumentRows(a,list,search.getText().toString(),selected[0]);
                });
                button.setSingleLine(true);
                button.setTextSize(11);
                LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)button.getLayoutParams();
                params.width=0;params.weight=1;params.setMarginEnd(u.dp(6));button.setLayoutParams(params);
                buttons.add(button);
            }
            if(!Arrays.asList(options).contains(initial))selected[0]="All";
            for(com.google.android.material.button.MaterialButton item:buttons) {
                boolean active=item.getText().toString().equals(selected[0]);
                int ink=u.color(active?R.color.ap_bg:R.color.ap_ink);
                item.setBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(active?R.color.ap_pine:R.color.ap_soft)));
                item.setTextColor(ink);
                item.setIconTint(android.content.res.ColorStateList.valueOf(ink));
            }
            search.addTextChangedListener(new android.text.TextWatcher() {
                public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                public void onTextChanged(CharSequence s,int start,int before,int count) {
                    h.savePageDraft("document-list",obj("search",s.toString(),"filter",selected[0]));
                    renderDocumentRows(a,list,s.toString(),selected[0]);
                }
                public void afterTextChanged(android.text.Editable value) {}
            });
            renderDocumentRows(a,list,search.getText().toString(),selected[0]);
        }
        );
    }
    private void renderDocumentRows(JSONArray rows,LinearLayout list,String query,String filter) {
        list.removeAllViews();
        String term=query==null?"":query.trim().toLowerCase(Locale.ROOT);
        int shown=0;
        for(int i=0;i<rows.length();i++) {
            JSONObject document=rows.optJSONObject(i);
            if(document==null)continue;
            boolean isPublic=document.optBoolean("public");
            if(filter.equals("Public")&&!isPublic||filter.equals("Private")&&isPublic)continue;
            if(!term.isEmpty()&&!document.optString("title").toLowerCase(Locale.ROOT).contains(term)&&!document.optString("category").toLowerCase(Locale.ROOT).contains(term))continue;
            String visibility=document.optBoolean("archived")?"Archived":isPublic?"Residents":"Staff only";
            row(u.card(list),document.optString("title"),document.optString("category")+" · "+visibility,R.drawable.ic_copy,"document",document.optString("id"));
            shown++;
        }
        if(shown==0)u.empty(list,R.drawable.ic_report,term.isEmpty()&&filter.equals("All")?"No shared files":"No matching documents",term.isEmpty()&&filter.equals("All")?"Apartment documents appear here.":"Try another title or category.");
    }
    private void document() {
        title("Document");
        get("/ops/documents/"+id,r-> {
            JSONObject x=(JSONObject)r;
            details(x,"title","Title","category","Category","public","Shared","archived","Archived");
            fileLink("DOCUMENT",id);
            if(h.isStaff())u.button(b,"Edit details",R.drawable.ic_settings,false,()->go("document-form",id));
        }
        );
    }
    private void documentForm() {
        title("Document details");
        FeatureHost.Reply build=r-> {
            JSONObject x=(JSONObject)r;
            Ui.Fields f=h.newFields();
            f.field("title","Title",val(x,"title"),TEXT);
            pick(f,"category","Category",new String[] {
                "Rules","Bill","AMC","Meeting minutes","Other"
            }
            ,x.optString("category","Other"));
            f.check("public","Residents can view",x.optBoolean("public"));
            if(!id.isEmpty())f.check("archived","Archived",x.optBoolean("archived"));
            submit("Save & attach",()->write("/ops/documents"+(id.isEmpty()?"":"/"+id),f.values(),z->go("attachments","DOCUMENT:"+((JSONObject)z).getString("id"))));
        };
        if(id.isEmpty()) {
            try {
                build.done(obj());
            } catch(Exception e) {
                h.problem(e.getMessage());
            }
        } else get("/ops/documents/"+id,build);
    }
    private void attachments() {
        title("Attachments");
        String[] parts=id.split(":",2);
        if(parts.length!=2) {
            u.empty(b,R.drawable.ic_report,"No record selected","");
            return;
        }
        String kind=parts[0],parent=parts[1];
        boolean writer=h.isStaff()||kind.equals("PAYMENT")||kind.equals("TICKET");
        if(writer)u.button(b,"Attach PDF or photo",R.drawable.ic_plus,true,()->h.chooseAttachment(kind,parent));
        u.note(b,"Up to 5 files · 5 MB each. Access follows the parent record.");
        get("/ops/files?kind="+kind+"&parentId="+parent,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_report,"No attachments","");
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                u.iconRow(card(),x.optString("name"),(x.optLong("size")/1024)+" KB",R.drawable.ic_copy,()->get("/ops/files/"+x.optString("id"),data->h.openSharedFile((JSONObject)data)));
            }
        }
        );
    }
    private void settings() {
        title("Financial controls");
        if(!h.isStaff()) {u.empty(b,R.drawable.ic_shield,"Staff access only","Your apartment admin manages these controls.");return;}
        LinearLayout c=card();
        row(c,"Monthly maintenance","Amount and due date",R.drawable.ic_receipt,"billing-settings");
        row(c,"Payment account","UPI and bank details",R.drawable.ic_wallet,"payment-settings");
        row(c,"Billing options","Vacant flats and optional late fees",R.drawable.ic_receipt,"billing-options");
        row(c,"Payment verification","Proof and resident visibility",R.drawable.ic_shield,"verification-settings");
        row(c,"Reminders","Schedule and quiet hours",R.drawable.ic_bell,"notification-settings");
        row(c,"Booking rules","Common-space policy",R.drawable.ic_calendar,"booking-policy");
        if(h.isAdmin())row(c,"Opening balance","Optional reconciled starting balance",R.drawable.ic_wallet,"opening-balance");
        u.note(b,"Start with maintenance and a payment account. Change the other controls only when needed.");
    }
    private void settingsForm(String section) {
        String heading=section.equals("payment")?"Payment account":section.equals("billing")?"Billing options":section.equals("verification")?"Payment verification":section.equals("reminders")?"Reminder settings":"Booking rules";
        title(heading);
        if(!h.isStaff()) {u.empty(b,R.drawable.ic_shield,"Staff access only","Your apartment admin manages these controls.");return;}
        get("/ops/settings",r-> {
            JSONObject x=(JSONObject)r;
            Ui.Fields f=h.newFields();
            switch(section) {
                case "payment":
                    u.note(b,"Add your association's UPI ID or bank details. Unused payment methods can stay blank.");
                    for(String[] a:new String[][]{{"payee","Account holder"},{"upi","UPI ID"},{"bank","Bank name"},{"account","Account number"},{"ifsc","IFSC"}})f.field(a[0],a[1],val(x,a[0]),TEXT);
                    break;
                case "billing":
                    f.check("billVacant","Charge vacant flats",x.optBoolean("billVacant",true));
                    f.check("lateEnabled","Enable late fee for future bills",x.optBoolean("lateEnabled"));
                    f.field("lateFee","One-time late fee",x.optString("lateFee","0"),MONEY);
                    f.field("graceDays","Grace days",x.optString("graceDays","0"),NUM);
                    u.note(b,"Existing bills stay unchanged. Leave late fees off if your apartment does not use them.");
                    break;
                case "verification":
                    f.check("proofRequired","Require proof before non-cash approval",x.optBoolean("proofRequired"));
                    f.check("expensesVisible","Share paid expenses with residents",x.optBoolean("expensesVisible",true));
                    u.note(b,"The admin or treasurer must verify payments. The app does not confirm bank transfers automatically.");
                    break;
                case "reminders":
                    f.check("dueNotify","New bill notifications",x.optBoolean("dueNotify",true));
                    f.check("reminders","Unpaid reminders",x.optBoolean("reminders",true));
                    f.field("reminderDays","Days of month · e.g. 5,9,11,15",x.optString("reminderDays","5,9,11,15"),TEXT);
                    f.field("quietStart","Quiet hours start · HH:mm",x.optString("quietStart","21:00"),TEXT);
                    f.field("quietEnd","Quiet hours end · HH:mm",x.optString("quietEnd","08:00"),TEXT);
                    u.note(b,"Reminders pause during payment review. In-app inbox is available; phone push is not configured.");
                    break;
                default:
                    f.field("bookingRules","Rules",x.optString("bookingRules"),TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    break;
            }
            submit("Save "+heading.toLowerCase(Locale.ROOT),()-> {
                JSONObject j=f.values();
                try {
                    if(section.equals("billing")) {
                        j.put("graceDays",Integer.parseInt(f.get("graceDays")));
                        j.put("lateFee",amount(f,"lateFee"));
                    }
                } catch(JSONException e) {
                    throw new IllegalArgumentException(e);
                }
                write("/ops/settings/"+section,j,z->go(section.equals("payment")?"payee":"settings"));
            }
            );
        }
        );
    }
    private void opening() {
        title("Opening cash balance");
        get("/ops/settings",r-> {
            JSONObject x=(JSONObject)r;
            if(!x.isNull("openingDate")) {
                details(x,"openingDate","Opening date","openingAmount","Opening amount");
                u.note(b,"Already set. Corrections must be recorded as documented income/expense.");
                return;
            }
            Ui.Fields f=h.newFields();
            f.field("amount","Opening bank + cash","0",MONEY);
            f.field("date","As of YYYY-MM-DD · first day of month",YearMonth.now().atDay(1).toString(),TEXT);
            u.note(b,"Use a reconciled starting balance. Do not include money recorded separately as received payments.");
            submit("Confirm opening balance",()->u.confirm("Set opening balance once?",f.get("date")+" · ₹"+f.get("amount"),"Confirm",()->write("/ops/opening",obj("amount",amount(f,"amount"),"date",f.get("date")),z->go("reports"))));
        }
        );
    }
    private void overrides() {
        title("Flat-specific rates");
        u.button(b,"Add rate exception",R.drawable.ic_plus,true,()->go("rate-override-form"));
        get("/ops/rate-overrides",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_expense,"One rate for all flats","Add only agreed exceptions.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                LinearLayout c=card();
                u.kv(c,x.optString("flatLabel"),Ui.money(x.opt("amount")));
                u.note(c,"From "+x.optString("effectiveMonth")+" · "+x.optString("reason"));
            }
        }
        );
    }
    private void overrideForm() {
        title("Flat rate");
        get("/ops/flats",r-> {
            Ui.Fields f=h.newFields();
            choices(f,"flatId","Flat",(JSONArray)r,"label",false,null);
            f.field("amount","Monthly amount","",MONEY);
            f.field("effective","Effective month · YYYY-MM",YearMonth.now().plusMonths(1).toString(),TEXT);
            f.field("reason","Agreed reason","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            u.note(b,"Previously issued bills and receipts stay unchanged.");
            submit("Save rate",()->write("/ops/rate-overrides",obj("flatId",f.get("flatId"),"amount",amount(f,"amount"),"effective",f.get("effective"),"reason",f.get("reason")),x->go("rate-overrides")));
        }
        );
    }
    private void contributions() {
        title("One-time contributions");
        h.monthControl();
        u.button(b,"Create contribution",R.drawable.ic_plus,true,()->go("contribution-form"));
        get("/bills?month="+h.selectedMonth(),r-> {
            JSONArray arr=(JSONArray)r;
            int found=0;
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                if(!x.optString("kind").equals("CONTRIBUTION"))continue;
                found++;
                row(card(),x.optString("title"),x.optString("flatLabel")+" · "+Ui.money(Ui.decimal(x.opt("amount")).subtract(Ui.decimal(x.opt("paid")))),R.drawable.ic_receipt,"bill",x.optString("id"));
            }
            if(found==0)u.empty(b,R.drawable.ic_expense,"No contributions for "+h.selectedMonth(),"Regular monthly maintenance remains separate.");
        }
        );
    }
    private void charge(String kind) {
        title(kind.equals("OPENING_DUE")?"Opening flat dues":"One-time collection");
        get("/ops/flats",r-> {
            Ui.Fields f=h.newFields();
            if(kind.equals("CONTRIBUTION")) {
                f.select("scope","Charge to",new String[] {"All active flats","Selected flats"},new String[] {"ALL","SELECTED"});
                f.field("flatLabels","Selected flats · comma separated","",TEXT);
            } else choices(f,"flatId","Flat",(JSONArray)r,"label",false,null);
            f.field("title","Charge description",kind.equals("OPENING_DUE")?"Opening maintenance due":"",TEXT);
            f.field("amount","Amount per selected flat","",MONEY);
            f.field("month","Bill month · YYYY-MM",h.selectedMonth(),TEXT);
            f.field("dueDate","Due date · YYYY-MM-DD",today(),TEXT);
            u.note(b,kind.equals("OPENING_DUE")?"One opening-due entry per flat. Enter verified outstanding only.":"This charge does not change monthly maintenance rates.");
            if(kind.equals("CONTRIBUTION"))submit("Review collection",()-> {
                JSONObject draft=obj("kind",kind,"scope",f.get("scope"),"flatLabels",f.get("scope").equals("SELECTED")?f.get("flatLabels"):"","title",f.get("title"),"amount",amount(f,"amount"),"month",f.get("month"),"dueDate",f.get("dueDate"));
                h.requestApi("POST","/ops/charges/preview",draft,value-> {
                    h.savePageDraft("contribution",(JSONObject)value);
                    go("contribution-preview");
                });
            });
            else submit("Create opening due",()->u.confirm("Create opening due?",f.get("title")+" · ₹"+f.get("amount"),"Create",()->write("/ops/charges",obj("kind",kind,"flatId",f.get("flatId"),"title",f.get("title"),"amount",amount(f,"amount"),"month",f.get("month"),"dueDate",f.get("dueDate")),z->go("dues"))));
        }
        );
    }
    private void contributionPreview() {
        title("Review contribution");
        JSONObject draft=h.pageDraft("contribution");
        if(!draft.has("title")) {
            u.empty(b,R.drawable.ic_expense,"No contribution draft","Start a new collection to review it here.");
            u.button(b,"New collection",R.drawable.ic_plus,true,()->go("contribution-form"));
            return;
        }
        LinearLayout c=card();
        u.kv(c,"Collection",draft.optString("title"));
        u.kv(c,"Flats",draft.optInt("flatCount")+"");
        u.kv(c,"Each flat",Ui.money(draft.opt("amount")));
        u.kv(c,"Total billed",Ui.money(draft.opt("total")));
        u.kv(c,"Due",draft.optString("dueDate"));
        u.note(b,"Selected: "+draft.optString("flatLabels"));
        u.note(b,"Creating dues records no payment received. Monthly maintenance remains unchanged.");
        submit("Create dues",()-> {
            JSONObject command;
            try {
                command=new JSONObject(draft.toString());
            } catch(JSONException e) {
                throw new IllegalArgumentException("The contribution draft is invalid.");
            }
            write("/ops/charges",command,value-> {
                JSONObject result=(JSONObject)value;
                h.clearPageDraft("contribution");
                u.message(result.optInt("created")+" contribution due(s) created. No payment recorded.",true);
                go("contributions");
            });
        });
    }
    private void income() {
        title("Other income");
        u.button(b,"Record income",R.drawable.ic_plus,true,()->go("income-form"));
        get("/ops/income",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_expense,"No other income","");
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                LinearLayout c=card();
                u.kv(c,x.optString("title"),Ui.money(x.opt("amount")));
                u.note(c,x.optString("receivedOn")+" · "+friendly(x.optString("mode")));
            }
        }
        );
    }
    private void incomeForm() {
        title("Record income");
        Ui.Fields f=h.newFields();
        f.field("title","Description","",TEXT);
        f.field("amount","Amount received","",MONEY);
        f.field("receivedOn","Received on · YYYY-MM-DD",today(),TEXT);
        pick(f,"mode","Mode",new String[] {
            "CASH","UPI","BANK_TRANSFER","CHEQUE"
        }
        ,"UPI");
        submit("Save received income",()->write("/ops/income",obj("title",f.get("title"),"amount",amount(f,"amount"),"receivedOn",f.get("receivedOn"),"mode",f.get("mode")),r->go("income")));
    }
    private void expense() {
        title("Expense detail");
        get("/ops/expenses/"+id,r-> {
            JSONObject x=(JSONObject)r;
            u.hero(b,x.optString("title"),Ui.money(x.opt("amount")),!x.isNull("reversedOn")?"Reversed":x.optBoolean("paid")?"Paid":"Draft");
            details(x,"category","Category","paidOn","Date","mode","Paid via","notes","Notes","reversalReason","Reversal reason");
            if(!x.isNull("vendorName"))u.kv(card(),"Vendor",x.optString("vendorName"));
            u.button(b,"View bill",R.drawable.ic_copy,false,()->go("expense-proof",id));
            if(h.isStaff()) {
                if(!x.optBoolean("paid"))u.button(b,"Edit / confirm paid",R.drawable.ic_check,true,()->go("expense-edit",id));
                else if(x.isNull("reversedOn"))u.button(b,"Record reversal",R.drawable.ic_error,false,()->go("expense-reverse",id));
            }
        }
        );
    }
    private void expenseEdit() {
        title("Edit draft expense");
        get("/ops/expenses/"+id,r-> {
            JSONObject x=(JSONObject)r;
            Ui.Fields f=h.newFields();
            f.field("title","Description",x.optString("title"),TEXT);
            expenseCategory(f,x.optString("category"));
            f.field("amount","Amount",x.optString("amount"),MONEY);
            f.field("paidOn","Date · YYYY-MM-DD",x.optString("paidOn"),TEXT);
            pick(f,"mode","Payment mode",new String[] {
                "CASH","UPI","BANK_TRANSFER","CHEQUE"
            }
            ,x.optString("mode","UPI"));
            f.field("notes","Notes",val(x,"notes"),TEXT);
            f.check("paid","Payment completed",false);
            f.check("visibleToResidents","Visible to residents",x.optBoolean("public",true));
            submit("Save expense",()-> {
                validateExpense(f);
                JSONObject j=f.values();
                try {
                    j.put("amount",expenseAmount(f));
                } catch(JSONException e) {
                    throw new IllegalArgumentException(e);
                }
                u.confirm(f.checked("paid")?"Confirm money spent?":"Save draft?",f.get("title")+" · ₹"+f.get("amount"),"Save",()->write("/ops/expenses/"+id,j,z->go("expense",id)));
            }
            );
        }
        );
    }
    private void reverse(boolean payment) {
        title(payment?"Reverse approved payment":"Reverse paid expense");
        Ui.Fields f=h.newFields();
        f.field("reason","Reason for correction / returned funds","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        f.check("verified","I verified the full reversal and cash adjustment",false);
        u.note(b,"Original records remain in the audit history. This records a full correction; it does not send or refund money.");
        submit("Review reversal",()-> {
            String reason=f.get("reason").trim();
            if(reason.isEmpty())throw new IllegalArgumentException("Explain the correction or returned funds first.");
            if(!f.checked("verified"))throw new IllegalArgumentException("Confirm you verified the full adjustment before recording the reversal.");
            u.confirm("Record full reversal?",reason,"Confirm reversal",()->write("/ops/"+(payment?"payments/":"expenses/")+id+"/reverse",obj("reason",reason,"verified",true),r->go(payment?"payment":"expense",id)));
        });
    }
    private void recurring() {
        title("Recurring expenses");
        u.button(b,"Add recurring expense",R.drawable.ic_plus,true,()->go("recurring-form"));
        get("/ops/recurring",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_expense,"No recurring templates","Recurring entries are drafts until payment is confirmed.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                row(card(),x.optString("title"),Ui.money(x.opt("amount"))+" · "+x.optString("nextOn")+" · "+(x.optBoolean("active")?"Active":"Paused"),R.drawable.ic_clock,"recurring-detail",x.optString("id"));
            }
        }
        );
    }
    private void recurringDetail() {
        title("Recurring expense");
        if(!h.isStaff()){h.problem("Admin or treasurer access required.");return;}
        if(id.isEmpty()){u.empty(b,R.drawable.ic_expense,"No recurring expense selected","Open a template from Recurring expenses.");return;}
        get("/ops/recurring/"+id,r->{
            JSONObject x=(JSONObject)r;
            boolean active=x.optBoolean("active");
            LinearLayout c=card();
            c.addView(u.text(active?"Active":"Paused",11,true,active?R.color.ap_green:R.color.ap_amber));
            u.gap(c,10);c.addView(u.text(x.optString("title"),22,true,R.color.ap_ink));
            c.addView(u.text(Ui.money(x.opt("amount")),32,true,R.color.ap_ink));
            u.kv(c,"Frequency",friendly(x.optString("frequency")));
            u.kv(c,"Next due",x.optString("nextOn"));
            u.kv(c,"Vendor",x.isNull("vendorName")?"Not linked":x.optString("vendorName"));
            u.button(b,"Edit",R.drawable.ic_settings,false,()->go("recurring-form",id));
            u.button(b,active?"Pause":"Resume",R.drawable.ic_clock,false,()->u.confirm(active?"Pause recurring expense?":"Resume recurring expense?","Existing drafts and paid records will be retained.",active?"Pause":"Resume",()->write("/ops/recurring/"+id+"/active",obj("active",!active,"expectedActive",active),z->h.refreshPage())));
            if(active)u.button(b,"Prepare expense",R.drawable.ic_plus,true,()->u.confirm("Prepare expense draft?",x.optString("title")+" · "+Ui.money(x.opt("amount"))+" · "+x.optString("nextOn")+"\nNo money is posted until payment is confirmed.","Prepare",()->write("/ops/recurring/"+id+"/prepare",obj("cycle",x.optString("nextOn")),z->go("expense",((JSONObject)z).optString("id")))));
            else u.note(b,"Resume this template before preparing another draft.");
            u.note(b,"Creates one unpaid draft for this cycle. The next due date advances when this cycle is confirmed paid. Scheduled automation retains its existing catch-up behavior.");
        });
    }
    private void recurringForm() {
        title("Recurring setup");
        if(!h.isStaff()){h.problem("Admin or treasurer access required.");return;}
        if(id.isEmpty())recurringFields(new JSONObject());
        else get("/ops/recurring/"+id,r->recurringFields((JSONObject)r));
    }
    private void recurringFields(JSONObject x) {
        get("/ops/contacts?kind=VENDOR",r-> {
            Ui.Fields f=h.newFields();
            f.field("title","Description",val(x,"title"),TEXT);
            expenseCategory(f,x.optString("category","Security"));
            f.field("amount","Expected amount",val(x,"amount"),MONEY);
            pick(f,"frequency","Frequency",FREQUENCIES,x.optString("frequency","MONTHLY"));
            f.field("nextOn","Next due · YYYY-MM-DD",x.optString("nextOn",today()),TEXT);
            choices(f,"vendorId","Vendor · optional",(JSONArray)r,"name",true,val(x,"vendorId"));
            f.check("active","Enabled",x.optBoolean("active",true));
            u.note(b,"Each cycle prepares an expense draft, never an automatic cash payment.");
            submit("Save template",()-> {
                JSONObject j=f.values();
                try {
                    j.put("amount",amount(f,"amount"));
                } catch(JSONException e) {
                    throw new IllegalArgumentException(e);
                }
                write("/ops/recurring"+(id.isEmpty()?"":"/"+id),j,z->go("recurring-detail",((JSONObject)z).optString("id")));
            }
            );
        }
        );
    }
    private void cashbook() {
        title("Cashbook · "+h.selectedMonth());
        h.monthControl();
        get("/ops/cashbook?month="+h.selectedMonth(),r-> {
            JSONArray arr=(JSONArray)r;
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                LinearLayout c=card();
                u.kv(c,x.optString("title"),Ui.money(x.opt("amount")));
                u.note(c,x.optString("date")+" · "+friendly(x.optString("kind")));
            }
            if(arr.length()==0)u.empty(b,R.drawable.ic_expense,"No transactions this month","");
            u.button(b,"Export PDF",R.drawable.ic_report,false,()->h.exportPdf("Cashbook · "+h.selectedMonth(),obj("entries",arr)));
        }
        );
    }
    private void reports() {
        title("Reports · "+h.selectedMonth());
        h.monthControl();
        get("/reports/monthly?month="+h.selectedMonth(),r-> {
            JSONObject x=(JSONObject)r;
            u.hero(b,"Available balance",Ui.money(x.opt("balance")),h.selectedMonth());
            LinearLayout c=card();
            for(String[] k:new String[][] {
                {
                    "opening","Opening balance"
                }
                , {
                    "received","Approved receipts"
                }
                , {
                    "otherIncome","Other income"
                }
                , {
                    "spent","Paid expenses"
                }
                , {
                    "expenseReturns","Expense reversals"
                }
                , {
                    "refunds","Payment reversals"
                }
                , {
                    "billed","Bills issued"
                }
                , {
                    "collectedForBills","Collected for bills"
                }
                , {
                    "outstanding","Outstanding"
                }
            }
            )if(x.has(k[0]))u.kv(c,k[1],Ui.money(x.opt(k[0])));
            u.note(b,"Cash movements follow transaction dates; outstanding follows the selected invoice month.");
            u.button(b,"Export report",R.drawable.ic_report,false,()->go("export-report"));
            u.button(b,"Expense breakdown",R.drawable.ic_expense,false,()->go("expense-report"));
            if(h.isStaff())u.button(b,"Collections by flat",R.drawable.ic_users,false,()->go("collection-report"));
            if(h.isStaff())u.button(b,"Cashbook",R.drawable.ic_wallet,false,()->go("cashbook"));
            if(h.isStaff())u.button(b,"Resident view",R.drawable.ic_users,false,()->go("transparency"));
        }
        );
    }
    private void transparency() {
        title("Resident finance view");
        if(!h.isStaff()) {
            h.problem("Admin or treasurer access required.");
            return;
        }
        h.monthControl();
        get("/reports/monthly?month="+h.selectedMonth(),reportValue-> {
            JSONObject report=(JSONObject)reportValue;
            u.note(b,"Preview: residents see approved apartment totals and public expenses, never another flat’s payment proof.");
            u.hero(b,"Apartment closing balance",Ui.money(report.opt("closing")),h.selectedMonth());
            LinearLayout totals=card();
            u.kv(totals,"Received",Ui.money(report.opt("received")));
            u.kv(totals,"Spent",Ui.money(report.opt("spent")));
            get("/ops/settings",settingsValue-> {
                JSONObject settings=(JSONObject)settingsValue;
                Ui.Fields f=h.newFields();
                f.check("expensesVisible","Resident transparency · approved totals and public expenses",settings.optBoolean("expensesVisible",true));
                submit("Save visibility",()-> {
                    JSONObject payload;
                    try {
                        payload=new JSONObject(settings.toString());
                        payload.put("expensesVisible",f.checked("expensesVisible"));
                    } catch(JSONException e) {
                        throw new IllegalArgumentException("Could not prepare visibility settings.");
                    }
                    write("/ops/settings",payload,value->go("reports"));
                });
            });
        });
    }
    private void payee() {
        title("Association payment details");
        get("/ops/settings",r-> {
            JSONObject x=(JSONObject)r;
            details(x,"payee","Account holder","upi","UPI ID","bank","Bank","account","Account number","ifsc","IFSC");
            u.note(b,"Pay outside the app. Verify the account with your treasurer first.");
            if(!val(x,"upi").trim().isEmpty())u.button(b,"Copy UPI ID",R.drawable.ic_copy,true,()-> {
                ((android.content.ClipboardManager)h.activity().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Association UPI",x.optString("upi")));
                u.message("UPI ID copied",false);
            }
            );
            if(h.isStaff())u.button(b,"Edit payment account",R.drawable.ic_settings,false,()->go("payment-settings"));
        }
        );
    }
    private void tasks() {
        title("Approval inbox");
        get("/ops/tasks",r-> {
            JSONObject x=(JSONObject)r;
            LinearLayout c=card();
            row(c,"Payments",x.optString("payments","0")+" pending",R.drawable.ic_check,"approvals");
            if(h.isAdmin()) {
                row(c,"Join requests",x.optString("joinRequests","0")+" pending",R.drawable.ic_users,"join-requests");
                row(c,"Event requests",x.optString("bookings","0")+" pending",R.drawable.ic_calendar,"events");
            }
            row(c,"Expense drafts",x.optString("expenseDrafts","0")+" need confirmation",R.drawable.ic_expense,"expenses");
            row(c,"Building issues",x.optString("issues","0")+" active",R.drawable.ic_tools,"issues");
        }
        );
    }
    private void automation() {
        title("Automation");
        u.note(b,"Backend jobs prepare bills, expense drafts, service reminders and scheduled notices. Nothing runs from this screen while the backend is stopped.");
        LinearLayout c=card();
        row(c,"Maintenance","Rates & billing day",R.drawable.ic_receipt,"billing-settings");
        row(c,"Reminders","Schedule and quiet hours",R.drawable.ic_bell,"notification-settings");
        row(c,"Recurring expenses","Automatic drafts",R.drawable.ic_clock,"recurring");
        row(c,"Service schedules","Preventive maintenance",R.drawable.ic_tools,"services");
        u.button(b,"Run due tasks now",R.drawable.ic_clock,true,()->u.confirm("Run scheduled tasks?","Due tasks are evaluated for today's date. Existing bills and jobs are not duplicated.","Run tasks",()->h.requestApi("POST","/ops/automation/run",obj(),r-> {
            JSONObject x=(JSONObject)r;
            u.message("Tasks ran: "+x.optInt("billsCreated")+" bills, "+x.optInt("expenseDraftsCreated")+" expense drafts, "+x.optInt("noticesPublished")+" notices",true);
            u.heading(b,"Last run result");
            details(x,"billsCreated","Bills generated","expenseDraftsCreated","Expense drafts","lateFeesApplied","Late charges","reminderRecipientsConsidered","Eligible reminder recipients","noticesPublished","Notices published","bookingsCompleted","Ended reservations");
        }
        )));
        u.button(b,"Activity log",R.drawable.ic_clock,false,()->go("audit"));
    }
    private void subscription() {
        title("Subscription & referrals");
        get("/ops/subscription",r-> {
            JSONObject s=(JSONObject)r;
            String subscriptionStatus=s.optString("status");
            u.hero(b,"Category "+s.optString("category")+" · "+Ui.count(s.optInt("flats"),"flat","flats"),Ui.money(s.opt("price"))+" / month",friendly(subscriptionStatus));
            LinearLayout c=card();
            u.kv(c,"Resident access","One account per flat");
            // The whole first month is the free trial; the first paid month follows it.
            if(subscriptionStatus.equals("TRIAL")) {
                u.kv(c,"Free trial (first month) ends",com.rapolus.apartmentpilotai.data.DateText.format(Instant.parse(s.optString("trialEndsAt"))));
                u.kv(c,"First paid month starts",com.rapolus.apartmentpilotai.data.DateText.day(s.optString("firstEnd")));
            }
            else if(subscriptionStatus.equals("PAYMENT_DUE"))u.note(c,"Subscription required. Normal apartment access is paused until a verified subscription payment or earned referral credit is recorded.");
            u.kv(c,"Coverage","Admin, Treasurer & Residents");
            u.kv(c,"Automatic debit","Not enabled");
            u.button(b,"Application charges",R.drawable.ic_receipt,false,()->go("subscription-plans"));
            u.button(b,"First-month offer",R.drawable.ic_users,false,()->go("referral-offer"));
            u.button(b,"Referral rules",R.drawable.ic_shield,false,()->go("referral-rules"));
            u.heading(b,"Refer two new apartments");
            String code=s.optString("referralCode");
            u.kv(card(),"Your referral code",code);
            u.button(b,"Share referral",R.drawable.ic_users,true,()->h.activity().startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,"ApartmentPilotAI referral code: "+code+". Every apartment gets its first month free. If two new apartments join with this code and are verified within their first month, all three apartments also get their first paid month free. Terms apply."),"Share referral")));
            if(s.optBoolean("referralApplied"))u.note(b,"✓ A referral code has been applied to this apartment.");
            else u.button(b,"I was referred",R.drawable.ic_plus,false,()->go("referral-claim"));
            if(s.isNull("installedAt"))u.button(b,"Register this installation",R.drawable.ic_check,false,()->h.requestApi("POST","/ops/subscription/install",obj(),x->{u.message("Installation reported. The service operator verifies it separately.",true);h.refreshPage();}));
            else u.note(b,"✓ Installation reported on "+s.optString("installedAt")+". Awaiting independent verification.");
            JSONArray referrals=s.optJSONArray("referrals");
            if(referrals!=null) {
                int count=0;
                for(int i=0; i<referrals.length(); i++)if(referrals.getJSONObject(i).optBoolean("verified"))count++;
                u.kv(card(),"Verified activations",count+" / 2");
                for(int i=0; i<referrals.length(); i++) {
                    JSONObject x=referrals.getJSONObject(i);
                    u.kv(card(),x.optString("apartmentName"),x.optBoolean("rewarded")?"Reward recorded":x.optBoolean("verified")?"Verified":x.optBoolean("installed")?"Review pending":"Awaiting activation");
                }
            }
            u.heading(b,"Application invoices");
            JSONArray invoices=s.optJSONArray("invoices");
            if(invoices!=null)for(int i=0; i<invoices.length(); i++) {
                JSONObject x=invoices.getJSONObject(i);
                LinearLayout invoice=card();
                boolean trialMonth=x.optString("startsOn").equals(s.optString("activatedAt"));
                u.kv(invoice,com.rapolus.apartmentpilotai.data.DateText.day(x.optString("startsOn"))+" – "+com.rapolus.apartmentpilotai.data.DateText.day(x.optString("endsOn")),trialMonth&&"WAIVED".equals(x.optString("status"))?"Free trial":friendly(x.optString("status")));
                u.kv(invoice,"Price",Ui.money(x.opt("amount")));
                u.kv(invoice,trialMonth?"Free trial credit":"Referral credit",Ui.money(x.opt("credit")));
                u.kv(invoice,"Verified paid",Ui.money(x.opt("paid")));
                u.button(invoice,"Share invoice PDF",R.drawable.ic_receipt,false,()->h.exportPdf("Subscription record · not a tax invoice",x));
            }
            u.note(b,"No checkout or automatic debit is connected. Only the service operator can independently verify activations and subscription payments. This is separate from residents' maintenance.");
            if(s.optBoolean("accessAllowed"))u.button(b,"Continue to ApartmentCare",R.drawable.ic_home,true,()->h.openPage("home",""));
        }
        );
    }
    private void referralClaim() {
        title("Apply referral code");
        Ui.Fields f=h.newFields();
        f.field("code","New-apartment referral code","",TEXT);
        u.note(b,"Not a resident invite. One referrer per new apartment, within the first month. Self and circular referrals are blocked.");
        submit("Apply code",()->write("/ops/referrals/claim",obj("code",f.get("code").toUpperCase(Locale.ROOT)),r->go("subscription")));
    }
    private void appearance() {
        title("Appearance");
        for(int mode:new int[] {
            AppCompatDelegate.MODE_NIGHT_NO,AppCompatDelegate.MODE_NIGHT_YES,AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        )u.button(b,mode==AppCompatDelegate.MODE_NIGHT_NO?"Light":mode==AppCompatDelegate.MODE_NIGHT_YES?"Dark":"Device setting",mode==AppCompatDelegate.MODE_NIGHT_NO?R.drawable.ic_sun:R.drawable.ic_moon,false,()-> {
            h.activity().getSharedPreferences("appearance",0).edit().putInt("mode",mode).apply();
            AppCompatDelegate.setDefaultNightMode(mode);
        }
        );
        u.note(b,"Uses your device font scale. Financial confirmations retain their descriptions.");
    }
    private void privacy() {
        title("Privacy & access");
        u.note(b,"Resident payment records and private requests are restricted to that flat and authorised staff. Public reports contain apartment totals, not other residents' payment proofs. Committee titles do not grant application permissions.");
        u.button(b,"Contacts",R.drawable.ic_phone,false,()->go("contacts"));
        LinearLayout access=card();
        row(access,"Your flat only","Resident ledgers and payment proofs stay private",R.drawable.ic_home,"permissions");
        row(access,"Manual verification","Admin or treasurer confirms payment receipt",R.drawable.ic_shield,"payment-help");
        row(access,"Shared totals","Public apartment reports and visible expenses",R.drawable.ic_report,"reports");
        row(access,"Private requests","Your flat and the management team",R.drawable.ic_tools,"complaints");
    }
    private void help() {
        title("Help");
        LinearLayout c=card();
        row(c,"Apartment contacts","Get help from your team",R.drawable.ic_phone,"contacts");
        row(c,"Privacy & access","Who can see what",R.drawable.ic_shield,"privacy");
        row(c,"Permissions","Admin, Treasurer & Resident",R.drawable.ic_shield,"permissions");
        row(c,"Payment status guide","Review, approval & receipts",R.drawable.ic_receipt,"payment-help");
        row(c,"Get help","Record a private help request",R.drawable.ic_tools,"support");
        row(c,"Change PIN","Revoke existing sessions",R.drawable.ic_shield,"change-pin");
        row(c,"Build status","Integrations & test limits",R.drawable.ic_grid,"status");
        u.note(b,"Submitting a payment is not approval. Receipts require verification. This app is not an emergency response service.");
    }
    private void permissions() {
        title("Permissions");
        LinearLayout c=card();
        String[] labels={"Apartment setup","Resident approval","Payment approval","Expenses","Own payment history","Public reports","Common issues","Private requests","Post notices","Admin documents"};
        String[] access={"Admin","Admin","Admin · Treasurer","Admin · Treasurer","All roles · own records","All roles · when enabled","All roles","All roles · own records","Admin · Treasurer","Admin · Treasurer"};
        for(int i=0;i<labels.length;i++)u.kv(c,labels[i],access[i]);
        u.note(b,"Residents see their own flat and public community data. Committee titles do not grant application access. Permissions are enforced by the server.");
    }
    private void auditDetail() {
        title("Activity detail");
        get("/audit/"+id,r->{
            JSONObject a=(JSONObject)r;
            details(a,"action","Action","recordId","Record","actor","Actor","createdAt","Time");
            u.heading(b,"Changes");
            details(a,"beforeValue","Before","afterValue","After");
        });
    }
    private void paymentHelp() {
        title("Payment status guide");
        LinearLayout c=card();
        row(c,"In review","Your declaration is awaiting verification",R.drawable.ic_clock,"dues");
        row(c,"Paid","Approved payment and receipt available",R.drawable.ic_check,"payments");
        row(c,"Rejected","Correct the details and submit again",R.drawable.ic_close,"dues");
        u.note(b,"Manual UPI and bank transfers require human verification. A payment declaration is not a bank confirmation; receipts follow approval.");
    }
    private void support() {
        title("Get help");
        Ui.Fields f=h.newFields();
        String[] topics={"Payment approval","Account access","Flat mapping","Something else"};
        f.select("topic","Topic",topics,topics);
        f.field("message","Message","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        u.note(b,"Saved privately in this application. External support delivery is not configured. Do not include PINs or passwords.");
        submit("Save request",()->write("/ops/support",f.values(),r->{
            u.message("Help request saved privately",true);
            h.refreshPage();
        }));
        u.button(b,"Apartment contacts",R.drawable.ic_phone,false,()->go("contacts"));
        get("/ops/support",r->{
            JSONArray requests=(JSONArray)r;
            if(requests.length()>0)u.heading(b,"Your saved requests");
            for(int i=0;i<requests.length();i++) {
                JSONObject record=requests.getJSONObject(i);
                LinearLayout c=card();
                c.addView(u.text(record.optString("topic"),14,true,R.color.ap_ink));
                u.note(c,record.optString("message"));
                u.kv(c,"Saved",record.optString("createdAt"));
            }
        });
    }
    private void subscriptionPlans() {
        title("Application charges");
        get("/ops/subscription",r->{
            JSONObject s=(JSONObject)r;
            String[] categories={"A","B","C"},ranges={"5–10 flats","11–30 flats","31–50 flats"};
            int[] prices={99,149,249};
            for(int i=0;i<3;i++) {
                LinearLayout c=card();
                u.kv(c,"Category "+categories[i],s.optString("category").equals(categories[i])?"Your plan":"Whole apartment");
                c.addView(u.text("₹"+prices[i]+" / month",28,true,R.color.ap_ink));
                u.note(c,ranges[i]);
            }
            u.note(b,"Software subscription is separate from maintenance. Tax treatment and live billing configuration remain to be finalised.");
        });
    }
    private void referralOffer() {
        title("First-month offer");
        get("/ops/subscription",r->{
            JSONObject s=(JSONObject)r;
            u.state(b,R.drawable.ic_users,"Refer two. All three benefit.","Installation and independently verified activation required.",R.color.ap_green,R.color.ap_mint);
            LinearLayout c=card();
            u.kv(c,"You refer","2 distinct new apartments");
            u.kv(c,"Free trial","First month, for every apartment");
            u.kv(c,"Reward","First paid month (month 2) free");
            u.kv(c,"Who benefits","Your apartment + both new apartments");
            u.kv(c,"From month 3",Ui.money(s.opt("price"))+" / month (month 2 without the reward)");
            if(!s.isNull("offerAcceptedAt"))u.note(b,"Offer rules accepted on "+s.optString("offerAcceptedAt")+". Activation verification is still required.");
            else {
                Ui.Fields f=h.newFields();
                f.check("terms","I accept the referral offer rules.",false);
                submit("Activate offer",()->write("/ops/subscription/offer",obj("terms",f.checked("terms")),x->h.refreshPage()));
            }
            u.note(b,"No automatic debit. Without two qualifying activations, the subscription is due from month 2.");
            u.button(b,"Read referral rules",R.drawable.ic_shield,false,()->go("referral-rules"));
        });
    }
    private void referralRules() {
        title("Referral rules");
        if(!h.isAdmin()){h.problem("Apartment Admin access required.");return;}
        LinearLayout c=card();
        u.iconRow(c,"Two new apartments","Distinct identities; no self-referrals",R.drawable.ic_users,null);
        u.iconRow(c,"Verified activation","Service provider verifies identity, setup and an approved resident",R.drawable.ic_check,null);
        u.iconRow(c,"Free first month","Every new apartment uses the app free for its first month",R.drawable.ic_calendar,null);
        u.iconRow(c,"Qualify in month one","Referrals must be verified within the initial monthly period; one reward per apartment",R.drawable.ic_calendar,null);
        u.iconRow(c,"All three benefit","Referrer and the two qualifying apartments get their first paid month (month 2) free",R.drawable.ic_building,null);
        u.iconRow(c,"No stacking","Further referrals do not create another free month",R.drawable.ic_clock,null);
        u.iconRow(c,"Paid after that","Regular category price is due; no automatic debit",R.drawable.ic_receipt,null);
    }
    private void status() {
        title("Development status · 02");
        u.note(b,"Connected source modules: identity, maintenance, finance, members/committee, notices, files, issues, services, events/parking and subscription/referral ledger.");
        u.note(b,"Not release-ready: live phone verification/recovery, FCM delivery and Play purchase verification are not connected. This local build does not grant access based on unverified external events. Exact native/reference visual parity and device/integration testing remain required.");
        u.note(b,"Use only synthetic local data. See docs/TEST_EVIDENCE.md for checks that actually ran.");
    }
    private void polls() {
        title("Apartment polls");
        if(h.isAdmin())u.button(b,"New poll",R.drawable.ic_plus,true,()->go("poll-form"));
        get("/ops/polls",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,R.drawable.ic_check,"No polls yet","");
            for(int i=0; i<arr.length(); i++) {
                JSONObject p=arr.getJSONObject(i);
                row(card(),p.optString("title"),p.optBoolean("closed")?"Closed":"Ends "+com.rapolus.apartmentpilotai.data.DateText.humanize(p.optString("closesAt")),R.drawable.ic_check,"poll",p.optString("id"));
            }
        }
        );
    }
    private void pollForm() {
        title("Create poll");
        Ui.Fields f=h.newFields();
        f.field("title","Question","",TEXT);
        f.field("description","Details · optional","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        for(int i=1; i<=6; i++)f.field("option"+i,"Option "+i+(i>2?" · optional":""),"",TEXT);
        f.field("closesAt","Closing time · IST YYYY-MM-DDTHH:mm",timeDefault(),TEXT);
        u.note(b,"One vote per flat. Members can change their vote until voting closes. This is not a formal election platform.");
        submit("Publish poll",()-> {
            JSONArray options=new JSONArray();
            for(int i=1; i<=6; i++)if(!f.get("option"+i).trim().isEmpty())options.put(f.get("option"+i));
            write("/ops/polls",obj("title",f.get("title"),"description",f.get("description"),"closesAt",instant(f.get("closesAt")),"options",options),r->go("poll",((JSONObject)r).getString("id")));
        }
        );
    }
    private void poll() {
        title("Community poll");
        get("/ops/polls/"+id,r-> {
            JSONObject p=(JSONObject)r;
            LinearLayout c=card();
            c.addView(u.text(p.optString("title"),22,true,R.color.ap_ink));
            u.note(c,p.optString("description"));
            u.kv(c,"Voting",p.optBoolean("open")?"Open":"Closed");
            JSONArray options=p.getJSONArray("options");
            for(int i=0; i<options.length(); i++) {
                JSONObject option=options.getJSONObject(i);
                boolean mine=option.optString("id").equals(p.optString("myVote"));
                LinearLayout line=card();
                u.kv(line,option.optString("label")+(mine?" ✓":""),Ui.count(option.optInt("votes"),"vote","votes"));
                if(p.optBoolean("open")&&!h.client().account().isNull("flatId"))u.button(line,mine?"Your choice":"Vote",R.drawable.ic_check,mine,()->h.requestApi("POST","/ops/polls/"+id+"/vote",obj("optionId",option.optString("id")),x->h.refreshPage()));
            }
            if(h.isAdmin()&&p.optBoolean("open"))u.button(b,"Close voting",R.drawable.ic_clock,false,()->u.confirm("Close this poll?","Votes are preserved. No more changes will be accepted.","Close voting",()->h.requestApi("POST","/ops/polls/"+id+"/close",obj(),x->h.refreshPage())));
        }
        );
    }
    private void vehicles() {
        title("Vehicles");
        u.button(b,"Add vehicle",R.drawable.ic_plus,true,()->go("vehicle-form"));
        EditText search=h.newFields().field("search","Search vehicle or flat",h.pageDraft("vehicle-list").optString("search"),TEXT);
        LinearLayout list=u.column();
        b.addView(list);
        get("/ops/vehicles",r-> {
            JSONArray arr=(JSONArray)r;
            search.addTextChangedListener(new android.text.TextWatcher() {
                public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                public void onTextChanged(CharSequence s,int start,int before,int count) {
                    h.savePageDraft("vehicle-list",obj("search",s.toString()));
                    renderVehicleRows(arr,list,s.toString());
                }
                public void afterTextChanged(android.text.Editable value) {}
            });
            renderVehicleRows(arr,list,search.getText().toString());
        }
        );
    }
    private void renderVehicleRows(JSONArray rows,LinearLayout list,String query) {
        list.removeAllViews();
        String term=query==null?"":query.trim().toLowerCase(Locale.ROOT);
        int shown=0;
        for(int i=0;i<rows.length();i++) {
            JSONObject vehicle=rows.optJSONObject(i);
            if(vehicle==null)continue;
            if(!term.isEmpty()&&!vehicle.optString("registration").toLowerCase(Locale.ROOT).contains(term)&&!vehicle.optString("flatLabel").toLowerCase(Locale.ROOT).contains(term)&&!vehicle.optString("parkingLabel").toLowerCase(Locale.ROOT).contains(term))continue;
            row(u.card(list),vehicle.optString("registration"),vehicle.optString("flatLabel")+" · "+vehicle.optString("parkingLabel"),R.drawable.ic_car,"vehicle",vehicle.optString("id"));
            shown++;
        }
        if(shown==0)u.empty(list,R.drawable.ic_car,term.isEmpty()?"No vehicles":"No matching vehicles",term.isEmpty()?"":"Try another registration, flat, or parking label.");
    }
    private void vehicle() {
        title("Vehicle");
        get("/ops/vehicles",r-> {
            JSONObject v=find((JSONArray)r,id);
            details(v,"registration","Vehicle","kind","Type","flatLabel","Flat","parkingLabel","Parking");
            if(h.isAdmin()||v.optString("flatId").equals(h.client().account().optString("flatId")))u.button(b,"Edit vehicle",R.drawable.ic_settings,false,()->go("vehicle-form",id));
            Ui.Fields f=h.newFields();
            f.field("message","Message to flat member","Please move your vehicle; the parking access is blocked.",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            u.note(b,"Notifies the flat's in-app inbox without revealing phone numbers.");
            submit("Contact owner",()->write("/ops/vehicles/"+id+"/contact",obj("message",f.get("message")),x->u.message("Request added to the owner's inbox",true)));
        }
        );
    }
    private void vehicleForm() {
        title("Vehicle details");
        get("/ops/vehicles",r-> {
            JSONObject v=find((JSONArray)r,id);
            get("/ops/flats",fdata-> {
                Ui.Fields f=h.newFields();
                if(h.isAdmin())choices(f,"flatId","Flat",(JSONArray)fdata,"label",false,val(v,"flatId"));
                f.field("registration","Registration number",val(v,"registration"),TEXT);
                pick(f,"kind","Type",new String[] {
                    "CAR","BIKE","OTHER"
                }
                ,v.optString("kind","CAR"));
                f.field("parkingLabel","Parking label · optional",val(v,"parkingLabel"),TEXT);
                f.check("active","Active",v.optBoolean("active",true));
                submit("Save vehicle",()->write("/ops/vehicles"+(id.isEmpty()?"":"/"+id),f.values(),x->go("vehicles")));
            }
            );
        }
        );
    }
    private void eventCalendar() {
        title("Event calendar");
        android.widget.CalendarView calendar=new android.widget.CalendarView(h.activity());
        b.addView(calendar,new LinearLayout.LayoutParams(-1,u.dp(300)));
        LinearLayout selected=u.column();
        b.addView(selected);
        calendar.setOnDateChangeListener((view,year,month,day)-> {
            String date=java.time.LocalDate.of(year,month+1,day).toString();
            get("/ops/bookings",r-> {
                selected.removeAllViews();
                u.heading(selected,date);
                JSONArray arr=(JSONArray)r;
                int found=0;
                for(int i=0; i<arr.length(); i++) {
                    JSONObject booking=arr.getJSONObject(i);
                    if(!"APPROVED".equals(booking.optString("status")))continue;
                    String from=local(booking.optString("startsAt")).substring(0,10),to=local(booking.optString("endsAt")).substring(0,10);
                    if(date.compareTo(from)<0||date.compareTo(to)>0)continue;
                    found++;
                    row(u.card(selected),booking.optString("title"),booking.optString("startsAt"),R.drawable.ic_calendar,"booking",booking.optString("id"));
                }
                if(found==0)u.note(selected,"No visible approved bookings on this date.");
            }
            );
        }
        );
        u.note(b,"Admin sees apartment requests. Residents see their own event details; shared-space availability never exposes private guest details.");
    }
    private void directoryDetail() {
        title("Member");
        get("/ops/directory",r-> {
            JSONObject member=find((JSONArray)r,id);
            details(member,"name","Name","flatLabel","Flat","residentType","Type","role","App role","status","Account","mobile","Mobile");
            if(h.isAdmin()) {
                if(member.optString("source").equals("RECORD"))u.button(b,"Edit member record",R.drawable.ic_user,false,()->go("member-record-form",id));
                else u.button(b,"Access & history",R.drawable.ic_shield,false,()->go("member",id));
            }
        }
        );
    }
    private void newExpense() {
        title("Add expense");
        Ui.Fields f=h.newFields();
        f.field("title","Description","",TEXT);
        pick(f,"category","Category",new String[] {
            "Security","Housekeeping","Electricity","Water","Lift","Repairs","Generator","Other"
        }
        ,"Other");
        f.field("amount","Amount","",MONEY);
        f.field("paidOn","Date · YYYY-MM-DD",today(),TEXT);
        pick(f,"mode","Payment mode",new String[] {
            "CASH","UPI","BANK_TRANSFER","CHEQUE"
        }
        ,"UPI");
        f.field("notes","Notes · optional","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        f.check("paid","Payment completed",false);
        f.check("visibleToResidents","Visible to residents when paid",true);
        submit("Review & save",()-> {
            validateExpense(f);
            JSONObject body=f.values();
            try {
                body.put("amount",expenseAmount(f));
            } catch(JSONException e) {
                throw new IllegalArgumentException(e);
            }
            u.confirm(f.checked("paid")?"Record money spent?":"Save expense draft?",f.get("title")+" · ₹"+f.get("amount"),"Save",()->write("/ops/expenses",body,r->go("expense",((JSONObject)r).getString("id"))));
        }
        );
    }
}
