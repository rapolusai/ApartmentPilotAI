package com.rapolus.apartmentpilotai;
import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.*;
import androidx.core.view.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rapolus.apartmentpilotai.data.ApiClient;
import com.rapolus.apartmentpilotai.data.HttpFailurePolicy;
import com.rapolus.apartmentpilotai.ui.Ui;
import com.rapolus.apartmentpilotai.ui.FeatureHost;
import com.rapolus.apartmentpilotai.ui.FeatureScreens;
import com.rapolus.apartmentpilotai.ui.LocalFiles;
import org.json.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
/**
* Native local-development slice: all visible enabled actions call the real backend.
* The complete frozen reference stays outside the APK; this is not a WebView wrapper.
* Remaining modules are listed explicitly under Implementation status.
*/
public final class MainActivity extends AppCompatActivity implements FeatureHost {
    private FeatureScreens featureScreens;
    private LocalFiles localFiles;
    private String operationKey=UUID.randomUUID().toString(),attachmentKind="",attachmentParent="";
    private final java.util.concurrent.ExecutorService fileWorker=java.util.concurrent.Executors.newSingleThreadExecutor();
    private ApiClient api;
    private Ui ui;
    private LinearLayout content;
    private BottomNavigationView nav;
    private TextView title,subtitle;
    private View progress;
    private Ui.Fields fields;
    private JSONObject restoreFields;
    private String page="welcome",id="",loginRole="ADMIN",month=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString(),inviteCode="",paymentRequestKey=UUID.randomUUID().toString(),reminderRequestKey=UUID.randomUUID().toString(),expenseRequestKey=UUID.randomUUID().toString();
    private String resumePage="",resumeId="";
    private JSONObject resumeFields;
    private JSONObject signupDraft=new JSONObject(),invitePreview=new JSONObject();
    private final Deque<String[]> history=new ArrayDeque<>();
    private int generation=0;
    private boolean busy=false;
    private final Set<String> selected=new LinkedHashSet<>();
    private interface Success {
        void accept(Object result)throws Exception;
    }
    private static final int TEXT=InputType.TYPE_CLASS_TEXT,NUMBER=InputType.TYPE_CLASS_NUMBER,MONEY=InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        WindowCompat.setDecorFitsSystemWindows(getWindow(),false);
        setContentView(R.layout.activity_main);
        api=((PilotApplication)getApplication()).api();
        ui=new Ui(this);
        content=findViewById(R.id.content);
        nav=findViewById(R.id.navigation);
        title=findViewById(R.id.title);
        subtitle=findViewById(R.id.subtitle);
        progress=findViewById(R.id.progress);
        featureScreens=new FeatureScreens(this);
        localFiles=new LocalFiles(this);
        View root=findViewById(R.id.root);
        ViewCompat.setOnApplyWindowInsetsListener(root,(v,insets)-> {
            androidx.core.graphics.Insets sys=insets.getInsets(WindowInsetsCompat.Type.systemBars());
            androidx.core.graphics.Insets keyboard=insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(sys.left,sys.top,sys.right,Math.max(sys.bottom,keyboard.bottom));
            return insets;
        }
        );
        boolean dark=(getResources().getConfiguration().uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;
        new WindowInsetsControllerCompat(getWindow(),root).setAppearanceLightStatusBars(!dark);
        findViewById(R.id.back).setOnClickListener(v->back());
        findViewById(R.id.theme).setOnClickListener(v-> {
            int mode=dark?AppCompatDelegate.MODE_NIGHT_NO:AppCompatDelegate.MODE_NIGHT_YES;
            getSharedPreferences("appearance",0).edit().putInt("mode",mode).apply();
            AppCompatDelegate.setDefaultNightMode(mode);
        }
        );
        findViewById(R.id.notifications).setOnClickListener(v->go("notifications"));
        nav.setOnItemSelectedListener(item-> {
            if(busy)return false;
            int itemId=item.getItemId();
            go(itemId==R.id.nav_home?"home":itemId==R.id.nav_money?(staff()?"money":"dues"):itemId==R.id.nav_community?"community":itemId==R.id.nav_issues?"issues":"more");
            return true;
        }
        );
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                back();
            }
        }
        );
        if(b!=null) {
            operationKey=b.getString("operationKey",operationKey);
            attachmentKind=b.getString("attachmentKind","");
            attachmentParent=b.getString("attachmentParent","");
            page=b.getString("page","welcome");
            id=b.getString("id","");
            month=b.getString("month",month);
            loginRole=b.getString("loginRole","ADMIN");
            paymentRequestKey=b.getString("paymentKey",paymentRequestKey);
            reminderRequestKey=b.getString("reminderKey",reminderRequestKey);
            expenseRequestKey=b.getString("expenseKey",expenseRequestKey);
            ArrayList<String> savedSelection=b.getStringArrayList("selectedPayments");
            if(savedSelection!=null)selected.addAll(savedSelection);
            inviteCode=b.getString("inviteCode","");
            try {
                restoreFields=new JSONObject(b.getString("form","{}"));
                resumeFields=new JSONObject(b.getString("resumeFields","{}"));
                signupDraft=((PilotApplication)getApplication()).transientSignup;
                invitePreview=new JSONObject(b.getString("invitePreview","{}"));
            } catch(JSONException ignored) {
            }
            resumePage=b.getString("resumePage","");
            resumeId=b.getString("resumeId","");
        } else if(api.signedIn())page="home";
        render();
    }
    @Override protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString("operationKey",operationKey);
        b.putString("attachmentKind",attachmentKind);
        b.putString("attachmentParent",attachmentParent);
        b.putString("page",page);
        b.putString("id",id);
        b.putString("month",month);
        b.putString("loginRole",loginRole);
        b.putString("paymentKey",paymentRequestKey);
        b.putString("reminderKey",reminderRequestKey);
        b.putString("expenseKey",expenseRequestKey);
        b.putStringArrayList("selectedPayments",new ArrayList<>(selected));
        b.putString("inviteCode",inviteCode);
        b.putString("invitePreview",invitePreview.toString());
        b.putString("resumePage",resumePage);
        b.putString("resumeId",resumeId);
        b.putString("resumeFields",resumeFields==null?"{}":resumeFields.toString());
        if(fields!=null) {
            JSONObject safeValues=fields.values();
            java.util.ArrayList<String> privateKeys=new java.util.ArrayList<>();
            safeValues.keys().forEachRemaining(k-> {
                String key=k.toLowerCase(java.util.Locale.ROOT);
                if(key.contains("pin")||key.contains("password")||key.contains("token"))privateKeys.add(k);
            }
            );
            privateKeys.forEach(safeValues::remove);
            b.putString("form",safeValues.toString());
        }
    }
    private boolean staff() {
        return api.signedIn()&&!"RESIDENT".equals(api.account().optString("role"));
    }
    private boolean admin() {
        return api.signedIn()&&"ADMIN".equals(api.account().optString("role"));
    }
    private void go(String p) {
        go(p,"");
    }
    private void go(String p,String record) {
        history.push(new String[] {
            page,id
        }
        );
        page=p;
        id=record;
        resetCommandKey();
        restoreFields=null;
        if(!p.equals("bulk-approval")&&!p.equals("bulk-review"))selected.clear();
        render();
        ((ScrollView)findViewById(R.id.scroll)).scrollTo(0,0);
    }
    private void back() {
        if(busy)return;
        if(history.isEmpty()) {
            if(api.signedIn()&&!page.equals("home")) {
                page="home";
                render();
            } else new Ui(this).confirm("Close app?","Your records remain on the server.","Close",this::finish);
            return;
        }
        String[] p=history.pop();
        page=p[0];
        id=p[1];
        resetCommandKey();
        restoreFields=null;
        render();
    }
    private void header(String s) {
        title.setText(s);
    }
    private void render() {
        generation++;
        busy=false;
        progress.setVisibility(View.GONE);
        content.removeAllViews();
        fields=null;
        boolean authPage=Arrays.asList("welcome","login","admin-login","treasurer-login","resident-login","forgot-pin","verify-reset","reset-pin","invite-invalid","admin-signup","setup-apartment","setup-done","join","signup","choose-flat","signup-pending","connection","status","offline","session-expired","error-state","not-found","loading-state").contains(page);
        if(!api.signedIn()&&!authPage) {
            page="welcome";
            id="";
        }
        nav.setVisibility(api.signedIn()?View.VISIBLE:View.GONE);
        nav.getMenu().findItem(R.id.nav_money).setTitle(staff()?R.string.money:R.string.dues);
        findViewById(R.id.notifications).setVisibility(api.signedIn()?View.VISIBLE:View.GONE);
        subtitle.setText(api.signedIn()?api.account().optString("apartmentName","ApartmentCare"):"ApartmentCare");
        header(page.substring(0,1).toUpperCase(Locale.ROOT)+page.substring(1).replace('-',' '));
        if(featureScreens.render(page,id)) {
            if(fields!=null&&restoreFields!=null) {
                fields.restore(restoreFields);
                restoreFields=null;
            }
            return;
        }
        switch(page) {
            case "welcome":welcome();
            break;
            case "login":welcome();
            break;
            case "admin-login":loginRole="ADMIN";login();
            break;
            case "treasurer-login":loginRole="TREASURER";login();
            break;
            case "resident-login":loginRole="RESIDENT";login();
            break;
            case "forgot-pin":case "verify-reset":case "reset-pin":recoveryUnavailable();
            break;
            case "invite-invalid":systemState("Check your invite","The code is invalid, expired or revoked.",R.drawable.ic_error,true,R.color.ap_amber,R.color.ap_ambg,()->go("join"),"Try another code");
            break;
            case "admin-signup":register();
            break;
            case "setup-apartment":setup();
            break;
            case "join":join();
            break;
            case "signup":signup();
            break;
            case "choose-flat":chooseFlat();
            break;
            case "signup-pending":ui.empty(content,"Almost home.","Your request is waiting for admin approval.");
            ui.button(content,"Sign in",R.drawable.ic_arrow_right,true,()-> {
                loginRole="RESIDENT";
                go("resident-login");
            }
            );
            break;
            case "setup-done":setupDone();
            break;
            case "offline":systemState("Connection paused","Saved information is still available.",R.drawable.ic_error,true,R.color.ap_amber,R.color.ap_ambg,this::resumePrevious,"Retry connection");
            break;
            case "session-expired":systemState("Sign in again","Your session expired. Your records are safe.",R.drawable.ic_shield,true,R.color.ap_green,R.color.ap_mint,this::signInAfterExpiry,"Sign in");
            break;
            case "access-denied":systemState("This page is restricted","Use an account with the right permission.",R.drawable.ic_shield,true,R.color.ap_green,R.color.ap_mint,()->go("home"),"Back to home");
            break;
            case "empty-state":systemState("A fresh start","Your first record will appear here.",R.drawable.ic_building,false,R.color.ap_green,R.color.ap_mint,()->go("home"),"Back to home");
            break;
            case "error-state":systemState("Couldn’t load this page","Try again. Your saved work is still here.",R.drawable.ic_error,true,R.color.ap_amber,R.color.ap_ambg,this::resumePrevious,"Try again");
            break;
            case "not-found":systemState("That page isn’t here","Return home and choose an available feature.",R.drawable.ic_error,false,R.color.ap_green,R.color.ap_mint,()->go(api.signedIn()?"home":"welcome"),"Back");
            break;
            case "loading-state":loadingState();
            break;
            case "home":home();
            break;
            case "money":moneyMenu();
            break;
            case "dues":dues();
            break;
            case "bill":bill();
            break;
            case "mark-paid":markPaid();
            break;
            case "billing-settings":billing();
            break;
            case "rate-history":rateHistory();
            break;
            case "payments":case "approvals":payments();
            break;
            case "bulk-approval":bulkApproval();
            break;
            case "bulk-review":bulkReview();
            break;
            case "payment-reject":paymentReject();
            break;
            case "outstanding":outstanding();
            break;
            case "reminder-preview":reminderPreview();
            break;
            case "bill-preview":billPreview();
            break;
            case "payment":payment();
            break;
            case "receipt":receipt();
            break;
            case "expenses":expenses();
            break;
            case "expense-form":expenseForm();
            break;
            case "reports":reports();
            break;
            case "community":community();
            break;
            case "members":members();
            break;
            case "member":member();
            break;
            case "invite":invite();
            break;
            case "notices":notices();
            break;
            case "notice":notice();
            break;
            case "notice-form":noticeForm();
            break;
            case "notification":notificationDetail();
            break;
            case "notifications":notifications();
            break;
            case "audit":audit();
            break;
            case "more":more();
            break;
            case "profile":profile();
            break;
            case "connection":connection();
            break;
            case "status":case "pending-module":status();
            break;
            default:page="not-found";render();return;
        }
        if(fields!=null&&restoreFields!=null) {
            fields.restore(restoreFields);
            restoreFields=null;
        }
    }
    private void request(String method,String path,JSONObject data,Success onSuccess) {
        if(busy)return;
        final boolean authenticatedRequest=api.signedIn();
        final String originPage=page,originId=id,originRole=authenticatedRequest?api.account().optString("role",loginRole):loginRole;
        final JSONObject originFields=safeFieldSnapshot();
        busy=true;
        progress.setVisibility(View.VISIBLE);
        setButtons(content,false);
        final int version=generation;
        api.call(method,path,data,(result,error)-> {
            if(isFinishing()||isDestroyed()||version!=generation)return;
            busy=false;
            progress.setVisibility(View.GONE);
            setButtons(content,true);
            if(error!=null) {
                if(error.status==404&&!authenticatedRequest&&path.startsWith("/auth/invites/")) {
                    page="invite-invalid";
                    id="";
                    render();
                    return;
                }
                HttpFailurePolicy.Destination destination=HttpFailurePolicy.destination(error.status,authenticatedRequest);
                if(destination!=HttpFailurePolicy.Destination.INLINE) {
                    showFailureState(destination,originPage,originId,originRole,originFields);
                    return;
                }
                showError(error.message);
                return;
            }
            try {
                onSuccess.accept(result);
                if(fields!=null&&restoreFields!=null) {
                    fields.restore(restoreFields);
                    restoreFields=null;
                }
            } catch(Exception e) {
                showError("Could not display the response: "+(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()));
            }
        }
        );
    }
    private void setButtons(View v,boolean enabled) {
        if(v instanceof Button||v instanceof Spinner)v.setEnabled(enabled);
        if(v instanceof ViewGroup) {
            ViewGroup g=(ViewGroup)v;
            for(int i=0; i<g.getChildCount(); i++)setButtons(g.getChildAt(i),enabled);
        }
    }
    private void showError(String text) {
        LinearLayout card=ui.card(content);
        card.addView(ui.text(text,12,false,R.color.ap_red));
        ui.button(card,"Retry / refresh",R.drawable.ic_clock,false,()-> {
            restoreFields=fields==null?null:fields.values();
            render();
        }
        );
    }
    private JSONObject safeFieldSnapshot() {
        if(fields==null)return null;
        JSONObject values=fields.values();
        ArrayList<String> privateKeys=new ArrayList<>();
        values.keys().forEachRemaining(k-> {
            String key=k.toLowerCase(Locale.ROOT);
            if(key.contains("pin")||key.contains("password")||key.contains("token"))privateKeys.add(k);
        });
        privateKeys.forEach(values::remove);
        return values;
    }
    private void showFailureState(HttpFailurePolicy.Destination destination,String originPage,String originId,String originRole,JSONObject originFields) {
        resumePage=originPage;
        resumeId=originId;
        resumeFields=originFields;
        if(destination==HttpFailurePolicy.Destination.SESSION_EXPIRED) {
            loginRole=originRole;
            history.clear();
            page="session-expired";
        } else if(destination==HttpFailurePolicy.Destination.OFFLINE)page="offline";
        else if(destination==HttpFailurePolicy.Destination.ACCESS_DENIED)page="access-denied";
        else if(destination==HttpFailurePolicy.Destination.NOT_FOUND)page="not-found";
        else page="error-state";
        id="";
        render();
    }
    private void resumePrevious() {
        String target=resumePage;
        String record=resumeId;
        JSONObject values=resumeFields;
        resumePage="";
        resumeId="";
        resumeFields=null;
        page=target.isEmpty()?(api.signedIn()?"home":"welcome"):target;
        id=record;
        restoreFields=values;
        render();
    }
    private void signInAfterExpiry() {
        page="ADMIN".equals(loginRole)?"admin-login":"TREASURER".equals(loginRole)?"treasurer-login":"resident-login";
        id="";
        history.clear();
        render();
    }
    private void systemState(String heading,String detail,int icon,boolean primary,int accent,int background,Runnable action,String actionLabel) {
        ui.state(content,icon,heading,detail,accent,background);
        ui.button(content,actionLabel,R.drawable.ic_arrow_right,primary,action);
    }
    private void loadingState() {
        ui.state(content,R.drawable.ic_clock,"Loading","Please wait while the latest information is retrieved.",R.color.ap_green,R.color.ap_mint);
        ProgressBar spinner=new ProgressBar(this);
        spinner.setContentDescription("Loading");
        content.addView(spinner,new LinearLayout.LayoutParams(-1,ui.dp(52)));
    }
    private JSONObject json(Object... values) {
        JSONObject out=new JSONObject();
        try {
            for(int i=0; i<values.length; i+=2)out.put(values[i].toString(),values[i+1]);
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return out;
    }
    private void safe(Runnable action) {
        try {
            action.run();
        } catch(Exception e) {
            showError(e.getMessage()==null?"Check the entered values.":e.getMessage());
        }
    }
    private void formStart() {
        fields=new Ui.Fields(ui,content);
    }
    private void welcome() {
        header("Welcome");
        LinearLayout art=ui.card(content);
        ImageView logo=new ImageView(this);
        logo.setImageResource(R.drawable.ic_building);
        art.setGravity(Gravity.CENTER);
        art.addView(logo,new LinearLayout.LayoutParams(ui.dp(100),ui.dp(124)));
        ui.gap(content,12);
        content.addView(ui.text("Your building.\nAll together.",30,true,R.color.ap_ink));
        ui.note(content,"Choose how you sign in.");
        ui.button(content,"Admin login",R.drawable.ic_shield,true,()-> {
            loginRole="ADMIN";
            go("admin-login");
        }
        );
        ui.button(content,"Resident login",R.drawable.ic_user,false,()-> {
            loginRole="RESIDENT";
            go("resident-login");
        }
        );
        ui.button(content,"Treasurer login",R.drawable.ic_wallet,false,()-> {
            loginRole="TREASURER";
            go("treasurer-login");
        }
        );
        ui.gap(content,12);
        ui.button(content,"New apartment",R.drawable.ic_building,false,()->go("admin-signup"));
        ui.button(content,"Join with invite",R.drawable.ic_users,false,()->go("join"));
        if(BuildConfig.DEBUG)ui.button(content,"Local server",R.drawable.ic_settings,false,()->go("connection"));
    }
    private void login() {
        header(pretty(loginRole)+" login");
        ui.heading(content,loginRole.equals("RESIDENT")?"Your flat & community":"Apartment management");
        formStart();
        fields.field("mobile","Mobile number","",InputType.TYPE_CLASS_PHONE);
        fields.field("pin","PIN · 4–6 digits","",NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        ui.button(content,"Sign in",R.drawable.ic_arrow_right,true,()->request("POST","/auth/login",json("mobile",fields.get("mobile"),"pin",fields.get("pin"),"role",loginRole),r-> {
            api.session((JSONObject)r);
            history.clear();
            if(resumePage.isEmpty()) {
                page="home";
                render();
            } else resumePrevious();
        }
        ));
        ui.button(content,"Switch login",R.drawable.ic_arrow_left,false,()->go("welcome"));
        ui.button(content,"Forgot PIN?",R.drawable.ic_clock,false,()->go("forgot-pin"));
        ui.note(content,"Use accounts created on your local server. Mobile verification is not configured in this build.");
    }
    private void recoveryUnavailable() {
        header("Reset PIN");
        ui.state(content,R.drawable.ic_shield,"Reset PIN unavailable","Verified mobile recovery is not configured. Contact your apartment administrator; no test code can reset a release account.",R.color.ap_amber,R.color.ap_ambg);
        ui.button(content,"Back to sign in",R.drawable.ic_arrow_left,true,()->go("login"));
    }
    private void register() {
        header("Create apartment");
        ui.heading(content,"Less admin. More community.");
        formStart();
        fields.field("name","Your name",signupDraft.optString("name"),TEXT);
        fields.field("mobile","Mobile number",signupDraft.optString("mobile"),InputType.TYPE_CLASS_PHONE);
        fields.field("pin","Create PIN",signupDraft.optString("pin"),NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        fields.check("authorised","I am authorised to manage this apartment.",false);
        ui.button(content,"Continue",R.drawable.ic_arrow_right,true,()-> {
            if(!fields.checked("authorised")) {
                showError("Confirm you are authorised to manage this apartment.");
                return;
            }
            signupDraft=fields.values();
            ((PilotApplication)getApplication()).transientSignup=signupDraft;
            go("setup-apartment");
        }
        );
        ui.note(content,"Local test accounts only. Do not use a real PIN.");
    }
    private void setup() {
        header("Apartment details");
        if(signupDraft.optString("pin").isEmpty()) {
            ui.empty(content,"Enter your details again","Your PIN was cleared when the app restarted.");
            ui.button(content,"Admin details",R.drawable.ic_user,true,()->go("admin-signup"));
            return;
        }
        formStart();
        fields.field("apartmentName","Apartment name","",TEXT);
        fields.select("city","City",new String[] {
            "Hyderabad","Bengaluru","Chennai","Pune","Mumbai","Other"
        }
        ,new String[] {
            "Hyderabad","Bengaluru","Chennai","Pune","Mumbai","Other"
        }
        );
        fields.field("flats","Number of flats · 5–50","10",NUMBER);
        ui.note(content,"Initial units are A-101 onward. Detailed block/flat editing is a later increment.");
        ui.button(content,"Create apartment",R.drawable.ic_building,true,()->safe(()-> {
            JSONObject body=json("name",signupDraft.optString("name"),"mobile",signupDraft.optString("mobile"),"pin",signupDraft.optString("pin"),"apartmentName",fields.get("apartmentName"),"city",fields.get("city"),"flats",Integer.parseInt(fields.get("flats")));
            request("POST","/auth/register",body,r-> {
                api.session((JSONObject)r);
                signupDraft=new JSONObject();
                ((PilotApplication)getApplication()).transientSignup=new JSONObject();
                go("setup-done");
            }
            );
        }
        ));
    }
    private void setupDone() {
        header("Apartment ready");
        String apartment=api.account()==null?"Your community":api.account().optString("apartmentName","Your community");
        ui.state(content,R.drawable.ic_check,"Your community is ready.",apartment,R.color.ap_green,R.color.ap_mint);
        LinearLayout card=ui.card(content);
        ui.iconRow(card,"Set maintenance","Monthly amount and due date",R.drawable.ic_receipt,()->go("billing-settings"));
        ui.iconRow(card,"Review flats","Confirm units and occupancy",R.drawable.ic_home,()->go("flats"));
        ui.iconRow(card,"Invite residents","Share the apartment code",R.drawable.ic_users,()->go("invite"));
        ui.button(content,"Open dashboard",R.drawable.ic_home,true,()->go("home"));
    }
    private void join() {
        header("Join your apartment");
        ui.heading(content,"You’re invited.");
        formStart();
        fields.field("invite","Invite code","",TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        ui.button(content,"Find apartment",R.drawable.ic_arrow_right,true,()-> {
            inviteCode=fields.get("invite").toUpperCase(Locale.ROOT);
            if(!inviteCode.matches("[A-Z0-9_-]{8,60}")) {
                showError("Enter the code shared by your apartment admin.");
                return;
            }
            request("GET","/auth/invites/"+inviteCode,null,r-> {
                invitePreview=(JSONObject)r;
                signupDraft=new JSONObject();
                ((PilotApplication)getApplication()).transientSignup=signupDraft;
                go("signup");
            }
            );
        }
        );
    }
    private void signup() {
        header("Create your account");
        ui.heading(content,invitePreview.optString("apartmentName","Your apartment"));
        formStart();
        fields.field("name","Your name","",TEXT);
        fields.field("mobile","Mobile number","",InputType.TYPE_CLASS_PHONE);
        fields.field("pin","Create PIN","",NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        fields.check("consent","I agree to share my details with the apartment admin.",false);
        ui.button(content,"Choose flat",R.drawable.ic_arrow_right,true,()-> {
            if(!fields.checked("consent")) {
                showError("Confirm your agreement to join.");
                return;
            }
            signupDraft=fields.values();
            ((PilotApplication)getApplication()).transientSignup=signupDraft;
            go("choose-flat");
        }
        );
    }
    private void chooseFlat() {
        header("Choose your flat");
        if(signupDraft.optString("pin").isEmpty()) {
            ui.empty(content,"Enter your details again","Your PIN was cleared when the app restarted.");
            ui.button(content,"Account details",R.drawable.ic_user,true,()->go("signup"));
            return;
        }
        JSONArray flats=invitePreview.optJSONArray("flats");
        if(flats==null||flats.length()==0) {
            ui.empty(content,"No unassigned flats","Ask the admin to complete a resident handover.");
            ui.button(content,"Try another invite",R.drawable.ic_arrow_left,false,()->go("join"));
            return;
        }
        String[] labels=new String[flats.length()];
        for(int i=0; i<labels.length; i++)labels[i]=flats.optJSONObject(i).optString("label");
        ui.state(content,R.drawable.ic_home,"Make yourself at home.","One resident account per flat.",R.color.ap_green,R.color.ap_mint);
        formStart();
        fields.select("flatLabel","Choose flat",labels,labels);
        ui.button(content,"Request to join",R.drawable.ic_users,true,()->request("POST","/auth/join",json("invite",inviteCode,"name",signupDraft.optString("name"),"mobile",signupDraft.optString("mobile"),"pin",signupDraft.optString("pin"),"flatLabel",fields.get("flatLabel")),r-> {
            signupDraft=new JSONObject();
            ((PilotApplication)getApplication()).transientSignup=signupDraft;
            go("signup-pending");
        }));
        ui.note(content,"Access begins only after the apartment admin approves this flat claim.");
    }
    private void monthSelector() {
        ui.button(content,month+"  ·  Change month",R.drawable.ic_calendar,false,()-> {
            EditText input=new EditText(this);
            input.setText(month);
            input.setHint("YYYY-MM");
            LinearLayout box=ui.column();
            box.setPadding(ui.dp(24),0,ui.dp(24),0);
            box.addView(input);
            getLayoutInflater().inflate(R.layout.view_brand_footer,box,true);
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this).setTitle("Month").setView(box).setNegativeButton("Cancel",null).setPositiveButton("Apply",(d,w)->safe(()-> {
                month=YearMonth.parse(input.getText().toString().trim()).toString();
                render();
            }
            )).show();
        }
        );
        ui.gap(content,12);
    }
    private void home() {
        header("Home");
        content.addView(ui.text("Hello, "+api.account().optString("name").split(" ")[0],26,true,R.color.ap_ink));
        ui.gap(content,16);
        request("GET","/reports/monthly?month="+month,null,r-> {
            JSONObject j=(JSONObject)r;
            ui.hero(content,staff()?"Maintenance collection":"Apartment this month",Ui.money(j.opt("collectedForBills")),month+" · "+Ui.money(j.opt("outstanding"))+" outstanding");
            LinearLayout c=ui.card(content);
            ui.kv(c,"Money received",Ui.money(j.opt("received")));
            ui.kv(c,"Expenses",Ui.money(j.opt("spent")));
            ui.kv(c,"Closing balance",Ui.money(j.opt("closing")));
            ui.button(content,staff()?"Maintenance":"My dues",R.drawable.ic_receipt,true,()->go("dues"));
            if(staff()) {
                ui.button(content,"Review payments · "+j.optLong("pendingPayments"),R.drawable.ic_check,false,()->go("approvals"));
                ui.button(content,"Add expense",R.drawable.ic_expense,false,()-> {
                    expenseRequestKey=UUID.randomUUID().toString();
                    go("expense-form");
                }
                );
            }
            ui.button(content,"Notices",R.drawable.ic_notice,false,()->go("notices"));
            if(admin())ui.button(content,"Invite residents",R.drawable.ic_users,false,()->go("invite"));
        }
        );
    }
    private void moneyMenu() {
        header("Money");
        monthSelector();
        LinearLayout c=ui.card(content);
        ui.iconRow(c,"Monthly dues","Bills and outstanding",R.drawable.ic_receipt,()->go("dues"));
        ui.iconRow(c,"Payments","Verify resident declarations",R.drawable.ic_check,()->go("approvals"));
        ui.iconRow(c,"Expenses","Apartment spending",R.drawable.ic_expense,()->go("expenses"));
        ui.iconRow(c,"Reports","Monthly money in and out",R.drawable.ic_report,()->go("reports"));
        ui.iconRow(c,"Maintenance amount","Future bills only",R.drawable.ic_settings,()->go("billing-settings"));
    }
    private void dues() {
        header("Maintenance");
        monthSelector();
        if(staff())ui.button(content,"Maintenance amount",R.drawable.ic_settings,false,()->go("billing-settings"));
        request("GET","/bills?month="+month,null,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)ui.empty(content,"No dues for this month",staff()?"Configure maintenance, then generate bills.":"The admin has not generated a bill for your flat.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject b=arr.getJSONObject(i);
                BigDecimal due=Ui.decimal(b.opt("amount")).subtract(Ui.decimal(b.opt("paid")));
                String state=due.signum()==0?"Paid":b.optBoolean("inReview")?"In review":"Pending";
                LinearLayout c=ui.card(content);
                ui.iconRow(c,b.optString("flatLabel"),state+" · "+Ui.money(due),R.drawable.ic_receipt,()->go("bill",b.optString("id")));
            }
            if(staff()) {
                ui.button(content,"Unpaid flats",R.drawable.ic_clock,false,()->go("outstanding"));
                ui.button(content,"Generate missing bills",R.drawable.ic_plus,true,()->go("bill-preview"));
            }
        }
        );
    }
    private void bill() {
        header("Maintenance bill");
        request("GET","/bills/"+id,null,r-> {
            JSONObject b=(JSONObject)r;
            BigDecimal amount=Ui.decimal(b.opt("amount")),paid=Ui.decimal(b.opt("paid"));
            BigDecimal due=amount.subtract(paid);
            LinearLayout c=ui.card(content);
            ui.kv(c,"Flat",b.optString("flatLabel"));
            ui.hero(c,"Amount due",Ui.money(due),"Due "+b.optString("dueDate"));
            ui.kv(c,b.optString("title","Monthly maintenance"),Ui.money(b.has("baseAmount")?b.opt("baseAmount"):amount));
            if(Ui.decimal(b.opt("lateFee")).signum()>0)ui.kv(c,"Late fee",Ui.money(b.opt("lateFee")));
            ui.kv(c,"Approved payments",Ui.money(paid));
            ui.kv(c,"Status",due.signum()==0?"Paid":b.optBoolean("inReview")?"In review":"Pending");
            if(due.signum()>0&&!b.optBoolean("inReview"))ui.button(content,staff()?"Record payment":"I've paid",R.drawable.ic_wallet,true,()-> {
                paymentRequestKey=UUID.randomUUID().toString();
                go("mark-paid",b.optString("id"));
            }
            );
            ui.button(content,"Payment history",R.drawable.ic_clock,false,()->go("payments"));
            ui.button(content,"Association payment details",R.drawable.ic_wallet,false,()->go("payee"));
        }
        );
    }
    private void markPaid() {
        header("Payment details");
        request("GET","/bills/"+id,null,r-> {
            JSONObject b=(JSONObject)r;
            formStart();
            fields.field("amount","Amount paid",Ui.decimal(b.opt("amount")).subtract(Ui.decimal(b.opt("paid"))).toPlainString(),MONEY);
            fields.select("mode","Paid using",new String[] {
                "UPI","Cash","Bank transfer","Cheque"
            }
            ,new String[] {
                "UPI","CASH","BANK_TRANSFER","CHEQUE"
            }
            );
            fields.field("reference","Transaction reference · required except cash","",TEXT);
            fields.field("paidOn","Payment date · YYYY-MM-DD",LocalDate.now(ZoneId.of("Asia/Kolkata")).toString(),TEXT);
            fields.field("note","Note · optional","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            fields.check("verified","I have already made this payment.",false);
            ui.note(content,"Submitting does not mark the bill paid. The admin or treasurer verifies it.");
            ui.button(content,"Submit payment",R.drawable.ic_check,true,()->safe(()-> {
                if(!fields.checked("verified"))throw new IllegalArgumentException("Confirm the payment was already made.");
                request("POST","/payments",json("billId",id,"amount",new BigDecimal(fields.get("amount")),"mode",fields.get("mode"),"reference",fields.get("reference"),"paidOn",LocalDate.parse(fields.get("paidOn")).toString(),"note",fields.get("note"),"requestKey",paymentRequestKey),x->go("payment",((JSONObject)x).getString("id")));
            }
            ));
            if(restoreFields!=null) {
                fields.restore(restoreFields);
                restoreFields=null;
            }
        }
        );
    }
    private void billing() {
        header("Maintenance amount");
        if(!staff()) {
            showError("Admin or treasurer access required.");
            return;
        }
        request("GET","/billing/rules",null,r-> {
            JSONArray rules=(JSONArray)r;
            JSONObject latest=rules.length()==0?new JSONObject():rules.getJSONObject(0);
            ui.note(content,"Apartment maintenance · not your application subscription.");
            formStart();
            fields.field("amount","Monthly maintenance per flat",latest.optString("amount",""),MONEY);
            fields.field("effective","Effective from · YYYY-MM",rules.length()==0?YearMonth.now(ZoneId.of("Asia/Kolkata")).toString():YearMonth.now(ZoneId.of("Asia/Kolkata")).plusMonths(1).toString(),TEXT);
            fields.field("billingDay","Billing day · 1–28",latest.optString("billingDay","1"),NUMBER);
            fields.field("dueDay","Due day · 1–28",latest.optString("dueDay","10"),NUMBER);
            ui.note(content,"Existing bills and receipts remain unchanged.");
            ui.button(content,"Save maintenance",R.drawable.ic_check,true,()->safe(()->request("POST","/billing/rules",json("amount",new BigDecimal(fields.get("amount")),"effective",YearMonth.parse(fields.get("effective")).toString(),"billingDay",Integer.parseInt(fields.get("billingDay")),"dueDay",Integer.parseInt(fields.get("dueDay"))),x->go("dues"))));
            ui.button(content,"Rate history",R.drawable.ic_clock,false,()->go("rate-history"));
            ui.button(content,"Flat-specific rates",R.drawable.ic_home,false,()->go("rate-overrides"));
            ui.button(content,"Late fees & reminders",R.drawable.ic_settings,false,()->go("settings"));
            if(restoreFields!=null) {
                fields.restore(restoreFields);
                restoreFields=null;
            }
        }
        );
    }
    private void rateHistory() {
        header("Rate history");
        request("GET","/billing/rules",null,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)ui.empty(content,"Maintenance not configured","Add your first monthly amount.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject rule=arr.getJSONObject(i);
                LinearLayout c=ui.card(content);
                ui.kv(c,"From",rule.optString("effectiveMonth"));
                ui.kv(c,"Per flat",Ui.money(rule.opt("amount")));
                ui.kv(c,"Due day",rule.optString("dueDay"));
            }
        }
        );
    }
    private void payments() {
        header(staff()?"Payment approvals":"Payment history");
        boolean approvals=page.equals("approvals");
        request("GET","/payments",null,r-> {
            JSONArray arr=(JSONArray)r;
            int displayed=0;
            for(int i=0; i<arr.length(); i++) {
                JSONObject p=arr.getJSONObject(i);
                if(approvals&&!"PENDING".equals(p.optString("status")))continue;
                displayed++;
                LinearLayout c=ui.card(content);
                ui.iconRow(c,p.optString("flatLabel")+" · "+Ui.money(p.opt("amount")),(p.optBoolean("reversed")?"Reversed":pretty(p.optString("status")))+" · "+pretty(p.optString("mode")),R.drawable.ic_wallet,()->go("payment",p.optString("id")));
            }
            if(displayed==0)ui.empty(content,"No payments here","Submitted payments appear here for verification.");
            if(approvals&&staff())ui.button(content,"Select multiple",R.drawable.ic_check,true,()-> {
                selected.clear();
                go("bulk-approval");
            });
            if(approvals)ui.button(content,"All payment history",R.drawable.ic_clock,false,()->go("payments"));
        }
        );
    }
    private void bulkApproval() {
        header("Select payments");
        if(!staff()) {
            page="access-denied";
            render();
            return;
        }
        request("GET","/payments",null,r-> {
            JSONArray arr=(JSONArray)r;
            List<JSONObject> pending=new ArrayList<>();
            for(int i=0; i<arr.length(); i++)if("PENDING".equals(arr.getJSONObject(i).optString("status")))pending.add(arr.getJSONObject(i));
            Set<String> available=new LinkedHashSet<>();
            for(JSONObject p:pending)available.add(p.optString("id"));
            selected.retainAll(available);
            LinearLayout tools=ui.card(content);
            ui.kv(tools,"Pending",Integer.toString(pending.size()));
            CheckBox all=new CheckBox(this);
            all.setText("Select all payments");
            all.setTextColor(ui.color(R.color.ap_sub));
            tools.addView(all);
            List<CheckBox> boxes=new ArrayList<>();
            for(JSONObject p:pending) {
                String paymentId=p.optString("id");
                LinearLayout c=ui.card(content);
                CheckBox check=new CheckBox(this);
                check.setText(p.optString("flatLabel")+" · "+Ui.money(p.opt("amount")));
                check.setTextColor(ui.color(R.color.ap_ink));
                check.setChecked(selected.contains(paymentId));
                check.setOnCheckedChangeListener((button,checked)-> {
                    if(checked)selected.add(paymentId);
                    else selected.remove(paymentId);
                });
                c.addView(check);
                boxes.add(check);
                ui.kv(c,"Mode",pretty(p.optString("mode")));
                ui.kv(c,"Reference",p.isNull("reference")?"Cash declaration":p.optString("reference"));
            }
            all.setChecked(!pending.isEmpty()&&selected.containsAll(available));
            all.setOnCheckedChangeListener((button,checked)-> {
                for(CheckBox box:boxes)box.setChecked(checked);
            });
            if(pending.isEmpty())ui.empty(content,"No pending payments","New declarations will appear here.");
            ui.button(content,"Review selected",R.drawable.ic_check,true,()-> {
                if(selected.isEmpty()) {
                    showError("Select at least one payment.");
                    return;
                }
                go("bulk-review");
            });
        });
    }
    private void bulkReview() {
        header("Confirm approval");
        if(!staff()) {
            page="access-denied";
            render();
            return;
        }
        request("GET","/payments",null,r-> {
            JSONArray arr=(JSONArray)r;
            List<JSONObject> pending=new ArrayList<>();
            BigDecimal total=BigDecimal.ZERO;
            for(int i=0; i<arr.length(); i++) {
                JSONObject p=arr.getJSONObject(i);
                if(selected.contains(p.optString("id"))&&"PENDING".equals(p.optString("status"))) {
                    pending.add(p);
                    total=total.add(Ui.decimal(p.opt("amount")));
                }
            }
            Set<String> stillPending=new LinkedHashSet<>();
            for(JSONObject p:pending)stillPending.add(p.optString("id"));
            selected.retainAll(stillPending);
            if(pending.isEmpty()) {
                ui.empty(content,"Selection is no longer available","Refresh pending payments and select again.");
                ui.button(content,"Back to approvals",R.drawable.ic_arrow_right,true,()->go("approvals"));
                return;
            }
            LinearLayout summary=ui.card(content);
            ui.hero(summary,pending.size()+" selected payments",Ui.money(total),"Confirm every bank or cash record");
            for(JSONObject p:pending) {
                LinearLayout c=ui.card(content);
                ui.iconRow(c,p.optString("flatLabel"),pretty(p.optString("mode"))+" · "+(p.isNull("reference")?"Cash":p.optString("reference")),R.drawable.ic_wallet,null);
                ui.kv(c,"Amount",Ui.money(p.opt("amount")));
            }
            formStart();
            fields.check("verified","I have verified every selected payment.",false);
            ui.note(content,"Only approve payments matched to the association bank record or received in cash.");
            ui.button(content,"Approve "+pending.size()+" payments",R.drawable.ic_check,true,()->safe(()-> {
                if(!fields.checked("verified"))throw new IllegalArgumentException("Confirm you verified every selected payment.");
                request("POST","/payments/approve",json("paymentIds",new JSONArray(selected),"verified",true),x-> {
                    selected.clear();
                    go("approvals");
                });
            }));
        });
    }
    private void paymentReject() {
        header("Reject payment");
        if(!staff()) {
            page="access-denied";
            render();
            return;
        }
        request("GET","/payments",null,r-> {
            JSONObject p=find((JSONArray)r,id);
            if(p==null||!"PENDING".equals(p.optString("status"))) {
                ui.empty(content,"Payment unavailable","It may already have been reviewed.");
                return;
            }
            LinearLayout c=ui.card(content);
            ui.kv(c,"Flat",p.optString("flatLabel"));
            ui.kv(c,"Amount",Ui.money(p.opt("amount")));
            formStart();
            fields.select("reasonType","Reason",new String[] {"Payment not found","Amount mismatch","Duplicate reference","Proof unclear","Other"},new String[] {"Payment not found","Amount mismatch","Duplicate reference","Proof unclear","Other"});
            fields.field("reason","Message to resident","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            ui.button(content,"Reject payment",R.drawable.ic_error,true,()->safe(()-> {
                String message=fields.get("reason");
                if(message.trim().isEmpty())throw new IllegalArgumentException("Explain what the resident needs to correct.");
                String reason=fields.get("reasonType")+": "+message;
                if(reason.length()>300)throw new IllegalArgumentException("Keep the rejection message within 300 characters.");
                request("POST","/payments/"+id+"/reject",json("reason",reason),x->go("approvals"));
            }));
        });
    }
    private void outstanding() {
        header("Unpaid flats");
        if(!staff()) {
            page="access-denied";
            render();
            return;
        }
        monthSelector();
        request("GET","/bills?month="+month,null,r-> {
            JSONArray arr=(JSONArray)r;
            List<JSONObject> unpaid=new ArrayList<>();
            BigDecimal total=BigDecimal.ZERO;
            int underReview=0;
            for(int i=0; i<arr.length(); i++) {
                JSONObject b=arr.getJSONObject(i);
                BigDecimal due=Ui.decimal(b.opt("amount")).subtract(Ui.decimal(b.opt("paid")));
                if(due.signum()>0) {
                    unpaid.add(b);
                    total=total.add(due);
                    if(b.optBoolean("inReview"))underReview++;
                }
            }
            ui.hero(content,"Outstanding maintenance",Ui.money(total),(unpaid.size()-underReview)+" unpaid · "+underReview+" under review");
            for(JSONObject b:unpaid) {
                BigDecimal due=Ui.decimal(b.opt("amount")).subtract(Ui.decimal(b.opt("paid")));
                LinearLayout c=ui.card(content);
                ui.iconRow(c,b.optString("flatLabel"),b.optBoolean("inReview")?"In review":"Payment due",R.drawable.ic_clock,()->go("bill",b.optString("id")));
                ui.kv(c,"Outstanding",Ui.money(due));
            }
            if(unpaid.isEmpty())ui.empty(content,"Everyone is up to date","There are no outstanding dues for this month.");
            ui.button(content,"Send reminders",R.drawable.ic_bell,true,()-> {
                reminderRequestKey=UUID.randomUUID().toString();
                go("reminder-preview");
            });
        });
    }
    private void reminderPreview() {
        header("Send reminders");
        if(!staff()) {
            page="access-denied";
            render();
            return;
        }
        request("GET","/billing/reminders/preview?month="+month,null,r-> {
            JSONObject preview=(JSONObject)r;
            LinearLayout summary=ui.card(content);
            ui.kv(summary,"Recipients",preview.optLong("recipientCount")+" accounts");
            ui.kv(summary,"Unpaid flats",preview.optLong("flatCount")+"");
            ui.kv(summary,"Outstanding",Ui.money(preview.opt("outstanding")));
            ui.heading(content,"Message preview");
            LinearLayout message=ui.card(content);
            message.addView(ui.text(preview.optString("title")+" · "+month,14,true,R.color.ap_ink));
            ui.gap(message,10);
            message.addView(ui.text(preview.optString("message"),12,false,R.color.ap_sub));
            ui.heading(content,"Unpaid flats");
            JSONArray flats=preview.optJSONArray("flats");
            if(flats!=null)for(int i=0; i<flats.length(); i++) {
                JSONObject flat=flats.getJSONObject(i);
                LinearLayout c=ui.card(content);
                ui.iconRow(c,flat.optString("flatLabel"),Ui.money(flat.opt("outstanding")),R.drawable.ic_home,null);
            }
            if(preview.optLong("recipientCount")==0)ui.empty(content,"No reminder recipients","Paid and in-review payments are excluded.");
            ui.note(content,"Reminders are saved to the in-app inbox. External push delivery: "+preview.optString("externalDeliveryStatus","NOT_CONFIGURED")+".");
            ui.button(content,"Send reminder",R.drawable.ic_bell,true,()->request("POST","/billing/reminders/send?month="+month,json("requestKey",reminderRequestKey),x-> {
                JSONObject result=(JSONObject)x;
                Toast.makeText(this,result.optInt("created")+" in-app reminder(s) saved. External push "+result.optString("externalDeliveryStatus","NOT_CONFIGURED")+".",Toast.LENGTH_LONG).show();
                go("outstanding");
            }));
        });
    }
    private void billPreview() {
        header("Generate monthly dues");
        if(!staff()) {
            page="access-denied";
            render();
            return;
        }
        monthSelector();
        request("GET","/billing/preview?month="+month,null,r-> {
            JSONObject preview=(JSONObject)r;
            if(!preview.optBoolean("configured")) {
                ui.empty(content,"Maintenance amount needed","Set the amount and due day first.");
                ui.button(content,"Set maintenance",R.drawable.ic_settings,true,()->go("billing-settings"));
                return;
            }
            LinearLayout c=ui.card(content);
            ui.kv(c,"Flats",preview.optInt("flats")+"");
            ui.kv(c,"Base rate",Ui.money(preview.opt("baseRate"))+" / flat");
            ui.kv(c,"Total scheduled",Ui.money(preview.opt("scheduled")));
            ui.kv(c,"Existing monthly bills",preview.optLong("existing")+"");
            ui.kv(c,"Due date",preview.optString("dueDate"));
            ui.note(content,"Existing flat/month maintenance bills are skipped. Contributions and opening dues remain separate.");
            ui.button(content,"Generate missing bills",R.drawable.ic_plus,true,()->request("POST","/billing/generate?month="+month,json(),x->go("dues")));
        });
    }
    private void payment() {
        header("Payment");
        request("GET","/payments",null,r-> {
            JSONObject p=find((JSONArray)r,id);
            if(p==null) {
                ui.empty(content,"Payment not found","Refresh payment history.");
                return;
            }
            LinearLayout c=ui.card(content);
            ui.hero(c,p.optBoolean("reversed")?"Reversed":pretty(p.optString("status")),Ui.money(p.opt("amount")),p.optString("flatLabel"));
            ui.button(content,"Payment proof",R.drawable.ic_copy,false,()->go("attachments","PAYMENT:"+id));
            if(staff()&&"APPROVED".equals(p.optString("status"))&&!p.optBoolean("reversed"))ui.button(content,"Record reversal",R.drawable.ic_error,false,()->go("payment-reverse",id));
            ui.kv(c,"Paid using",pretty(p.optString("mode")));
            ui.kv(c,"Reference",p.isNull("reference")?"—":p.optString("reference"));
            ui.kv(c,"Payment date",p.optString("paidOn"));
            ui.kv(c,"Note",p.optString("note"));
            if(!p.isNull("reason"))ui.note(c,p.optString("reason"));
            if("APPROVED".equals(p.optString("status")))ui.button(content,"Receipt",R.drawable.ic_receipt,true,()->go("receipt",id));
            if(staff()&&"PENDING".equals(p.optString("status"))) {
                ui.button(content,"Approve",R.drawable.ic_check,true,()->ui.confirm("Payment verified?","Confirm the association has received this money. An approved receipt will be created.","Verified · approve",()->request("POST","/payments/approve",json("paymentIds",new JSONArray(Collections.singletonList(id)),"verified",true),x->go("receipt",id))));
                ui.button(content,"Reject",R.drawable.ic_error,false,()->go("payment-reject",id));
            }
        }
        );
    }
    private void receipt() {
        header("Digital receipt");
        request("GET","/receipts/"+id,null,r-> {
            JSONObject p=(JSONObject)r;
            LinearLayout c=ui.card(content);
            c.addView(ui.text(p.optString("apartmentName"),20,true,R.color.ap_ink));
            ui.gap(c,15);
            c.addView(ui.text(p.optBoolean("reversed")?"REVERSED · original receipt":"PAID",13,true,p.optBoolean("reversed")?R.color.ap_red:R.color.ap_green));
            c.addView(ui.text(Ui.money(p.opt("amount")),35,true,R.color.ap_ink));
            ui.kv(c,"Flat",p.optString("flatLabel"));
            ui.kv(c,"Month",p.optString("billingMonth"));
            ui.kv(c,"Mode",pretty(p.optString("mode")));
            ui.kv(c,"Paid on",p.optString("paidOn"));
            ui.kv(c,"Approved by",p.optString("approvedBy"));
            ui.note(c,p.optString("receiptNumber"));
            getLayoutInflater().inflate(R.layout.view_brand_footer,c,true);
            ui.button(content,"Share PDF",R.drawable.ic_receipt,true,()->exportPdf("Maintenance receipt",p));
            ui.button(content,"Share text",R.drawable.ic_receipt,false,()-> {
                String text=p.optString("apartmentName")+(p.optBoolean("reversed")?"\nREVERSED ":"\nPAID ")+Ui.money(p.opt("amount"))+"\nFlat "+p.optString("flatLabel")+"\nMonth "+p.optString("billingMonth")+"\nReceipt "+p.optString("receiptNumber")+"\nPowered By @Rapolu`s";
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,text),"Share receipt"));
            }
            );
        }
        );
    }
    private void expenses() {
        header("Expenses");
        monthSelector();
        if(staff())ui.button(content,"Add expense",R.drawable.ic_plus,true,()-> {
            expenseRequestKey=UUID.randomUUID().toString();
            go("expense-form");
        }
        );
        request("GET","/expenses?month="+month,null,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)ui.empty(content,"No expenses here","Paid expenses appear in monthly reports.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject e=arr.getJSONObject(i);
                LinearLayout c=ui.card(content);
                ui.iconRow(c,e.optString("title"),e.optString("category")+" · "+e.optString("paidOn"),R.drawable.ic_expense,()->go("expense",e.optString("id")));
                ui.kv(c,!e.isNull("reversedOn")?"Reversed":e.optBoolean("paid")?"Paid":"Draft",Ui.money(e.opt("amount")));
            }
        }
        );
    }
    private void expenseForm() {
        header("Add expense");
        formStart();
        fields.field("amount","Amount","",MONEY);
        fields.field("title","Expense title","",TEXT);
        String[] cats= {
            "Water","Electricity","Security","Housekeeping","Lift","Plumbing","Repairs","Generator","Other"
        };
        fields.select("category","Category",cats,cats);
        fields.field("paidOn","Date · YYYY-MM-DD",LocalDate.now(ZoneId.of("Asia/Kolkata")).toString(),TEXT);
        fields.check("paid","Payment completed",false);
        fields.check("public","Visible to residents",true);
        ui.note(content,"Drafts do not change the balance. Paid entries are locked in this increment.");
        ui.button(content,"Save expense",R.drawable.ic_check,true,()->safe(()->request("POST","/expenses",json("title",fields.get("title"),"category",fields.get("category"),"amount",new BigDecimal(fields.get("amount")),"paidOn",LocalDate.parse(fields.get("paidOn")).toString(),"paid",fields.checked("paid"),"visibleToResidents",fields.checked("public"),"requestKey",expenseRequestKey),r->go("expenses"))));
    }
    private void reports() {
        header("Monthly report");
        monthSelector();
        request("GET","/reports/monthly?month="+month,null,r-> {
            JSONObject j=(JSONObject)r;
            ui.hero(content,"Closing balance",Ui.money(j.opt("closing")),month);
            LinearLayout c=ui.card(content);
            ui.kv(c,"Opening balance",Ui.money(j.opt("opening")));
            ui.kv(c,"Money received",Ui.money(j.opt("received")));
            ui.kv(c,"Paid expenses",Ui.money(j.opt("spent")));
            ui.kv(c,"Closing balance",Ui.money(j.opt("closing")));
            LinearLayout b=ui.card(content);
            ui.kv(b,"Monthly billed",Ui.money(j.opt("billed")));
            ui.kv(b,"Collected for bills",Ui.money(j.opt("collectedForBills")));
            ui.kv(b,"Outstanding",Ui.money(j.opt("outstanding")));
            ui.note(content,"Cash movement follows payment date. Bill collection follows the invoice month.");
        }
        );
    }
    private void community() {
        header("Community");
        LinearLayout c=ui.card(content);
        ui.iconRow(c,"Notice board","Apartment updates",R.drawable.ic_notice,()->go("notices"));
        if(admin()) {
            ui.iconRow(c,"Members","Residents and join requests",R.drawable.ic_users,()->go("members"));
            ui.iconRow(c,"Invite residents","Share a seven-day invite",R.drawable.ic_plus,()->go("invite"));
        }
        ui.iconRow(c,"Implementation status","Remaining reference modules",R.drawable.ic_grid,()->go("status"));
    }
    private void members() {
        header("Members");
        if(!admin()) {
            showError("Admin access required.");
            return;
        }
        ui.button(content,"Invite residents",R.drawable.ic_users,true,()->go("invite"));
        request("GET","/members",null,r-> {
            JSONArray arr=(JSONArray)r;
            for(int i=0; i<arr.length(); i++) {
                JSONObject m=arr.getJSONObject(i);
                LinearLayout c=ui.card(content);
                ui.iconRow(c,m.optString("name"),(m.isNull("flatLabel")?"Apartment team":m.optString("flatLabel"))+" · "+pretty(m.optString("status")),R.drawable.ic_user,()->go("member",m.optString("id")));
            }
        }
        );
    }
    private void member() {
        header("Member");
        request("GET","/members",null,r-> {
            JSONObject m=find((JSONArray)r,id);
            if(m==null) {
                showError("Member not found.");
                return;
            }
            LinearLayout c=ui.card(content);
            c.addView(ui.text(m.optString("name"),24,true,R.color.ap_ink));
            ui.kv(c,"Mobile",m.optString("mobile"));
            ui.kv(c,"Flat",m.isNull("flatLabel")?"—":m.optString("flatLabel"));
            ui.kv(c,"Role",pretty(m.optString("role")));
            ui.kv(c,"Status",pretty(m.optString("status")));
            if("PENDING".equals(m.optString("status"))) {
                ui.button(content,"Approve resident",R.drawable.ic_check,true,()->ui.confirm("Approve flat access?","Confirm this person's identity and flat. Account access starts after approval.","Approve",()->request("POST","/members/"+id+"/approve",json(),x->render())));
                Ui.Fields rejection=new Ui.Fields(ui,content);
                ui.gap(content,12);
                rejection.field("reason","Rejection reason","",TEXT);
                ui.button(content,"Reject request",R.drawable.ic_error,false,()->request("POST","/members/"+id+"/reject",json("reason",rejection.get("reason")),x->render()));
            } else if("ACTIVE".equals(m.optString("status"))&&"RESIDENT".equals(m.optString("role"))) {
                ui.button(content,"Assign treasurer",R.drawable.ic_shield,false,()->ui.confirm("Grant financial access?","Treasurer can approve payments, add expenses and view financial records. Existing sessions are revoked.","Grant access",()->request("POST","/members/"+id+"/treasurer",json(),x->render())));
            }
        }
        );
    }
    private void invite() {
        header("Invite residents");
        ui.note(content,"Generate a code, share it with residents, then approve their flat requests.");
        ui.button(content,"Generate new invite",R.drawable.ic_plus,true,()->ui.confirm("Create a new invite?","Previous invite codes stop working. Pending resident requests are not removed.","Generate",()->request("POST","/invites",json(),r-> {
            JSONObject j=(JSONObject)r;
            LinearLayout c=ui.card(content);
            ui.hero(c,"Resident invite",j.getString("code"),"Expires "+j.optString("expiresAt").substring(0,10));
            ui.button(c,"Copy code",R.drawable.ic_copy,false,()-> {
                ((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Apartment invite",j.optString("code")));
                Toast.makeText(this,"Invite copied",Toast.LENGTH_SHORT).show();
            }
            );
            ui.button(c,"Share invite",R.drawable.ic_users,true,()->startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,"Join "+api.account().optString("apartmentName")+" in ApartmentCare. Invite: "+j.optString("code")),"Share invite")));
        }
        )));
    }
    private void notices() {
        header("Notice board");
        if(staff())ui.button(content,"Send notice",R.drawable.ic_notice,true,()->go("notice-form"));
        request("GET","/notices",null,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)ui.empty(content,"No notices yet","Published apartment updates appear here.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject n=arr.getJSONObject(i);
                LinearLayout c=ui.card(content);
                ui.iconRow(c,n.optString("title"),pretty(n.optString("status"))+" · All flats",R.drawable.ic_notice,()->go("notice",n.optString("id")));
            }
        }
        );
    }
    private void notice() {
        header("Notice");
        request("GET","/notices",null,r-> {
            JSONObject n=find((JSONArray)r,id);
            if(n==null) {
                showError("Notice not available.");
                return;
            }
            LinearLayout c=ui.card(content);
            c.addView(ui.text(n.optString("title"),23,true,R.color.ap_ink));
            ui.note(c,"All flats · "+pretty(n.optString("status")));
            c.addView(ui.text(n.optString("body"),14,false,R.color.ap_sub));
            if(staff()&&"DRAFT".equals(n.optString("status")))ui.button(content,"Publish",R.drawable.ic_notice,true,()->ui.confirm("Publish to all flats?","This adds the notice to every active account's in-app inbox. Push is not connected yet.","Publish",()->request("POST","/notices/"+id+"/publish",json(),x->render())));
        }
        );
    }
    private void noticeForm() {
        header("Send notice");
        formStart();
        fields.field("title","Title","",TEXT);
        fields.field("body","Message","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        fields.check("publish","Publish to all flats now",true);
        ui.note(content,"All active accounts in this apartment. In-app inbox only in this build.");
        ui.button(content,"Preview & save",R.drawable.ic_notice,true,()-> {
            JSONObject payload=json("title",fields.get("title"),"body",fields.get("body"),"publish",fields.checked("publish"));
            ui.confirm(fields.checked("publish")?"Publish notice?":"Save draft?",fields.get("title")+"\n\n"+fields.get("body"),"Confirm",()->request("POST","/notices",payload,r->go("notice",((JSONObject)r).getString("id"))));
        }
        );
    }
    private void notifications() {
        header("Notifications");
        ui.button(content,"Mark all read",R.drawable.ic_check,false,()->request("POST","/ops/notifications/read-all",json(),r->render()));
        request("GET","/notifications",null,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)ui.empty(content,"You're all caught up","New activity appears in your inbox.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject n=arr.getJSONObject(i);
                LinearLayout c=ui.card(content);
                ui.iconRow(c,n.optString("title"),n.isNull("readAt")?"Unread": "Read",R.drawable.ic_bell,()->request("POST","/notifications/"+n.optString("id")+"/read",json(),x-> {
                    go("notification",n.optString("id"));
                }
                ));
            }
        }
        );
    }
    private void notificationDetail() {
        header("Notification");
        request("GET","/notifications/"+id,null,r->{
            JSONObject n=(JSONObject)r;
            LinearLayout c=ui.card(content);
            c.addView(ui.text(n.optString("title"),20,true,R.color.ap_ink));
            ui.note(c,n.optString("createdAt"));
            c.addView(ui.text(n.optString("body"),14,false,R.color.ap_sub));
            ui.button(content,"View related record",R.drawable.ic_check,true,()->{
                String[] target=n.optString("target").split(":",2);
                go(target[0],target.length==2?target[1]:"");
            });
        });
    }
    private void audit() {
        header("Activity log");
        request("GET","/audit",null,r-> {
            JSONArray arr=(JSONArray)r;
            for(int i=0; i<arr.length(); i++) {
                JSONObject a=arr.getJSONObject(i);
                LinearLayout c=ui.card(content);
                c.addView(ui.text(pretty(a.optString("action")),13,true,R.color.ap_ink));
                ui.note(c,a.optString("createdAt"));
                if(!a.isNull("afterValue"))ui.note(c,a.optString("afterValue"));
            }
        }
        );
    }
    private void more() {
        header("More");
        LinearLayout c=ui.card(content);
        ui.iconRow(c,api.account().optString("name"),pretty(api.account().optString("role")),R.drawable.ic_user,()->go("profile"));
        ui.iconRow(c,"Payment history","Receipts and declarations",R.drawable.ic_receipt,()->go("payments"));
        if(staff())ui.iconRow(c,"Activity log","Financial and access changes",R.drawable.ic_clock,()->go("audit"));
        ui.iconRow(c,"Appearance","Use the theme icon above",R.drawable.ic_moon,null);
        ui.iconRow(c,"Implementation status","Cumulative development 02",R.drawable.ic_grid,()->go("status"));
        if(BuildConfig.DEBUG)ui.iconRow(c,"Local server",api.base(),R.drawable.ic_settings,()->go("connection"));
        ui.button(content,"Sign out",R.drawable.ic_logout,false,()->ui.confirm("Sign out?","Your records remain on the server.","Sign out",()->request("POST","/logout",json(),r-> {
            api.clear();
            history.clear();
            page="welcome";
            render();
        }
        )));
    }
    private void profile() {
        header("My profile");
        JSONObject a=api.account();
        LinearLayout c=ui.card(content);
        c.addView(ui.text(a.optString("name"),25,true,R.color.ap_ink));
        ui.kv(c,"Apartment",a.optString("apartmentName"));
        ui.kv(c,"Mobile",a.optString("mobile"));
        ui.kv(c,"Role",pretty(a.optString("role")));
        ui.note(content,"Edit your profile from More. Verified account recovery is not connected.");
    }
    private void connection() {
        header("Local server");
        if(!BuildConfig.DEBUG) {
            showError("Development tools are unavailable.");
            return;
        }
        formStart();
        fields.field("url","Backend URL",api.base(),InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_URI);
        ui.note(content,"Emulator: http://10.0.2.2:8080/api/v1\nPhone with adb reverse: http://127.0.0.1:8080/api/v1");
        ui.button(content,"Save address",R.drawable.ic_check,true,()->safe(()-> {
            api.setBase(fields.get("url"));
            render();
        }
        ));
        ui.button(content,"Test backend",R.drawable.ic_building,false,()->request("GET","/status",null,r-> {
            JSONObject j=(JSONObject)r;
            LinearLayout c=ui.card(content);
            ui.kv(c,"Connection","Successful");
            ui.kv(c,"Backend",j.optString("version"));
            ui.kv(c,"Local registration",j.optBoolean("localRegistration")?"Enabled":"Disabled");
        }
        ));
    }
    private void status() {
        header("Implementation status");
        ui.empty(content,"Development build 02","Not the completed application or the final UI implementation.");
        LinearLayout c=ui.card(content);
        ui.note(c,"Source included: login, local apartment setup, resident invitations/approval, maintenance rules, bill generation, payments/receipts, basic expenses/reports, all-flat notices and inbox.");
        ui.note(c,"Operational source now includes events/parking, committee, financial additions, attachments, issues/services and subscription/referral records. Live verification, push, purchase integration, entitlement enforcement and exact native UI matching remain open.");
        ui.note(c,"Native visual parity and Android runtime tests are not yet verified. See the packaged status and test evidence before testing.");
    }
    private static String pretty(String s) {
        if(s==null||s.isEmpty())return "";
        String t=s.toLowerCase(Locale.ROOT).replace('_',' ');
        return Character.toUpperCase(t.charAt(0))+t.substring(1);
    }
    private JSONObject find(JSONArray arr,String key)throws JSONException {
        for(int i=0; i<arr.length(); i++)if(arr.getJSONObject(i).optString("id").equals(key))return arr.getJSONObject(i);
        return null;
    }
    @Override public android.app.Activity activity() {
        return this;
    }
    @Override public Ui components() {
        return ui;
    }
    @Override public LinearLayout body() {
        return content;
    }
    @Override public ApiClient client() {
        return api;
    }
    @Override public boolean isStaff() {
        return staff();
    }
    @Override public boolean isAdmin() {
        return admin();
    }
    @Override public String selectedMonth() {
        return month;
    }
    @Override public String recordId() {
        return id;
    }
    @Override public String commandKey() {
        return operationKey;
    }
    @Override public void resetCommandKey() {
        operationKey=UUID.randomUUID().toString();
    }
    @Override public void openPage(String page,String record) {
        go(page,record);
    }
    @Override public void refreshPage() {
        render();
    }
    @Override public void pageTitle(String label) {
        header(label);
    }
    @Override public void problem(String message) {
        showError(message==null?"Check your input.":message);
    }
    @Override public Ui.Fields newFields() {
        formStart();
        return fields;
    }
    @Override public void monthControl() {
        monthSelector();
    }
    @Override public void requestApi(String method,String path,JSONObject payload,FeatureHost.Reply reply) {
        request(method,path,payload,reply::done);
    }
    @Override public void exitSession() {
        api.clear();
        ((PilotApplication)getApplication()).transientSignup=new JSONObject();
        history.clear();
        signupDraft=new JSONObject();
        page="welcome";
        id="";
        resumePage="";
        resumeId="";
        resumeFields=null;
        render();
    }
    @Override public void chooseAttachment(String kind,String parent) {
        attachmentKind=kind;
        attachmentParent=parent;
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE).putExtra(Intent.EXTRA_MIME_TYPES,new String[] {
            "application/pdf","image/jpeg","image/png"
        }
        );
        startActivityForResult(intent,2202);
    }
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode!=2202||resultCode!=RESULT_OK||data==null||data.getData()==null)return;
        android.net.Uri uri=data.getData();
        String kind=attachmentKind,parent=attachmentParent;
        final int screen=generation;
        String initialAccount=api.signedIn()?api.account().optString("id"):"";
        busy=true;
        progress.setVisibility(View.VISIBLE);
        setButtons(content,false);
        fileWorker.execute(()-> {
            try {
                String name="attachment";
                try(android.database.Cursor cursor=getContentResolver().query(uri,new String[] {
                    android.provider.OpenableColumns.DISPLAY_NAME
                }
                ,null,null,null)) {
                    if(cursor!=null&&cursor.moveToFirst())name=cursor.getString(0);
                }
                byte[] bytes;
                try(java.io.InputStream in=getContentResolver().openInputStream(uri);
                java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream()) {
                    if(in==null)throw new java.io.IOException("Cannot open selected document.");
                    byte[] buf=new byte[8192];
                    int n;
                    while((n=in.read(buf))!=-1) {
                        if(out.size()+n>5*1024*1024)throw new java.io.IOException("Choose a file no larger than 5 MB.");
                        out.write(buf,0,n);
                    }
                    bytes=out.toByteArray();
                }
                JSONObject payload=json("kind",kind,"parentId",parent,"name",name,"content",android.util.Base64.encodeToString(bytes,android.util.Base64.NO_WRAP));
                runOnUiThread(()-> {
                    if(isDestroyed()||screen!=generation||!api.signedIn()||!initialAccount.equals(api.account().optString("id")))return;
                    busy=false;
                    progress.setVisibility(View.GONE);
                    setButtons(content,true);
                    request("POST","/ops/files",payload,r->render());
                }
                );
            } catch(Exception ex) {
                runOnUiThread(()-> {
                    if(isDestroyed()||screen!=generation)return;
                    busy=false;
                    progress.setVisibility(View.GONE);
                    setButtons(content,true);
                    showError(ex.getMessage());
                }
                );
            }
        }
        );
    }
    @Override public void exportPdf(String title,JSONObject record) {
        try {
            localFiles.exportPdf(title,record);
        } catch(Exception e) {
            showError("Export failed: "+e.getMessage());
        }
    }
    @Override public void openSharedFile(JSONObject file) {
        try {
            localFiles.open(file);
        } catch(Exception e) {
            showError("Could not open attachment: "+e.getMessage());
        }
    }
    @Override protected void onDestroy() {
        super.onDestroy();
        fileWorker.shutdown();
    }
}
