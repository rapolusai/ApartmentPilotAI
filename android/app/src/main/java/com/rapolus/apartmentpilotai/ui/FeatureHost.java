package com.rapolus.apartmentpilotai.ui;
import android.app.Activity;
import android.widget.LinearLayout;
import org.json.*;
import com.rapolus.apartmentpilotai.data.ApiClient;
public interface FeatureHost {
    interface Reply {
        void done(Object value)throws Exception;
    }
    Activity activity();
    Ui components();
    LinearLayout body();
    ApiClient client();
    boolean isStaff();
    boolean isAdmin();
    String selectedMonth();
    String recordId();
    String commandKey();
    void openPage(String page,String id);
    void refreshPage();
    void pageTitle(String title);
    void problem(String text);
    Ui.Fields newFields();
    void requestApi(String method,String path,JSONObject body,Reply reply);
    void resetCommandKey();
    JSONObject pageDraft(String key);
    void savePageDraft(String key,JSONObject value);
    void clearPageDraft(String key);
    void chooseAttachment(String kind,String id);
    void exportPdf(String title,JSONObject record);
    void openSharedFile(JSONObject file);
    void exitSession();
    void monthControl();
}
