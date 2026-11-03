package com.rapolus.apartmentpilotai.play;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.*;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * Calls the Google Play Developer API with a service account. The service-account JSON key lives only on the server
 * (APP_PLAY_SERVICE_ACCOUNT_FILE), never in the app or in git. Access tokens are cached until shortly before expiry.
 */
@Component public class GooglePlayGateway implements PlayGateway {
    private static final String API="https://androidpublisher.googleapis.com/androidpublisher/v3/applications/";
    private static final String SCOPE="https://www.googleapis.com/auth/androidpublisher";
    private final ObjectMapper json=new ObjectMapper();
    private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final String packageName,keyFile;
    private String accessToken;
    private Instant accessExpires=Instant.EPOCH;
    public GooglePlayGateway(@Value("${app.play.package-name:com.rapolus.apartmentpilotai}")String packageName,@Value("${app.play.service-account-file:}")String keyFile) {
        this.packageName=packageName;
        this.keyFile=keyFile;
    }
    private static String enc(String v) {
        return URLEncoder.encode(v,StandardCharsets.UTF_8);
    }
    private static String b64(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
    private synchronized String token() {
        if(accessToken!=null&&Instant.now().isBefore(accessExpires.minusSeconds(60)))return accessToken;
        if(keyFile==null||keyFile.isBlank())throw new IllegalStateException("Google Play service account is not configured (APP_PLAY_SERVICE_ACCOUNT_FILE).");
        try {
            JsonNode key=json.readTree(Files.readString(Path.of(keyFile)));
            String email=key.path("client_email").asText(),tokenUri=key.path("token_uri").asText("https://oauth2.googleapis.com/token");
            String pem=key.path("private_key").asText().replaceAll("-----[A-Z ]+-----","").replaceAll("\\s","");
            PrivateKey privateKey=KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
            long now=Instant.now().getEpochSecond();
            String header=b64("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            String claims=b64(json.writeValueAsBytes(java.util.Map.of("iss",email,"scope",SCOPE,"aud",tokenUri,"iat",now,"exp",now+3600)));
            Signature signer=Signature.getInstance("SHA256withRSA");
            signer.initSign(privateKey);
            signer.update((header+"."+claims).getBytes(StandardCharsets.UTF_8));
            String assertion=header+"."+claims+"."+b64(signer.sign());
            var response=http.send(HttpRequest.newBuilder(URI.create(tokenUri)).timeout(Duration.ofSeconds(20)).header("Content-Type","application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type="+enc("urn:ietf:params:oauth:grant-type:jwt-bearer")+"&assertion="+enc(assertion))).build(),HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!=200)throw new IllegalStateException("Google sign-in for the Play API failed ("+response.statusCode()+").");
            JsonNode body=json.readTree(response.body());
            accessToken=body.path("access_token").asText();
            accessExpires=Instant.now().plusSeconds(body.path("expires_in").asLong(3600));
            return accessToken;
        } catch(IllegalStateException e) {
            throw e;
        } catch(Exception e) {
            throw new IllegalStateException("Google Play service account could not be used ("+e.getClass().getSimpleName()+").");
        }
    }
    @Override public JsonNode subscription(String purchaseToken) {
        try {
            var response=http.send(HttpRequest.newBuilder(URI.create(API+enc(packageName)+"/purchases/subscriptionsv2/tokens/"+enc(purchaseToken))).timeout(Duration.ofSeconds(20))
                    .header("Authorization","Bearer "+token()).GET().build(),HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()==404||response.statusCode()==410||response.statusCode()==400)return null;
            if(response.statusCode()!=200)throw new IllegalStateException("Google Play did not answer the purchase check ("+response.statusCode()+").");
            return json.readTree(response.body());
        } catch(IllegalStateException e) {
            throw e;
        } catch(Exception e) {
            throw new IllegalStateException("Google Play could not be reached ("+e.getClass().getSimpleName()+").");
        }
    }
    @Override public void acknowledge(String productId,String purchaseToken) {
        try {
            var response=http.send(HttpRequest.newBuilder(URI.create(API+enc(packageName)+"/purchases/subscriptions/"+enc(productId)+"/tokens/"+enc(purchaseToken)+":acknowledge")).timeout(Duration.ofSeconds(20))
                    .header("Authorization","Bearer "+token()).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString("{}")).build(),HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()/100!=2)throw new IllegalStateException("Google Play did not accept the acknowledgement ("+response.statusCode()+").");
        } catch(IllegalStateException e) {
            throw e;
        } catch(Exception e) {
            throw new IllegalStateException("Google Play could not be reached ("+e.getClass().getSimpleName()+").");
        }
    }
}
