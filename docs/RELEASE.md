# Building a release for Google Play

Release builds are refused unless three things are present, none of which is in git:

1. **The live API address**, as a Gradle property or environment variable:
   `-PapiBaseUrl=https://api.apartmentcare.rapolu.in/api/v1` or `APT_API_BASE_URL=...`.
   It must start with `https://`. The debug build keeps using the local server over USB.
2. **The upload keystore**, in `android/keystore.properties` (ignored by git) or environment variables.
3. **The Firebase app settings** for phone verification, in the same `keystore.properties` (see below).

## One-time: create the upload key (on your PC, PowerShell)
```
keytool -genkeypair -v -keystore D:\Keys\apartmentcare-upload.jks -alias apartmentcare -keyalg RSA -keysize 2048 -validity 10000
```
Choose a strong password when asked and keep it in your password manager. Back up the `.jks` file somewhere that is
not the project folder or a cloud drive synced into git. If it is lost you can still recover through Play App Signing,
but it takes a support request.

Then create `D:\ApartmentPilotAI\android\keystore.properties` (never commit it):
```
storeFile=D:/Keys/apartmentcare-upload.jks
storePassword=<the password>
keyAlias=apartmentcare
keyPassword=<the password>
```

## One-time: Firebase phone verification (SMS code)
Sign-up, join and Forgot PIN need the person to prove their mobile number. Google's Firebase Authentication sends and
checks the SMS code; our server only checks Google's signed result. Until this is set up, the live server keeps sign-up off.

1. https://console.firebase.google.com → **Add project** → name `apartmentcare` (Google Analytics: off).
2. Build → **Authentication** → Get started → Sign-in method → **Phone** → Enable. Upgrade the project to the
   **Blaze** (pay as you go) plan: SMS is charged per message. Set a budget alert in Google Cloud Billing.
3. Project settings → **Add app** → Android → package `com.rapolus.apartmentpilotai`.
4. In that app, **Add fingerprint** twice:
   - the SHA-256 of your upload key: `keytool -list -v -keystore D:\Keys\apartmentcare-upload.jks -alias apartmentcare`
   - after the first upload, the SHA-256 of the **app signing key** from Play Console → Test and release → App integrity.
     Without it, the app installed from Play cannot verify phones.
5. Google Cloud console (same project) → enable the **Play Integrity API**.
6. Project settings → General → Your apps: copy **App ID**, **Web API key** and **Project ID** into
   `android/keystore.properties` (git-ignored):
   ```
   firebaseApiKey=<Web API key>
   firebaseAppId=<App ID, 1:...:android:...>
   firebaseProjectId=<Project ID>
   ```
7. Server: put the Project ID in SSM and redeploy (sign-up turns on; `/api/v1/status` then shows `phoneVerification: true`
   and `releaseReady: true`):
   ```
   aws ssm put-parameter --profile apartmentcare --region ap-south-1 --name /apartmentcare/prod/APP_FIREBASE_PROJECT_ID --type String --value <Project ID>
   ```

The debug build uses package `com.rapolus.apartmentpilotai.debug`; register it as a second Android app in Firebase (with
the debug key's SHA-256) only if you want to test SMS codes on a debug build. For testing without real SMS, add
fictional numbers under Authentication → Sign-in method → Phone → *Phone numbers for testing*.

## Build the bundle for Play
```
cd D:\ApartmentPilotAI\android
.\gradlew :app:bundleRelease -PapiBaseUrl=https://api.apartmentcare.rapolu.in/api/v1
```
Output: `app\build\outputs\bundle\release\app-release.aab`. Upload it on the Play Console → Testing → Internal testing.
Play App Signing is on by default for new apps: Google signs what users download, and this key only signs uploads.

## Before each upload
- Bump `versionCode` (and `versionName`) in `android/app/build.gradle`; Play refuses a reused `versionCode`.
- `gradlew :app:testDebugUnitTest` passes and the debug build has run on the tablet against the staging server.
- `/api/v1/status` on the live server answers and its version matches the backend you deployed.

## Rollout order
1. **Internal testing** (up to 100 testers, immediate): you and the QA phones.
2. **Closed testing**: for a personal developer account, at least 12 testers opted in for 14 continuous days before
   production is unlocked. An organisation account skips this.
3. **Production**, staged rollout starting at 20 %.
