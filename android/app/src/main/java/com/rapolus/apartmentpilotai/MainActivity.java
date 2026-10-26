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
import com.google.android.material.button.MaterialButton;
import com.rapolus.apartmentpilotai.data.ApiClient;
import com.rapolus.apartmentpilotai.data.HttpFailurePolicy;
import com.rapolus.apartmentpilotai.data.NavigationPolicy;
import com.rapolus.apartmentpilotai.data.LoginPolicy;
import com.rapolus.apartmentpilotai.data.RegistrationPolicy;
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
    private String saveAttachmentId="",saveAttachmentAccount="";
    private final java.util.concurrent.ExecutorService fileWorker=java.util.concurrent.Executors.newSingleThreadExecutor();
    private ApiClient api;
    private Ui ui;
    private LinearLayout content;
    // Tablet list + detail: sibling list shown beside Wave 1 detail pages.
    private LinearLayout side;
    private View sideScroll;
    private BottomNavigationView nav;
    private TextView title,subtitle;
    private View progress;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout refresher;
    private View inlineError;
    private Ui.Fields fields;
    private JSONObject restoreFields;
    private String page="welcome",id="",loginRole="ADMIN",month=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString(),inviteCode="",offlineDetail="The connection dropped. Check the previous action before submitting it again.",paymentRequestKey=UUID.randomUUID().toString(),reminderRequestKey=UUID.randomUUID().toString(),expenseRequestKey=UUID.randomUUID().toString();
    private String resumePage="",resumeId="",resumeAccount="";
    private JSONObject resumeFields;
    private JSONObject signupDraft=new JSONObject(),invitePreview=new JSONObject();
    private JSONObject pageDrafts=new JSONObject();
    private final Deque<String[]> history=new ArrayDeque<>();
    // True only while a successful non-GET request runs its success callback.
    private boolean afterSave;
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
        side=findViewById(R.id.side);
        sideScroll=findViewById(R.id.sideScroll);
        nav=findViewById(R.id.navigation);
        nav.setBackgroundColor(ui.color(R.color.ap_surface));
        ViewCompat.setOnApplyWindowInsetsListener(nav,(v,insets)->insets);
        title=findViewById(R.id.title);
        subtitle=findViewById(R.id.subtitle);
        progress=findViewById(R.id.progress);
        refresher=findViewById(R.id.refresh);
        refresher.setColorSchemeColors(ui.color(R.color.ap_pine));
        refresher.setProgressBackgroundColorSchemeColor(ui.color(R.color.ap_surface));
        refresher.setOnRefreshListener(()-> {
            refresher.setRefreshing(false);
            // Pull down reloads the page; anything typed into a search box is kept.
            if(busy||!api.signedIn())return;
            restoreFields=fields==null?null:fields.values();
            render();
        });
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
        ((ImageButton)findViewById(R.id.theme)).setImageResource(dark?R.drawable.ic_sun:R.drawable.ic_moon);
        findViewById(R.id.theme).setContentDescription(dark?"Switch to light theme":"Switch to dark theme");
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
            saveAttachmentId=b.getString("saveAttachmentId","");
            saveAttachmentAccount=b.getString("saveAttachmentAccount","");
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
                pageDrafts=new JSONObject(b.getString("pageDrafts","{}"));
            } catch(JSONException ignored) {
            }
            resumePage=b.getString("resumePage","");
            resumeId=b.getString("resumeId","");
            resumeAccount=b.getString("resumeAccount","");
        } else if(api.signedIn())page="home";
        else if(api.pendingJoin()!=null)page="signup-pending";
        render();
    }
    @Override protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString("operationKey",operationKey);
        b.putString("attachmentKind",attachmentKind);
        b.putString("attachmentParent",attachmentParent);
        b.putString("saveAttachmentId",saveAttachmentId);
        b.putString("saveAttachmentAccount",saveAttachmentAccount);
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
        b.putString("pageDrafts",pageDrafts.toString());
        b.putString("resumePage",resumePage);
        b.putString("resumeId",resumeId);
        b.putString("resumeAccount",resumeAccount);
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
        if(afterSave&&isFormPage(page)) {
            // A saved form is finished: return to an earlier instance of the destination instead of
            // stacking it again, and never leave the submitted form reachable with Back.
            int depth=0,found=-1;
            for(String[] entry:history) {
                if(entry[0].equals(p)&&(record.isEmpty()||record.equals(entry[1]))) {
                    found=depth;
                    break;
                }
                depth++;
            }
            for(int i=0; i<=found; i++)history.pop();
        } else history.push(new String[] {
            page,id
        }
        );
        // A new apartment signup started from sign-in must not show an earlier, abandoned attempt.
        if(p.equals("admin-signup")&&!page.equals("setup-apartment")) {
            signupDraft=new JSONObject();
            ((PilotApplication)getApplication()).transientSignup=signupDraft;
        }
        page=p;
        id=record;
        resetCommandKey();
        if(p.equals("mark-paid"))paymentRequestKey=UUID.randomUUID().toString();
        restoreFields=null;
        if(!p.equals("bulk-approval")&&!p.equals("bulk-review"))selected.clear();
        render();
        ((ScrollView)findViewById(R.id.scroll)).scrollTo(0,0);
    }
    /** On tablets, list sibling records beside a detail page so Admins can move between them without Back. */
    private void renderSidePane() {
        boolean wide=api.signedIn()&&com.rapolus.apartmentpilotai.data.TabletPolicy.split(getResources().getConfiguration().screenWidthDp);
        String path=wide?com.rapolus.apartmentpilotai.data.TabletPolicy.sideListPath(page,staff(),admin()):null;
        side.removeAllViews();
        if(path==null) {
            sideScroll.setVisibility(View.GONE);
            return;
        }
        sideScroll.setVisibility(View.VISIBLE);
        side.addView(ui.text(com.rapolus.apartmentpilotai.data.TabletPolicy.sideListTitle(page),13,true,R.color.ap_ink));
        ui.gap(side,8);
        LinearLayout card=ui.card(side);
        List<String> paths=new ArrayList<>();
        paths.add(path);
        if(page.equals("ticket")&&staff())paths.add("/ops/tickets?kind=COMPLAINT");
        loadSideRows(paths,0,card,page,id,generation);
    }
    // Uses ApiClient directly: request() allows one call at a time and the detail page is loading in parallel.
    private void loadSideRows(List<String> paths,int index,LinearLayout card,String listPage,String current,int version) {
        if(index>=paths.size())return;
        api.call("GET",paths.get(index),null,(result,error)-> {
            if(isFinishing()||isDestroyed()||version!=generation)return;
            if(error!=null||!(result instanceof JSONArray)) {
                ui.note(card,"List unavailable.");
                return;
            }
            JSONArray rows=(JSONArray)result;
            for(int i=0; i<rows.length(); i++) {
                JSONObject r=rows.optJSONObject(i);
                if(r==null)continue;
                String rid=r.optString("id");
                boolean selected=rid.equals(current);
                String[] text=sideRow(listPage,r);
                ui.iconRow(card,(selected?"▶ ":"")+text[0],text[1],sideIcon(listPage),selected?null:()->openSibling(listPage,rid));
            }
            if(index==paths.size()-1&&card.getChildCount()==0)ui.note(card,"Nothing here yet.");
            loadSideRows(paths,index+1,card,listPage,current,version);
        });
    }
    private String[] sideRow(String listPage,JSONObject r) {
        switch(listPage) {
            case "flat": return new String[] {r.optString("label"),"Block "+r.optString("block")+" · "+(r.optBoolean("occupied")?"Occupied":"Vacant")+(r.optBoolean("active",true)?"":" · Inactive")};
            case "join-review": case "member": return new String[] {r.optString("name"),(r.isNull("flatLabel")?"Apartment team":r.optString("flatLabel"))+" · "+pretty(r.optString("status"))};
            case "payment": return new String[] {r.optString("flatLabel")+" · "+Ui.money(r.opt("amount")),pretty(r.optString("status"))+" · "+pretty(r.optString("mode"))};
            case "ticket": return new String[] {r.optString("title"),pretty(r.optString("status"))+" · "+r.optString("category")};
            default: return new String[] {r.optString("title"),pretty(r.optString("status"))};
        }
    }
    private int sideIcon(String listPage) {
        switch(listPage) {
            case "flat": return R.drawable.ic_home;
            case "join-review": case "member": return R.drawable.ic_users;
            case "payment": return R.drawable.ic_wallet;
            case "ticket": return R.drawable.ic_tools;
            default: return R.drawable.ic_notice;
        }
    }
    /** Switch the detail pane to a sibling record; Back still returns to the list the user came from. */
    private void openSibling(String listPage,String record) {
        if(busy)return;
        page=listPage;
        id=record;
        resetCommandKey();
        restoreFields=null;
        render();
        ((ScrollView)findViewById(R.id.scroll)).scrollTo(0,0);
    }
    /** Pages whose job ends when their command is saved; they must not stay reachable with Back. */
    private static boolean isFormPage(String p) {
        return p.endsWith("-form")||p.endsWith("-edit")||p.endsWith("-reverse")||p.endsWith("-settings")||p.endsWith("-preview")||Arrays.asList("mark-paid","payment-reject","bulk-review","change-pin","ticket-update","service-complete","join-reject","referral-claim","handover","opening-balance","opening-dues","transparency","booking-alternative","billing-options","payee","verification","reminders","booking-policy").contains(p);
    }
    private void back() {
        if(busy)return;
        if(page.equals("subscription-required"))return;
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
        // Screen changes are announced by TalkBack, and the title is a heading it can jump to.
        androidx.core.view.ViewCompat.setAccessibilityHeading(title,true);
        androidx.core.view.ViewCompat.setAccessibilityLiveRegion(title,androidx.core.view.ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
    }
    private void render() {
        android.util.Log.d("ApartmentPilotAI.Activity","screen="+page);
        generation++;
        busy=false;
        progress.setVisibility(View.GONE);
        content.removeAllViews();
        inlineError=null;
        fields=null;
        boolean authPage=Arrays.asList("welcome","login","admin-login","treasurer-login","resident-login","forgot-pin","verify-reset","reset-pin","invite-invalid","admin-signup","setup-apartment","setup-done","join","signup","choose-flat","signup-pending","connection","status","offline","session-expired","error-state","not-found","loading-state").contains(page);
        if(!api.signedIn()&&!authPage) {
            page="welcome";
            id="";
        }
        if(api.signedIn()&&!staff()&&page.equals("payment"))page="payment-status";
        renderSidePane();
        // Forms are never reloaded by a pull, so typed entries cannot be lost.
        refresher.setEnabled(api.signedIn()&&!isFormPage(page));
        nav.setVisibility(api.signedIn()&&!page.equals("subscription-required")?View.VISIBLE:View.GONE);
        nav.getMenu().findItem(R.id.nav_money).setTitle(staff()?R.string.money:R.string.dues);
        if(api.signedIn()) {
            NavigationPolicy.Group group=NavigationPolicy.group(page,id);
            int tab=group==NavigationPolicy.Group.HOME?R.id.nav_home:group==NavigationPolicy.Group.MONEY?R.id.nav_money:group==NavigationPolicy.Group.ISSUES?R.id.nav_issues:group==NavigationPolicy.Group.COMMUNITY?R.id.nav_community:R.id.nav_more;
            nav.getMenu().findItem(tab).setChecked(true);
        }
        boolean homeShell=api.signedIn()&&page.equals("home");
        // Root pages (signed-in Home, the sign-in chooser) have nowhere to go back to.
        findViewById(R.id.back).setVisibility(homeShell&&history.isEmpty()||!api.signedIn()&&page.equals("welcome")?View.INVISIBLE:View.VISIBLE);
        findViewById(R.id.notifications).setVisibility(homeShell?View.VISIBLE:View.GONE);
        subtitle.setText(api.signedIn()?api.account().optString("apartmentName","ApartmentCare"):"ApartmentCare");
        subtitle.setVisibility(homeShell?View.VISIBLE:View.GONE);
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
            case "signup-pending":joinPending();
            break;
            case "setup-done":setupDone();
            break;
            case "offline":systemState("Connection paused",offlineDetail,R.drawable.ic_error,true,R.color.ap_amber,R.color.ap_ambg,this::retryConnection,"Retry connection");
            break;
            case "session-expired":systemState("Sign in again","Your session expired. Your records are safe.",R.drawable.ic_shield,true,R.color.ap_green,R.color.ap_mint,this::signInAfterExpiry,"Sign in");
            break;
            case "access-denied":systemState("This page is restricted","Use an account with the right permission.",R.drawable.ic_shield,true,R.color.ap_green,R.color.ap_mint,()->go("home"),"Back to home");
            break;
            case "subscription-required":subscriptionRequired();
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
        final String originAccount=api.signedIn()?api.account().optString("id"):"";
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
                    showFailureState(destination,originPage,originId,originRole,originFields,originAccount,!"GET".equals(method));
                    return;
                }
                showError(error.message,error.status>=500||error.status==0);
                return;
            }
            try {
                afterSave=!"GET".equals(method);
                try {
                    onSuccess.accept(result);
                } finally {
                    afterSave=false;
                }
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
        if(v instanceof Button||v instanceof Spinner||v instanceof AutoCompleteTextView)v.setEnabled(enabled);
        if(v instanceof ViewGroup) {
            ViewGroup g=(ViewGroup)v;
            for(int i=0; i<g.getChildCount(); i++)setButtons(g.getChildAt(i),enabled);
        }
    }
    private void showError(String text) {
        showError(text,false);
    }
    // Validation and request errors are fixed by editing the form; only server failures offer a retry.
    private void showError(String text,boolean retryable) {
        if(inlineError!=null&&inlineError.getParent()==content)content.removeView(inlineError);
        int childIndex=content.getChildCount();
        LinearLayout card=ui.card(content);
        inlineError=content.getChildAt(childIndex);
        card.addView(ui.text(text,12,false,R.color.ap_red));
        if(retryable)ui.button(card,"Retry / refresh",R.drawable.ic_clock,false,()-> {
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
    private void showFailureState(HttpFailurePolicy.Destination destination,String originPage,String originId,String originRole,JSONObject originFields,String originAccount,boolean write) {
        resumePage=originPage;
        resumeId=originId;
        resumeFields=originFields;
        resumeAccount=originAccount;
        if(destination==HttpFailurePolicy.Destination.SESSION_EXPIRED) {
            loginRole=originRole;
            history.clear();
            page="session-expired";
        } else if(destination==HttpFailurePolicy.Destination.OFFLINE) {
            // Only a write can have half-happened; a failed read or server check simply did not connect.
            offlineDetail=write?"The connection dropped. Your previous action may have reached the server; check its result before submitting again."
                :"The server could not be reached. Check the connection and try again.";
            page="offline";
        }
        else if(destination==HttpFailurePolicy.Destination.ACCESS_DENIED)page="access-denied";
        else if(destination==HttpFailurePolicy.Destination.SUBSCRIPTION_REQUIRED) {
            history.clear();
            page="subscription-required";
        }
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
        resumeAccount="";
        page=target.isEmpty()?(api.signedIn()?"home":"welcome"):target;
        id=record;
        restoreFields=values;
        render();
    }
    private void retryConnection() {
        if(busy)return;
        busy=true;
        progress.setVisibility(View.VISIBLE);
        setButtons(content,false);
        final int version=generation;
        api.call("GET","/status",null,(result,error)-> {
            if(isFinishing()||isDestroyed()||version!=generation)return;
            busy=false;
            progress.setVisibility(View.GONE);
            setButtons(content,true);
            if(error==null&&result instanceof JSONObject&&((JSONObject)result).optBoolean("localRegistration")) {
                offlineDetail="The connection dropped. Check the previous action before submitting it again.";
                resumePrevious();
                return;
            }
            offlineDetail=error==null
                ?"The backend is reachable, but local apartment registration is disabled. Start the local server profile before continuing."
                :"Still unable to reach the backend. Keep the PC server running and reconnect the phone bridge, then retry.";
            render();
        }
        );
    }
    private void signInAfterExpiry() {
        page=LoginPolicy.routeForRole(loginRole);
        id="";
        history.clear();
        render();
    }
    private void systemState(String heading,String detail,int icon,boolean primary,int accent,int background,Runnable action,String actionLabel) {
        ui.state(content,icon,heading,detail,accent,background);
        ui.button(content,actionLabel,R.drawable.ic_arrow_right,primary,action);
    }
    private void subscriptionRequired() {
        header("Subscription required");
        ui.state(content,R.drawable.ic_shield,"Apartment access paused","The apartment’s free first month has ended. An active subscription or earned referral credit is required to continue. One apartment subscription covers its Admin, Treasurer, and Residents.",R.color.ap_amber,R.color.ap_ambg);
        if(admin())ui.button(content,"View subscription",R.drawable.ic_receipt,true,()->go("subscription"));
        else ui.note(content,"Please ask your apartment Admin to review Subscription & referrals.");
        ui.button(content,"Sign out",R.drawable.ic_logout,false,()->ui.confirm("Sign out?","You can sign in again after the apartment Admin restores access.","Sign out",()->request("POST","/logout",json(),r->exitSession())));
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
        header("Choose login");
        FrameLayout art=new FrameLayout(this);
        art.setLayoutParams(new LinearLayout.LayoutParams(-1,ui.dp(128)));
        ImageView buildings=new ImageView(this);
        buildings.setImageResource(R.drawable.welcome_community);
        buildings.setScaleType(ImageView.ScaleType.FIT_XY);
        buildings.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        art.addView(buildings,new FrameLayout.LayoutParams(-1,-1));
        TextView badge=ui.text("✓  A little less admin.",11,true,R.color.ap_ink);
        badge.setGravity(Gravity.CENTER);
        badge.setPadding(ui.dp(10),ui.dp(6),ui.dp(10),ui.dp(6));
        android.graphics.drawable.GradientDrawable badgeBg=new android.graphics.drawable.GradientDrawable();
        badgeBg.setColor(ui.color(R.color.ap_surface));
        badgeBg.setCornerRadius(ui.dp(13));
        badge.setBackground(badgeBg);
        FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-2,ui.dp(32),Gravity.TOP|Gravity.START);
        bp.topMargin=ui.dp(12); bp.leftMargin=ui.dp(15);
        art.addView(badge,bp);
        content.addView(art);
        ui.gap(content,19);
        content.addView(ui.text("Your building.\nAll together.",30,true,R.color.ap_ink));
        TextView sub=ui.text("Choose how you sign in.",14,false,R.color.ap_sub);
        content.addView(sub);
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
        LinearLayout divider=ui.row();
        divider.setGravity(Gravity.CENTER_VERTICAL);
        View line1=new View(this); line1.setBackgroundColor(ui.color(R.color.ap_line));
        divider.addView(line1,new LinearLayout.LayoutParams(0,ui.dp(1),1));
        TextView or=ui.text("or",11,false,R.color.ap_muted); or.setGravity(Gravity.CENTER);
        divider.addView(or,new LinearLayout.LayoutParams(ui.dp(42),ui.dp(24)));
        View line2=new View(this); line2.setBackgroundColor(ui.color(R.color.ap_line));
        divider.addView(line2,new LinearLayout.LayoutParams(0,ui.dp(1),1));
        LinearLayout.LayoutParams dp=new LinearLayout.LayoutParams(-1,ui.dp(24)); dp.topMargin=ui.dp(12); content.addView(divider,dp);
        LinearLayout actions=ui.row();
        MaterialButton create=ui.button(actions,"New apartment",R.drawable.ic_building,false,()->go("admin-signup"));
        MaterialButton join=ui.button(actions,"Join with invite",R.drawable.ic_users,false,()->go("join"));
        create.setTextSize(12); join.setTextSize(12);
        create.setLayoutParams(new LinearLayout.LayoutParams(0,ui.dp(48),1));
        join.setLayoutParams(new LinearLayout.LayoutParams(0,ui.dp(48),1));
        ((LinearLayout.LayoutParams)join.getLayoutParams()).leftMargin=ui.dp(8);
        content.addView(actions);
        if(BuildConfig.DEBUG)ui.note(content,"Local testing build · use test accounts only.");
        if(BuildConfig.DEBUG)ui.button(content,"Local server",R.drawable.ic_settings,false,()->go("connection"));
    }
    private void login() {
        header(pretty(loginRole)+" login");
        LinearLayout heading=ui.row();
        ImageView roleIcon=new ImageView(this);
        roleIcon.setImageResource(loginRole.equals("RESIDENT")?R.drawable.ic_user:loginRole.equals("TREASURER")?R.drawable.ic_wallet:R.drawable.ic_shield);
        roleIcon.setImageTintList(android.content.res.ColorStateList.valueOf(ui.color(R.color.ap_pine)));
        roleIcon.setPadding(ui.dp(12),ui.dp(12),ui.dp(12),ui.dp(12));
        android.graphics.drawable.GradientDrawable roleBackground=new android.graphics.drawable.GradientDrawable();
        roleBackground.setColor(ui.color(R.color.ap_mint));
        roleBackground.setCornerRadius(ui.dp(14));
        roleIcon.setBackground(roleBackground);
        roleIcon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        heading.addView(roleIcon,new LinearLayout.LayoutParams(ui.dp(48),ui.dp(48)));
        LinearLayout headingText=ui.column();
        LinearLayout.LayoutParams headingParams=new LinearLayout.LayoutParams(0,-2,1);
        headingParams.leftMargin=ui.dp(12);
        heading.addView(headingText,headingParams);
        headingText.addView(ui.text(pretty(loginRole)+" login",22,true,R.color.ap_ink));
        headingText.addView(ui.text(loginRole.equals("RESIDENT")?"Your flat & community":loginRole.equals("TREASURER")?"Payments & accounts":"Apartment management",12,false,R.color.ap_sub));
        content.addView(heading);
        ui.gap(content,20);
        formStart();
        EditText mobile=fields.field("mobile","Mobile number","",InputType.TYPE_CLASS_PHONE);
        mobile.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(10)});
        fields.placeholder("mobile","10-digit mobile number");
        EditText pin=fields.field("pin","PIN","",NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pin.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(6)});
        fields.placeholder("pin","4–6 digit PIN");
        LinearLayout tools=ui.row();
        MaterialButton switchLogin=ui.button(tools,"Switch login",R.drawable.ic_arrow_left,false,()->go("welcome"));
        MaterialButton forgotPin=ui.button(tools,"Forgot PIN?",R.drawable.ic_key,false,()->go("forgot-pin"));
        for(MaterialButton tool:new MaterialButton[]{switchLogin,forgotPin}) {
            tool.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
            tool.setTextSize(12);
            tool.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
        }
        content.addView(tools);
        ui.note(content,"Use your server account. Mobile verification and recovery are not configured in this local build.");
        ui.button(content,"Sign in",R.drawable.ic_arrow_right,true,()-> {
            String validation=LoginPolicy.validationMessage(fields.get("mobile"),fields.get("pin"));
            if(!validation.isEmpty()) {showError(validation);return;}
            request("POST","/auth/login",json("mobile",fields.get("mobile"),"pin",fields.get("pin"),"role",loginRole),r-> {
            api.session((JSONObject)r);
            history.clear();
            if(!LoginPolicy.canResume(resumePage,resumeAccount,api.account().optString("id"))) {
                resumePage="";
                resumeId="";
                resumeFields=null;
                resumeAccount="";
                pageDrafts=new JSONObject();
                selected.clear();
                restoreFields=null;
                page="home";
                render();
            } else resumePrevious();
        }
        );
        });
        if(loginRole.equals("ADMIN"))ui.button(content,"New apartment? Set up",R.drawable.ic_building,false,()->go("admin-signup"));
        else if(loginRole.equals("RESIDENT"))ui.button(content,"New resident? Join",R.drawable.ic_users,false,()->go("join"));
        else ui.note(content,"Treasurer access is assigned by your apartment admin.");
    }
    private void recoveryUnavailable() {
        header("Reset PIN");
        ui.state(content,R.drawable.ic_shield,"Reset PIN unavailable","Verified mobile recovery is not configured. Contact your apartment administrator; no test code can reset a release account.",R.color.ap_amber,R.color.ap_ambg);
        ui.button(content,"Back to sign in",R.drawable.ic_arrow_left,true,()->go(LoginPolicy.routeForRole(loginRole)));
    }
    private void register() {
        header("Create apartment");
        ui.heading(content,"Less admin. More community.");
        ui.note(content,"Start with your apartment basics.");
        formStart();
        fields.field("name","Your name",signupDraft.optString("name"),TEXT);
        fields.field("mobile","Mobile number",signupDraft.optString("mobile"),InputType.TYPE_CLASS_PHONE);
        fields.field("pin","Create 6-digit PIN","",NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        fields.check("authorised","I am authorised to manage this apartment.",false);
        fields.limit("name",80);fields.limit("mobile",10);fields.limit("pin",6);
        ui.button(content,"Continue",R.drawable.ic_arrow_right,true,()-> {
            String validation=RegistrationPolicy.person(fields.get("name"),fields.get("mobile"),fields.get("pin"),fields.checked("authorised"),true);
            if(!validation.isEmpty()) {showError(validation);return;}
            signupDraft=fields.values();
            ((PilotApplication)getApplication()).transientSignup=signupDraft;
            go("setup-apartment");
        }
        );
        if(BuildConfig.DEBUG)ui.note(content,"Local test accounts only. Do not use a real PIN.");
    }
    private void setup() {
        header("Apartment details");
        if(signupDraft.optString("pin").isEmpty()) {
            ui.empty(content,R.drawable.ic_key,"Enter your details again","Your PIN was cleared when the app restarted.");
            ui.button(content,"Admin details",R.drawable.ic_user,true,()->go("admin-signup"));
            return;
        }
        formStart();
        fields.field("apartmentName","Apartment name","",TEXT);
        fields.select("city","City",new String[] {
            "Hyderabad","Bengaluru","Chennai","Pune","Mumbai","Delhi NCR","Other"
        }
        ,new String[] {
            "Hyderabad","Bengaluru","Chennai","Pune","Mumbai","Delhi NCR","Other"
        }
        );
        fields.field("otherCity","City name · if you chose Other","",TEXT);
        fields.field("address","Address","",TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        fields.field("pinCode","PIN code","",NUMBER);
        fields.field("flats","Number of flats · 5–50","10",NUMBER);
        fields.limit("apartmentName",80);fields.limit("otherCity",80);fields.limit("address",300);fields.limit("pinCode",6);fields.limit("flats",2);
        ui.note(content,"Flats start at A-101. You can rename them after setup.");
        ui.button(content,"Create apartment",R.drawable.ic_building,true,()->safe(()-> {
            String validation=RegistrationPolicy.apartment(fields.get("apartmentName"),fields.get("address"),fields.get("pinCode"),fields.get("flats"));
            if(!validation.isEmpty()) {showError(validation);return;}
            String city=fields.get("city").equals("Other")?fields.get("otherCity").trim():fields.get("city");
            if(city.isEmpty()) {showError("Enter the city name.");return;}
            JSONObject body=json("name",signupDraft.optString("name"),"mobile",signupDraft.optString("mobile"),"pin",signupDraft.optString("pin"),"apartmentName",fields.get("apartmentName"),"city",city,"flats",Integer.parseInt(fields.get("flats")),"address",fields.get("address"),"pinCode",fields.get("pinCode"));
            request("GET","/status",null,statusResult-> {
                JSONObject status=(JSONObject)statusResult;
                if(!status.optBoolean("localRegistration")) {
                    showError("Apartment registration is not available on this server. Contact support; your details have not been submitted.");
                    return;
                }
                request("POST","/auth/register",body,r-> {
                    api.session((JSONObject)r);
                    signupDraft=new JSONObject();
                    ((PilotApplication)getApplication()).transientSignup=new JSONObject();
                    go("setup-done");
                }
                );
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
        ui.note(content,"Enter the code shared by your admin.");
        formStart();
        fields.field("invite","Invite code","",TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        fields.limit("invite",60);
        ui.button(content,"Find apartment",R.drawable.ic_arrow_right,true,()-> {
            inviteCode=fields.get("invite").toUpperCase(Locale.ROOT);
            if(!inviteCode.matches("[A-Z0-9_-]{8,60}")) {
                showError("Enter the code shared by your apartment admin.");
                return;
            }
            request("GET","/auth/invites/"+inviteCode,null,r-> {
                invitePreview=(JSONObject)r;
                savePageDraft("registration-flat",new JSONObject());
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
        ui.note(content,invitePreview.optString("city",""));
        formStart();
        fields.field("name","Your name","",TEXT);
        fields.field("mobile","Mobile number","",InputType.TYPE_CLASS_PHONE);
        fields.select("residentType","I am an",new String[] {"Owner","Tenant"},new String[] {"OWNER","TENANT"});
        fields.field("pin","Create 6-digit PIN","",NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        fields.check("consent","I agree to share my details with the apartment admin.",false);
        fields.limit("name",80);fields.limit("mobile",10);fields.limit("pin",6);
        // Returning from the flat step (for example after a rejected mobile) keeps what was typed, except the PIN.
        try {
            JSONObject kept=new JSONObject(signupDraft.toString());
            kept.remove("pin");
            if(kept.length()>0)fields.restore(kept);
        } catch(JSONException ignored) {}
        ui.button(content,"Choose flat",R.drawable.ic_arrow_right,true,()-> {
            String validation=RegistrationPolicy.person(fields.get("name"),fields.get("mobile"),fields.get("pin"),fields.checked("consent"),false);
            if(!validation.isEmpty()) {showError(validation);return;}
            signupDraft=fields.values();
            ((PilotApplication)getApplication()).transientSignup=signupDraft;
            go("choose-flat");
        }
        );
    }
    private void chooseFlat() {
        header("Choose your flat");
        if(signupDraft.optString("pin").isEmpty()) {
            ui.empty(content,R.drawable.ic_key,"Enter your details again","Your PIN was cleared when the app restarted.");
            ui.button(content,"Account details",R.drawable.ic_user,true,()->go("signup"));
            return;
        }
        JSONArray flats=invitePreview.optJSONArray("flats");
        if(flats==null||flats.length()==0) {
            ui.empty(content,R.drawable.ic_building,"No unassigned flats","Ask the admin to complete a resident handover.");
            ui.button(content,"Try another invite",R.drawable.ic_arrow_left,false,()->go("join"));
            return;
        }
        String[] labels=new String[flats.length()];
        for(int i=0; i<labels.length; i++)labels[i]=flats.optJSONObject(i).optString("label");
        ui.state(content,R.drawable.ic_home,"Make yourself at home.","One resident account per flat.",R.color.ap_green,R.color.ap_mint);
        String selectedFlat=pageDraft("registration-flat").optString("flatLabel");
        // Weighted rows of three, not a GridLayout: GridLayout re-measures an icon button with TEXT_START gravity until the
        // first column fills the screen on tablets, pushing the other two flats off screen.
        LinearLayout row=null;
        for(int i=0; i<labels.length; i++) {
            String label=labels[i];
            if(i%3==0) {row=ui.row();content.addView(row);}
            LinearLayout holder=ui.column();
            MaterialButton choice=ui.button(holder,label,R.drawable.ic_home,label.equals(selectedFlat),()-> {
                savePageDraft("registration-flat",json("flatLabel",label));render();
            });
            choice.setContentDescription("Choose flat "+label);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,ui.dp(64),1f);
            params.setMargins(ui.dp(3),ui.dp(3),ui.dp(3),ui.dp(3));
            holder.removeView(choice);
            row.addView(choice,params);
        }
        // Keep the last row's buttons the same width as the rows above.
        for(int i=labels.length%3; i>0&&i<3; i++) {
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,ui.dp(64),1f);
            params.setMargins(ui.dp(3),ui.dp(3),ui.dp(3),ui.dp(3));
            row.addView(new android.widget.Space(this),params);
        }
        ui.button(content,"Request to join",R.drawable.ic_users,true,()-> {
            if(selectedFlat.isEmpty()||!Arrays.asList(labels).contains(selectedFlat)) {showError("Choose a flat.");return;}
            request("POST","/auth/join",json("invite",inviteCode,"name",signupDraft.optString("name"),"mobile",signupDraft.optString("mobile"),"pin",signupDraft.optString("pin"),"residentType",signupDraft.optString("residentType"),"flatLabel",selectedFlat),r-> {
            api.pendingJoin((JSONObject)r);
            signupDraft=new JSONObject();
            ((PilotApplication)getApplication()).transientSignup=signupDraft;
            // The join form is finished and its PIN cleared: Back from the receipt goes to sign-in, not to an emptied form.
            openPageWithBackTarget("signup-pending","","welcome");
        });});
        ui.note(content,"Access begins only after the apartment admin approves this flat claim.");
    }
    private void joinPending() {
        header("Join request");
        JSONObject pending=api.pendingJoin();
        if(pending==null) {
            ui.empty(content,R.drawable.ic_shield,"Check your access","The status receipt is unavailable or expired. Sign in after approval, or contact your admin.");
        } else {
            String status=pending.optString("status","PENDING");
            boolean approved=status.equals("ACTIVE"),rejected=status.equals("REJECTED");
            ui.state(content,approved?R.drawable.ic_check:rejected?R.drawable.ic_error:R.drawable.ic_clock,approved?"Welcome home!":rejected?"Request not approved":"Almost home.",rejected?pending.optString("reason","Contact your apartment admin."):pending.optString("flatLabel")+" · "+(approved?"Access approved":"Awaiting admin approval"),approved?R.color.ap_green:R.color.ap_amber,approved?R.color.ap_mint:R.color.ap_ambg);
            LinearLayout card=ui.card(content);
            ui.kv(card,"Apartment",pending.optString("apartmentName"));
            ui.kv(card,"Flat",pending.optString("flatLabel"));
            ui.kv(card,"Status",approved?"Approved":rejected?"Rejected":status.equals("PENDING")?"Pending":"Not active");
            ui.button(content,"Check status",R.drawable.ic_clock,false,()->request("POST","/auth/join-status",json("receipt",pending.optString("receipt")),r-> {
                JSONObject refreshed=(JSONObject)r;
                refreshed.put("receipt",pending.getString("receipt"));refreshed.put("expiresAt",pending.getString("expiresAt"));
                api.pendingJoin(refreshed);render();
            }));
            if(rejected)ui.button(content,"Try again",R.drawable.ic_arrow_left,false,()-> {api.clearPendingJoin();go("join");});
        }
        ui.button(content,"Back to sign in",R.drawable.ic_arrow_right,true,()-> {loginRole="RESIDENT";go("resident-login");});
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
        content.addView(ui.text(LocalDate.now(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("EEEE, d MMMM",Locale.ENGLISH)),11,false,R.color.ap_muted));
        ui.gap(content,5);
        content.addView(ui.text("Hello, "+api.account().optString("name").split(" ")[0]+" ✦",26,true,R.color.ap_ink));
        ui.gap(content,16);
        if(!staff()) {
            request("GET","/bills?month="+month,null,r->{
                JSONArray bills=(JSONArray)r;
                JSONObject own=null;
                for(int i=0;i<bills.length();i++)if("MAINTENANCE".equals(bills.getJSONObject(i).optString("kind","MAINTENANCE"))){own=bills.getJSONObject(i);break;}
                if(own==null) {
                    ui.hero(content,monthLabel()+" maintenance","Not billed","Your apartment has not generated this month's bill.");
                    ui.button(content,"My dues",R.drawable.ic_receipt,true,()->go("dues"));
                } else {
                    JSONObject bill=own;
                    BigDecimal due=Ui.decimal(bill.opt("amount")).subtract(Ui.decimal(bill.opt("paid")));
                    String state=due.signum()<=0?"Paid":bill.optBoolean("inReview")?"In review":"Pending";
                    ui.hero(content,monthLabel()+" maintenance",Ui.money(due),bill.optString("flatLabel")+" · Due "+com.rapolus.apartmentpilotai.data.DateText.day(bill.optString("dueDate"))+" · "+state);
                    LinearLayout rejection=ui.column();
                    content.addView(rejection);
                    ui.button(content,due.signum()<=0?"Payment history":bill.optBoolean("inReview")?"Track payment":"I've paid",R.drawable.ic_wallet,true,()->go(due.signum()<=0?"history":bill.optBoolean("inReview")?"bill":"mark-paid",bill.optString("id")));
                    if(due.signum()>0&&!bill.optBoolean("inReview")) {
                        // Tell the resident why an earlier declaration for this bill was not accepted.
                        request("GET","/payments",null,payments->{
                            JSONArray list=(JSONArray)payments;
                            JSONObject latest=null;
                            for(int i=0;i<list.length();i++) {
                                JSONObject p=list.getJSONObject(i);
                                if(!bill.optString("id").equals(p.optString("billId")))continue;
                                if(latest==null||p.optString("reviewedAt").compareTo(latest.optString("reviewedAt"))>0)latest=p;
                            }
                            if(latest!=null&&"REJECTED".equals(latest.optString("status"))) {
                                LinearLayout card=ui.card(rejection);
                                card.addView(ui.text("Your last payment was not accepted",13,true,R.color.ap_red));
                                card.addView(ui.text(latest.optString("reason","Contact your apartment admin."),12,false,R.color.ap_sub));
                                card.addView(ui.text("Correct the details and submit the payment again.",11,false,R.color.ap_muted));
                            }
                            homeActions(false);
                            homeCommunity();
                        });
                        return;
                    }
                }
                homeActions(false);
                homeCommunity();
            });
            return;
        }
        request("GET","/billing/rules",null,rules->{
            if(admin()&&((JSONArray)rules).length()==0) {
                ui.iconRow(ui.card(content),"Set monthly maintenance","Add your apartment's amount and due date",R.drawable.ic_receipt,()->go("billing-settings"));
            }
            homeStaff();
        });
    }
    private void homeStaff() {
        request("GET","/reports/monthly?month="+month,null,r-> {
            JSONObject j=(JSONObject)r;
            request("GET","/bills?month="+month,null,rows->{
                JSONArray bills=(JSONArray)rows;
                Set<String> flats=new HashSet<>(),paid=new HashSet<>();
                for(int i=0;i<bills.length();i++) {
                    JSONObject bill=bills.getJSONObject(i);
                    if(!"MAINTENANCE".equals(bill.optString("kind","MAINTENANCE")))continue;
                    flats.add(bill.optString("flatId"));
                    if(Ui.decimal(bill.opt("paid")).compareTo(Ui.decimal(bill.opt("amount")))>=0)paid.add(bill.optString("flatId"));
                }
                BigDecimal billed=Ui.decimal(j.opt("billed")),collected=Ui.decimal(j.opt("collectedForBills"));
                int percent=billed.signum()==0?0:collected.multiply(BigDecimal.valueOf(100)).divide(billed,0,java.math.RoundingMode.HALF_UP).intValue();
                LinearLayout hero=ui.hero(content,monthLabel()+" collection",Ui.money(collected),"of "+Ui.money(billed)+" billed");
                ProgressBar collection=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
                collection.setProgress(Math.max(0,Math.min(100,percent)));
                collection.setProgressTintList(android.content.res.ColorStateList.valueOf(ui.color(R.color.ap_lime)));
                collection.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(0xff447568));
                collection.setContentDescription(percent+" percent collected");
                hero.addView(collection,new LinearLayout.LayoutParams(-1,ui.dp(8)));
                ui.gap(hero,10);
                TextView footer=ui.text(paid.size()+" paid / "+flats.size()+" billed flats     "+percent+"% collected",11,true,R.color.ap_bg);
                footer.setTextColor(0xfff1f8ed); hero.addView(footer);
                LinearLayout metrics=ui.row();
                homeMetric(metrics,"Outstanding",Ui.money(j.opt("outstanding")),"outstanding");
                homeMetric(metrics,"Available balance",Ui.money(j.opt("closing")),"reports");
                content.addView(metrics);
                if(j.optLong("pendingPayments")>0)ui.button(content,(j.optLong("pendingPayments")==1?"1 payment needs":j.optLong("pendingPayments")+" payments need")+" your review",R.drawable.ic_check,false,()->go("approvals"));
                homeActions(true);
                homeCommunity();
                ui.heading(content,"Recent activity");
                ui.iconRow(ui.card(content),"Activity log","Changes and decisions in your apartment",R.drawable.ic_clock,()->go("audit"));
            });
        }
        );
    }
    private String monthLabel() {return YearMonth.parse(month).format(DateTimeFormatter.ofPattern("MMMM",Locale.ENGLISH));}
    private void homeMetric(LinearLayout parent,String label,String value,String route) {
        LinearLayout column=ui.column();
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1); p.rightMargin=ui.dp(4); column.setLayoutParams(p);
        LinearLayout card=ui.card(column);
        card.addView(ui.text(label,10,false,R.color.ap_muted)); ui.gap(card,8);
        card.addView(ui.text(value,21,true,R.color.ap_ink));
        card.setContentDescription(label+" "+value);card.setFocusable(true);card.setOnClickListener(v->go(route));
        parent.addView(column);
    }
    private void homeActions(boolean management) {
        LinearLayout actions=ui.row();
        String[] labels=management?new String[]{"Dues","Expense","Notice",admin()?"Invite":"Report"}:new String[]{"History","Report issue","My requests","Contacts"};
        String[] routes=management?new String[]{"dues","expense-form","notice-form",admin()?"invite":"reports"}:new String[]{"history","issue-categories","complaints","contacts"};
        int[] icons=management?new int[]{R.drawable.ic_receipt,R.drawable.ic_expense,R.drawable.ic_notice,admin()?R.drawable.ic_users:R.drawable.ic_report}:new int[]{R.drawable.ic_receipt,R.drawable.ic_tools,R.drawable.ic_tools,R.drawable.ic_phone};
        for(int i=0;i<4;i++) {
            final String route=routes[i];
            LinearLayout tile=ui.column();tile.setGravity(Gravity.CENTER);tile.setPadding(0,ui.dp(12),0,ui.dp(12));
            tile.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
            ImageView icon=new ImageView(this);icon.setImageResource(icons[i]);icon.setImageTintList(android.content.res.ColorStateList.valueOf(ui.color(R.color.ap_pine)));
            tile.addView(icon,new LinearLayout.LayoutParams(ui.dp(24),ui.dp(24)));ui.gap(tile,7);
            TextView label=ui.text(labels[i],10,true,R.color.ap_ink);label.setGravity(Gravity.CENTER);tile.addView(label);
            tile.setFocusable(true);tile.setContentDescription(labels[i]);tile.setBackgroundResource(android.R.drawable.list_selector_background);tile.setOnClickListener(v->{if(route.equals("expense-form"))expenseRequestKey=UUID.randomUUID().toString();go(route);});
            actions.addView(tile);
        }
        content.addView(actions);
    }
    private void homeCommunity() {
        ui.heading(content,staff()?"Needs attention":"In your building");
        LinearLayout issues=ui.column();content.addView(issues);
        String kind=admin()?"COMPLAINT":"ISSUE";
        request("GET","/ops/tickets?kind="+kind,null,r->{
            JSONArray rows=(JSONArray)r;JSONObject current=null;int activeCount=0;
            for(int i=0;i<rows.length();i++) {
                JSONObject issue=rows.getJSONObject(i);
                if(!Arrays.asList("RESOLVED","CLOSED").contains(issue.optString("status"))) {
                    activeCount++;
                    if(current==null)current=issue;
                }
            }
            if(admin()) {
                ui.iconRow(ui.card(issues),"Active complaints",activeCount+" open · View all",R.drawable.ic_tools,()->go("complaints"));
                // Pending flat claims block residents from using the app, so surface them on Home.
                LinearLayout joins=ui.column();
                issues.addView(joins);
                request("GET","/members",null,members->{
                    JSONArray list=(JSONArray)members;
                    int pending=0;
                    for(int i=0;i<list.length();i++)if("PENDING".equals(list.getJSONObject(i).optString("status")))pending++;
                    if(pending>0)ui.iconRow(ui.card(joins),"Join requests",Ui.count(pending,"resident is","residents are")+" waiting for approval",R.drawable.ic_users,()->go("join-requests"));
                });
            }
            else if(current==null)ui.empty(issues,R.drawable.ic_check,"All caught up","No open common issues.");
            else {JSONObject issue=current;ui.iconRow(ui.card(issues),issue.optString("title"),pretty(issue.optString("status")),R.drawable.ic_tools,()->go("issue",issue.optString("id")));}
            if(!staff()) {
                ui.heading(content,"Community update");
                LinearLayout notices=ui.column();content.addView(notices);
                request("GET","/notices",null,n->{
                    JSONArray items=(JSONArray)n;
                    if(items.length()==0)ui.empty(notices,R.drawable.ic_notice,"No notices yet","");
                    else {JSONObject notice=items.getJSONObject(0);ui.iconRow(ui.card(notices),notice.optString("title"),notice.optString("createdAt"),R.drawable.ic_notice,()->go("notice",notice.optString("id")));}
                });
            }
        });
    }
    private void moneyMenu() {
        header("Money");
        monthSelector();
        LinearLayout c=ui.card(content);
        ui.iconRow(c,"Monthly dues","Bills and outstanding",R.drawable.ic_receipt,()->go("dues"));
        ui.iconRow(c,"Payments","Verify resident declarations",R.drawable.ic_check,()->go("approvals"));
        ui.iconRow(c,"Expenses","Apartment spending",R.drawable.ic_expense,()->go("expenses"));
        ui.iconRow(c,"Recurring expenses","Prepare expense drafts by schedule",R.drawable.ic_clock,()->go("recurring"));
        ui.iconRow(c,"Reports","Monthly money in and out",R.drawable.ic_report,()->go("reports"));
        ui.iconRow(c,"Maintenance amount","Future bills only",R.drawable.ic_settings,()->go("billing-settings"));
    }
    private void dues() {
        header("Maintenance");
        monthSelector();
        if(staff()) {
            MaterialButton amount=ui.button(content,"Maintenance amount",R.drawable.ic_settings,false,()->go("billing-settings"));
            amount.setTextSize(15);amount.setIconSize(ui.dp(23));amount.setMinHeight(ui.dp(54));
            ui.gap(content,12);
        }
        request("GET","/bills?month="+month,null,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)ui.empty(content,R.drawable.ic_receipt,"No dues for this month",staff()?"Configure maintenance, then generate bills.":"The admin has not generated a bill for your flat.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject b=arr.getJSONObject(i);
                BigDecimal due=Ui.decimal(b.opt("amount")).subtract(Ui.decimal(b.opt("paid")));
                String state=due.signum()==0?"Paid":b.optBoolean("inReview")?"In review":"Pending";
                LinearLayout c=ui.card(content);
                ui.statusIconRow(c,b.optString("flatLabel"),state,Ui.money(due),R.drawable.ic_receipt,()->go("bill",b.optString("id")));
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
            if(b.optBoolean("inReview")&&!b.isNull("pendingPaymentId"))ui.button(content,staff()?"Review payment":"Track payment",R.drawable.ic_clock,true,()->go(staff()?"payment":"payment-status",b.optString("pendingPaymentId")));
            ui.button(content,"Payment history",R.drawable.ic_clock,false,()->go("history"));
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
                request("POST","/payments",json("billId",id,"amount",new BigDecimal(fields.get("amount")),"mode",fields.get("mode"),"reference",fields.get("reference"),"paidOn",LocalDate.parse(fields.get("paidOn")).toString(),"note",fields.get("note"),"requestKey",paymentRequestKey),x->go("payment-status",((JSONObject)x).getString("id")));
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
            String thisMonth=YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
            // Until this month's bills exist, a correction applies to this month; afterwards it starts next month.
            fields.field("effective","Effective from · YYYY-MM",thisMonth,TEXT);
            if(rules.length()>0)request("GET","/billing/preview?month="+thisMonth,null,preview->{
                if(((JSONObject)preview).optInt("existing")>0&&fields!=null)fields.restore(json("effective",YearMonth.parse(thisMonth).plusMonths(1).toString()));
            });
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
            if(arr.length()==0)ui.empty(content,R.drawable.ic_receipt,"Maintenance not configured","Add your first monthly amount.");
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
        boolean approvals=page.equals("approvals");
        header(approvals&&staff()?"Payment approvals":"Payment history");
        request("GET","/payments",null,r-> {
            JSONArray arr=(JSONArray)r;
            int displayed=0;
            for(int i=0; i<arr.length(); i++) {
                JSONObject p=arr.getJSONObject(i);
                if(approvals&&!"PENDING".equals(p.optString("status")))continue;
                displayed++;
                LinearLayout c=ui.card(content);
                ui.statusIconRow(c,p.optString("flatLabel")+" · "+Ui.money(p.opt("amount")),p.optBoolean("reversed")?"Reversed":pretty(p.optString("status")),pretty(p.optString("mode")),R.drawable.ic_wallet,()->go(staff()?"payment":"payment-status",p.optString("id")));
            }
            if(displayed==0)ui.empty(content,R.drawable.ic_receipt,"No payments here","Submitted payments appear here for verification.");
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
            if(pending.isEmpty())ui.empty(content,R.drawable.ic_receipt,"No pending payments","New declarations will appear here.");
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
                ui.empty(content,R.drawable.ic_receipt,"Selection is no longer available","Refresh pending payments and select again.");
                ui.button(content,"Back to approvals",R.drawable.ic_arrow_right,true,()->go("approvals"));
                return;
            }
            LinearLayout summary=ui.card(content);
            ui.hero(summary,Ui.count(pending.size(),"selected payment","selected payments"),Ui.money(total),"Confirm every bank or cash record");
            for(JSONObject p:pending) {
                LinearLayout c=ui.card(content);
                ui.iconRow(c,p.optString("flatLabel"),pretty(p.optString("mode"))+" · "+(p.isNull("reference")?"Cash":p.optString("reference")),R.drawable.ic_wallet,null);
                ui.kv(c,"Amount",Ui.money(p.opt("amount")));
            }
            formStart();
            fields.check("verified","I have verified every selected payment.",false);
            ui.note(content,"Only approve payments matched to the association bank record or received in cash.");
            ui.button(content,"Approve "+Ui.count(pending.size(),"payment","payments"),R.drawable.ic_check,true,()->safe(()-> {
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
                ui.empty(content,R.drawable.ic_receipt,"Payment unavailable","It may already have been reviewed.");
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
            Set<String> unpaidFlats=new HashSet<>();
            for(JSONObject b:unpaid)unpaidFlats.add(b.optString("flatId"));
            // Bills include one-time contributions and opening dues, so count flats and name the bill type.
            ui.hero(content,"Outstanding dues",Ui.money(total),Ui.count(unpaidFlats.size(),"flat","flats")+" · "+Ui.count(unpaid.size(),"open bill","open bills")+" · "+underReview+" in review");
            for(JSONObject b:unpaid) {
                BigDecimal due=Ui.decimal(b.opt("amount")).subtract(Ui.decimal(b.opt("paid")));
                LinearLayout c=ui.card(content);
                ui.iconRow(c,b.optString("flatLabel")+" · "+("MAINTENANCE".equals(b.optString("kind","MAINTENANCE"))?"Maintenance":b.optString("title","Other charge")),b.optBoolean("inReview")?"In review":"Payment due",R.drawable.ic_clock,()->go("bill",b.optString("id")));
                ui.kv(c,"Outstanding",Ui.money(due));
            }
            if(unpaid.isEmpty())ui.empty(content,R.drawable.ic_check,"Everyone is up to date","There are no outstanding dues for this month.");
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
            if(preview.optLong("recipientCount")==0)ui.empty(content,R.drawable.ic_receipt,"No reminder recipients","Paid and in-review payments are excluded.");
            ui.note(content,"Reminders are saved to residents' in-app inbox. Phone (SMS/push) delivery is "+Ui.delivery(preview.optString("externalDeliveryStatus"))+".");
            ui.button(content,"Send reminder",R.drawable.ic_bell,true,()->request("POST","/billing/reminders/send?month="+month,json("requestKey",reminderRequestKey),x-> {
                JSONObject result=(JSONObject)x;
                ui.message(Ui.count(result.optInt("created"),"in-app reminder","in-app reminders")+" sent. Phone delivery is "+Ui.delivery(result.optString("externalDeliveryStatus"))+".",true);
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
                ui.empty(content,R.drawable.ic_receipt,"Maintenance amount needed","Set the amount and due day first.");
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
        request("GET","/payments/"+id,null,r-> {
            JSONObject p=(JSONObject)r;
            LinearLayout c=ui.card(content);
            ui.hero(c,p.optBoolean("reversed")?"Reversed":pretty(p.optString("status")),Ui.money(p.opt("amount")),p.optString("flatLabel"));
            ui.button(content,"Payment proof",R.drawable.ic_copy,false,()->go("proof",id));
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
            ui.kv(c,"Month",p.optString("billingMonth").length()>=7?YearMonth.parse(p.optString("billingMonth").substring(0,7)).format(DateTimeFormatter.ofPattern("MMMM uuuu",Locale.ENGLISH)):p.optString("billingMonth"));
            ui.kv(c,"Mode",pretty(p.optString("mode")));
            ui.kv(c,"Paid on",com.rapolus.apartmentpilotai.data.DateText.day(p.optString("paidOn")));
            ui.kv(c,"Approved by",p.optString("approvedBy"));
            ui.note(c,p.optString("receiptNumber"));
            getLayoutInflater().inflate(R.layout.view_brand_footer,c,true);
            ui.button(content,"Share PDF",R.drawable.ic_receipt,true,()->exportReport(receiptReport(p)));
            ui.button(content,"Share text",R.drawable.ic_receipt,false,()-> {
                String text=p.optString("apartmentName")+(p.optBoolean("reversed")?"\nREVERSED ":"\nPAID ")+Ui.money(p.opt("amount"))+"\nFlat "+p.optString("flatLabel")+"\nMonth "+p.optString("billingMonth")+"\nReceipt "+p.optString("receiptNumber")+"\nPowered By @Rapolu`s";
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,text),"Share receipt"));
            }
            );
        }
        );
    }
    /** Maintenance receipt in the standard report layout: the same fields as the receipt screen. */
    private com.rapolus.apartmentpilotai.ui.ReportPdf.Doc receiptReport(JSONObject p) {
        boolean reversed=p.optBoolean("reversed");
        java.util.List<String[]> rows=new ArrayList<>();
        rows.add(new String[]{"Flat",p.optString("flatLabel")});
        rows.add(new String[]{"Billing month",com.rapolus.apartmentpilotai.data.ReportText.month(p.optString("billingMonth"))});
        rows.add(new String[]{"Paid using",pretty(p.optString("mode"))});
        rows.add(new String[]{"Paid on",com.rapolus.apartmentpilotai.data.DateText.day(p.optString("paidOn"))});
        rows.add(new String[]{"Approved by",p.optString("approvedBy")});
        rows.add(new String[]{"Receipt number",p.optString("receiptNumber")});
        com.rapolus.apartmentpilotai.ui.ReportPdf.Doc doc=new com.rapolus.apartmentpilotai.ui.ReportPdf.Doc(p.optString("apartmentName"),"Maintenance receipt",com.rapolus.apartmentpilotai.data.ReportText.month(p.optString("billingMonth")),p.optString("receiptNumber"))
            .amount("Amount received",com.rapolus.apartmentpilotai.data.ReportText.amount(p.opt("amount")),reversed?"REVERSED":"PAID",reversed)
            .section("Payment details")
            .details(rows);
        doc.note(reversed?"This payment was reversed. The original receipt is kept for the record; check the current bill before paying again.":"This receipt was created when apartment staff approved the payment.");
        return doc;
    }
    private void expenses() {
        header("Expenses");
        monthSelector();
        if(staff())ui.button(content,"Add expense",R.drawable.ic_plus,true,()-> {
            expenseRequestKey=UUID.randomUUID().toString();
            go("expense-form");
        }
        );
        LinearLayout summary=ui.card(content);
        summary.addView(ui.text("Paid expenses · "+monthLabel().split(" ")[0],11,false,R.color.ap_muted));
        TextView total=ui.text("—",32,true,R.color.ap_ink);
        summary.addView(total);
        Ui.Fields searchFields=new Ui.Fields(ui,content);
        EditText search=searchFields.field("search","Search spending","",TEXT);
        LinearLayout filters=ui.row();
        content.addView(filters);
        LinearLayout list=ui.column();
        content.addView(list);
        String[] selected={"All"};
        String[] options=staff()?new String[]{"All","Paid","Drafts"}:new String[]{"All"};
        for(String option:options) {
            MaterialButton filter=ui.button(filters,option,0,selected[0].equals(option),()->{
                selected[0]=option;
                for(int i=0;i<filters.getChildCount();i++) {
                    View child=filters.getChildAt(i);
                    if(child instanceof MaterialButton) {
                        MaterialButton button=(MaterialButton)child;
                        boolean active=button.getText().toString().equals(option);
                        int ink=ui.color(active?R.color.ap_bg:R.color.ap_ink);
                        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ui.color(active?R.color.ap_pine:R.color.ap_soft)));
                        button.setTextColor(ink);
                        button.setIconTint(android.content.res.ColorStateList.valueOf(ink));
                    }
                }
                renderExpenseRows(expenses, list, search.getText().toString(), selected[0]);
            });
            filter.setSingleLine(true);
            filter.setTextSize(11);
            filter.setPadding(ui.dp(4),0,ui.dp(4),0);
            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)filter.getLayoutParams();
            params.width=0;params.weight=1;params.setMarginEnd(ui.dp(6));filter.setLayoutParams(params);
        }
        request("GET","/expenses?month="+month,null,r-> {
            expenses=(JSONArray)r;
            renderExpenseRows(expenses,list,search.getText().toString(),selected[0]);
            search.addTextChangedListener(new android.text.TextWatcher() {
                public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                public void onTextChanged(CharSequence s,int start,int before,int count) {renderExpenseRows(expenses,list,s.toString(),selected[0]);}
                public void afterTextChanged(android.text.Editable value) {}
            });
            request("GET","/reports/monthly?month="+month,null,report->total.setText(Ui.money(((JSONObject)report).opt("spent"))));
        }
        );
        if(staff())ui.button(content,"Recurring expenses",R.drawable.ic_clock,false,()->go("recurring"));
    }
    private JSONArray expenses=new JSONArray();
    private void renderExpenseRows(JSONArray rows,LinearLayout list,String query,String filter) {
        list.removeAllViews();
        String term=query==null?"":query.trim().toLowerCase(Locale.ROOT);
        int visible=0;
        for(int i=0;i<rows.length();i++) {
            JSONObject expense=rows.optJSONObject(i);
            if(expense==null)continue;
            boolean paid=expense.optBoolean("paid");
            if(filter.equals("Paid")&&!paid||filter.equals("Drafts")&&paid)continue;
            if(!term.isEmpty()&&!expense.optString("title").toLowerCase(Locale.ROOT).contains(term)&&!expense.optString("category").toLowerCase(Locale.ROOT).contains(term))continue;
            LinearLayout card=ui.card(list);
            ui.iconRow(card,expense.optString("title"),expense.optString("category")+" · "+expense.optString("paidOn"),R.drawable.ic_expense,()->go("expense",expense.optString("id")));
            ui.kv(card,!expense.isNull("reversedOn")?"Reversed":paid?"Paid":"Draft",Ui.money(expense.opt("amount")));
            visible++;
        }
        if(visible==0)ui.empty(list,R.drawable.ic_expense,term.isEmpty()&&filter.equals("All")?"No expenses here":"No matching expenses",term.isEmpty()&&filter.equals("All")?"Paid expenses appear in monthly reports.":"Try another search or filter.");
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
                String destination="PENDING".equals(m.optString("status"))?"join-review":"member";
                LinearLayout c=ui.card(content);
                ui.iconRow(c,m.optString("name"),(m.isNull("flatLabel")?"Apartment team":m.optString("flatLabel"))+" · "+pretty(m.optString("status")),R.drawable.ic_user,()->go(destination,m.optString("id")));
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
                ui.button(content,"Reject request",R.drawable.ic_error,false,()->go("join-reject",id));
            } else if("ACTIVE".equals(m.optString("status"))&&"RESIDENT".equals(m.optString("role"))) {
                ui.button(content,"Assign treasurer",R.drawable.ic_shield,false,()->ui.confirm("Grant financial access?","Treasurer can approve payments, add expenses and view financial records. Existing sessions are revoked.","Grant access",()->request("POST","/members/"+id+"/treasurer",json(),x->render())));
            }
        }
        );
    }
    private void invite() {
        header("Invite residents");
        if(!admin()) {
            ui.empty(content,R.drawable.ic_shield,"Admin access required","Only your apartment Admin can share resident invites.");
            return;
        }
        LinearLayout introduction=ui.column();
        introduction.setGravity(Gravity.CENTER_HORIZONTAL);
        introduction.setPadding(ui.dp(10),ui.dp(16),ui.dp(10),ui.dp(20));
        ImageView invitationIcon=new ImageView(this);
        invitationIcon.setImageResource(R.drawable.ic_users);
        invitationIcon.setImageTintList(android.content.res.ColorStateList.valueOf(ui.color(R.color.ap_pine)));
        invitationIcon.setPadding(ui.dp(16),ui.dp(16),ui.dp(16),ui.dp(16));
        android.graphics.drawable.GradientDrawable iconBackground=new android.graphics.drawable.GradientDrawable();
        iconBackground.setColor(ui.color(R.color.ap_mint));
        iconBackground.setCornerRadius(ui.dp(24));
        invitationIcon.setBackground(iconBackground);
        introduction.addView(invitationIcon,new LinearLayout.LayoutParams(ui.dp(64),ui.dp(64)));
        ui.gap(introduction,12);
        TextView invitationTitle=ui.text("A place for everyone.",22,true,R.color.ap_ink);
        invitationTitle.setGravity(Gravity.CENTER);
        introduction.addView(invitationTitle);
        ui.gap(introduction,4);
        TextView invitationDetail=ui.text("Share this code with your residents.",12,false,R.color.ap_muted);
        invitationDetail.setGravity(Gravity.CENTER);
        introduction.addView(invitationDetail);
        content.addView(introduction);
        request("GET","/invites/current",null,r-> {
            JSONObject invite=(JSONObject)r;
            String code=invite.isNull("code")?"":invite.optString("code");
            String day=invite.isNull("expiresOn")?"—":invite.optString("expiresOn","—");
            String shownDate=day.equals("—")?day:LocalDate.parse(day).format(DateTimeFormatter.ofPattern("d MMM uuuu",Locale.ENGLISH));
            LinearLayout display=ui.column();
            display.setGravity(Gravity.CENTER_HORIZONTAL);
            display.setPadding(ui.dp(18),ui.dp(18),ui.dp(18),ui.dp(18));
            android.graphics.drawable.GradientDrawable codeBackground=new android.graphics.drawable.GradientDrawable();
            codeBackground.setColor(ui.color(R.color.ap_pine));
            codeBackground.setCornerRadius(ui.dp(20));
            display.setBackground(codeBackground);
            TextView codeLabel=ui.text("APARTMENT INVITE",10,true,R.color.ap_bg);
            codeLabel.setTextColor(0xffe7f3e7);
            codeLabel.setGravity(Gravity.CENTER);
            display.addView(codeLabel);
            ui.gap(display,8);
            TextView codeText=ui.text(code.isEmpty()?"Code unavailable":code,22,true,R.color.ap_bg);
            codeText.setTextColor(0xfff1f8ed);
            codeText.setGravity(Gravity.CENTER);
            codeText.setSingleLine(true);
            codeText.setTextSize(code.length()>18?17:22);
            display.addView(codeText);
            ui.gap(display,8);
            TextView codeStatus=ui.text(!invite.optBoolean("exists")?"No code has been issued.":!invite.optBoolean("active")?"Revoked":!invite.optBoolean("valid")?"Expired":"Expires "+shownDate,11,false,R.color.ap_bg);
            codeStatus.setTextColor(0xffd3e0d4);
            codeStatus.setGravity(Gravity.CENTER);
            display.addView(codeStatus);
            content.addView(display);
            ui.gap(content,6);
            if(!invite.optBoolean("displayConfigured"))ui.note(content,"Invite codes are not set up on the server yet. Configure the invite display key before issuing a code.");
            else if(!invite.isNull("displayError")&&!invite.optString("displayError").isEmpty())ui.note(content,invite.optString("displayError"));
            else if(invite.optBoolean("exists")&&!invite.optBoolean("codeAvailable"))ui.note(content,"Legacy code cannot be redisplayed. Generate a new code to replace it.");
            if(!code.isEmpty()&&invite.optBoolean("valid")) {
                LinearLayout actions=ui.row();
                Button copy=ui.button(actions,"Copy code",R.drawable.ic_copy,false,()-> {
                    ((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Apartment invite",code));
                    ui.message("Invite copied",false);
                });
                Button share=ui.button(actions,"Share",R.drawable.ic_users,true,()->startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,"Join "+api.account().optString("apartmentName")+" in ApartmentCare. Invite: "+code+". Valid until "+day+". Flat access needs Admin approval."),"Share invite")));
                copy.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
                LinearLayout.LayoutParams shareParams=new LinearLayout.LayoutParams(0,-2,1);
                shareParams.setMarginStart(ui.dp(8));
                share.setLayoutParams(shareParams);
                content.addView(actions);
            }
            ui.heading(content,"Invite settings");
            LinearLayout settings=ui.card(content);
            settings.setPadding(0,0,0,0);
            ui.iconRow(settings,"Expiry date",shownDate,R.drawable.ic_calendar,()->go("invite-settings"));
            View divider=new View(this);
            divider.setBackgroundColor(ui.color(R.color.ap_line));
            settings.addView(divider,new LinearLayout.LayoutParams(-1,ui.dp(1)));
            ui.iconRow(settings,"Approval required","One active account per flat",R.drawable.ic_shield,()->go("join-requests"));
            ui.button(content,"Manage invite",R.drawable.ic_settings,false,()->go("invite-settings"));
        });
    }
    private void notices() {
        header("Notice board");
        if(staff())ui.button(content,"Send notice",R.drawable.ic_notice,true,()->go("notice-form"));
        request("GET","/notices",null,r-> {
            JSONArray arr=(JSONArray)r;
            if(arr.length()==0)ui.empty(content,R.drawable.ic_notice,"No notices yet","Published apartment updates appear here.");
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
            if(arr.length()==0)ui.empty(content,R.drawable.ic_check,"You're all caught up","New activity appears in your inbox.");
            for(int i=0; i<arr.length(); i++) {
                JSONObject n=arr.getJSONObject(i);
                LinearLayout c=ui.card(content);
                String detail=n.optString("body");
                ui.iconRow(c,(n.isNull("readAt")?"● ":"")+n.optString("title"),(detail.isEmpty()?"":detail+" · ")+com.rapolus.apartmentpilotai.data.DateText.humanize(n.optString("createdAt"))+(n.isNull("readAt")?" · Unread":""),R.drawable.ic_bell,()->request("POST","/notifications/"+n.optString("id")+"/read",json(),x-> {
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
                ui.note(c,com.rapolus.apartmentpilotai.data.DateText.humanize(a.optString("createdAt"))+(a.isNull("actor")?"":" · by "+a.optString("actor")));
                if(!a.isNull("afterValue"))ui.note(c,a.optString("afterValue"));
                ui.button(c,"View activity",R.drawable.ic_arrow_right,false,()->go("audit-detail",a.optString("id")));
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
            exitSession();
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
            boolean registration=j.optBoolean("localRegistration");
            LinearLayout c=ui.card(content);
            ui.kv(c,"Connection","Successful");
            ui.kv(c,"Backend",j.optString("version"));
            ui.kv(c,"Local registration",registration?"Enabled":"Disabled");
            if(!registration) c.addView(ui.text("Apartment creation is disabled. Start Spring Boot with the local profile before creating an apartment.",12,false,R.color.ap_red));
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
        if(s.equalsIgnoreCase("UPI"))return "UPI";
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
    @Override public JSONObject pageDraft(String key) {
        JSONObject value=pageDrafts.optJSONObject(key);
        return value==null?new JSONObject():value;
    }
    @Override public void savePageDraft(String key,JSONObject value) {
        try {
            pageDrafts.put(key,value);
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }
    @Override public void clearPageDraft(String key) {
        pageDrafts.remove(key);
    }
    @Override public void openPage(String page,String record) {
        go(page,record);
    }
    @Override public void openPageWithBackTarget(String next,String record,String backPage) {
        history.clear();
        history.push(new String[]{backPage,""});
        page=next;
        id=record;
        resetCommandKey();
        restoreFields=null;
        selected.clear();
        render();
        ((ScrollView)findViewById(R.id.scroll)).scrollTo(0,0);
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
        pageDrafts=new JSONObject();
        page="welcome";
        id="";
        resumePage="";
        resumeId="";
        resumeFields=null;
        resumeAccount="";
        restoreFields=null;
        selected.clear();
        attachmentKind="";
        attachmentParent="";
        saveAttachmentId="";
        saveAttachmentAccount="";
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
        if(requestCode==2203) {
            String fileId=saveAttachmentId,account=saveAttachmentAccount;
            saveAttachmentId="";saveAttachmentAccount="";
            if(resultCode!=RESULT_OK||data==null||data.getData()==null)return;
            if(fileId.isEmpty()||!api.signedIn()||!account.equals(api.account().optString("id"))){showError("Sign in again before saving the attachment.");return;}
            android.net.Uri destination=data.getData();
            request("GET","/ops/files/"+fileId,null,result->{
                final int screen=generation;
                JSONObject file=(JSONObject)result;
                fileWorker.execute(()->{
                    try {
                        localFiles.save(file,destination);
                        runOnUiThread(()->{if(!isDestroyed()&&screen==generation)ui.message("Attachment saved",false);});
                    } catch(Exception e){runOnUiThread(()->{if(!isDestroyed()&&screen==generation)showError("Could not save attachment: "+e.getMessage());});}
                });
            });
            return;
        }
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
    @Override public void exportReport(com.rapolus.apartmentpilotai.ui.ReportPdf.Doc doc) {
        try {
            localFiles.exportReport(doc);
        } catch(Exception e) {
            showError("Export failed: "+e.getMessage());
        }
    }
    @Override public void exportCsv(String title,java.util.List<java.util.List<String>> rows) {
        try {
            localFiles.exportCsv(title,rows);
        } catch(Exception e) {
            showError("CSV export failed: "+e.getMessage());
        }
    }
    @Override public void openSharedFile(JSONObject file) {
        try {
            localFiles.open(file);
        } catch(Exception e) {
            showError("Could not open attachment: "+e.getMessage());
        }
    }
    @Override public void previewAttachment(JSONObject file,LinearLayout target) {
        final int screen=generation;
        final Object selection=target.getTag();
        fileWorker.execute(()->{
            try {
                android.graphics.Bitmap bitmap=localFiles.preview(file);
                runOnUiThread(()->{
                    if(isDestroyed()||screen!=generation||!target.isAttachedToWindow()||!java.util.Objects.equals(selection,target.getTag())){bitmap.recycle();return;}
                    ImageView image=new ImageView(this);image.setAdjustViewBounds(true);image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    image.setContentDescription(file.optString("name","Attachment preview"));image.setImageBitmap(bitmap);
                    target.addView(image,new LinearLayout.LayoutParams(-1,-2));
                });
            } catch(Exception e){runOnUiThread(()->{if(!isDestroyed()&&screen==generation&&target.isAttachedToWindow()&&java.util.Objects.equals(selection,target.getTag()))ui.note(target,"Preview unavailable. You can still open or save the original file.");});}
        });
    }
    @Override public void saveAttachment(JSONObject file) {
        if(!api.signedIn())return;
        try {
            String mime=file.getString("mime");
            saveAttachmentId=file.getString("id");saveAttachmentAccount=api.account().optString("id");
            Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType(mime)
                .putExtra(Intent.EXTRA_TITLE,com.rapolus.apartmentpilotai.data.AttachmentPolicy.name(file.optString("name"),mime));
            startActivityForResult(intent,2203);
        } catch(Exception e){saveAttachmentId="";saveAttachmentAccount="";showError("Could not choose save destination: "+e.getMessage());}
    }
    @Override protected void onDestroy() {
        super.onDestroy();
        fileWorker.shutdown();
    }
}
