package com.rapolus.apartmentpilotai.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.rapolus.apartmentpilotai.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Separate native payment substates; the backend owns visibility and review status. */
public final class PaymentScreens {
    private final FeatureHost h;
    private final Ui u;
    private LinearLayout b;
    private String id;
    public PaymentScreens(FeatureHost host) {h=host;u=h.components();}
    public boolean render(String route,String record) {
        b=h.body();id=record;
        switch(route) {
            case "payment-status":status();break;
            case "history":history();break;
            case "proof":proof();break;
            default:return false;
        }
        return true;
    }
    private void get(String path,FeatureHost.Reply reply) {h.requestApi("GET",path,null,reply);}
    private void go(String route,String record) {h.openPage(route,record);}
    private String text(JSONObject value,String key,String fallback) {return value.isNull(key)?fallback:value.optString(key,fallback);}
    private String date(String value) {
        return com.rapolus.apartmentpilotai.data.DateText.day(value);
    }
    private String period(String value) {
        try{return YearMonth.parse(value.substring(0,7)).format(DateTimeFormatter.ofPattern("MMMM uuuu",Locale.ENGLISH));}
        catch(Exception ignored){return value;}
    }
    private String mode(String value) {return value.equals("BANK_TRANSFER")?"Bank transfer":value.equals("CASH")?"Cash":value.equals("CHEQUE")?"Cheque":value;}
    private void status() {
        h.pageTitle("Payment status");
        if(id.isEmpty()) {u.empty(b,"No payment to track","Open a payment from history.");u.button(b,"Payment history",R.drawable.ic_receipt,true,()->go("history",""));return;}
        get("/payments/"+id,r->{
            JSONObject p=(JSONObject)r;
            String status=p.optBoolean("reversed")?"REVERSED":p.optString("status");
            if(status.equals("APPROVED")) {
                u.state(b,R.drawable.ic_check,"Payment approved",Ui.money(p.opt("amount"))+" · "+p.optString("flatLabel"),R.color.ap_green,R.color.ap_mint);
                u.button(b,"View receipt",R.drawable.ic_receipt,true,()->go("receipt",p.optString("id")));
            } else if(status.equals("REJECTED")) {
                u.state(b,R.drawable.ic_error,"Payment not approved",text(p,"reason","Please check your payment details."),R.color.ap_red,R.color.ap_redbg);
                LinearLayout c=u.card(b);u.kv(c,"Amount",Ui.money(p.opt("amount")));u.kv(c,"Reason",text(p,"reason",""));
                u.button(b,"Resubmit details",R.drawable.ic_rotate,true,()->go("mark-paid",p.optString("billId")));
            } else if(status.equals("REVERSED")) {
                u.state(b,R.drawable.ic_error,"Payment reversed","The original receipt is retained. Check your current bill before paying again.",R.color.ap_amber,R.color.ap_ambg);
                u.button(b,"View original receipt",R.drawable.ic_receipt,false,()->go("receipt",p.optString("id")));
                u.button(b,"View bill",R.drawable.ic_receipt,true,()->go("bill",p.optString("billId")));
            } else {
                u.state(b,R.drawable.ic_clock,"Sent for review",Ui.money(p.opt("amount"))+" · "+p.optString("flatLabel"),R.color.ap_amber,R.color.ap_ambg);
                LinearLayout c=u.card(b);
                u.kv(c,"Mode",mode(p.optString("mode")));u.kv(c,"Paid on",date(p.optString("paidOn")));
                u.kv(c,"Reference",text(p,"reference","Cash declaration"));u.kv(c,"Status","In review");
                LinearLayout timeline=u.card(b);
                u.iconRow(timeline,"Payment details submitted",date(p.optString("submittedAt")),R.drawable.ic_check,null);
                u.iconRow(timeline,"Treasurer verifies payment","Awaiting approval",R.drawable.ic_clock,null);
                u.iconRow(timeline,"Receipt becomes available","After approval",R.drawable.ic_receipt,null);
                u.button(b,"Payment proof",R.drawable.ic_copy,false,()->go("proof",p.optString("id")));
                if(h.isStaff())u.button(b,"Review payment",R.drawable.ic_check,true,()->go("payment",p.optString("id")));
            }
            u.button(b,"Back to maintenance",R.drawable.ic_arrow_left,false,()->go("dues",""));
        });
    }
    private void history() {
        h.pageTitle("Payment history");
        Ui.Fields fields=h.newFields();
        String draftKey=id.startsWith("flat:")?"paymentHistory:"+id:"paymentHistory";
        String previous=h.pageDraft(draftKey).optString("search");
        EditText search=fields.field("search","Search payments",previous,InputType.TYPE_CLASS_TEXT);
        LinearLayout list=u.column();b.addView(list);
        String path=id.startsWith("flat:")?"/payments?flatId="+id.substring(5):"/payments";
        get(path,r->{
            JSONArray rows=(JSONArray)r;
            Runnable display=()->{
                String term=search.getText().toString().trim().toLowerCase(Locale.ROOT);
                list.removeAllViews();int count=0;
                for(int i=0;i<rows.length();i++) {
                    JSONObject p=rows.optJSONObject(i);if(p==null)continue;
                    String month=period(p.optString("billingMonth"));
                    String searchable=p.optString("flatLabel")+" "+text(p,"reference","")+" "+month;
                    if(!searchable.toLowerCase(Locale.ROOT).contains(term))continue;
                    count++;
                    String state=p.optBoolean("reversed")?"Reversed":p.optString("status").equals("APPROVED")?"Paid":p.optString("status").equals("REJECTED")?"Rejected":"In review";
                    String label=h.isStaff()?p.optString("flatLabel")+" · "+Ui.money(p.opt("amount")):month+" · "+Ui.money(p.opt("amount"));
                    String target=p.optString("status").equals("APPROVED")?"receipt":h.isStaff()?"payment":"payment-status";
                    u.iconRow(u.card(list),label,mode(p.optString("mode"))+" · "+date(p.optString("paidOn"))+" · "+state,R.drawable.ic_receipt,()->go(target,p.optString("id")));
                }
                if(count==0)u.empty(list,term.isEmpty()?"No payment history":"No matching payments",term.isEmpty()?"Your declarations and receipts will appear here.":"Try another flat, reference or month.");
            };
            display.run();
            search.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
                public void onTextChanged(CharSequence s,int start,int before,int count) {JSONObject draft=new JSONObject();try{draft.put("search",s.toString());h.savePageDraft(draftKey,draft);}catch(Exception ignored){}display.run();}
                public void afterTextChanged(Editable value) {}
            });
        });
    }
    private void proof() {
        h.pageTitle("Payment proof");
        if(id.isEmpty()) {u.empty(b,"No payment selected","Open proof from your payment.");return;}
        get("/payments/"+id,r->{
            JSONObject p=(JSONObject)r;
            LinearLayout summary=u.card(b);
            u.kv(summary,"Amount",Ui.money(p.opt("amount")));u.kv(summary,"Mode",mode(p.optString("mode")));
            u.kv(summary,"Paid on",date(p.optString("paidOn")));u.kv(summary,"Reference",text(p,"reference","Cash declaration"));
            get("/ops/files?kind=PAYMENT&parentId="+p.optString("id"),files->{
                JSONArray items=(JSONArray)files;
                if(items.length()==0)u.empty(b,"No proof uploaded","No sample or bank confirmation is shown.");
                for(int i=0;i<items.length();i++) {
                    JSONObject file=items.getJSONObject(i);
                    u.iconRow(u.card(b),file.optString("name"),file.optString("mime")+" · "+((file.optLong("size")+1023)/1024)+" KB",R.drawable.ic_copy,()->get("/ops/files/"+file.optString("id"),value->h.openSharedFile((JSONObject)value)));
                }
                if(p.optBoolean("canAttach")&&items.length()<5)u.button(b,"Attach PDF or photo",R.drawable.ic_plus,true,()->h.chooseAttachment("PAYMENT",p.optString("id")));
                u.note(b,"Private to this flat and the finance team. A proof file is not bank verification.");
                u.button(b,h.isStaff()?"Back to review":"Track payment",R.drawable.ic_arrow_left,true,()->go(h.isStaff()?"payment":"payment-status",p.optString("id")));
            });
        });
    }
}
