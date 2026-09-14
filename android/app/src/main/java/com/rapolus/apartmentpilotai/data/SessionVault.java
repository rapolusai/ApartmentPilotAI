package com.rapolus.apartmentpilotai.data;
import android.content.Context;
import android.security.keystore.*;
import android.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.KeyStore;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
/** Device-bound encrypted session storage. Authentication and expiry remain server-authoritative. */ final class SessionVault {
    private static final String ALIAS="ApartmentPilotAI.Session.v1";
    private final Context context;
    SessionVault(Context context) {
        this.context=context.getApplicationContext();
    }
    private javax.crypto.SecretKey key()throws Exception {
        KeyStore store=KeyStore.getInstance("AndroidKeyStore");
        store.load(null);
        if(store.containsAlias(ALIAS))return (javax.crypto.SecretKey)store.getKey(ALIAS,null);
        KeyGenerator generator=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        generator.init(new KeyGenParameterSpec.Builder(ALIAS,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setRandomizedEncryptionRequired(true).build());
        return generator.generateKey();
    }
    void save(JSONObject session) {
        try {
            Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,key());
            String iv=Base64.encodeToString(cipher.getIV(),Base64.NO_WRAP),data=Base64.encodeToString(cipher.doFinal(session.toString().getBytes(StandardCharsets.UTF_8)),Base64.NO_WRAP);
            context.getSharedPreferences("secure-session",0).edit().putString("iv",iv).putString("data",data).apply();
        } catch(Exception e) {
            clear();
        }
    }
    JSONObject load() {
        try {
            var prefs=context.getSharedPreferences("secure-session",0);
            String data=prefs.getString("data",""),iv=prefs.getString("iv","");
            if(data.isEmpty()||iv.isEmpty())return null;
            Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,key(),new GCMParameterSpec(128,Base64.decode(iv,Base64.NO_WRAP)));
            JSONObject session=new JSONObject(new String(cipher.doFinal(Base64.decode(data,Base64.NO_WRAP)),StandardCharsets.UTF_8));
            if(!java.time.Instant.parse(session.getString("expiresAt")).isAfter(java.time.Instant.now())) {
                clear();
                return null;
            }
            return session;
        } catch(Exception e) {
            clear();
            return null;
        }
    }
    void clear() {
        context.getSharedPreferences("secure-session",0).edit().clear().apply();
    }
}
