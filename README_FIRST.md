# ApartmentPilotAI — cumulative implementation 02

**This is NEW source code, not the previously re-shared Increment 01 ZIP.** It includes the previous foundation plus connected native screens and backend operations for community management, services, bookings, additional accounting and referral/subscription records.

**Not a release build or a claim of complete 203-screen visual parity.** Live phone verification/recovery, push delivery, purchase verification and subscription access enforcement are not enabled. Use synthetic local data. Do not publish this build on Google Play or expose the local profile publicly.

## Updating your already-working Windows project

Existing root: `D:\ApartmentPilotAI`. Keep your current PostgreSQL database, credentials and port. The new code does not require a different Java version, Gradle plugin or Android dependency.

1. Stop the **ApartmentPilotAI backend Run configuration** in IntelliJ and stop the app run in Android Studio. Leave PostgreSQL running. Do not stop or change ParkingPilotAI.
2. Make a database backup in your database client. A source backup is NOT a database backup.
3. Extract this ZIP to a **separate staging directory**, for example `D:\ApartmentPilotAI_Update02`. Do not extract it over the working folder yet.
4. In PowerShell run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File "D:\ApartmentPilotAI_Update02\scripts\Apply-Update.ps1" -Target "D:\ApartmentPilotAI"
```

The updater backs up files before replacing them, preserves existing database configuration/IDE settings, preserves your working Android Gradle/JDK/SDK configuration, and verifies that existing migrations and the frozen reference have not changed. It does not connect to the database, run migrations or kill processes.

5. Rebuild and start `ApartmentPilotAiApplication` in IntelliJ with your existing **Java 21 / local profile / port 8080 / DB variables**. Flyway should apply **V2 — community operations and bookings**. V1 remains unchanged.
6. Verify:

```powershell
Invoke-RestMethod "http://127.0.0.1:8080/actuator/health" -TimeoutSec 10 | ConvertTo-Json
Invoke-RestMethod "http://127.0.0.1:8080/api/v1/status" -TimeoutSec 10 | ConvertTo-Json
```

Expected status metadata: `version: 0.2.0`, `releaseReady: false`. A successful startup does not prove all business workflows passed.

7. Android Studio: keep/open `D:\ApartmentPilotAI\android`, sync and Run the **debug** app. Do not run the AGP Upgrade Assistant just for this update. Your locally upgraded AGP configuration is preserved by the updater. The archive's original baseline is AGP 8.13.2, compile/target SDK36 and minimum SDK26.
8. Emulator **Local server** address stays `http://10.0.2.2:8080/api/v1`. Your physical-phone ADB-reverse setup, if used, stays `http://127.0.0.1:8080/api/v1` with port8080 forwarded.

### Important upgrade details

- Do not delete `flyway_schema_history`, drop the schema, run Flyway clean, or modify V1 to make a failed startup disappear. Share the exact error instead.
- The V2 migration extends the existing schema and preserves records. It replaces the monthly-bill uniqueness rule with a charge-aware rule so contributions do not replace maintenance.
- The updater preserves existing `pom.xml`, Gradle scripts/wrappers, `local.properties`, `application*.yml`, `.env*` and IDE settings. No new library dependency is required for the added source.
- Consequently your local Android `versionName` or Maven artifact version may still display the previous value. Backend `/status` and the in-app development page identify source increment02.
- Do not uninstall the app just to update it. Existing records live in PostgreSQL. A first-increment memory-only login may require signing in again; new sessions are stored encrypted using Android Keystore.
- Keep your database password local. There is no password in this package.

## What is included

| Component | Contents |
|---|---|
| `backend/` | Full cumulative Java21 Spring Boot source, immutable V1 plus new V2 migration |
| `android/` | Full cumulative Java/XML native app; no WebView; real API calls |
| `reference/` | Unchanged final v2.1 reference, original handoff and screen manifest |
| `scripts/` | Safe update script, pure-rule checks, local HTTP suites and existing launch/tooling helpers |
| `docs/` | Change log, API contract, screen coverage, known limits and actual test evidence |

## Test commands

```powershell
cd D:\ApartmentPilotAI
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Test-Domain.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Test-Local-Api.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Test-Operations.ps1 -AllowSyntheticRecords
# Optional concurrent approval test:
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Test-Operations.ps1 -AllowSyntheticRecords -Concurrency
```

HTTP suites require the running backend and create clearly labelled synthetic apartments; they do not delete data. Keep the local registration rate limit in mind when repeatedly rerunning suites. For framework/JUnit tests, run `backend\mvnw.cmd test`; for the Android build, run `android\gradlew.bat :app:assembleDebug` after the wrapper is available. These integration/build commands are supplied for local execution, not claimed to have passed here.

**Read `docs/IMPLEMENTATION_STATUS.md` and `docs/TEST_EVIDENCE.md` before judging completion.**
