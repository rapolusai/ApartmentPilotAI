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
import java.util.*;
/** Connected native operational screens. All writes are server-validated; unavailable providers are never simulated. */ public final class FeatureScreens {
    private final FeatureHost h;
    private final Ui u;
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
    }
    public boolean render(String route,String record) {
        b=h.body();
        id=record;
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
            case "settings":case "finance-settings":case "payment-settings":case "notification-settings":settings();
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
    private JSONObject find(JSONArray rows,String target)throws JSONException {
        for(int i=0; i<rows.length(); i++)if(target.equals(rows.getJSONObject(i).optString("id")))return rows.getJSONObject(i);
        return new JSONObject();
    }
    private String friendly(String t) {
        return t.toLowerCase(Locale.ROOT).replace('_',' ');
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
    private void pick(Ui.Fields f,String key,String label,String[] values,String selected) {
        String[] labels=Arrays.stream(values).map(this::friendly).toArray(String[]::new);
        f.select(key,label,labels,values);
        if(selected!=null)f.restore(obj(key,selected));
    }
    private static String today() {
        return LocalDate.now(ZoneId.of("Asia/Kolkata")).toString();
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
        row(c,"Payment history","Declarations & receipts",R.drawable.ic_receipt,"payments");
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
        title("Apartment details");
        get("/ops/apartment",r-> {
            JSONObject a=(JSONObject)r;
            Ui.Fields f=h.newFields();
            for(String k:new String[] {
                "name","city","address","pincode"
            }
            )f.field(k,k.equals("name")?"Apartment name":friendly(k),val(a,k),TEXT);
            submit("Save",()->write("/ops/apartment",f.values(),x->go("apartment")));
        }
        );
    }
    private void flats() {
        title("Flats");
        if(h.isAdmin())u.button(b,"Add flat",R.drawable.ic_plus,true,()->go("flat-form"));
        get("/ops/flats",r-> {
            JSONArray arr=(JSONArray)r;
            for(int i=0; i<arr.length(); i++) {
                JSONObject f=arr.getJSONObject(i);
                row(card(),f.optString("label"),f.optString("name","")+" · "+(f.optBoolean("active")?"Active":"Inactive"),R.drawable.ic_home,"flat-form",f.optString("id"));
            }
        }
        );
    }
    private void blocks() {
        title("Blocks");
        u.button(b,"Add block",R.drawable.ic_plus,true,()->go("block-form"));
        get("/ops/blocks",r-> {
            JSONArray rows=(JSONArray)r;
            if(rows.length()==0)u.empty(b,"No blocks configured","Add a block before creating its flats.");
            for(int i=0;i<rows.length();i++) {
                JSONObject block=rows.getJSONObject(i);
                row(card(),"Block "+block.optString("name"),block.optInt("flatCount")+" flats",R.drawable.ic_building,"block-form",block.optString("id"));
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
            u.button(b,"Invites & approvals",R.drawable.ic_users,false,()->go("members"));
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
            if(arr.length()==0)u.empty(b,"Committee not added","Apartment team appears here.");
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
            choices(f,"member","End account access",arr,"name",false,"");
            f.field("reason","Reason","Resident moved out",TEXT);
            u.note(b,"Existing bills and receipts stay with the flat. Invite and verify the next resident afterward.");
            submit("End old access",()->u.confirm("Revoke old access?","The selected account will be disabled and signed out.","End access",()->write("/ops/members/"+f.get("member")+"/end-access",obj("reason",f.get("reason")),x->go("members"))));
        }
        );
    }
    private void contacts(String kind) {
        title(kind.equals("VENDOR")?"Vendors":"Contacts");
        String route=kind.equals("VENDOR")?"vendor":"contact";
        if(h.isStaff())u.button(b,"Add "+route,R.drawable.ic_plus,true,()->go(route+"-form"));
        get("/ops/contacts?kind="+kind,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,"No contacts yet","Add verified apartment contacts.");
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
        title(kind.equals("ISSUE")?"Building issues":"Private requests");
        u.button(b,"Report "+(kind.equals("ISSUE")?"issue":"request"),R.drawable.ic_plus,true,()->go(kind.equals("ISSUE")?"issue-categories":"complaint-form"));
        if(kind.equals("ISSUE")) {
            LinearLayout c=card();
            row(c,"My requests","Private flat issues",R.drawable.ic_user,"complaints");
            row(c,"Scheduled services","Preventive maintenance",R.drawable.ic_clock,"services");
        }
        get("/ops/tickets?kind="+kind,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,"All clear","No issues have been reported.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject t=arr.getJSONObject(i);
                row(card(),t.optString("title"),friendly(t.optString("status"))+" · "+t.optString("scope")+(kind.equals("ISSUE")?" · "+t.optInt("affectedCount")+" following":""),categoryIcon(t.optString("category")),"ticket",t.optString("id"));
            }
        }
        );
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
                u.kv(ev,e.optString("actor"),val(e,"status"));
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
                u.hero(b,rows.length()+" flats",ticket.optString("title"),ticket.optString("scope"));
                if(rows.length()==0)u.empty(b,"No affected flats yet","Residents can follow an active shared-area issue.");
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
            if(arr.length()==0)u.empty(b,"No schedules yet","Set reminders for lift service, cleaning and equipment checks.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject s=arr.getJSONObject(i);
                row(card(),s.optString("title"),s.optString("nextOn")+" · "+friendly(s.optString("frequency")),categoryIcon(s.optString("category")),"service",s.optString("id"));
            }
        }
        );
    }
    private void service() {
        title("Scheduled service");
        get("/ops/services/"+id,r-> {
            JSONObject s=(JSONObject)r;
            details(s,"title","Service","vendorName","Vendor","nextOn","Next due","frequency","Frequency");
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
            submit("Complete",()->write("/ops/services/"+id+"/complete",obj("scheduledOn",s.optString("nextOn"),"completedOn",f.get("completedOn"),"cost",amount(f,"cost"),"note",f.get("note"),"paid",f.checked("paid")),x->go("service",id)));
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
        get("/ops/bookings",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,"No event requests","Request a space for your next celebration.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject e=arr.getJSONObject(i);
                row(card(),e.optString("title"),friendly(e.optString("status"))+" · "+local(e.optString("startsAt"))+" IST",R.drawable.ic_calendar,"booking",e.optString("id"));
            }
        }
        );
    }
    private void booking() {
        title("Event details");
        get("/ops/bookings/"+id,r-> {
            JSONObject e=(JSONObject)r;
            u.hero(b,friendly(e.optString("status")),e.optString("title"),local(e.optString("startsAt"))+" IST");
            details(e,"eventType","Event","flatLabel","Flat","guests","Guests","notes","Notes","decisionNote","Decision");
            LinearLayout timing=card();
            u.kv(timing,"Start · IST",local(e.optString("startsAt")));
            u.kv(timing,"End · IST",local(e.optString("endsAt")));
            JSONArray items=e.optJSONArray("items");
            if(items!=null)for(int i=0; i<items.length(); i++) {
                JSONObject v=items.getJSONObject(i);
                u.kv(timing,(v.optBoolean("offered")?"Alternative: ":"")+v.optString("name"),v.optString("quantity"));
            }
            String s=e.optString("status");
            if("ALTERNATIVE".equals(s)) {
                u.kv(timing,"Proposed start",local(val(e,"offeredStart")));
                u.kv(timing,"Proposed end",local(val(e,"offeredEnd")));
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
                    u.confirm(offer?"Send alternative?":"Submit event request?",f.get("start")+" to "+f.get("end")+" IST\n"+items.length()+" resource selections. No booking fee.\n"+(offer?"The requester must accept.":"Admin approval is required."),"Submit",()->write(offer?"/ops/bookings/"+id+"/decision":"/ops/bookings",payload,x->go("booking",((JSONObject)x).getString("id"))));
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
            if(arr.length()==0)u.empty(b,"No spaces configured","Admin can add a terrace, event area and guest parking.");
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
                u.kv(c,"Unavailable",local(v.optString("startsAt"))+" — "+local(v.optString("endsAt")));
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
        if(h.isStaff())u.button(b,"Send notice",R.drawable.ic_notice,true,()->go("notice-templates"));
        get("/notices",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,"No notices yet","Updates from your apartment team.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject n=arr.getJSONObject(i);
                String status=n.optBoolean("scheduleConfirmed")?"Scheduled":friendly(n.optString("status"));
                row(card(),(n.optBoolean("pinned")?"★ ":"")+n.optString("title"),friendly(n.optString("audience"))+" · "+status,R.drawable.ic_notice,"notice",n.optString("id"));
            }
        }
        );
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
                    else u.note(b,"Scheduled · "+local(n.optString("scheduledAt"))+" IST · in-app delivery");
                } else u.button(b,n.optBoolean("pinned")?"Unpin":"Pin notice",R.drawable.ic_notice,false,()->h.requestApi("POST","/notices/"+id+"/pin",obj("pinned",!n.optBoolean("pinned")),x->h.refreshPage()));
                if(n.optBoolean("phoneNotify"))u.note(b,"Phone notification requested · "+n.optString("externalDeliveryStatus","NOT_CONFIGURED"));
                JSONArray recipients=n.optJSONArray("recipients");
                if(recipients!=null) {
                    u.heading(b,"Audience · "+recipients.length());
                    for(int i=0; i<recipients.length(); i++) {
                        JSONObject x=recipients.getJSONObject(i);
                        u.kv(card(),x.optString("name"),!x.isNull("acknowledgedAt")?"Acknowledged":!x.isNull("readAt")?"Read":"Not opened");
                    }
                }
            } else {
                u.button(b,"Mark read",R.drawable.ic_check,false,()->h.requestApi("POST","/notices/"+id+"/read",obj(),x->Toast.makeText(h.activity(),"Marked read",Toast.LENGTH_SHORT).show()));
                if(n.optBoolean("acknowledge"))u.button(b,"Acknowledge",R.drawable.ic_check,true,()->u.confirm("Acknowledge notice?","Confirm you have read this announcement.","Acknowledge",()->h.requestApi("POST","/notices/"+id+"/read",obj("acknowledge",true),x->Toast.makeText(h.activity(),"Acknowledged",Toast.LENGTH_SHORT).show())));
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
            u.note(b,"Preview first. Publishing creates only authenticated in-app inbox records. Phone delivery remains NOT_CONFIGURED.");
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
            u.kv(delivery,"Phone notification",n.optBoolean("phoneNotify")?"Requested · "+n.optString("externalDeliveryStatus","NOT_CONFIGURED"):"Off");
            u.kv(delivery,"Acknowledgement",n.optBoolean("acknowledge")?"Required":"Not requested");
            u.kv(delivery,"Publish",n.isNull("scheduledAt")?"Now":local(n.optString("scheduledAt"))+" IST");
            u.note(b,"Delivery mode: authenticated in-app inbox only. A phone request is not proof of external delivery.");
            u.button(b,"Edit",R.drawable.ic_settings,false,()->go("notice-form",id));
            if(n.isNull("scheduledAt"))submit("Publish",()->u.confirm("Publish notice?","Send this immutable message to the reviewed in-app audience.","Publish",()->h.requestApi("POST","/notices/"+id+"/publish",obj(),x->go("notice",id))));
            else submit("Schedule",()->u.confirm("Schedule notice?","The local backend will publish it at "+local(n.optString("scheduledAt"))+" IST.","Schedule",()->write("/notices/"+id+"/schedule",obj(),x->go("notice",id))));
            u.button(b,"Keep draft",R.drawable.ic_report,false,()->go("notices"));
        });
    }
    private void documents() {
        title("Documents");
        if(h.isStaff())u.button(b,"Add document",R.drawable.ic_plus,true,()->go("document-form"));
        get("/ops/documents",r-> {
            JSONArray a=(JSONArray)r;
            if(a.length()==0)u.empty(b,"No shared files","Apartment documents appear here.");
            for(int i=0; i<a.length(); i++) {
                JSONObject x=a.getJSONObject(i);
                row(card(),x.optString("title"),x.optString("category")+" · "+(x.optBoolean("archived")?"Archived":x.optBoolean("public")?"Residents":"Staff only"),R.drawable.ic_copy,"document",x.optString("id"));
            }
        }
        );
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
            u.empty(b,"No record selected","");
            return;
        }
        String kind=parts[0],parent=parts[1];
        boolean writer=h.isStaff()||kind.equals("PAYMENT")||kind.equals("TICKET");
        if(writer)u.button(b,"Attach PDF or photo",R.drawable.ic_plus,true,()->h.chooseAttachment(kind,parent));
        u.note(b,"Up to 5 files · 5 MB each. Access follows the parent record.");
        get("/ops/files?kind="+kind+"&parentId="+parent,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,"No attachments","");
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                u.iconRow(card(),x.optString("name"),(x.optLong("size")/1024)+" KB",R.drawable.ic_copy,()->get("/ops/files/"+x.optString("id"),data->h.openSharedFile((JSONObject)data)));
            }
        }
        );
    }
    private void settings() {
        title("Financial controls");
        get("/ops/settings",r-> {
            JSONObject x=(JSONObject)r;
            Ui.Fields f=h.newFields();
            u.heading(b,"Association payment account");
            for(String[] a:new String[][] {
                {
                    "payee","Account holder"
                }
                , {
                    "upi","UPI ID"
                }
                , {
                    "bank","Bank name"
                }
                , {
                    "account","Account number"
                }
                , {
                    "ifsc","IFSC"
                }
            }
            )f.field(a[0],a[1],val(x,a[0]),TEXT);
            u.heading(b,"Billing & verification");
            f.check("billVacant","Charge vacant flats",x.optBoolean("billVacant",true));
            f.check("lateEnabled","Enable late fee for future bills",x.optBoolean("lateEnabled"));
            f.field("lateFee","One-time late fee",x.optString("lateFee","0"),MONEY);
            f.field("graceDays","Grace days",x.optString("graceDays","0"),NUM);
            f.check("proofRequired","Require proof before non-cash approval",x.optBoolean("proofRequired"));
            f.check("expensesVisible","Share paid expenses with residents",x.optBoolean("expensesVisible",true));
            u.heading(b,"Reminders");
            f.check("dueNotify","New bill notifications",x.optBoolean("dueNotify",true));
            f.check("reminders","Unpaid reminders",x.optBoolean("reminders",true));
            f.field("reminderDays","Days of month · e.g. 5,9,11,15",x.optString("reminderDays","5,9,11,15"),TEXT);
            f.field("quietStart","Quiet hours start · HH:mm",x.optString("quietStart","21:00"),TEXT);
            f.field("quietEnd","Quiet hours end · HH:mm",x.optString("quietEnd","08:00"),TEXT);
            u.heading(b,"Common-space booking rules");
            f.field("bookingRules","Rules",x.optString("bookingRules"),TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            u.note(b,"Reminders pause while payments are under review. This build has an in-app inbox; phone push is not connected.");
            submit("Save settings",()-> {
                JSONObject j=f.values();
                try {
                    j.put("graceDays",Integer.parseInt(f.get("graceDays")));
                    j.put("lateFee",amount(f,"lateFee"));
                } catch(JSONException e) {
                    throw new IllegalArgumentException(e);
                }
                write("/ops/settings",j,z->go("money"));
            }
            );
            if(h.isAdmin())u.button(b,"Opening bank + cash",R.drawable.ic_wallet,false,()->go("opening-balance"));
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
            if(arr.length()==0)u.empty(b,"One rate for all flats","Add only agreed exceptions.");
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
            if(found==0)u.empty(b,"No contributions for "+h.selectedMonth(),"Regular monthly maintenance remains separate.");
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
            u.empty(b,"No contribution draft","Start a new collection to review it here.");
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
                Toast.makeText(h.activity(),result.optInt("created")+" contribution due(s) created. No payment recorded.",Toast.LENGTH_LONG).show();
                go("contributions");
            });
        });
    }
    private void income() {
        title("Other income");
        u.button(b,"Record income",R.drawable.ic_plus,true,()->go("income-form"));
        get("/ops/income",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,"No other income","");
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
            fileLink("EXPENSE",id);
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
            f.field("category","Category",x.optString("category"),TEXT);
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
                JSONObject j=f.values();
                try {
                    j.put("amount",amount(f,"amount"));
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
        submit("Review reversal",()->u.confirm("Record full reversal?",f.get("reason"),"Confirm reversal",()->write("/ops/"+(payment?"payments/":"expenses/")+id+"/reverse",obj("reason",f.get("reason"),"verified",f.checked("verified")),r->go(payment?"payment":"expense",id))));
    }
    private void recurring() {
        title("Recurring expenses");
        u.button(b,"Add recurring expense",R.drawable.ic_plus,true,()->go("recurring-form"));
        get("/ops/recurring",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,"No recurring templates","Recurring entries are drafts until payment is confirmed.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject x=arr.getJSONObject(i);
                row(card(),x.optString("title"),Ui.money(x.opt("amount"))+" · "+x.optString("nextOn")+" · "+(x.optBoolean("active")?"Active":"Paused"),R.drawable.ic_clock,"recurring-form",x.optString("id"));
            }
        }
        );
    }
    private void recurringForm() {
        title("Recurring expense");
        get("/ops/recurring",r-> {
            JSONObject x=find((JSONArray)r,id);
            Ui.Fields f=h.newFields();
            f.field("title","Description",val(x,"title"),TEXT);
            f.field("category","Category",x.optString("category","Security"),TEXT);
            f.field("amount","Expected amount",val(x,"amount"),MONEY);
            pick(f,"frequency","Frequency",FREQUENCIES,x.optString("frequency","MONTHLY"));
            f.field("nextOn","Next due · YYYY-MM-DD",x.optString("nextOn",today()),TEXT);
            f.check("active","Enabled",x.optBoolean("active",true));
            u.note(b,"Each cycle prepares an expense draft, never an automatic cash payment.");
            submit("Save template",()-> {
                JSONObject j=f.values();
                try {
                    j.put("amount",amount(f,"amount"));
                } catch(JSONException e) {
                    throw new IllegalArgumentException(e);
                }
                write("/ops/recurring"+(id.isEmpty()?"":"/"+id),j,z->go("recurring"));
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
            if(arr.length()==0)u.empty(b,"No transactions this month","");
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
            u.button(b,"Share PDF",R.drawable.ic_report,false,()->h.exportPdf("Apartment monthly statement · "+h.selectedMonth(),x));
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
                Toast.makeText(h.activity(),"UPI ID copied",Toast.LENGTH_SHORT).show();
            }
            );
            if(h.isStaff())u.button(b,"Edit payment account",R.drawable.ic_settings,false,()->go("settings"));
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
                row(c,"Join requests",x.optString("joinRequests","0")+" pending",R.drawable.ic_users,"members");
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
        row(c,"Reminders & late fees","Notification rules",R.drawable.ic_bell,"settings");
        row(c,"Recurring expenses","Automatic drafts",R.drawable.ic_clock,"recurring");
        row(c,"Service schedules","Preventive maintenance",R.drawable.ic_tools,"services");
        u.button(b,"Run due tasks now",R.drawable.ic_clock,true,()->u.confirm("Run scheduled tasks?","Due tasks are evaluated for today's date. Existing bills and jobs are not duplicated.","Run tasks",()->h.requestApi("POST","/ops/automation/run",obj(),r-> {
            JSONObject x=(JSONObject)r;
            details(x,"billsCreated","Bills generated","expenseDraftsCreated","Expense drafts","lateFeesApplied","Late charges","reminderRecipientsConsidered","Eligible reminder recipients","noticesPublished","Notices published","bookingsCompleted","Ended reservations");
        }
        )));
        u.button(b,"Activity log",R.drawable.ic_clock,false,()->go("audit"));
    }
    private void subscription() {
        title("Subscription & referrals");
        get("/ops/subscription",r-> {
            JSONObject s=(JSONObject)r;
            u.hero(b,"Category "+s.optString("category")+" · "+s.optInt("flats")+" flats",Ui.money(s.opt("price"))+" / month",friendly(s.optString("status")));
            LinearLayout c=card();
            u.kv(c,"Resident access","One account per flat");
            u.kv(c,"First month ends",s.optString("firstEnd"));
            u.kv(c,"Automatic debit","Not enabled");
            u.heading(b,"Refer two new apartments");
            String code=s.optString("referralCode");
            u.kv(card(),"Your referral code",code);
            u.button(b,"Share referral",R.drawable.ic_users,true,()->h.activity().startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,"ApartmentPilotAI referral: "+code+". Two new independently verified apartment activations qualify the three apartments for their first month only. Terms apply."),"Share referral")));
            u.button(b,"I was referred",R.drawable.ic_plus,false,()->go("referral-claim"));
            if(s.isNull("installedAt"))u.button(b,"Register this installation",R.drawable.ic_check,false,()->h.requestApi("POST","/ops/subscription/install",obj(),x->h.refreshPage()));
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
                u.kv(invoice,x.optString("startsOn")+" – "+x.optString("endsOn"),friendly(x.optString("status")));
                u.kv(invoice,"Price",Ui.money(x.opt("amount")));
                u.kv(invoice,"Referral credit",Ui.money(x.opt("credit")));
                u.kv(invoice,"Verified paid",Ui.money(x.opt("paid")));
                u.button(invoice,"Save invoice record",R.drawable.ic_receipt,false,()->h.exportPdf("Subscription record · not a tax invoice",x));
            }
            u.note(b,"No checkout or automatic debit is connected. Only the service operator can independently verify activations and subscription payments. This is separate from residents' maintenance.");
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
    }
    private void help() {
        title("Help");
        LinearLayout c=card();
        row(c,"Apartment contacts","Get help from your team",R.drawable.ic_phone,"contacts");
        row(c,"Privacy & access","Who can see what",R.drawable.ic_shield,"privacy");
        row(c,"Change PIN","Revoke existing sessions",R.drawable.ic_shield,"change-pin");
        row(c,"Build status","Integrations & test limits",R.drawable.ic_grid,"status");
        u.note(b,"Submitting a payment is not approval. Receipts require verification. This app is not an emergency response service.");
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
            if(arr.length()==0)u.empty(b,"No polls yet","");
            for(int i=0; i<arr.length(); i++) {
                JSONObject p=arr.getJSONObject(i);
                row(card(),p.optString("title"),p.optBoolean("closed")?"Closed":"Ends "+local(p.optString("closesAt"))+" IST",R.drawable.ic_check,"poll",p.optString("id"));
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
                u.kv(line,option.optString("label")+(mine?" ✓":""),option.optInt("votes")+" votes");
                if(p.optBoolean("open")&&!h.client().account().isNull("flatId"))u.button(line,mine?"Your choice":"Vote",R.drawable.ic_check,mine,()->h.requestApi("POST","/ops/polls/"+id+"/vote",obj("optionId",option.optString("id")),x->h.refreshPage()));
            }
            if(h.isAdmin()&&p.optBoolean("open"))u.button(b,"Close voting",R.drawable.ic_clock,false,()->u.confirm("Close this poll?","Votes are preserved. No more changes will be accepted.","Close voting",()->h.requestApi("POST","/ops/polls/"+id+"/close",obj(),x->h.refreshPage())));
        }
        );
    }
    private void vehicles() {
        title("Vehicles");
        u.button(b,"Add vehicle",R.drawable.ic_plus,true,()->go("vehicle-form"));
        get("/ops/vehicles",r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)u.empty(b,"No vehicles","");
            for(int i=0; i<arr.length(); i++) {
                JSONObject v=arr.getJSONObject(i);
                row(card(),v.optString("registration"),v.optString("flatLabel")+" · "+v.optString("parkingLabel"),R.drawable.ic_car,"vehicle",v.optString("id"));
            }
        }
        );
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
            submit("Contact owner",()->write("/ops/vehicles/"+id+"/contact",obj("message",f.get("message")),x->Toast.makeText(h.activity(),"Request added to the owner's inbox",Toast.LENGTH_LONG).show()));
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
                    row(u.card(selected),booking.optString("title"),local(booking.optString("startsAt"))+" IST",R.drawable.ic_calendar,"booking",booking.optString("id"));
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
            JSONObject body=f.values();
            try {
                body.put("amount",amount(f,"amount"));
            } catch(JSONException e) {
                throw new IllegalArgumentException(e);
            }
            u.confirm(f.checked("paid")?"Record money spent?":"Save expense draft?",f.get("title")+" · ₹"+f.get("amount"),"Save",()->write("/ops/expenses",body,r->go("expense",((JSONObject)r).getString("id"))));
        }
        );
    }
}
