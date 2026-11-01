package com.rapolus.apartmentpilotai.ui;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.rapolus.apartmentpilotai.R;
import org.json.*;
import java.time.*;
import java.util.*;
/**
 * Guided setup wizard. The server owns progress (GET /setup) and re-checks every step, so this screen only
 * shows one step at a time, saves through the existing endpoints, then asks the server to mark the step done.
 */
public final class SetupScreens {
    private static final int TEXT=InputType.TYPE_CLASS_TEXT,NUM=InputType.TYPE_CLASS_NUMBER,MONEY=InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL;
    static final String PLAY_LINK="https://play.google.com/store/apps/details?id=com.rapolus.apartmentpilotai";
    private final FeatureHost h;
    private final Ui u;
    private LinearLayout b;
    public SetupScreens(FeatureHost host) {
        h=host;
        u=h.components();
    }
    public boolean render(String route) {
        b=h.body();
        switch(route) {
            case "setup":
                wizard();
                return true;
            case "setup-members":
                members();
                return true;
            default:
                return false;
        }
    }
    // ---------- shared helpers ----------
    private static JSONObject obj(Object...args) {
        JSONObject j=new JSONObject();
        try {
            for(int i=0; i<args.length; i+=2)j.put(args[i].toString(),args[i+1]);
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return j;
    }
    private void get(String path,FeatureHost.Reply reply) {
        h.requestApi("GET",path,null,reply);
    }
    private void post(String path,JSONObject body,FeatureHost.Reply reply) {
        h.requestApi("POST",path,body,reply);
    }
    private JSONObject command(JSONObject body) {
        try {
            body.put("requestKey",h.commandKey());
        } catch(JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return body;
    }
    /** Tells the server the step is done; the server re-checks the real data before accepting it. */
    private void complete(String key) {
        post("/setup/steps/"+key+"/complete",new JSONObject(),r-> {
            h.resetCommandKey();
            h.refreshPage();
        });
    }
    private void safe(Runnable fn) {
        try {
            fn.run();
        } catch(Exception e) {
            h.problem(e.getMessage()==null?"Check the entered values.":e.getMessage());
        }
    }
    private com.google.android.material.button.MaterialButton primary(LinearLayout parent,String label,Runnable action) {
        return u.button(parent,label,R.drawable.ic_arrow_right,true,()->safe(action));
    }
    private com.google.android.material.button.MaterialButton secondary(LinearLayout parent,String label,int icon,Runnable action) {
        return u.button(parent,label,icon,false,()->safe(action));
    }
    private GradientDrawable round(int color,float radiusDp) {
        GradientDrawable d=new GradientDrawable();
        d.setColor(u.color(color));
        d.setCornerRadius(u.dp(radiusDp));
        return d;
    }
    /** A row of tap-to-pick chips; returns a holder whose value changes on tap. */
    private String[] chips(LinearLayout parent,String label,String[] labels,String[] values,String selected,Runnable changed) {
        String[] holder= {
            selected
        };
        if(label!=null&&!label.isEmpty())parent.addView(u.text(label,12,true,R.color.ap_sub));
        u.gap(parent,6);
        HorizontalScrollView scroll=new HorizontalScrollView(u.c);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row=u.row();
        row.setLayoutParams(new FrameLayout.LayoutParams(-2,-2));
        List<TextView> all=new ArrayList<>();
        for(int i=0; i<labels.length; i++) {
            TextView chip=u.text(labels[i],13,true,R.color.ap_ink);
            chip.setPadding(u.dp(16),u.dp(10),u.dp(16),u.dp(10));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);
            lp.rightMargin=u.dp(8);
            chip.setLayoutParams(lp);
            chip.setMinHeight(u.dp(44));
            chip.setGravity(Gravity.CENTER);
            final String value=values[i];
            chip.setContentDescription(labels[i]);
            chip.setOnClickListener(v-> {
                holder[0]=value;
                for(int k=0; k<all.size(); k++)styleChip(all.get(k),values[k].equals(holder[0]));
                if(changed!=null)changed.run();
            });
            styleChip(chip,value.equals(selected));
            all.add(chip);
            row.addView(chip);
        }
        scroll.addView(row);
        parent.addView(scroll);
        u.gap(parent,14);
        return holder;
    }
    private void styleChip(TextView chip,boolean on) {
        GradientDrawable d=round(on?R.color.ap_pine:R.color.ap_soft,22);
        if(!on)d.setStroke(u.dp(1),u.color(R.color.ap_line));
        chip.setBackground(d);
        chip.setTextColor(u.color(on?R.color.ap_bg:R.color.ap_ink));
        chip.setSelected(on);
    }
    /** A big +/- number picker (no typing). */
    private int[] stepper(LinearLayout parent,String label,int value,int min,int max,Runnable changed) {
        int[] holder= {
            value
        };
        LinearLayout row=u.row();
        TextView title=u.text(label,13,true,R.color.ap_ink);
        row.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        TextView minus=u.text("−",22,true,R.color.ap_ink),plus=u.text("+",22,true,R.color.ap_ink),shown=u.text(String.valueOf(value),18,true,R.color.ap_ink);
        for(TextView t:new TextView[] {
            minus,plus
        }) {
            t.setGravity(Gravity.CENTER);
            t.setBackground(round(R.color.ap_soft,22));
        }
        minus.setContentDescription("Less "+label);
        plus.setContentDescription("More "+label);
        shown.setGravity(Gravity.CENTER);
        row.addView(minus,new LinearLayout.LayoutParams(u.dp(48),u.dp(48)));
        row.addView(shown,new LinearLayout.LayoutParams(u.dp(56),-2));
        row.addView(plus,new LinearLayout.LayoutParams(u.dp(48),u.dp(48)));
        minus.setOnClickListener(v-> {
            if(holder[0]>min) {
                holder[0]--;
                shown.setText(String.valueOf(holder[0]));
                if(changed!=null)changed.run();
            }
        });
        plus.setOnClickListener(v-> {
            if(holder[0]<max) {
                holder[0]++;
                shown.setText(String.valueOf(holder[0]));
                if(changed!=null)changed.run();
            }
        });
        parent.addView(row);
        u.gap(parent,12);
        return holder;
    }
    private static String text(JSONObject j,String key) {
        return j==null||j.isNull(key)?"":j.optString(key);
    }
    // ---------- the wizard frame ----------
    private void wizard() {
        h.pageTitle("Setup");
        get("/setup",r-> {
            JSONObject s=(JSONObject)r;
            if(s.optBoolean("requiredComplete")) {
                finished(s);
                return;
            }
            JSONArray steps=s.getJSONArray("steps");
            String current=s.optString("current");
            int index=0;
            JSONObject step=null;
            for(int i=0; i<steps.length(); i++)if(steps.getJSONObject(i).optString("key").equals(current)) {
                index=i;
                step=steps.getJSONObject(i);
            }
            if(step==null)return;
            header(steps,index,s.optInt("done"),s.optInt("total"),s.optInt("percent"));
            b.addView(u.text(step.optString("title"),22,true,R.color.ap_ink));
            u.gap(b,8);
            LinearLayout why=u.column();
            why.setPadding(u.dp(14),u.dp(12),u.dp(14),u.dp(12));
            why.setBackground(round(R.color.ap_mint,14));
            why.addView(u.text("Why this matters",11,true,R.color.ap_green));
            u.gap(why,3);
            why.addView(u.text(step.optString("why"),13,false,R.color.ap_ink));
            b.addView(why);
            u.gap(b,16);
            step(current);
            u.gap(b,22);
            TextView out=u.text("Sign out and continue later",12,false,R.color.ap_muted);
            out.setGravity(Gravity.CENTER);
            out.setMinHeight(u.dp(44));
            out.setOnClickListener(v->h.exitSession());
            b.addView(out);
        });
    }
    private void header(JSONArray steps,int index,int done,int total,int percent)throws JSONException {
        String line="Step "+(index+1)+" of "+total+" · "+percent+"% complete";
        b.addView(u.text(line,12,true,R.color.ap_sub));
        u.gap(b,8);
        ProgressBar bar=new ProgressBar(u.c,null,android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setProgress(Math.max(4,percent));
        bar.setProgressTintList(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_pine)));
        bar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(u.color(R.color.ap_line)));
        bar.setContentDescription(line);
        b.addView(bar,new LinearLayout.LayoutParams(-1,u.dp(10)));
        u.gap(b,12);
        // Numbered circles: done (tick), current (highlighted), still to do (grey).
        HorizontalScrollView scroll=new HorizontalScrollView(u.c);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout dots=u.row();
        dots.setLayoutParams(new FrameLayout.LayoutParams(-2,-2));
        for(int i=0; i<steps.length(); i++) {
            JSONObject st=steps.getJSONObject(i);
            boolean ok=st.optBoolean("done"),now=i==index;
            TextView dot=u.text(ok?"✓":String.valueOf(i+1),12,true,ok||now?R.color.ap_bg:R.color.ap_muted);
            dot.setGravity(Gravity.CENTER);
            GradientDrawable d=new GradientDrawable();
            d.setShape(GradientDrawable.OVAL);
            d.setColor(u.color(ok?R.color.ap_green:now?R.color.ap_pine:R.color.ap_soft));
            if(now)d.setStroke(u.dp(3),u.color(R.color.ap_lime));
            dot.setBackground(d);
            dot.setContentDescription(st.optString("title")+(ok?", done":now?", current step":", to do"));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(u.dp(now?34:28),u.dp(now?34:28));
            lp.rightMargin=u.dp(6);
            dots.addView(dot,lp);
        }
        scroll.addView(dots);
        b.addView(scroll);
        u.gap(b,18);
    }
    private void finished(JSONObject s) {
        h.pageTitle("All set");
        u.state(b,R.drawable.ic_check,"You're all set","Setup is complete. Your dashboard is ready.",R.color.ap_green,R.color.ap_mint);
        u.button(b,"Open my dashboard",R.drawable.ic_home,true,()->h.openPageWithBackTarget("home","","home"));
    }
    private void step(String key) {
        switch(key) {
            case "apartment":apartment();
            break;
            case "blocks":blocks();
            break;
            case "flats":flats();
            break;
            case "payment":payment(false);
            break;
            case "maintenance":maintenance();
            break;
            case "late_fee":lateFee("late_fee",false);
            break;
            case "opening_balance":openingBalance();
            break;
            case "old_dues":yesNoScreen("old_dues","Does any flat owe money from before?","Flats that owed maintenance before you started using the app.","No old dues","Yes, add old dues","opening-dues");
            break;
            case "invite":invite();
            break;
            case "terms":terms();
            break;
            case "treasurer_rules":treasurerRules();
            break;
            case "payment_check":payment(true);
            break;
            case "late_fee_check":lateFee("late_fee_check",true);
            break;
            case "recurring":yesNoScreen("recurring","Any regular monthly expenses?","For example watchman salary, electricity or cleaning. You can add them now or later.","None for now","Add a regular expense","recurring-form");
            break;
            case "profile":profile();
            break;
            case "vehicles":yesNoScreen("vehicles","Do you have a vehicle?","Add the number so parking is easy and neighbours can reach you about it.","No vehicle","Add my vehicle","vehicle-form");
            break;
            case "how_to_pay":howToPay();
            break;
            case "stay_informed":stayInformed();
            break;
            default:
                b.addView(u.text("Update the app to continue this step.",13,false,R.color.ap_sub));
        }
    }
    // ---------- Admin steps ----------
    private void apartment() {
        get("/ops/apartment",r-> {
            JSONObject a=(JSONObject)r;
            LinearLayout c=u.card(b);
            Ui.Fields f=new Ui.Fields(u,c);
            f.field("name","Apartment name",text(a,"name"),TEXT);
            f.field("city","City",text(a,"city"),TEXT);
            f.field("address","Address",text(a,"address"),TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            f.field("pincode","PIN code",text(a,"pincode"),NUM);
            f.limit("name",80);
            f.limit("city",80);
            f.limit("address",300);
            f.limit("pincode",6);
            primary(b,"Save & continue",()-> {
                if(f.get("name").isEmpty()||f.get("city").isEmpty())throw new IllegalArgumentException("Enter the apartment name and city.");
                if(f.get("address").isEmpty())throw new IllegalArgumentException("Enter the address.");
                if(!f.get("pincode").matches("[0-9]{6}"))throw new IllegalArgumentException("Enter a 6-digit PIN code.");
                post("/ops/apartment",obj("name",f.get("name"),"city",f.get("city"),"address",f.get("address"),"pincode",f.get("pincode")),x->complete("apartment"));
            });
        });
    }
    private void blocks() {
        get("/ops/blocks",r-> {
            JSONArray existing=(JSONArray)r;
            JSONObject draft=h.pageDraft("setup-blocks");
            int start=draft.optInt("count",Math.max(1,existing.length()));
            LinearLayout c=u.card(b);
            c.addView(u.text("How many buildings or blocks?",15,true,R.color.ap_ink));
            u.gap(c,10);
            LinearLayout names=u.column();
            List<EditText> inputs=new ArrayList<>();
            Runnable[] redraw=new Runnable[1];
            String[] count=chips(c,null,new String[] {
                "1","2","3","4","5","6"
            }
            ,new String[] {
                "1","2","3","4","5","6"
            }
            ,String.valueOf(Math.min(6,start)),()->redraw[0].run());
            c.addView(names);
            Runnable draw=()-> {
                names.removeAllViews();
                inputs.clear();
                int n=Integer.parseInt(count[0]);
                if(n>1)names.addView(u.text("Block names (tap to change)",12,true,R.color.ap_sub));
                u.gap(names,6);
                Ui.Fields f=new Ui.Fields(u,names);
                for(int i=0; i<n; i++) {
                    String fallback=i<existing.length()?existing.optJSONObject(i).optString("name"):String.valueOf((char)('A'+i));
                    String value=draft.optJSONArray("names")!=null&&i<draft.optJSONArray("names").length()?draft.optJSONArray("names").optString(i):fallback;
                    EditText e=f.field("block"+i,n==1?"Building name (optional)":"Block "+(i+1),value,TEXT);
                    e.setFilters(new android.text.InputFilter[] {
                        new android.text.InputFilter.LengthFilter(20)
                    });
                    inputs.add(e);
                }
            };
            redraw[0]=draw;
            draw.run();
            primary(b,"Save & continue",()-> {
                JSONArray chosen=new JSONArray();
                Set<String> seen=new HashSet<>();
                for(EditText e:inputs) {
                    String name=e.getText().toString().trim().toUpperCase(Locale.ROOT);
                    if(name.isEmpty())name=String.valueOf((char)('A'+chosen.length()));
                    if(!name.matches("[A-Z0-9][A-Z0-9 -]{0,19}"))throw new IllegalArgumentException("Block names use letters and numbers only.");
                    if(!seen.add(name))throw new IllegalArgumentException("Each block needs a different name.");
                    chosen.put(name);
                }
                h.savePageDraft("setup-blocks",obj("count",chosen.length(),"names",chosen));
                Set<String> have=new HashSet<>();
                for(int i=0; i<existing.length(); i++)have.add(existing.optJSONObject(i).optString("name"));
                createBlocks(chosen,0,have);
            });
        });
    }
    private void createBlocks(JSONArray names,int i,Set<String> have) {
        if(i>=names.length()) {
            complete("blocks");
            return;
        }
        String name=names.optString(i);
        if(have.contains(name)) {
            createBlocks(names,i+1,have);
            return;
        }
        post("/ops/blocks",command(obj("name",name)),x-> {
            h.resetCommandKey();
            createBlocks(names,i+1,have);
        });
    }
    private void payment(boolean check) {
        get("/ops/settings",r-> {
            JSONObject s=(JSONObject)r;
            boolean ready=!text(s,"payee").isEmpty()&&(!text(s,"upi").isEmpty()||(!text(s,"account").isEmpty()&&!text(s,"ifsc").isEmpty()));
            boolean cashOnly=text(s,"upi").isEmpty()&&text(s,"account").isEmpty();
            String key=check?"payment_check":"payment";
            if(check&&cashOnly&&!h.pageDraft("setup-payment").optBoolean("edit")) {
                LinearLayout c=u.card(b);
                c.addView(u.text("Residents pay in cash",15,true,R.color.ap_ink));
                u.gap(c,4);
                c.addView(u.text("No UPI ID or bank account has been added yet. Residents pay the Admin or Treasurer in cash, and it is recorded in the app.",12,false,R.color.ap_sub));
                primary(b,"That's right",()->complete(key));
                secondary(b,"Add UPI or bank details",R.drawable.ic_settings,()-> {
                    h.savePageDraft("setup-payment",obj("edit",true));
                    h.refreshPage();
                });
                return;
            }
            if(check&&ready&&!h.pageDraft("setup-payment").optBoolean("edit")) {
                LinearLayout c=u.card(b);
                c.addView(u.text("Residents send money to:",12,true,R.color.ap_sub));
                u.gap(c,8);
                u.kv(c,"Payee",text(s,"payee"));
                if(!text(s,"upi").isEmpty())u.kv(c,"UPI ID",text(s,"upi"));
                if(!text(s,"account").isEmpty())u.kv(c,"Bank account",text(s,"bank")+" · "+text(s,"account")+" · "+text(s,"ifsc"));
                primary(b,"These are correct",()->complete("payment_check"));
                secondary(b,"Something is wrong. Fix it",R.drawable.ic_settings,()-> {
                    h.savePageDraft("setup-payment",obj("edit",true));
                    h.refreshPage();
                });
                return;
            }
            LinearLayout cash=u.card(b);
            cash.addView(u.text("Collecting cash?",15,true,R.color.ap_ink));
            u.gap(cash,4);
            cash.addView(u.text("Cash works without any details. Add UPI or a bank account now or later in Settings, and residents will see them.",12,false,R.color.ap_sub));
            secondary(b,"Cash only for now · add UPI or bank later",R.drawable.ic_wallet,()-> {
                h.savePageDraft("setup-payment",new JSONObject());
                complete(key);
            });
            LinearLayout c=u.card(b);
            c.addView(u.text("UPI or bank (optional)",15,true,R.color.ap_ink));
            u.gap(c,10);
            Ui.Fields f=new Ui.Fields(u,c);
            f.field("payee","Account name (association)",text(s,"payee"),TEXT);
            f.field("upi","UPI ID (for example name@okbank)",text(s,"upi"),TEXT);
            c.addView(u.text("Or a bank account",12,true,R.color.ap_sub));
            u.gap(c,6);
            f.field("bank","Bank name",text(s,"bank"),TEXT);
            f.field("account","Account number",text(s,"account"),NUM);
            f.field("ifsc","IFSC",text(s,"ifsc"),TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            f.limit("payee",100);
            f.limit("upi",100);
            f.limit("bank",100);
            f.limit("account",30);
            f.limit("ifsc",11);
            u.note(b,"Fill the UPI ID, or the bank account with IFSC, or both. Leave everything empty for cash only.");
            primary(b,"Save & continue",()-> {
                String upi=f.get("upi"),account=f.get("account"),ifsc=f.get("ifsc").toUpperCase(Locale.ROOT);
                if(f.get("payee").isEmpty()&&upi.isEmpty()&&account.isEmpty()&&ifsc.isEmpty()&&f.get("bank").isEmpty()) {
                    h.savePageDraft("setup-payment",new JSONObject());
                    complete(key);
                    return;
                }
                if(f.get("payee").isEmpty())throw new IllegalArgumentException("Enter the account name for the UPI or bank details.");
                if(upi.isEmpty()&&(account.isEmpty()||ifsc.isEmpty()))throw new IllegalArgumentException("Add a UPI ID, or a bank account with IFSC.");
                if(!upi.isEmpty()&&!upi.matches("[A-Za-z0-9._-]{2,}@[A-Za-z0-9.-]{2,}"))throw new IllegalArgumentException("Check the UPI ID, for example name@okbank.");
                if(!ifsc.isEmpty()&&!ifsc.matches("[A-Z]{4}0[A-Z0-9]{6}"))throw new IllegalArgumentException("Check the IFSC, for example HDFC0001234.");
                post("/ops/settings/payment",command(obj("payee",f.get("payee"),"upi",upi,"bank",f.get("bank"),"account",account,"ifsc",ifsc)),x-> {
                    h.savePageDraft("setup-payment",new JSONObject());
                    complete(key);
                });
            });
        });
    }
    private void maintenance() {
        get("/billing/rules",r-> {
            JSONArray rules=(JSONArray)r;
            if(rules.length()>0&&!h.pageDraft("setup-maintenance").optBoolean("edit")) {
                JSONObject rule=rules.getJSONObject(rules.length()-1);
                LinearLayout c=u.card(b);
                u.kv(c,"Amount per flat",Ui.money(rule.opt("amount")));
                u.kv(c,"Due day","Day "+rule.optInt("dueDay")+" of every month");
                u.kv(c,"Starts",text(rule,"effectiveMonth"));
                primary(b,"Keep this & continue",()->complete("maintenance"));
                secondary(b,"Change the amount",R.drawable.ic_settings,()-> {
                    h.savePageDraft("setup-maintenance",obj("edit",true));
                    h.refreshPage();
                });
                return;
            }
            LinearLayout c=u.card(b);
            c.addView(u.text("How much does each flat pay every month?",15,true,R.color.ap_ink));
            u.gap(c,10);
            Ui.Fields f=new Ui.Fields(u,c);
            f.field("amount","Amount per flat (₹)","",MONEY);
            f.limit("amount",9);
            String[] due=chips(c,"Pay by which day of the month?",new String[] {
                "5th","10th","15th","20th"
            }
            ,new String[] {
                "5","10","15","20"
            }
            ,"10",null);
            YearMonth now=YearMonth.now(ZoneId.of("Asia/Kolkata"));
            String[] month=chips(c,"Start from",new String[] {
                "This month","Next month"
            }
            ,new String[] {
                now.toString(),now.plusMonths(1).toString()
            }
            ,now.toString(),null);
            u.note(b,"Bills already sent are never changed later. You can set different amounts for single flats from Money later.");
            primary(b,"Save & continue",()-> {
                java.math.BigDecimal amount;
                try {
                    amount=new java.math.BigDecimal(f.get("amount"));
                } catch(Exception e) {
                    throw new IllegalArgumentException("Enter the monthly amount, for example 2500.");
                }
                if(amount.signum()<=0)throw new IllegalArgumentException("Enter an amount greater than zero.");
                post("/billing/rules",obj("amount",amount,"effective",month[0],"billingDay",1,"dueDay",Integer.parseInt(due[0])),x-> {
                    h.savePageDraft("setup-maintenance",new JSONObject());
                    complete("maintenance");
                });
            });
        });
    }
    private void lateFee(String key,boolean check) {
        get("/ops/settings",r-> {
            JSONObject s=(JSONObject)r;
            if(check&&!h.pageDraft("setup-late").optBoolean("edit")) {
                LinearLayout c=u.card(b);
                u.kv(c,"Late fee",s.optBoolean("lateEnabled")?Ui.money(s.opt("lateFee"))+" after "+s.optInt("graceDays")+" days":"No late fee");
                u.kv(c,"Reminders",s.optBoolean("reminders")?"On":"Off");
                primary(b,"Looks right",()->complete(key));
                secondary(b,"Change",R.drawable.ic_settings,()-> {
                    h.savePageDraft("setup-late",obj("edit",true));
                    h.refreshPage();
                });
                return;
            }
            LinearLayout c=u.card(b);
            c.addView(u.text("Charge a late fee? (optional)",15,true,R.color.ap_ink));
            u.gap(c,10);
            LinearLayout extra=u.column();
            Ui.Fields f=new Ui.Fields(u,extra);
            f.field("lateFee","Late fee amount (₹)",s.optBoolean("lateEnabled")?text(s,"lateFee"):"",MONEY);
            f.field("graceDays","Days after the due date",String.valueOf(Math.max(0,s.optInt("graceDays",5))),NUM);
            f.limit("graceDays",2);
            String[][] lateHolder=new String[1][];
            lateHolder[0]=chips(c,null,new String[] {
                "No late fee (recommended)","Add a late fee"
            }
            ,new String[] {
                "no","yes"
            }
            ,s.optBoolean("lateEnabled")?"yes":"no",()->extra.setVisibility("yes".equals(lateHolder[0][0])?View.VISIBLE:View.GONE));
            String[] late=lateHolder[0];
            c.addView(extra);
            extra.setVisibility(s.optBoolean("lateEnabled")?View.VISIBLE:View.GONE);
            LinearLayout rc=u.card(b);
            rc.addView(u.text("Reminders",15,true,R.color.ap_ink));
            u.gap(rc,6);
            Ui.Fields rf=new Ui.Fields(u,rc);
            rf.check("reminders","Remind residents before the due date",!s.has("reminders")||s.optBoolean("reminders"));
            rf.check("dueNotify","Tell residents when a new bill is ready",!s.has("dueNotify")||s.optBoolean("dueNotify"));
            primary(b,"Save & continue",()-> {
                boolean on="yes".equals(late[0]);
                Object fee=0;
                int grace=0;
                if(on) {
                    try {
                        fee=new java.math.BigDecimal(f.get("lateFee"));
                        grace=Integer.parseInt(f.get("graceDays"));
                    } catch(Exception e) {
                        throw new IllegalArgumentException("Enter the late fee amount and the number of days.");
                    }
                    if(((java.math.BigDecimal)fee).signum()<=0)throw new IllegalArgumentException("Enter a late fee greater than zero.");
                    if(grace<0||grace>30)throw new IllegalArgumentException("Days must be between 0 and 30.");
                }
                JSONObject billing=command(obj("billVacant",!s.has("billVacant")||s.optBoolean("billVacant"),"lateEnabled",on,"lateFee",fee,"graceDays",grace));
                post("/ops/settings/billing",billing,x-> {
                    h.resetCommandKey();
                    post("/ops/settings/reminders",command(obj("reminders",rf.checked("reminders"),"dueNotify",rf.checked("dueNotify"))),y-> {
                        h.savePageDraft("setup-late",new JSONObject());
                        complete(key);
                    });
                });
            });
        });
    }
    private void openingBalance() {
        get("/ops/settings",r-> {
            JSONObject s=(JSONObject)r;
            if(!text(s,"openingDate").isEmpty()) {
                LinearLayout c=u.card(b);
                u.kv(c,"Existing balance",Ui.money(s.opt("openingAmount"))+" on "+text(s,"openingDate"));
                primary(b,"Continue",()->complete("opening_balance"));
                return;
            }
            LinearLayout c=u.card(b);
            c.addView(u.text("How much money does the association have today?",15,true,R.color.ap_ink));
            u.gap(c,4);
            c.addView(u.text("Cash in hand plus the bank balance. Reports and the cashbook start from this amount.",12,false,R.color.ap_sub));
            u.gap(c,12);
            Ui.Fields f=new Ui.Fields(u,c);
            f.field("amount","Amount (₹)","",MONEY);
            f.limit("amount",12);
            String date=YearMonth.now(ZoneId.of("Asia/Kolkata")).atDay(1).toString();
            u.note(b,"This is counted from "+date+". It can be set only once; later corrections are recorded as income or expense.");
            primary(b,"Save & continue",()-> {
                java.math.BigDecimal amount;
                try {
                    amount=new java.math.BigDecimal(f.get("amount"));
                } catch(Exception e) {
                    throw new IllegalArgumentException("Enter the amount, or tap Starting from ₹0.");
                }
                if(amount.signum()<0)throw new IllegalArgumentException("The amount cannot be negative.");
                post("/ops/opening",obj("date",date,"amount",amount),x->complete("opening_balance"));
            });
            secondary(b,"Starting from ₹0",R.drawable.ic_wallet,()->post("/ops/opening",obj("date",date,"amount",0),x->complete("opening_balance")));
        });
    }
    /** "No" completes the step; "Yes" opens the real screen and comes back here with a Done button. */
    private void yesNoScreen(String key,String question,String detail,String no,String yes,String route) {
        LinearLayout c=u.card(b);
        c.addView(u.text(question,16,true,R.color.ap_ink));
        u.gap(c,4);
        c.addView(u.text(detail,12,false,R.color.ap_sub));
        boolean visited=h.pageDraft("setup-visit").optString("key").equals(key);
        if(visited) {
            u.gap(b,6);
            u.note(b,"Added what you need? Tap Done. You can always add more later.");
            primary(b,"Done, continue",()-> {
                h.savePageDraft("setup-visit",new JSONObject());
                complete(key);
            });
            secondary(b,yes+" (another)",R.drawable.ic_plus,()->open(key,route));
            return;
        }
        primary(b,no,()->complete(key));
        secondary(b,yes,R.drawable.ic_plus,()->open(key,route));
    }
    private void open(String key,String route) {
        h.savePageDraft("setup-visit",obj("key",key));
        h.openPageWithBackTarget(route,"","setup");
    }
    private void invite() {
        get("/invites/current",r-> {
            JSONObject inv=(JSONObject)r;
            if(!inv.optBoolean("valid")||!inv.optBoolean("codeAvailable")) {
                LinearLayout c=u.card(b);
                c.addView(u.text("Create your apartment's invite code",15,true,R.color.ap_ink));
                u.gap(c,4);
                c.addView(u.text("Residents enter this code in the app to join. You approve each one.",12,false,R.color.ap_sub));
                primary(b,"Create invite code",()->h.requestApi("POST","/invites",command(new JSONObject()),x-> {
                    h.resetCommandKey();
                    String until=LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(30).toString();
                    h.requestApi("PUT","/invites/current/expiry",obj("expiry",until),y->h.refreshPage());
                }));
                return;
            }
            String code=inv.optString("code");
            LinearLayout box=u.column();
            box.setPadding(u.dp(18),u.dp(18),u.dp(18),u.dp(18));
            box.setBackground(round(R.color.ap_pine,18));
            TextView label=u.text("Invite code",11,true,R.color.ap_bg);
            box.addView(label);
            TextView big=u.text(code,26,true,R.color.ap_bg);
            big.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
            big.setTextIsSelectable(true);
            box.addView(big);
            box.addView(u.text("Valid until "+inv.optString("expiresOn"),11,false,R.color.ap_bg));
            b.addView(box);
            u.gap(b,12);
            String apartment=h.client().account()==null?"our apartment":h.client().account().optString("apartmentName","our apartment");
            String message="Join "+apartment+" in ApartmentCare.\n1. Install the app: "+PLAY_LINK+"\n2. Tap Join with invite and enter code: "+code+"\n3. Choose your flat. The Admin will approve you.\nCode valid until "+inv.optString("expiresOn")+".";
            primary(b,"Share with residents",()->h.activity().startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,message),"Share invite")));
            u.note(b,"Tip: share it in your apartment WhatsApp group.");
            secondary(b,"I've shared it. Continue",R.drawable.ic_check,()->complete("invite"));
        });
    }
    // ---------- Treasurer and resident steps ----------
    private void terms() {
        LinearLayout c=u.card(b);
        String[][] points= {
            {"Your details","Your name, mobile number and flat are shared only with your apartment's Admin and Treasurer."},
            {"Money","The app does not move money. You pay the association directly; the Treasurer confirms it."},
            {"Your choice","You can delete your account anytime from More → Profile."}
        };
        for(String[] p:points) {
            c.addView(u.text(p[0],13,true,R.color.ap_ink));
            c.addView(u.text(p[1],12,false,R.color.ap_sub));
            u.gap(c,10);
        }
        Ui.Fields f=new Ui.Fields(u,c);
        f.check("agree","I agree to the Terms and Privacy Policy",false);
        primary(b,"Agree & continue",()-> {
            if(!f.checked("agree"))throw new IllegalArgumentException("Tick the box to agree.");
            complete("terms");
        });
    }
    private void treasurerRules() {
        String[][] rules= {
            {"Two people handle money","You can never approve a payment you recorded yourself. Another Admin or Treasurer approves it."},
            {"Always check the statement","Before approving, match the amount and reference with the bank or UPI statement."},
            {"Never delete, always correct","Mistakes are fixed with a reversal and a reason, so the books stay honest."}
        };
        for(String[] r:rules) {
            LinearLayout c=u.card(b);
            c.addView(u.text(r[0],14,true,R.color.ap_ink));
            u.gap(c,3);
            c.addView(u.text(r[1],12,false,R.color.ap_sub));
        }
        primary(b,"I understand",()->complete("treasurer_rules"));
    }
    private void profile() {
        get("/me",r-> {
            JSONObject me=(JSONObject)r;
            LinearLayout c=u.card(b);
            Ui.Fields f=new Ui.Fields(u,c);
            f.field("name","Your name",text(me,"name"),TEXT);
            f.field("email","Email (optional)",text(me,"email"),InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            f.limit("name",80);
            f.limit("email",120);
            JSONObject account=h.client().account();
            if(account!=null)u.kv(c,"Apartment",account.optString("apartmentName"));
            u.kv(c,"Mobile",text(me,"mobile"));
            primary(b,"Save & continue",()-> {
                if(f.get("name").isEmpty())throw new IllegalArgumentException("Enter your name.");
                post("/ops/profile",obj("name",f.get("name"),"email",f.get("email")),x->complete("profile"));
            });
        });
    }
    private void howToPay() {
        get("/ops/settings",r-> {
            JSONObject s=(JSONObject)r;
            String[][] steps= {
                {"1  See your bill","Open Dues at the bottom. Each month's bill shows the amount and due date."},
                {"2  Pay the association","Pay in cash to your Admin or Treasurer, or use the UPI or bank details below. The app never takes money."},
                {"3  Tap \"I've paid\"","Enter the amount and reference. You get a receipt once the Treasurer confirms it."}
            };
            for(String[] st:steps) {
                LinearLayout c=u.card(b);
                c.addView(u.text(st[0],14,true,R.color.ap_ink));
                u.gap(c,3);
                c.addView(u.text(st[1],12,false,R.color.ap_sub));
            }
            LinearLayout pay=u.card(b);
            pay.addView(u.text("Association payment details",12,true,R.color.ap_sub));
            u.gap(pay,6);
            if(text(s,"upi").isEmpty()&&text(s,"account").isEmpty())pay.addView(u.text("Cash only for now. Pay your Admin or Treasurer, then tap \"I've paid\" and choose Cash.",12,false,R.color.ap_muted));
            else {
                u.kv(pay,"Payee",text(s,"payee"));
                if(!text(s,"upi").isEmpty())u.kv(pay,"UPI ID",text(s,"upi"));
                if(!text(s,"account").isEmpty())u.kv(pay,"Bank",text(s,"bank")+" · "+text(s,"account")+" · "+text(s,"ifsc"));
            }
            primary(b,"Got it",()->complete("how_to_pay"));
        });
    }
    private void stayInformed() {
        String[][] items= {
            {"Notices","Community → Notices. Important news from your Admin."},
            {"Report an issue","Issues → Report. Water, lift, power or anything in the building."},
            {"The bell","Top of Home. Approvals, bills and replies show up here."}
        };
        int[] icons= {
            R.drawable.ic_notice,R.drawable.ic_tools,R.drawable.ic_bell
        };
        LinearLayout c=u.card(b);
        for(int i=0; i<items.length; i++)u.iconRow(c,items[i][0],items[i][1],icons[i],()-> { });
        u.note(b,"Alerts come inside the app. Open it to see what's new.");
        primary(b,"Finish",()->complete("stay_informed"));
    }
    // ---------- Admin: where every flat stands ----------
    private void members() {
        h.pageTitle("Members setup");
        get("/setup/members",r-> {
            JSONArray rows=(JSONArray)r;
            int ready=0,joined=0;
            for(int i=0; i<rows.length(); i++) {
                String st=rows.getJSONObject(i).optString("state");
                if(st.equals("COMPLETE"))ready++;
                if(st.equals("COMPLETE")||st.equals("IN_PROGRESS"))joined++;
            }
            u.hero(b,"Flats ready",ready+" of "+rows.length(),joined+" joined · "+(rows.length()-joined)+" still to join or approve");
            LinearLayout c=u.card(b);
            for(int i=0; i<rows.length(); i++) {
                JSONObject m=rows.getJSONObject(i);
                String st=m.optString("state");
                String label=st.equals("NOT_JOINED")?"Not joined":st.equals("WAITING_APPROVAL")?"Waiting for your approval":st.equals("IN_PROGRESS")?"Setup "+m.optInt("stepsDone")+" of "+m.optInt("stepsTotal"):"Setup complete";
                String who=m.isNull("name")?"—":m.optString("name");
                u.statusIconRow(c,m.optString("label"),label,who,st.equals("COMPLETE")?R.drawable.ic_check:st.equals("NOT_JOINED")?R.drawable.ic_home:R.drawable.ic_user,st.equals("WAITING_APPROVAL")?()->h.openPage("join-requests",""):null);
            }
            u.button(b,"Share the invite again",R.drawable.ic_users,false,()->h.openPage("invite",""));
        });
    }
    // ---------- flat generator ----------
    private void flats() {
        get("/ops/blocks",br-> {
            JSONArray blocks=(JSONArray)br;
            get("/ops/flats",fr-> {
                JSONArray current=(JSONArray)fr;
                new FlatGenerator(h,u,b,blocks,current,this::saveFlats,()->complete("flats")).show();
            });
        });
    }
    private void saveFlats(JSONArray flats) {
        post("/ops/flats/bulk",command(obj("flats",flats)),x-> {
            h.resetCommandKey();
            h.clearPageDraft("setup-flats");
            complete("flats");
        });
    }
}
