package com.rapolus.apartmentpilotai.data;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.rapolus.apartmentpilotai.BuildConfig;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import org.json.*;
/** Real HTTP client. Tokens are encrypted by the Android Keystore; PINs are never persisted. */ public final class ApiClient {
    public interface Callback {
        void complete(Object result,Error error);
    }
    public static final class Error {
        public final int status;
        public final String message;
        public Error(int status,String message) {
            this.status=status;
            this.message=message;
        }
    }
    private final ExecutorService executor=Executors.newFixedThreadPool(3);
    private final Handler main=new Handler(Looper.getMainLooper());
    private final Context context;
    private volatile String base;
    private volatile String token="";
    private JSONObject account;
    private final SessionVault vault;
    public ApiClient(Context context) {
        this.context=context.getApplicationContext();
        vault=new SessionVault(context);
        JSONObject saved=vault.load();
        if(saved!=null) {
            token=saved.optString("token","");
            account=saved.optJSONObject("account");
        }
        base=BuildConfig.DEBUG?context.getSharedPreferences("connection",0).getString("url",BuildConfig.API_BASE_URL):BuildConfig.API_BASE_URL;
    }
    public String base() {
        return base;
    }
    public boolean signedIn() {
        return !token.isEmpty()&&account!=null;
    }
    public JSONObject account() {
        return account;
    }
    public void updateAccount(JSONObject data) {
        account=data;
        JSONObject old=vault.load();
        if(old!=null)try {
            old.put("account",data);
            vault.save(old);
        } catch(JSONException ignored) {
        }
    }
    public synchronized void session(JSONObject data)throws JSONException {
        account=data.getJSONObject("account");
        token=data.getString("token");
        vault.save(data);
    }
    public synchronized void clear() {
        token="";
        account=null;
        vault.clear();
    }
    private synchronized boolean clearIfCurrent(String requestToken) {
        if(requestToken.isEmpty()||!requestToken.equals(token))return false;
        token="";
        account=null;
        vault.clear();
        return true;
    }
    public void setBase(String url) {
        if(!BuildConfig.DEBUG)throw new IllegalStateException("Release endpoint cannot be changed.");
        URI uri=URI.create(url.trim());
        String host=uri.getHost();
        if(!"http".equals(uri.getScheme())||host==null||!(host.equals("10.0.2.2")||host.equals("localhost")||host.equals("127.0.0.1"))||uri.getUserInfo()!=null||uri.getQuery()!=null||uri.getFragment()!=null) throw new IllegalArgumentException("Use http://10.0.2.2:8080/api/v1 (emulator) or http://127.0.0.1:8080/api/v1 with adb reverse.");
        base=url.trim().replaceAll("/+$","");
        clear();
        context.getSharedPreferences("connection",0).edit().putString("url",base).apply();
    }
    public void call(String method,String path,JSONObject data,Callback callback) {
        String requestBase=base,requestToken=token;
        executor.execute(()-> {
            HttpURLConnection c=null;
            Object result=null;
            Error error=null;
            try {
                c=(HttpURLConnection)new URL(requestBase+path).openConnection();
                c.setRequestMethod(method);
                c.setConnectTimeout(10000);
                c.setReadTimeout(30000);
                c.setInstanceFollowRedirects(false);
                c.setRequestProperty("Accept","application/json");
                if(!requestToken.isEmpty())c.setRequestProperty("Authorization","Bearer "+requestToken);
                if(data!=null) {
                    c.setDoOutput(true);
                    c.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                    byte[] bytes=data.toString().getBytes(StandardCharsets.UTF_8);
                    c.setFixedLengthStreamingMode(bytes.length);
                    try(OutputStream o=c.getOutputStream()) {
                        o.write(bytes);
                    }
                }
                int status=c.getResponseCode();
                InputStream stream=status>=200&&status<300?c.getInputStream():c.getErrorStream();
                String text="";
                if(stream!=null)try(InputStream in=stream;
                ByteArrayOutputStream buf=new ByteArrayOutputStream()) {
                    byte[] b=new byte[4096];
                    int n;
                    while((n=in.read(b))!=-1) {
                        if(buf.size()+n>8_000_000)throw new IOException("Response too large");
                        buf.write(b,0,n);
                    }
                    text=buf.toString("UTF-8");
                }
                Object parsed=text.isEmpty()?new JSONObject():new JSONTokener(text).nextValue();
                if(status>=200&&status<300)result=parsed;
                else if(status==401&&!requestToken.isEmpty()) {
                    clearIfCurrent(requestToken);
                    error=new Error(401,"Your session expired. Sign in again.");
                } else error=new Error(status,parsed instanceof JSONObject?((JSONObject)parsed).optString("message","Request failed ("+status+")."):"Request failed ("+status+").");
            } catch(SocketTimeoutException e) {
                error=new Error(0,"The server took too long. Check the latest record before retrying a write.");
            } catch(Exception e) {
                error=new Error(0,"Cannot reach the backend. Check the connection address and that Spring Boot is running.");
            } finally {
                if(c!=null)c.disconnect();
            }
            Object value=result;
            Error failure=error;
            main.post(()->callback.complete(value,failure));
        }
        );
    }
}
