package com.rapolus.apartmentpilotai.security;

import com.rapolus.apartmentpilotai.api.ApiError;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component public final class InviteCipher {
    private final SecretKeySpec key;
    private final SecureRandom random=new SecureRandom();

    public InviteCipher(@Value("${app.invite-display-key:}") String configuredKey) {
        if(configuredKey==null||configuredKey.isBlank()) {
            key=null;
            return;
        }
        byte[] decoded;
        try { decoded=Base64.getDecoder().decode(configuredKey.trim()); }
        catch(IllegalArgumentException e) { throw new IllegalStateException("Invite display key must be a base64-encoded 256-bit key.",e); }
        if(decoded.length!=32)throw new IllegalStateException("Invite display key must be a base64-encoded 256-bit key.");
        key=new SecretKeySpec(decoded,"AES");
    }

    public boolean configured() { return key!=null; }

    private SecretKeySpec requiredKey() {
        if(key==null)throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Invite display encryption is NOT_CONFIGURED. Configure APP_INVITE_DISPLAY_KEY before issuing codes.");
        return key;
    }

    public String encrypt(UUID tenant,UUID invite,String code) {
        try {
            byte[] nonce=new byte[12];
            random.nextBytes(nonce);
            Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,requiredKey(),new GCMParameterSpec(128,nonce));
            cipher.updateAAD((tenant+":"+invite).getBytes(StandardCharsets.UTF_8));
            byte[] body=cipher.doFinal(code.getBytes(StandardCharsets.UTF_8));
            byte[] packed=new byte[nonce.length+body.length];
            System.arraycopy(nonce,0,packed,0,nonce.length);
            System.arraycopy(body,0,packed,nonce.length,body.length);
            return Base64.getEncoder().encodeToString(packed);
        } catch(ApiError e) { throw e; }
        catch(Exception e) { throw new IllegalStateException("Could not protect the invite code.",e); }
    }

    public String decrypt(UUID tenant,UUID invite,String value) {
        try {
            byte[] packed=Base64.getDecoder().decode(value);
            if(packed.length<29)throw new IllegalArgumentException("Invalid ciphertext length.");
            byte[] nonce=new byte[12];
            System.arraycopy(packed,0,nonce,0,12);
            Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,requiredKey(),new GCMParameterSpec(128,nonce));
            cipher.updateAAD((tenant+":"+invite).getBytes(StandardCharsets.UTF_8));
            return new String(cipher.doFinal(packed,12,packed.length-12),StandardCharsets.UTF_8);
        } catch(ApiError e) { throw e; }
        catch(Exception e) { throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"The current invite cannot be displayed with this server key. Restore the configured key or issue a new code."); }
    }
}
