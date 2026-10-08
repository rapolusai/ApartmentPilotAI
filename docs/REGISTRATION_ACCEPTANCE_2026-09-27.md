# Registration checkpoint — 27 September 2026

Status: IN PROGRESS, not independent-QA-approved or release-ready. The user approved automatic sequential implementation without repeated flow-start approval. No physical device, unrelated project or production deployment was changed.

## Implemented changes

- `MainActivity.java`: Admin/resident input validation, required apartment address/PIN code, Delhi NCR option, length limits, explicit native flat grid selection, real pending/approved/rejected status checks and restoration after process restart.
- `data/RegistrationPolicy.java` and its unit test: consent, name, mobile, PIN, apartment address/PIN code and 5–50-flat validation.
- `data/SessionVault.java`, `data/ApiClient.java`: a separate Android-Keystore encrypted join receipt, with expiry; cleared when a real session starts or the backend address changes. No PIN is persisted.
- `ui/Ui.java`: semantic field accessibility labels and bounded input lengths.
- `security/AuthService.java`: reuse existing V2 address/pincode columns; duplicate registration rejection before tenant creation; safe same-request retries; authenticated rejected-request resubmission; hash-only expiring receipt scoped to one request. A receipt never creates a session or grants apartment permissions.
- `api/Requests.java`, `api/ApiController.java`: additive registration fields and validated, rate-limited POST `/auth/join-status`. Old local client payloads may omit address/PIN code; the new native form requires them.
- `V8__registration_details_and_status.sql`: adds only the join receipt table/index. V1–V7 and existing data were retained.
- New backend registration tests and real HTTP/PostgreSQL/native emulator test scripts.
- `SEQUENTIAL_IMPLEMENTATION_PLAN.md`: execution order and evidence reporting, not a completion claim.

## Page and storage mapping

| Reference page | Real behavior | Storage/access |
|---|---|---|
| admin-signup | Consent + valid person details → setup | PIN is transient RAM only |
| setup-apartment | Validate address/PIN code/flats; check server availability; register | ap_tenant, ap_block, ap_flat, ap_user, ap_session, ap_audit; one transaction |
| setup-done | Server apartment identity; review flats, maintenance, invite, dashboard links | Authenticated server account |
| join / invite-invalid | Look up actual valid invite or show rejection | Active/unexpired ap_invite hash; available flats only |
| signup | Consent + valid person details → choose-flat | Transient form only; no successful fake registration |
| choose-flat | Explicit flat selection → real pending claim | Tenant lock + partial unique flat index; server-derived invite tenant |
| signup-pending | Check status; approved state; rejection reason; retry/sign-in | Expiring hash-only ap_join_receipt bound to ap_user; no UUID-based anonymous private lookup |

## Evidence

- Corrected backend source: 44/44 unit regression tests passed, including five new registration security tests.
- Android: build passed, 25/25 unit tests passed, including two new registration policy tests.
- Real registration HTTP/PostgreSQL: initial 36/36 bounded checks passed; fixture `REG QA 4c202c0d` retained. Expanded 39/39 suite also passed with `REG QA 9c7a24c0`, including concurrent same-flat winner/conflict, one persisted claimant and no losing receipt.
- Login HTTP/PostgreSQL regression: 42/42 passed after registration changes; fixture `LOGIN QA A/B cf0333dc` retained.
- Native phone-emulator registration end-to-end: 15/15 bounded checks passed on the named APK; `REG UI ee45bfbd` retained. Includes real Admin setup, Resident flat selection, pending state, encrypted receipt/process restart, real approval status and Resident login.
- Native tablet-emulator registration end-to-end: 15/15 bounded checks passed on the same named Registration APK, retaining `REG UI 167ff252`.
- Scored >=95% frozen-reference parity, broader failure/rotation/accessibility scenarios and independent QA: pending.
- Production verified-mobile registration and Forgot PIN remain NOT_CONFIGURED. Local-profile fixture success is not production registration approval.

Registration checkpoint APK SHA256: `35479E189FDC7A36DB8CEC2D78B7C629B5CC909833B0569BBFE05CA095E8CB2D`. The subsequent Resident-approval flow builds a new APK; older native results are not substituted for its runtime testing.
Frozen reference SHA256: `FF5554324E630F3B63235EC723E094BE6CDFF02DBF434898BED5D6F290F3AE28`.

## Failures and corrections

The first Android build rejected passing GridLayout to the existing LinearLayout button helper; the native grid now reuses the styled button through a temporary parent. The first V8 attempt mistakenly duplicated the existing address column and rolled back; existing schema/history was inspected, only unapplied V8 was corrected, and its receipt-only migration then applied successfully. Windows rejected the unsigned backend launcher; the backend was started directly through Maven without changing execution policy.

No schema reset, history wipe, provider mock success or secret logging occurred. Bounded test pass rate is 100% for completed suites above; overall fully accepted inventory completion remains 0% while visual/provider/QA gates remain open.
