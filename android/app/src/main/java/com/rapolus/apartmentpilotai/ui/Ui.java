package com.rapolus.apartmentpilotai.ui;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.*;
import com.rapolus.apartmentpilotai.R;
import java.math.*;
import java.text.NumberFormat;
import java.util.*;
/** Shared native components following the frozen reference tokens. No WebView. */ public final class Ui {
    public final Context c;
    public Ui(Context c) {
        this.c=c;
    }
    public int dp(float v) {
        return Math.round(v*c.getResources().getDisplayMetrics().density);
    }
    public int color(int r) {
        return ContextCompat.getColor(c,r);
    }
    public LinearLayout column() {
        LinearLayout l=new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        return l;
    }
    public LinearLayout row() {
        LinearLayout l=new LinearLayout(c);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        l.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        return l;
    }
    public TextView text(String value,int size,boolean bold,int color) {
        TextView t=new TextView(c);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color(color));
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        return t;
    }
    public void gap(LinearLayout l,int size) {
        Space s=new Space(c);
        l.addView(s,new LinearLayout.LayoutParams(1,dp(size)));
    }
    public void heading(LinearLayout l,String title) {
        gap(l,18);
        l.addView(text(title,15,true,R.color.ap_ink));
        gap(l,10);
    }
    public LinearLayout card(LinearLayout parent) {
        MaterialCardView card=new MaterialCardView(c);
        card.setRadius(dp(21));
        card.setCardBackgroundColor(color(R.color.ap_surface));
        card.setStrokeColor(color(R.color.ap_line));
        card.setStrokeWidth(dp(1));
        card.setCardElevation(dp(1));
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);
        cp.bottomMargin=dp(12);
        card.setLayoutParams(cp);
        LinearLayout body=column();
        body.setPadding(dp(16),dp(16),dp(16),dp(16));
        card.addView(body);
        parent.addView(card);
        return body;
    }
    public LinearLayout hero(LinearLayout parent,String label,String amount,String detail) {
        LinearLayout box=column();
        box.setPadding(dp(22),dp(22),dp(22),dp(22));
        box.setBackgroundResource(R.drawable.hero_background);
        TextView a=text(label,11,false,R.color.ap_muted);
        a.setTextColor(0xffc3d7c6);
        box.addView(a);
        gap(box,7);
        TextView b=text(amount,36,true,R.color.ap_ink);
        b.setTextColor(0xfff1f8ed);
        box.addView(b);
        gap(box,15);
        TextView d=text(detail,11,false,R.color.ap_sub);
        d.setTextColor(0xffd3e0d4);
        box.addView(d);
        parent.addView(box);
        gap(parent,14);
        return box;
    }
    public MaterialButton button(LinearLayout l,String title,int icon,boolean primary,Runnable action) {
        MaterialButton b=new MaterialButton(c);
        b.setText(title);
        b.setAllCaps(false);
        b.setTextSize(13);
        b.setCornerRadius(dp(15));
        b.setMinHeight(dp(48));
        b.setInsetTop(0);
        b.setInsetBottom(0);
        b.setBackgroundTintList(ColorStateList.valueOf(color(primary?R.color.ap_pine:R.color.ap_soft)));
        b.setTextColor(color(primary?R.color.ap_bg:R.color.ap_ink));
        b.setIconTint(ColorStateList.valueOf(color(primary?R.color.ap_bg:R.color.ap_ink)));
        if(icon!=0) {
            b.setIconResource(icon);
            b.setIconSize(dp(19));
            b.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        }
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.topMargin=dp(8);
        b.setLayoutParams(p);
        b.setOnClickListener(v->action.run());
        l.addView(b);
        return b;
    }
    public void iconRow(LinearLayout parent,String title,String detail,int icon,Runnable action) {
        LinearLayout r=row();
        r.setMinimumHeight(dp(74));
        r.setPadding(dp(8),dp(9),dp(8),dp(9));
        ImageView image=new ImageView(c);
        image.setImageResource(icon);
        image.setImageTintList(ColorStateList.valueOf(color(R.color.ap_pine)));
        r.addView(image,new LinearLayout.LayoutParams(dp(28),dp(28)));
        LinearLayout texts=column();
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-2,1);
        tp.leftMargin=dp(13);
        texts.setLayoutParams(tp);
        texts.addView(text(title,13,true,R.color.ap_ink));
        if(detail!=null&&!detail.isEmpty()) {
            gap(texts,4);
            texts.addView(text(detail,11,false,R.color.ap_muted));
        }
        r.addView(texts);
        if(action!=null) {
            ImageView chevron=new ImageView(c);
            chevron.setImageResource(R.drawable.ic_arrow_right);
            chevron.setImageTintList(ColorStateList.valueOf(color(R.color.ap_muted)));
            r.addView(chevron,new LinearLayout.LayoutParams(dp(18),dp(18)));
            r.setOnClickListener(v->action.run());
            r.setFocusable(true);
            r.setContentDescription(title+". "+detail);
        }
        parent.addView(r);
    }
    public void kv(LinearLayout l,String key,String value) {
        LinearLayout r=row();
        r.setPadding(0,dp(9),0,dp(9));
        TextView a=text(key,12,false,R.color.ap_muted);
        a.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
        TextView b=text(value,12,true,R.color.ap_ink);
        b.setGravity(Gravity.END);
        b.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
        r.addView(a);
        r.addView(b);
        l.addView(r);
    }
    public void empty(LinearLayout l,String title,String detail) {
        LinearLayout b=card(l);
        b.setGravity(Gravity.CENTER);
        ImageView i=new ImageView(c);
        i.setImageResource(R.drawable.ic_building);
        b.addView(i,new LinearLayout.LayoutParams(dp(52),dp(52)));
        gap(b,15);
        TextView h=text(title,18,true,R.color.ap_ink);
        h.setGravity(Gravity.CENTER);
        b.addView(h);
        gap(b,8);
        TextView p=text(detail,12,false,R.color.ap_muted);
        p.setGravity(Gravity.CENTER);
        b.addView(p);
    }
    public void state(LinearLayout parent,int icon,String title,String detail,int accentColor,int backgroundColor) {
        LinearLayout box=column();
        box.setGravity(Gravity.CENTER_HORIZONTAL);
        box.setPadding(dp(18),dp(24),dp(18),dp(18));
        ImageView image=new ImageView(c);
        image.setImageResource(icon);
        image.setImageTintList(ColorStateList.valueOf(color(accentColor)));
        image.setPadding(dp(24),dp(24),dp(24),dp(24));
        android.graphics.drawable.GradientDrawable background=new android.graphics.drawable.GradientDrawable();
        background.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        background.setCornerRadius(dp(29));
        background.setColor(color(backgroundColor));
        image.setBackground(background);
        image.setContentDescription(title);
        box.addView(image,new LinearLayout.LayoutParams(dp(87),dp(87)));
        gap(box,21);
        TextView heading=text(title,25,true,R.color.ap_ink);
        heading.setGravity(Gravity.CENTER);
        box.addView(heading);
        gap(box,9);
        TextView message=text(detail,12,false,R.color.ap_muted);
        message.setGravity(Gravity.CENTER);
        message.setMaxWidth(dp(280));
        box.addView(message);
        parent.addView(box);
        gap(parent,8);
    }
    public void note(LinearLayout l,String message) {
        TextView t=text(message,11,false,R.color.ap_sub);
        t.setPadding(dp(12),dp(12),dp(12),dp(12));
        l.addView(t);
    }
    public void confirm(String title,String message,String positive,Runnable action) {
        LinearLayout body=column();
        body.setPadding(dp(24),dp(6),dp(24),dp(10));
        body.addView(text(message,13,false,R.color.ap_sub));
        gap(body,15);
        LayoutInflater.from(c).inflate(R.layout.view_brand_footer,body,true);
        new MaterialAlertDialogBuilder(c).setTitle(title).setView(body).setNegativeButton("Cancel",null).setPositiveButton(positive,(d,w)->action.run()).show();
    }
    public static String money(Object n) {
        try {
            NumberFormat f=NumberFormat.getCurrencyInstance(new Locale("en","IN"));
            return f.format(new BigDecimal(String.valueOf(n)));
        } catch(Exception e) {
            return "₹0.00";
        }
    }
    public static BigDecimal decimal(Object n) {
        try {
            return new BigDecimal(String.valueOf(n));
        } catch(Exception e) {
            return BigDecimal.ZERO;
        }
    }
    public static final class Fields {
        private final Ui ui;
        private final LinearLayout parent;
        private final Map<String,EditText> inputs=new LinkedHashMap<>();
        private final Map<String,Spinner> selects=new LinkedHashMap<>();
        private final Map<String,String[]> selectValues=new LinkedHashMap<>();
        private final Map<String,CheckBox> checks=new LinkedHashMap<>();
        public Fields(Ui ui,LinearLayout parent) {
            this.ui=ui;
            this.parent=parent;
        }
        public EditText field(String key,String label,String value,int inputType) {
            TextInputLayout wrap=new TextInputLayout(ui.c);
            wrap.setHint(label);
            wrap.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            wrap.setBoxCornerRadii(ui.dp(13),ui.dp(13),ui.dp(13),ui.dp(13));
            wrap.setBoxBackgroundColor(ui.color(R.color.ap_surface));
            TextInputEditText edit=new TextInputEditText(wrap.getContext());
            edit.setInputType(inputType);
            if((key.equalsIgnoreCase("pin")||key.toLowerCase(java.util.Locale.ROOT).endsWith("pin"))) {
                edit.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                wrap.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
            edit.setText(value);
            edit.setSingleLine((inputType&android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE)==0);
            edit.setTextSize(13);
            edit.setTextColor(ui.color(R.color.ap_ink));
            edit.setMinHeight(ui.dp(50));
            wrap.addView(edit,new LinearLayout.LayoutParams(-1,-2));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
            p.bottomMargin=ui.dp(16);
            parent.addView(wrap,p);
            inputs.put(key,edit);
            boolean dateField=label.contains("YYYY-MM-DD")||java.util.Arrays.asList("startOn","endOn","nextOn","paidOn","receivedOn","dueDate","completedOn","amcEnd").contains(key);
            if(dateField) {
                wrap.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                wrap.setEndIconDrawable(R.drawable.ic_calendar);
                wrap.setEndIconContentDescription("Choose date");
                wrap.setEndIconOnClickListener(v-> {
                    java.time.LocalDate current=java.time.LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"));
                    String previous=edit.getText().toString();
                    try {
                        current=java.time.LocalDate.parse(previous.length()>10?previous.substring(0,10):previous);
                    } catch(Exception ignored) {
                    }
                    android.app.DatePickerDialog dialog=new android.app.DatePickerDialog(ui.c,(picker,year,month,day)-> {
                        String chosen=java.time.LocalDate.of(year,month+1,day).toString();
                        if(label.contains("THH:mm")) {
                            new android.app.TimePickerDialog(ui.c,(clock,hour,minute)->edit.setText(chosen+String.format(java.util.Locale.ROOT,"T%02d:%02d",hour,minute)),17,0,true).show();
                        } else edit.setText(chosen);
                    }
                    ,current.getYear(),current.getMonthValue()-1,current.getDayOfMonth());
                    dialog.show();
                }
                );
            }
            return edit;
        }
        public void select(String key,String label,String[] labels,String[] values) {
            parent.addView(ui.text(label,11,true,R.color.ap_sub));
            Spinner s=new Spinner(ui.c);
            s.setMinimumHeight(ui.dp(48));
            ArrayAdapter<String> adapter=new ArrayAdapter<>(ui.c,android.R.layout.simple_spinner_item,labels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s.setAdapter(adapter);
            parent.addView(s);
            ui.gap(parent,12);
            selects.put(key,s);
            selectValues.put(key,values);
        }
        public void check(String key,String label,boolean checked) {
            CheckBox b=new CheckBox(ui.c);
            b.setText(label);
            b.setTextSize(12);
            b.setChecked(checked);
            b.setTextColor(ui.color(R.color.ap_sub));
            parent.addView(b);
            ui.gap(parent,8);
            checks.put(key,b);
        }
        public String get(String key) {
            if(inputs.containsKey(key))return inputs.get(key).getText().toString().trim();
            if(selects.containsKey(key)) {
                String[] values=selectValues.get(key);
                return values.length==0?"":values[selects.get(key).getSelectedItemPosition()];
            }
            return "";
        }
        public boolean checked(String key) {
            return checks.containsKey(key)&&checks.get(key).isChecked();
        }
        public org.json.JSONObject values() {
            org.json.JSONObject j=new org.json.JSONObject();
            try {
                for(String key:inputs.keySet())j.put(key,get(key));
                for(String key:selects.keySet())j.put(key,get(key));
                for(String key:checks.keySet())j.put(key,checked(key));
            } catch(org.json.JSONException e) {
                throw new IllegalStateException(e);
            }
            return j;
        }
        public void restore(org.json.JSONObject j) {
            if(j==null)return;
            for(String k:inputs.keySet())if(j.has(k))inputs.get(k).setText(j.optString(k));
            for(String k:checks.keySet())if(j.has(k))checks.get(k).setChecked(j.optBoolean(k));
            for(String k:selects.keySet())for(int i=0; i<selectValues.get(k).length; i++)if(selectValues.get(k)[i].equals(j.optString(k)))selects.get(k).setSelection(i);
        }
    }
}
