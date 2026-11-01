package com.rapolus.apartmentpilotai.ui;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.android.material.button.MaterialButton;
import com.rapolus.apartmentpilotai.R;
import com.rapolus.apartmentpilotai.data.ApiClient;
import com.rapolus.apartmentpilotai.data.TabletPolicy;
import com.rapolus.apartmentpilotai.data.TeamDashboard;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.json.*;
/**
 * ApartmentCare team (platform super user) screens: sign-in and a read-only dashboard across all apartments.
 * Uses the team session in ApiClient only, so the Admin, Treasurer and Resident flows are untouched.
 */
public final class TeamScreens {
    public interface Host {
        void open(String page);
        void title(String text);
        int generation();
    }
    private final Activity activity;
    private final Ui ui;
    private final ApiClient api;
    private final LinearLayout content;
    private final Host host;
    private JSONObject data;
    private String query="";
    private TeamDashboard.Sort sort=TeamDashboard.Sort.STATUS;
    private boolean busy;
    public TeamScreens(Activity activity,Ui ui,ApiClient api,LinearLayout content,Host host) {
        this.activity=activity;
        this.ui=ui;
        this.api=api;
        this.content=content;
        this.host=host;
    }
    public static boolean handles(String page) {
        return "team-login".equals(page)||"team-dashboard".equals(page);
    }
    public void render(String page) {
        busy=false;
        if("team-dashboard".equals(page)&&api.operatorSignedIn())dashboard();
        else login();
    }
    private void error(LinearLayout parent,String text) {
        LinearLayout card=ui.card(parent);
        card.addView(ui.text(text,12,false,R.color.ap_red));
    }
    private void login() {
        host.title("ApartmentCare team");
        ui.heading(content,"ApartmentCare team login");
        ui.note(content,"For the ApartmentCare team only. Apartment Admins, Treasurers and Residents use the other logins.");
        Ui.Fields fields=new Ui.Fields(ui,content);
        EditText mobile=fields.field("mobile","Mobile number","",InputType.TYPE_CLASS_PHONE);
        mobile.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(10)});
        fields.placeholder("mobile","10-digit mobile number");
        EditText pin=fields.field("pin","PIN","",InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pin.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(6)});
        fields.placeholder("pin","6-digit PIN");
        LinearLayout errors=ui.column();
        content.addView(errors);
        ui.button(content,"Sign in",R.drawable.ic_arrow_right,true,()-> {
            if(busy)return;
            errors.removeAllViews();
            String m=fields.get("mobile"),p=fields.get("pin");
            if(m==null||!m.matches("[6-9][0-9]{9}")) {error(errors,"Enter a valid 10-digit mobile number.");return;}
            if(p==null||!p.matches("[0-9]{6}")) {error(errors,"Enter your 6-digit PIN.");return;}
            JSONObject body=new JSONObject();
            try {
                body.put("mobile",m);
                body.put("pin",p);
            } catch(JSONException impossible) {
                return;
            }
            busy=true;
            int version=host.generation();
            api.callOperator("POST","/operator/login",body,(result,failure)-> {
                if(activity.isFinishing()||version!=host.generation())return;
                busy=false;
                if(failure!=null) {
                    error(errors,failure.status==0?failure.message:failure.message.isEmpty()?"Sign-in failed.":failure.message);
                    return;
                }
                try {
                    api.operatorSession((JSONObject)result);
                    data=null;
                    host.open("team-dashboard");
                } catch(JSONException|ClassCastException e) {
                    error(errors,"Unexpected server reply. Try again.");
                }
            });
        });
        ui.button(content,"Back to sign in",R.drawable.ic_arrow_left,false,()->host.open("welcome"));
    }
    private void dashboard() {
        host.title("All apartments");
        // Admin names and mobile numbers: no screenshots, and hidden in the recent-apps view.
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        if(data!=null) {
            show();
            return;
        }
        ProgressBar spinner=new ProgressBar(activity);
        content.addView(spinner);
        load();
    }
    private void load() {
        if(busy)return;
        busy=true;
        int version=host.generation();
        api.callOperator("GET","/operator/dashboard",null,(result,failure)-> {
            if(activity.isFinishing()||version!=host.generation())return;
            busy=false;
            content.removeAllViews();
            if(failure!=null) {
                if(failure.status==401) {
                    api.clearOperator();
                    host.open("team-login");
                    return;
                }
                ui.state(content,R.drawable.ic_error,"Dashboard unavailable",failure.message,R.color.ap_amber,R.color.ap_ambg);
                ui.button(content,"Retry",R.drawable.ic_rotate,true,this::load);
                ui.button(content,"Sign out",R.drawable.ic_logout,false,this::signOut);
                return;
            }
            data=result instanceof JSONObject?(JSONObject)result:new JSONObject();
            show();
        });
    }
    private void signOut() {
        api.callOperator("POST","/operator/logout",null,(r,e)-> {});
        api.clearOperator();
        data=null;
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        host.open("welcome");
    }
    private static String str(JSONObject o,String key) {
        return o==null||o.isNull(key)?"":o.optString(key,"");
    }
    private boolean wide() {
        return TabletPolicy.split(activity.getResources().getConfiguration().screenWidthDp);
    }
    private void show() {
        content.removeAllViews();
        JSONObject totals=data.optJSONObject("totals");
        if(totals==null)totals=new JSONObject();
        content.addView(ui.text("Signed in as "+str(api.operator(),"name")+" · updated "+str(data,"generatedAt"),11,false,R.color.ap_sub));
        ui.gap(content,10);
        String next=str(data,"nextMonth");
        String[][] tiles={
            {"Apartments",String.valueOf(totals.optInt("apartments")),"Registered"},
            {"Signed in",String.valueOf(totals.optInt("signedInLast30Days")),"Last "+data.optInt("activeWindowDays",30)+" days"},
            {"Active",String.valueOf(totals.optInt("activeSubscriptions")),"Paid this month"},
            {"On trial",String.valueOf(totals.optInt("onTrial")),"Free first month"},
            {"Payment due",String.valueOf(totals.optInt("paymentDue")),"Locked until paid"},
            {"Received",Ui.money(totals.opt("receivedThisMonth")),"This month"},
            {"Received total",Ui.money(totals.opt("receivedTotal")),"All time"},
            {"Expected",Ui.money(totals.opt("expectedNextMonth")),next.isEmpty()?"Next month":"Due in "+monthLabel(next)}
        };
        int perRow=wide()?4:2;
        LinearLayout row=null;
        for(int i=0; i<tiles.length; i++) {
            if(i%perRow==0) {
                row=ui.row();
                row.setGravity(Gravity.TOP);
                content.addView(row);
            }
            tile(row,tiles[i][0],tiles[i][1],tiles[i][2],i%perRow>0);
        }
        ui.heading(content,"Apartments");
        EditText search=new EditText(activity);
        search.setHint("Search name, city, Admin or mobile");
        search.setSingleLine(true);
        search.setText(query);
        search.setTextSize(ui.sp(13));
        content.addView(search,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout sorts=ui.row();
        String[] labels={"Needs attention","Name","Revenue","Last sign-in"};
        TeamDashboard.Sort[] values={TeamDashboard.Sort.STATUS,TeamDashboard.Sort.NAME,TeamDashboard.Sort.REVENUE,TeamDashboard.Sort.LAST_SIGN_IN};
        LinearLayout list=ui.column();
        for(int i=0; i<labels.length; i++) {
            TeamDashboard.Sort value=values[i];
            MaterialButton b=ui.button(sorts,labels[i],0,sort==value,()-> {
                sort=value;
                show();
            });
            b.setTextSize(ui.sp(11));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);
            if(i>0)p.leftMargin=ui.dp(4);
            b.setLayoutParams(p);
        }
        content.addView(sorts);
        ui.gap(content,8);
        content.addView(list);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c) {}
            @Override public void onTextChanged(CharSequence s,int a,int b,int c) {}
            @Override public void afterTextChanged(Editable s) {
                query=s.toString();
                fillList(list);
            }
        });
        fillList(list);
        ui.gap(content,8);
        ui.button(content,"Refresh",R.drawable.ic_rotate,false,()-> {
            data=null;
            content.removeAllViews();
            content.addView(new ProgressBar(activity));
            load();
        });
        ui.button(content,"Sign out",R.drawable.ic_logout,false,this::signOut);
        ui.note(content,"Read-only view. Revenue is subscription money verified as received. Expected is the plan price for each apartment's next billing cycle, less any credit.");
    }
    private static String monthLabel(String yearMonth) {
        try {
            return java.time.YearMonth.parse(yearMonth).format(java.time.format.DateTimeFormatter.ofPattern("MMM uuuu",Locale.ENGLISH));
        } catch(RuntimeException e) {
            return yearMonth;
        }
    }
    private void tile(LinearLayout row,String label,String value,String detail,boolean spaced) {
        LinearLayout holder=ui.column();
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);
        if(spaced)p.leftMargin=ui.dp(8);
        holder.setLayoutParams(p);
        row.addView(holder);
        LinearLayout card=ui.card(holder);
        card.setPadding(ui.dp(12),ui.dp(12),ui.dp(12),ui.dp(12));
        card.addView(ui.text(label,11,false,R.color.ap_muted));
        card.addView(ui.text(value,20,true,R.color.ap_ink));
        card.addView(ui.text(detail,10,false,R.color.ap_sub));
        card.setContentDescription(label+", "+value+". "+detail);
        card.setFocusable(true);
    }
    private List<JSONObject> rows() {
        JSONArray all=data.optJSONArray("apartments");
        List<JSONObject> out=new ArrayList<>();
        if(all==null)return out;
        for(int i=0; i<all.length(); i++) {
            JSONObject a=all.optJSONObject(i);
            if(a!=null&&TeamDashboard.matches(query,str(a,"name"),str(a,"city"),str(a,"adminName"),str(a,"adminMobile")))out.add(a);
        }
        out.sort((x,y)->TeamDashboard.compare(sort,str(x,"name"),str(y,"name"),Ui.decimal(x.opt("received")),Ui.decimal(y.opt("received")),str(x,"lastLoginAt"),str(y,"lastLoginAt"),str(x,"subscription"),str(y,"subscription")));
        return out;
    }
    private void fillList(LinearLayout list) {
        list.removeAllViews();
        List<JSONObject> rows=rows();
        if(rows.isEmpty()) {
            ui.empty(list,R.drawable.ic_building,query.isEmpty()?"No apartments yet":"No match","");
            return;
        }
        if(wide())table(list,rows);
        else for(JSONObject a:rows)card(list,a);
    }
    private int statusColor(String state) {
        return "ACTIVE".equals(state)?R.color.ap_green:"TRIAL".equals(state)?R.color.ap_blue:R.color.ap_red;
    }
    private String validity(JSONObject a) {
        String state=str(a,"subscription");
        if("ACTIVE".equals(state)&&!str(a,"paidUntil").isEmpty())return "Paid until "+com.rapolus.apartmentpilotai.data.DateText.day(str(a,"paidUntil"));
        if("TRIAL".equals(state))return "Trial ends "+com.rapolus.apartmentpilotai.data.DateText.day(str(a,"trialEndsAt"));
        return str(a,"paidUntil").isEmpty()?"Not paid yet":"Paid until "+com.rapolus.apartmentpilotai.data.DateText.day(str(a,"paidUntil"));
    }
    private void dial(String mobile) {
        if(mobile==null||!mobile.matches("[0-9]{10}"))return;
        try {
            activity.startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+mobile)));
        } catch(android.content.ActivityNotFoundException noDialer) {
            ui.message("No phone app on this device.",false);
        }
    }
    private void card(LinearLayout list,JSONObject a) {
        LinearLayout c=ui.card(list);
        LinearLayout top=ui.row();
        TextView name=ui.text(str(a,"name"),15,true,R.color.ap_ink);
        name.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
        top.addView(name);
        TextView status=ui.text(TeamDashboard.statusLabel(str(a,"subscription")),11,true,statusColor(str(a,"subscription")));
        status.setLayoutParams(new LinearLayout.LayoutParams(-2,-2));
        top.addView(status);
        c.addView(top);
        c.addView(ui.text(str(a,"city")+" · "+a.optInt("flats")+" flats · "+a.optInt("members")+" members",11,false,R.color.ap_sub));
        ui.gap(c,6);
        String mobile=str(a,"adminMobile");
        ui.kv(c,"Admin",str(a,"adminName").isEmpty()?"No active Admin":str(a,"adminName"));
        if(!mobile.isEmpty()) {
            MaterialButton call=ui.button(c,"Call "+TeamDashboard.spacedMobile(mobile),R.drawable.ic_phone,false,()->dial(mobile));
            call.setTextSize(ui.sp(12));
        }
        ui.kv(c,"Subscription",validity(a));
        ui.kv(c,"Received",Ui.money(a.opt("received")));
        ui.kv(c,"Expected next month",Ui.money(a.opt("expectedNextMonth")));
        ui.kv(c,"Last sign-in",TeamDashboard.lastSeen(str(a,"lastLoginAt"),Instant.now()));
        ui.kv(c,"Joined",com.rapolus.apartmentpilotai.data.DateText.day(str(a,"joinedOn")));
    }
    private static final String[] HEADERS={"Apartment","City","Admin","Contact","Flats","Status","Valid till","Received","Expected","Last sign-in","Joined"};
    private static final int[] WIDTHS={170,100,130,120,60,100,140,100,100,110,100};
    private void table(LinearLayout list,List<JSONObject> rows) {
        HorizontalScrollView scroll=new HorizontalScrollView(activity);
        LinearLayout grid=ui.column();
        grid.setLayoutParams(new FrameLayout.LayoutParams(-2,-2));
        scroll.addView(grid);
        LinearLayout header=tableRow(grid);
        for(int i=0; i<HEADERS.length; i++)cell(header,HEADERS[i],i,true,R.color.ap_muted,null);
        Instant now=Instant.now();
        for(JSONObject a:rows) {
            View line=new View(activity);
            line.setBackgroundColor(ui.color(R.color.ap_line));
            grid.addView(line,new LinearLayout.LayoutParams(-1,ui.dp(1)));
            LinearLayout r=tableRow(grid);
            String mobile=str(a,"adminMobile"),state=str(a,"subscription");
            cell(r,str(a,"name"),0,true,R.color.ap_ink,null);
            cell(r,str(a,"city"),1,false,R.color.ap_sub,null);
            cell(r,str(a,"adminName").isEmpty()?"No active Admin":str(a,"adminName"),2,false,R.color.ap_ink,null);
            cell(r,TeamDashboard.spacedMobile(mobile),3,true,R.color.ap_pine,mobile.isEmpty()?null:()->dial(mobile));
            cell(r,String.valueOf(a.optInt("flats")),4,false,R.color.ap_ink,null);
            cell(r,TeamDashboard.statusLabel(state),5,true,statusColor(state),null);
            cell(r,validity(a),6,false,R.color.ap_sub,null);
            cell(r,Ui.money(a.opt("received")),7,false,R.color.ap_ink,null);
            cell(r,Ui.money(a.opt("expectedNextMonth")),8,false,R.color.ap_ink,null);
            cell(r,TeamDashboard.lastSeen(str(a,"lastLoginAt"),now),9,false,R.color.ap_sub,null);
            cell(r,com.rapolus.apartmentpilotai.data.DateText.day(str(a,"joinedOn")),10,false,R.color.ap_sub,null);
        }
        LinearLayout card=ui.card(list);
        card.setPadding(ui.dp(8),ui.dp(8),ui.dp(8),ui.dp(8));
        card.addView(scroll,new LinearLayout.LayoutParams(-1,-2));
    }
    private LinearLayout tableRow(LinearLayout grid) {
        LinearLayout r=new LinearLayout(activity);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        grid.addView(r,new LinearLayout.LayoutParams(-2,-2));
        return r;
    }
    private void cell(LinearLayout row,String value,int column,boolean bold,int color,Runnable action) {
        TextView t=ui.text(value,12,bold,color);
        t.setPadding(ui.dp(6),ui.dp(10),ui.dp(6),ui.dp(10));
        t.setLayoutParams(new LinearLayout.LayoutParams(ui.dp(WIDTHS[column]),-2));
        if(action!=null) {
            t.setOnClickListener(v->action.run());
            t.setContentDescription("Call "+value);
            t.setMinHeight(ui.dp(48));
        }
        row.addView(t);
    }
}
