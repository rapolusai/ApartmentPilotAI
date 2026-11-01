package com.rapolus.apartmentpilotai.ui;
import android.app.AlertDialog;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.rapolus.apartmentpilotai.R;
import org.json.*;
import java.util.*;
/**
 * Builds every flat from three tap-to-pick answers (blocks, floors × flats per floor, numbering style), then shows
 * the building as a grid: tap a flat to rename it, mark it vacant or remove it. Saved in one call.
 */
final class FlatGenerator {
    interface Save {
        void save(JSONArray flats);
    }
    private static final String DRAFT="setup-flats";
    private final FeatureHost h;
    private final Ui u;
    private final LinearLayout b;
    private final JSONArray blocks,current;
    private final Save save;
    private final Runnable keep;
    private LinearLayout gridBox;
    private TextView summary;
    FlatGenerator(FeatureHost h,Ui u,LinearLayout body,JSONArray blocks,JSONArray current,Save save,Runnable keep) {
        this.h=h;
        this.u=u;
        this.b=body;
        this.blocks=blocks;
        this.current=current;
        this.save=save;
        this.keep=keep;
    }
    private List<String> blockNames() {
        List<String> names=new ArrayList<>();
        JSONArray chosen=h.pageDraft("setup-blocks").optJSONArray("names");
        if(chosen!=null)for(int i=0; i<chosen.length(); i++)names.add(chosen.optString(i));
        if(names.isEmpty())for(int i=0; i<blocks.length(); i++)names.add(blocks.optJSONObject(i).optString("name"));
        if(names.isEmpty())names.add("A");
        return names;
    }
    void show() {
        List<String> names=blockNames();
        JSONObject draft=h.pageDraft(DRAFT);
        int active=0;
        List<String> sample=new ArrayList<>();
        for(int i=0; i<current.length(); i++) {
            JSONObject f=current.optJSONObject(i);
            if(f!=null&&f.optBoolean("active",true)) {
                active++;
                if(sample.size()<4)sample.add(f.optString("label"));
            }
        }
        if(active>=5&&draft.optJSONArray("grid")==null) {
            LinearLayout now=u.card(b);
            now.addView(u.text("Your flats now: "+active,14,true,R.color.ap_ink));
            now.addView(u.text(String.join(", ",sample)+(active>sample.size()?" …":""),12,false,R.color.ap_sub));
            u.gap(now,4);
            now.addView(u.text("If these are your real flat numbers, keep them. Otherwise build them below.",12,false,R.color.ap_sub));
            u.button(now,"These are correct",R.drawable.ic_check,false,keep);
        }
        LinearLayout c=u.card(b);
        c.addView(u.text("Build your flats",16,true,R.color.ap_ink));
        u.gap(c,10);
        JSONObject params=draft.optJSONObject("params")==null?new JSONObject():draft.optJSONObject("params");
        Map<String,int[]> floors=new LinkedHashMap<>(),perFloor=new LinkedHashMap<>();
        TextView example=u.text("",12,false,R.color.ap_green);
        String[][] style=new String[1][];
        String[][] ground=new String[1][];
        Runnable refresh=()->example.setText(exampleText(names,floors,perFloor,style[0][0],ground[0][0]));
        for(String name:names) {
            if(names.size()>1)c.addView(u.text("Block "+name,13,true,R.color.ap_pine));
            floors.put(name,stepper(c,"Floors",params.optInt("floors_"+name,5),1,40,refresh));
            perFloor.put(name,stepper(c,"Flats on each floor",params.optInt("per_"+name,4),1,20,refresh));
        }
        ground[0]=chips(c,"Lowest floor",new String[] {
            "1st floor","Ground floor (G)"
        }
        ,new String[] {
            "first","ground"
        }
        ,params.optString("ground","first"),refresh);
        String first=names.get(0);
        style[0]=chips(c,"How are flats numbered?",new String[] {
            "101, 102",first+"-101",first+"1, "+first+"2"
        }
        ,new String[] {
            "plain","block","serial"
        }
        ,params.optString("style",names.size()>1?"block":"plain"),refresh);
        c.addView(example);
        refresh.run();
        u.gap(c,6);
        u.button(c,draft.optJSONArray("grid")==null?"Build my flats":"Build again",R.drawable.ic_grid,true,()-> {
            JSONArray grid=generate(names,floors,perFloor,style[0][0],ground[0][0]);
            if(grid.length()>200) {
                h.problem("This release supports up to 200 flats. Reduce floors or flats per floor.");
                return;
            }
            JSONObject p=new JSONObject();
            try {
                for(String n:names) {
                    p.put("floors_"+n,floors.get(n)[0]);
                    p.put("per_"+n,perFloor.get(n)[0]);
                }
                p.put("style",style[0][0]);
                p.put("ground",ground[0][0]);
                h.savePageDraft(DRAFT,new JSONObject().put("params",p).put("grid",grid));
            } catch(JSONException e) {
                throw new IllegalStateException(e);
            }
            drawGrid();
        });
        summary=u.text("",13,true,R.color.ap_ink);
        b.addView(summary);
        gridBox=u.column();
        b.addView(gridBox);
        drawGrid();
    }
    private String exampleText(List<String> names,Map<String,int[]> floors,Map<String,int[]> per,String style,String ground) {
        int total=0;
        for(String n:names)total+=floors.get(n)[0]*per.get(n)[0];
        JSONArray g=generate(names.subList(0,1),floors,per,style,ground);
        String a=g.length()>0?g.optJSONObject(0).optString("label"):"",z=g.length()>0?g.optJSONObject(g.length()-1).optString("label"):"";
        String second=g.length()>1?g.optJSONObject(1).optString("label"):"";
        return "Example: "+a+(second.isEmpty()?"":", "+second)+" … "+z+"   ·   "+total+" flats"+(total>200?" (too many: up to 200)":"");
    }
    static JSONArray generate(List<String> names,Map<String,int[]> floors,Map<String,int[]> per,String style,String ground) {
        JSONArray out=new JSONArray();
        boolean prefix="block".equals(style)||("plain".equals(style)&&names.size()>1);
        boolean hasGround="ground".equals(ground);
        try {
            for(String name:names) {
                int fl=floors.get(name)[0],pf=per.get(name)[0],serial=1;
                for(int k=0; k<fl; k++) {
                    int floor=hasGround?k:k+1;
                    for(int n=1; n<=pf; n++) {
                        String number=floor==0?"G"+(n<10?"0"+n:String.valueOf(n)):String.valueOf(floor*100+n);
                        String label="serial".equals(style)?name+serial:(prefix?name+"-"+number:number);
                        serial++;
                        out.put(new JSONObject().put("label",label).put("block",name).put("floor",floor).put("occupied",true).put("removed",false));
                    }
                }
            }
        } catch(JSONException e) {
            throw new IllegalStateException(e);
        }
        return out;
    }
    private JSONArray grid() {
        return h.pageDraft(DRAFT).optJSONArray("grid");
    }
    private void persist(JSONArray grid) {
        try {
            JSONObject d=h.pageDraft(DRAFT);
            d.put("grid",grid);
            h.savePageDraft(DRAFT,d);
        } catch(JSONException e) {
            throw new IllegalStateException(e);
        }
    }
    /** The building, floor by floor (top floor first), one box per flat. */
    private void drawGrid() {
        gridBox.removeAllViews();
        JSONArray grid=grid();
        if(grid==null) {
            summary.setText("");
            return;
        }
        int kept=0,vacant=0;
        for(int i=0; i<grid.length(); i++) {
            JSONObject f=grid.optJSONObject(i);
            if(!f.optBoolean("removed")) {
                kept++;
                if(!f.optBoolean("occupied",true))vacant++;
            }
        }
        summary.setText(kept+" flats"+(vacant>0?" · "+vacant+" vacant":"")+". Tap a flat to rename it, mark it vacant or remove it.");
        u.gap(gridBox,8);
        LinearLayout legend=u.row();
        legend.addView(u.text("■ Occupied   ",11,false,R.color.ap_pine),new LinearLayout.LayoutParams(-2,-2));
        legend.addView(u.text("■ Vacant   ",11,false,R.color.ap_amber),new LinearLayout.LayoutParams(-2,-2));
        legend.addView(u.text("■ Removed",11,false,R.color.ap_muted),new LinearLayout.LayoutParams(-2,-2));
        gridBox.addView(legend);
        u.gap(gridBox,8);
        Map<String,TreeMap<Integer,List<Integer>>> byBlock=new LinkedHashMap<>();
        for(int i=0; i<grid.length(); i++) {
            JSONObject f=grid.optJSONObject(i);
            byBlock.computeIfAbsent(f.optString("block"),k->new TreeMap<>(Comparator.reverseOrder())).computeIfAbsent(f.optInt("floor"),k->new ArrayList<>()).add(i);
        }
        for(var block:byBlock.entrySet()) {
            LinearLayout card=u.card(gridBox);
            if(byBlock.size()>1)card.addView(u.text("Block "+block.getKey(),14,true,R.color.ap_ink));
            for(var floor:block.getValue().entrySet()) {
                card.addView(u.text(floor.getKey()==0?"Ground floor":"Floor "+floor.getKey(),11,true,R.color.ap_sub));
                HorizontalScrollView scroll=new HorizontalScrollView(u.c);
                scroll.setHorizontalScrollBarEnabled(false);
                LinearLayout row=u.row();
                row.setLayoutParams(new FrameLayout.LayoutParams(-2,-2));
                for(int index:floor.getValue())row.addView(box(grid,index));
                TextView add=boxView("+",R.color.ap_soft,R.color.ap_ink,false);
                add.setContentDescription("Add a flat on this floor");
                String blockName=block.getKey();
                int floorNo=floor.getKey();
                add.setOnClickListener(v->addFlat(blockName,floorNo));
                row.addView(add);
                scroll.addView(row);
                card.addView(scroll);
                u.gap(card,6);
            }
        }
        u.button(gridBox,"Save all "+kept+" flats",R.drawable.ic_check,true,()-> {
            JSONArray out=new JSONArray();
            Set<String> seen=new HashSet<>();
            try {
                for(int i=0; i<grid.length(); i++) {
                    JSONObject f=grid.getJSONObject(i);
                    if(f.optBoolean("removed"))continue;
                    String label=f.optString("label").trim().toUpperCase(Locale.ROOT);
                    if(!seen.add(label)) {
                        h.problem("Flat "+label+" appears twice. Rename one of them.");
                        return;
                    }
                    out.put(new JSONObject().put("label",label).put("block",f.optString("block")).put("floor",f.optInt("floor")).put("occupied",f.optBoolean("occupied",true)));
                }
            } catch(JSONException e) {
                throw new IllegalStateException(e);
            }
            if(out.length()<5||out.length()>200) {
                h.problem("An apartment needs between 5 and 200 flats.");
                return;
            }
            save.save(out);
        });
    }
    private TextView boxView(String label,int background,int textColor,boolean struck) {
        TextView t=u.text(label,12,true,textColor);
        t.setGravity(Gravity.CENTER);
        t.setPadding(u.dp(6),0,u.dp(6),0);
        GradientDrawable d=new GradientDrawable();
        d.setColor(u.color(background));
        d.setCornerRadius(u.dp(10));
        t.setBackground(d);
        if(struck)t.setPaintFlags(t.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,u.dp(44));
        lp.rightMargin=u.dp(6);
        lp.topMargin=u.dp(4);
        t.setMinWidth(u.dp(56));
        t.setLayoutParams(lp);
        return t;
    }
    private TextView box(JSONArray grid,int index) {
        JSONObject f=grid.optJSONObject(index);
        boolean removed=f.optBoolean("removed"),vacant=!f.optBoolean("occupied",true);
        TextView t=boxView(f.optString("label"),removed?R.color.ap_soft:vacant?R.color.ap_ambg:R.color.ap_pine,removed?R.color.ap_muted:vacant?R.color.ap_amber:R.color.ap_bg,removed);
        t.setContentDescription("Flat "+f.optString("label")+(removed?", removed":vacant?", vacant":""));
        t.setOnClickListener(v->choose(grid,index));
        return t;
    }
    private void choose(JSONArray grid,int index) {
        JSONObject f=grid.optJSONObject(index);
        boolean removed=f.optBoolean("removed"),vacant=!f.optBoolean("occupied",true);
        String[] options=removed?new String[] {
            "Bring it back"
        }
        :new String[] {
            "Rename",vacant?"Mark occupied":"Mark vacant","Remove (shop, office or no flat)"
        };
        new AlertDialog.Builder(u.c).setTitle("Flat "+f.optString("label")).setItems(options,(d,which)-> {
            try {
                if(removed)f.put("removed",false);
                else if(which==0) {
                    rename(grid,index);
                    return;
                } else if(which==1)f.put("occupied",vacant);
                else f.put("removed",true);
            } catch(JSONException e) {
                throw new IllegalStateException(e);
            }
            persist(grid);
            drawGrid();
        }).show();
    }
    private void rename(JSONArray grid,int index) {
        JSONObject f=grid.optJSONObject(index);
        EditText input=new EditText(u.c);
        input.setText(f.optString("label"));
        input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        input.setFilters(new android.text.InputFilter[] {
            new android.text.InputFilter.LengthFilter(20)
        });
        new AlertDialog.Builder(u.c).setTitle("New flat number").setView(input).setPositiveButton("Save",(d,w)-> {
            String label=input.getText().toString().trim().toUpperCase(Locale.ROOT);
            if(!label.matches("[A-Z0-9][A-Z0-9 /-]{0,19}")) {
                h.problem("Use letters, numbers, - or / for the flat number.");
                return;
            }
            try {
                f.put("label",label);
            } catch(JSONException e) {
                throw new IllegalStateException(e);
            }
            persist(grid);
            drawGrid();
        }).setNegativeButton("Cancel",null).show();
    }
    private void addFlat(String block,int floor) {
        EditText input=new EditText(u.c);
        input.setHint("Flat number, for example "+(floor==0?"G05":(floor*100+9)));
        input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        new AlertDialog.Builder(u.c).setTitle("Add a flat").setView(input).setPositiveButton("Add",(d,w)-> {
            String label=input.getText().toString().trim().toUpperCase(Locale.ROOT);
            if(!label.matches("[A-Z0-9][A-Z0-9 /-]{0,19}")) {
                h.problem("Use letters, numbers, - or / for the flat number.");
                return;
            }
            JSONArray grid=grid();
            try {
                grid.put(new JSONObject().put("label",label).put("block",block).put("floor",floor).put("occupied",true).put("removed",false));
            } catch(JSONException e) {
                throw new IllegalStateException(e);
            }
            persist(grid);
            drawGrid();
        }).setNegativeButton("Cancel",null).show();
    }
    // Small copies of the wizard's pickers so this class stands alone.
    private int[] stepper(LinearLayout parent,String label,int value,int min,int max,Runnable changed) {
        int[] holder= {
            Math.max(min,Math.min(max,value))
        };
        LinearLayout row=u.row();
        row.addView(u.text(label,13,true,R.color.ap_ink),new LinearLayout.LayoutParams(0,-2,1));
        TextView minus=u.text("−",22,true,R.color.ap_ink),plus=u.text("+",22,true,R.color.ap_ink),shown=u.text(String.valueOf(holder[0]),18,true,R.color.ap_ink);
        for(TextView t:new TextView[] {
            minus,plus
        }) {
            t.setGravity(Gravity.CENTER);
            GradientDrawable d=new GradientDrawable();
            d.setColor(u.color(R.color.ap_soft));
            d.setCornerRadius(u.dp(22));
            t.setBackground(d);
        }
        minus.setContentDescription("Fewer "+label);
        plus.setContentDescription("More "+label);
        shown.setGravity(Gravity.CENTER);
        row.addView(minus,new LinearLayout.LayoutParams(u.dp(48),u.dp(48)));
        row.addView(shown,new LinearLayout.LayoutParams(u.dp(56),-2));
        row.addView(plus,new LinearLayout.LayoutParams(u.dp(48),u.dp(48)));
        minus.setOnClickListener(v-> {
            if(holder[0]>min) {
                holder[0]--;
                shown.setText(String.valueOf(holder[0]));
                changed.run();
            }
        });
        plus.setOnClickListener(v-> {
            if(holder[0]<max) {
                holder[0]++;
                shown.setText(String.valueOf(holder[0]));
                changed.run();
            }
        });
        parent.addView(row);
        u.gap(parent,10);
        return holder;
    }
    private String[] chips(LinearLayout parent,String label,String[] labels,String[] values,String selected,Runnable changed) {
        String[] holder= {
            selected
        };
        parent.addView(u.text(label,12,true,R.color.ap_sub));
        u.gap(parent,6);
        HorizontalScrollView scroll=new HorizontalScrollView(u.c);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row=u.row();
        row.setLayoutParams(new FrameLayout.LayoutParams(-2,-2));
        List<TextView> all=new ArrayList<>();
        for(int i=0; i<labels.length; i++) {
            TextView chip=u.text(labels[i],13,true,R.color.ap_ink);
            chip.setPadding(u.dp(16),u.dp(10),u.dp(16),u.dp(10));
            chip.setMinHeight(u.dp(44));
            chip.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);
            lp.rightMargin=u.dp(8);
            chip.setLayoutParams(lp);
            String value=values[i];
            chip.setOnClickListener(v-> {
                holder[0]=value;
                for(int k=0; k<all.size(); k++)style(all.get(k),values[k].equals(holder[0]));
                changed.run();
            });
            style(chip,value.equals(selected));
            all.add(chip);
            row.addView(chip);
        }
        scroll.addView(row);
        parent.addView(scroll);
        u.gap(parent,12);
        return holder;
    }
    private void style(TextView chip,boolean on) {
        GradientDrawable d=new GradientDrawable();
        d.setColor(u.color(on?R.color.ap_pine:R.color.ap_soft));
        d.setCornerRadius(u.dp(22));
        if(!on)d.setStroke(u.dp(1),u.color(R.color.ap_line));
        chip.setBackground(d);
        chip.setTextColor(u.color(on?R.color.ap_bg:R.color.ap_ink));
    }
}
