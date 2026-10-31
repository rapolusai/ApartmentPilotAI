package com.rapolus.apartmentpilotai.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.rapolus.apartmentpilotai.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.*;

/** Dedicated native reports, backed only by authenticated finance responses. */
public final class FinanceReportScreens {
    private final FeatureHost h;
    private final Ui u;
    private LinearLayout b;
    public FinanceReportScreens(FeatureHost host){h=host;u=h.components();}
    public boolean render(String page) {
        b=h.body();
        switch(page) {
            case "expense-report":expenses();break;
            case "collection-report":collections();break;
            case "export-report":exports();break;
            default:return false;
        }
        return true;
    }
    private void get(String path,FeatureHost.Reply reply){h.requestApi("GET",path,null,reply);}
    private void go(String page,String id){h.openPage(page,id);}
    private BigDecimal amount(JSONObject row,String key){return new BigDecimal(row.optString(key,"0"));}
    private String label(JSONObject bill) {
        if(bill.optBoolean("inReview"))return "In review";
        BigDecimal due=amount(bill,"amount"),paid=amount(bill,"paid");
        return paid.compareTo(due)>=0?"Paid":paid.signum()>0?"Part paid":"Unpaid";
    }
    private void expenses() {
        h.pageTitle("Expense breakdown");h.monthControl();
        // The summary endpoint enforces the resident transparency switch.
        get("/reports/monthly?month="+h.selectedMonth(),summary->get("/expenses?month="+h.selectedMonth(),result->{
            JSONArray rows=(JSONArray)result;
            Map<String,BigDecimal> totals=new HashMap<>();
            BigDecimal total=BigDecimal.ZERO;
            for(int i=0;i<rows.length();i++) {
                JSONObject expense=rows.getJSONObject(i);
                if(!expense.optBoolean("paid")||!expense.isNull("reversedOn"))continue;
                BigDecimal value=amount(expense,"amount");
                total=total.add(value);
                totals.merge(expense.optString("category","Other"),value,BigDecimal::add);
            }
            List<Map.Entry<String,BigDecimal>> categories=new ArrayList<>(totals.entrySet());
            categories.sort(Map.Entry.<String,BigDecimal>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()));
            LinearLayout chart=u.card(b);
            chart.addView(u.text("Total paid",11,false,R.color.ap_muted));
            chart.addView(u.text(Ui.money(total),35,true,R.color.ap_ink));
            u.gap(chart,18);
            if(categories.isEmpty())u.note(chart,"No spending this month");
            for(Map.Entry<String,BigDecimal> category:categories) {
                u.kv(chart,category.getKey(),Ui.money(category.getValue()));
                ProgressBar bar=new ProgressBar(h.activity(),null,android.R.attr.progressBarStyleHorizontal);
                bar.setMax(1000);
                bar.setProgress(total.signum()==0?0:category.getValue().multiply(BigDecimal.valueOf(1000)).divide(total,0,java.math.RoundingMode.HALF_UP).intValue());
                bar.setProgressTintList(ColorStateList.valueOf(u.color(R.color.ap_green)));
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,u.dp(8));lp.bottomMargin=u.dp(12);
                chart.addView(bar,lp);
            }
            if(!h.isStaff())u.note(b,"This breakdown includes expenses shared with residents. Private expense details remain hidden.");
            u.button(b,"View expenses",R.drawable.ic_expense,false,()->go("expenses",""));
        }));
    }
    private void collections() {
        h.pageTitle("Collection report");
        if(!h.isStaff()){h.problem("Admin or treasurer access required.");return;}
        h.monthControl();
        get("/bills?month="+h.selectedMonth(),result->{
            JSONArray bills=(JSONArray)result;
            BigDecimal billed=BigDecimal.ZERO,paid=BigDecimal.ZERO;
            List<List<String>> csv=new ArrayList<>();
            csv.add(Arrays.asList("Month","Flat","Bill type","Billed","Approved payments","Outstanding","Status"));
            for(int i=0;i<bills.length();i++) {
                JSONObject bill=bills.getJSONObject(i);
                billed=billed.add(amount(bill,"amount"));paid=paid.add(amount(bill,"paid"));
                csv.add(Arrays.asList(h.selectedMonth(),bill.optString("flatLabel"),bill.optString("kind"),amount(bill,"amount").toPlainString(),amount(bill,"paid").toPlainString(),amount(bill,"amount").subtract(amount(bill,"paid")).toPlainString(),label(bill)));
            }
            LinearLayout summary=u.card(b);
            u.kv(summary,"Billed",Ui.money(billed));u.kv(summary,"Approved",Ui.money(paid));u.kv(summary,"Outstanding",Ui.money(billed.subtract(paid)));
            u.button(b,"Export collections CSV",R.drawable.ic_report,false,()->h.exportCsv("Collections · "+h.selectedMonth(),csv));
            if(bills.length()==0)u.empty(b,R.drawable.ic_receipt,"No bills this month","Generate bills to see collections by flat.");
            for(int i=0;i<bills.length();i++) {
                JSONObject bill=bills.getJSONObject(i);
                u.iconRow(u.card(b),bill.optString("flatLabel")+" · "+label(bill),Ui.money(bill.opt("paid"))+" paid / "+Ui.money(bill.opt("amount"))+" · "+bill.optString("title",bill.optString("kind")),R.drawable.ic_receipt,()->go("bill",bill.optString("id")));
            }
            u.note(b,"Pending declarations are not collected money. Reversed payments are excluded from approved totals.");
        });
    }
    /** Monthly summary in the standard report layout. Every figure is a value the server returned; nothing is recalculated. */
    private ReportPdf.Doc monthlyReport(String apartment,JSONObject report,String[][] keys) {
        String month=com.rapolus.apartmentpilotai.data.ReportText.month(h.selectedMonth());
        ReportPdf.Doc doc=new ReportPdf.Doc(apartment,"Monthly summary",month,"");
        String[][] cards={{"Opening",money(report,"opening")},{"Received",money(report,"received")},{"Spent",money(report,"spent")},{"Closing",money(report,"closing")}};
        doc.cards(cards,3);
        doc.section("Money in and out");
        doc.bars(new String[]{"Approved receipts","Other income","Paid expenses"},
            new BigDecimal[]{value(report,"received"),value(report,"otherIncome"),value(report,"spent")},
            new int[]{ReportPdf.TONE_GOOD,ReportPdf.TONE_GOOD,ReportPdf.TONE_WAIT});
        if(report.has("billed")) {
            doc.section("Collection");
            doc.bars(new String[]{"Bills issued","Approved for bills","Outstanding"},
                new BigDecimal[]{value(report,"billed"),value(report,"collectedForBills"),value(report,"outstanding")},
                new int[]{ReportPdf.TONE_GOOD,ReportPdf.TONE_GOOD,ReportPdf.TONE_BAD});
        }
        doc.section("Account summary");
        ReportPdf.Table table=new ReportPdf.Table(new String[]{"Item","Amount"},new float[]{3,1.4f},new boolean[]{false,true});
        for(String[] key:keys)if(report.has(key[0])&&!key[0].equals("closing"))table.row(key[1],money(report,key[0]));
        if(report.has("closing"))table.total("Closing balance",money(report,"closing"));
        doc.table(table);
        if(report.has("pendingPayments")&&report.optLong("pendingPayments")>0)doc.note(report.optLong("pendingPayments")+" payment declaration(s) are waiting for approval and are not included in received money.");
        doc.note("Pending declarations are not collected money. Reversed payments are excluded from approved totals.");
        return doc;
    }
    private static BigDecimal value(JSONObject row,String key) {
        return com.rapolus.apartmentpilotai.data.ReportText.amount(row.opt(key));
    }
    private static String money(JSONObject row,String key) {
        return com.rapolus.apartmentpilotai.data.ReportText.money(value(row,key));
    }
    private void exports() {
        h.pageTitle("Export report");
        h.monthControl();
        get("/reports/monthly?month="+h.selectedMonth(),result->{
            JSONObject report=(JSONObject)result;
            LinearLayout summary=u.card(b);
            String apartment=h.client().account().optString("apartmentName","Apartment");
            u.kv(summary,"Report","Monthly summary");u.kv(summary,"Month",h.selectedMonth());u.kv(summary,"Apartment",apartment);
            String[][] keys={{"opening","Opening balance"},{"received","Approved receipts"},{"otherIncome","Other income"},{"spent","Paid expenses"},{"expenseReturns","Expense reversals"},{"refunds","Payment reversals"},{"closing","Closing balance"},{"billed","Bills issued"},{"collectedForBills","Approved for bills"},{"outstanding","Outstanding"}};
            List<List<String>> csv=new ArrayList<>();
            csv.add(Arrays.asList("Apartment","Month","Metric","Amount"));
            StringBuilder text=new StringBuilder(apartment+" · "+h.selectedMonth()+"\n");
            for(String[] key:keys)if(report.has(key[0])) {
                csv.add(Arrays.asList(apartment,h.selectedMonth(),key[1],report.optString(key[0])));
                text.append(key[1]).append(": ").append(Ui.money(report.opt(key[0]))).append('\n');
            }
            text.append("Powered By @Rapolu`s");
            u.button(b,"Export CSV",R.drawable.ic_report,false,()->h.exportCsv("Monthly summary · "+h.selectedMonth(),csv));
            u.button(b,"Save / share PDF",R.drawable.ic_receipt,false,()->h.exportReport(monthlyReport(apartment,report,keys)));
            u.button(b,"Share summary",R.drawable.ic_copy,false,()->{
                Intent intent=new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,text.toString());
                h.activity().startActivity(Intent.createChooser(intent,"Share monthly summary"));
            });
            u.note(b,"Choose a destination in Android to save or share. Nothing is sent automatically.");
        });
    }
}
