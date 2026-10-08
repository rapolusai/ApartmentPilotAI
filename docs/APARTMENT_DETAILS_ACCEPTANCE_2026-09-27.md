# Apartment Details — 27 September 2026

Status: Lead bounded functional checkpoint passed, including phone/tablet native testing. Independent QA remains on hold. Automatic sequential implementation is authorized by the user.

## Reference and changes

Frozen reference `apartment-edit` requires bounded name/city/address and a six-digit PIN code. Existing server allowed blank address and arbitrary PIN text; existing native form used generic text fields without immediate validation.

- `CommunityService.java`: validates all fields before tenant locking/writing; preserves Admin-only authorization, authenticated-tenant update and audit. Supplied tenant/role fields do not select authority.
- `RegistrationPolicy.java`, `RegistrationPolicyTest.java`: pure native profile validation with all required fields and bounds.
- `FeatureScreens.java`: Apartment profile title, bounded name/city fields, multiline address and numeric PIN code; actual existing POST and navigation remain.
- `ApartmentDetailsTest.java`: three server tests for invalid input without mutation, non-Admin denial, and caller-tenant-only audited update.
- `Test-Apartment-Details.ps1`: real localhost synthetic persistence, invalid-field and isolation tests.
- `Test-Apartment-Details-Emulator.ps1`: bounded native validation/save/reload/Back checks on dedicated emulators; no physical-device actions.

No migration, data reset, reference edit or provider mock was needed. Profile updates now require a nonblank address and six-digit PIN code; users with legacy empty profile fields must enter these when saving.

## Evidence

- Backend unit regression: 50/50 passed, including three Apartment Details tests.
- Android build and unit regression: 26/26 passed.
- Live localhost HTTP persistence/negative/isolation/audit: 23/23 passed; `PROFILE QA 64113025` retained.
- Fresh APK SHA256: `D86601EFDB073CF727164695715390D299A3F741FD20DBBF42471E998C959C50`.
- Backend restarted only after verifying ApartmentPilotAI identity and exact listener PID; localhost 8081 only. Flyway validated existing history at V8 without new migrations.
- Native phone `emulator-5580` 11/11 and tablet `emulator-5582` 11/11 passed on the exact APK above. Covers invalid PIN/address, unchanged database on invalid form, complete real save, reopening persisted details and Android Back. Retained fixtures: `PROFILE UI aad7a8e3` and `PROFILE UI 15fa5606`; screenshots `test-results/apartment-profile-native-final.png` and `test-results/login-emulator-5582/apartment-profile-native-final.png`. Earlier Registration/approval APK evidence is not substituted for this candidate.
- First native attempts correctly observed the PIN-code validation, then failed because the test expected an OK dialog button instead of the app's inline error. The test was corrected and rerun; incomplete attempts are not counted as passes.
- Visual >=95%, wider retries/failure/rotation/accessibility and independent QA remain pending. Whole-app acceptance remains 0%; bounded test suites above passed 100%.
