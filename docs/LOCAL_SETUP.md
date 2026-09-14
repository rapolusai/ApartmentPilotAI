# Local setup — incremental update 02

Use `README_FIRST.md` for the safe updater. Keep your already-working setup; do not change ports or database credentials just to install this update.

## Backend
- Project: `D:\ApartmentPilotAI\backend\pom.xml`
- Main class: `com.rapolus.apartmentpilotai.ApartmentPilotAiApplication`
- JDK: installed Java21.
- Environment: `SPRING_PROFILES_ACTIVE=local`, `DB_URL=jdbc:postgresql://localhost:5432/partmentpilotai_dev`, `DB_USER` your working local user, `DB_PASSWORD` entered locally. Port stays8080 unless intentionally changed.
- `.env` is only an example of local secret storage. Spring Boot does not automatically import an arbitrary `.env` file; put values in IntelliJ's Environment variables or use the supplied launcher.
- Preserve existing `application*.yml`; new code has safe defaults and no extra dependency. Optional `APP_PROVIDER_KEY` is read only by server-side operator endpoints; do not put it in Android or Git.
- Flyway should validate V1 and apply V2 once. Do not modify a previously applied migration or use clean/repair as a shortcut. Back up your database first.

## Android
- Open only `D:\ApartmentPilotAI\android`, not the unrelated ParkingPilotAI project.
- Use the debug variant. Existing working AGP/Gradle wrapper/local.properties are preserved by the update script. The baseline package uses AGP8.13.2, Java17 source compatibility, SDK36 and minSDK26. User's locally upgraded build setup still requires an actual local compile.
- Emulator API: `http://10.0.2.2:8080/api/v1`.
- USB device: use `adb reverse tcp:8080 tcp:8080`, API `http://127.0.0.1:8080/api/v1`.
- Android never connects directly to PostgreSQL.
- Keep app data. Sign in to the existing synthetic Admin account. If no account exists, use New apartment. No universal demo PIN/account is provided.
- Native files/PDF sharing may require a PDF viewer on your test device. Use only synthetic attachments.

## Verification sequence
1. Backend `/actuator/health` returns UP; `/api/v1/status` identifies0.2.0.
2. Execute pure rules (`scripts/Test-Domain.ps1`).
3. Execute Maven tests and the original HTTP suite, then `Test-Operations.ps1 -AllowSyntheticRecords`.
4. Build/install Android debug, test Admin+Resident on separate sessions/devices. Existing confirmed backend startup is not a substitute for new API tests.
5. Compare light/dark screenshots with the frozen reference. Capture first build/runtime error, not passwords or complete environment dumps.

## Rollback is two separate operations
The updater stores overwritten SOURCE files outside the project. Restore those source files if necessary. **Do not automatically reverse database migrations.** A database restore must use your own prior backup and an intentional decision about any new records created after that backup. Do not simply downgrade to increment01 after V2: V1 code's old bill uniqueness target differs from the V2 schema.
