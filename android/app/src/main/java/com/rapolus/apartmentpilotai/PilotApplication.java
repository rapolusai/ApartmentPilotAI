package com.rapolus.apartmentpilotai;
import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.rapolus.apartmentpilotai.data.ApiClient;
public final class PilotApplication extends Application {
    private ApiClient api;
    public org.json.JSONObject transientSignup = new org.json.JSONObject();
    @Override public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(getSharedPreferences("appearance",MODE_PRIVATE).getInt("mode",AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
        api=new ApiClient(this);
    }
    public ApiClient api() {
        return api;
    }
}
