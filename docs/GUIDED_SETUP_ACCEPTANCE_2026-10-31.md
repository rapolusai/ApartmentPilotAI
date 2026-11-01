# Guided setup – acceptance evidence (2026-10-31)

## What changed
- **Backend.** Migration V14 adds `ap_setup_step`. `/api/v1/setup` returns progress and the readiness checklist, steps are completed through `/setup/steps/{key}/complete`, and `/setup/tips/{key}/seen` and `/setup/members` are new. The security filter now returns HTTP 428 `SETUP_REQUIRED` outside the wizard's endpoints while required steps are open. `app.setup-enforced` / `APP_SETUP_ENFORCED` switches this lock and defaults to on. `POST /ops/flats/bulk` saves the flat grid in one transaction. The flat limit is now 5–200, with price tiers 51–100 at ₹349 and 101–200 at ₹499.
- **Android.** New `SetupScreens` (the wizard), `FlatGenerator` (blocks × floors × flats per floor, numbering styles and a building grid) and `Spotlight` (dims the screen and highlights the next button). Home shows the setup card and the "Your next step" banner only until setup is 100% complete. A 428 response opens the wizard.
- **Existing data.** V14 marks every step done for apartments that already exist and for ACTIVE accounts. The one exception is the one-time Terms & privacy step for existing Treasurers and residents.

## Steps
- **Admin (9, shared per apartment):** apartment details, blocks, flat numbers, payment details (optional: cash only is fine), monthly maintenance (one amount, or optionally different amounts by flat size), late fee & reminders (fee optional), existing balance, old dues, invite.
- **Treasurer (5):** terms, role rules, payment details check, late fee & reminders check, regular expenses.
- **Resident, owner or tenant (5):** terms, details, vehicles, how to pay, stay informed.
- **Admin readiness (does not block):** approve join requests, choose a Treasurer, add a second Admin, publish a welcome notice, check the subscription.

## Verified in the cloud container (Linux, PostgreSQL 16, Java 21)
| Check | Result |
|---|---|
| `mvn test` (backend unit tests, including new `SetupServiceTest`) | Pass |
| V14 on a database created by the previous build (existing Admin, active resident, pending resident) | Existing Admin: setup complete, features open. Existing resident: only Terms remains, then unlocked. |
| `scripts/Test-Guided-Setup.ps1` (lock on, PowerShell 7.4) | 32/32 PASS |
| `Test-Local-Api.ps1`, `Test-Operations.ps1` on the **previous** build | Both fail at "maintenance amount": the container runs in UTC, the server uses IST, and the run was near midnight |
| The same two scripts on this build with `APP_SETUP_ENFORCED=false` | Same results as the previous build: no new failures |
| Android: all main sources compiled with `javac` against the Android 14 framework jar, with stubs for androidx/material | Compiles |
| Android JVM unit tests (JUnit 4), including new `FlatGeneratorTest` | 30/30 pass |

## Not verified here
- A Gradle/AGP build and an APK. The Android SDK and Google Maven are blocked in the container, so this must be built on the developer PC.
- Running the app on an emulator or device, and its visual look.
- The existing feature suites with the lock on. They register fresh apartments and use features straight away, so run them with `APP_SETUP_ENFORCED=false` and run `Test-Guided-Setup.ps1` with the lock on.
