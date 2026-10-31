# Building a release for Google Play

Release builds are refused unless two things are present, neither of which is in git:

1. **The live API address**, as a Gradle property or environment variable:
   `-PapiBaseUrl=https://api.apartmentcare.rapolu.in/api/v1` or `APT_API_BASE_URL=...`.
   It must start with `https://`. The debug build keeps using the local server over USB.
2. **The upload keystore**, in `android/keystore.properties` (ignored by git) or environment variables.

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
