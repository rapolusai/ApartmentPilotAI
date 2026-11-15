package com.rapolus.apartmentpilotai.data;

import android.app.Activity;
import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rapolus.apartmentpilotai.BuildConfig;
import java.util.concurrent.TimeUnit;

/**
 * Proves the person holds their mobile number, using Firebase Authentication: Google sends the SMS code and checks it.
 * The result is a short-lived Firebase ID token that the app sends with sign-up, join or Forgot PIN; the server checks
 * it itself. The app keeps no Firebase session: it signs out as soon as it has the token.
 * Firebase is set up from build settings (no google-services.json in git); without them phone verification is off.
 */
public final class PhoneAuth {
    public interface Callback {
        void codeSent();
        void verified(String idToken);
        void failed(String message);
    }
    private static PhoneAuth instance;
    private final FirebaseAuth auth;
    private String verificationId,sentTo;
    private PhoneAuthProvider.ForceResendingToken resend;

    public static boolean configured() {
        return !BuildConfig.FIREBASE_APP_ID.isEmpty()&&!BuildConfig.FIREBASE_API_KEY.isEmpty()&&!BuildConfig.FIREBASE_PROJECT_ID.isEmpty();
    }
    public static synchronized PhoneAuth get(Context context) {
        if(instance==null)instance=new PhoneAuth(context.getApplicationContext());
        return instance;
    }
    private PhoneAuth(Context app) {
        FirebaseApp firebase;
        try {
            firebase=FirebaseApp.getInstance();
        } catch(IllegalStateException notYet) {
            firebase=FirebaseApp.initializeApp(app,new FirebaseOptions.Builder().setApiKey(BuildConfig.FIREBASE_API_KEY)
                .setApplicationId(BuildConfig.FIREBASE_APP_ID).setProjectId(BuildConfig.FIREBASE_PROJECT_ID).build());
        }
        auth=FirebaseAuth.getInstance(firebase);
        auth.setLanguageCode("en");
    }
    /** True when a code was already sent to this number in this app session (so reopening the screen does not resend). */
    public boolean sentTo(String mobile) {
        return mobile.equals(sentTo)&&verificationId!=null;
    }
    public void send(Activity activity,String mobile,boolean again,Callback callback) {
        PhoneAuthOptions.Builder options=PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+91"+mobile).setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(activity).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // Some phones read the SMS automatically; no code needs typing then.
                    signIn(credential,callback);
                }
                @Override public void onVerificationFailed(FirebaseException e) {
                    verificationId=null;
                    callback.failed(describe(e));
                }
                @Override public void onCodeSent(String id,PhoneAuthProvider.ForceResendingToken token) {
                    verificationId=id;
                    resend=token;
                    sentTo=mobile;
                    callback.codeSent();
                }
            });
        if(again&&resend!=null&&mobile.equals(sentTo))options.setForceResendingToken(resend);
        sentTo=mobile;
        PhoneAuthProvider.verifyPhoneNumber(options.build());
    }
    public void confirm(String code,Callback callback) {
        if(verificationId==null) {
            callback.failed("Tap Resend code to get a new code.");
            return;
        }
        signIn(PhoneAuthProvider.getCredential(verificationId,code),callback);
    }
    private void signIn(PhoneAuthCredential credential,Callback callback) {
        auth.signInWithCredential(credential).addOnCompleteListener(task-> {
            if(!task.isSuccessful()||task.getResult()==null||task.getResult().getUser()==null) {
                callback.failed(describe(task.getException()));
                return;
            }
            task.getResult().getUser().getIdToken(true).addOnCompleteListener(tokenTask-> {
                String token=tokenTask.isSuccessful()&&tokenTask.getResult()!=null?tokenTask.getResult().getToken():null;
                auth.signOut();
                if(token==null)callback.failed("Verification could not be completed. Check the connection and try again.");
                else {
                    verificationId=null;
                    sentTo=null;
                    callback.verified(token);
                }
            });
        });
    }
    private static String describe(Exception e) {
        if(e instanceof FirebaseAuthInvalidCredentialsException)return "That code is not right, or the number is not valid. Check it and try again.";
        if(e instanceof FirebaseTooManyRequestsException)return "Too many attempts from this phone. Wait a while and try again.";
        return "The code could not be sent or checked. Check the connection and try again.";
    }
}
