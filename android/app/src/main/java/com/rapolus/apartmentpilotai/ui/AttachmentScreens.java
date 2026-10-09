package com.rapolus.apartmentpilotai.ui;
import android.widget.LinearLayout;
import com.rapolus.apartmentpilotai.R;
import org.json.*;

/** Native expense attachment preview. Every content load rechecks the authenticated parent. */
public final class AttachmentScreens {
    private final FeatureHost h;
    private final Ui u;
    private int selection=0;
    public AttachmentScreens(FeatureHost host){h=host;u=h.components();}
    public boolean render(String route,String parent) {
        if(!route.equals("expense-proof"))return false;
        h.pageTitle("Expense attachment");
        LinearLayout body=h.body();
        if(parent.isEmpty()){u.empty(body,R.drawable.ic_expense,"No expense selected","Open an expense first.");return true;}
        h.requestApi("GET","/ops/files?kind=EXPENSE&parentId="+parent,null,value->{
            JSONArray files=(JSONArray)value;
            if(files.length()==0)u.empty(body,R.drawable.ic_report,"No attachment","A file has not been added.");
            else {
                LinearLayout preview=u.column();body.addView(preview);
                LinearLayout actions=u.column();body.addView(actions);
                for(int i=0;i<files.length();i++) {
                    JSONObject file=files.getJSONObject(i);
                    if(files.length()>1)u.iconRow(u.card(body),file.optString("name"),(file.optLong("size")/1024)+" KB",R.drawable.ic_copy,()->show(file,preview,actions));
                }
                show(files.getJSONObject(0),preview,actions);
            }
            if(h.isStaff())u.button(body,"Add attachment",R.drawable.ic_plus,false,()->h.chooseAttachment("EXPENSE",parent));
        });
        return true;
    }
    private void show(JSONObject metadata,LinearLayout preview,LinearLayout actions) {
        final int selected=++selection;
        preview.setTag(metadata.optString("id"));
        preview.removeAllViews();actions.removeAllViews();
        h.requestApi("GET","/ops/files/"+metadata.optString("id"),null,value->{
            if(selected!=selection)return;
            JSONObject file=(JSONObject)value;
            u.note(preview,file.optString("name")+" · "+(metadata.optLong("size")/1024)+" KB");
            h.previewAttachment(file,preview);
            if(file.optString("mime").equals("application/pdf"))u.note(preview,"First page · Open to view the complete PDF.");
            u.button(actions,"Save attachment",R.drawable.ic_copy,true,()->h.saveAttachment(file));
            u.button(actions,"Open attachment",R.drawable.ic_arrow_right,false,()->h.openSharedFile(file));
        });
    }
}
