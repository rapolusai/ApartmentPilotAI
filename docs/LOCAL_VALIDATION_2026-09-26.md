# Local validation — 26 September 2026

This is a bounded development checkpoint, not whole-app QA approval or release readiness. ParkingPilotAI and the connected physical phone were not modified. Independent QA remains on hold as requested.

## Simpler apartment administration

The financial-controls hub now opens focused native pages instead of one large form: payment account, billing options, verification, reminder settings and common-space policy. Existing controls remain available. New section saves merge with current tenant settings under the tenant transaction lock, preserving unrelated concurrent changes. Android request-key retries are recognised; changed retries fail with HTTP 409 rather than overwriting newer settings.

Apartment onboarding validates the admin name, mobile and PIN before the second step. Setup completion links directly to maintenance, flats and invitations. Home prompts the Admin to set maintenance when no rate exists. Unused bank or UPI fields can stay blank. Local server tools remain development-only; no database credential is requested by the apartment-admin screens. Production deployment and provider setup are still operator responsibilities, not solved by this local build.

## Evidence executed

The counts and hashes in the original checkpoint below are historical. The later payment/report checkpoint at the end of this document supersedes them for its explicitly tested scope.

- Java 21 Maven package: success, **21 unit tests**, zero failures/errors/skips.
- Native Android `testDebugUnitTest assembleDebug`: success, **5 unit tests**, zero failures/errors.
- PostgreSQL 18.6 retained development database: Flyway validated seven history entries, additively migrated through **V6**. A custom-format backup was created before V6; no records or schema history were deleted.
- `Test-Support-And-Consent.ps1 -AllowSyntheticRecords`: **48/48 HTTP checks** against the final backend. Includes private support persistence/revisit/retry, role and tenant isolation, consent without reward, audit detail scope, focused settings, Android-style request keys, input validation and concurrent section saves. Final synthetic tenants: `SUPPORT QA A/B 89bc4447`.
- Regression checkpoint before the final request-key-only fix: **50/50** core HTTP checks (`TEST A/B bd0ece7a`), **112/112** operations/concurrency checks (`TEST02 A/B a124c549`, report `test-results/operations-a124c549.json`), and **33/33** invite checks (`INVITE QA A/B 109f445a`). These suites were not all rerun after the final fix; do not conflate this checkpoint with final-source whole-app regression acceptance.
- Dedicated `emulator-5580`, Pixel_10_Pro: latest APK installed, backend connection successful, real synthetic apartment creation reached Apartment ready, Home rendered real empty totals, maintenance prompt appeared, and saved session survived an APK update.
- Android-to-backend UPI-only payment-account save succeeded after the command-key fix. Revisit displayed saved account holder and UPI ID with unused bank fields blank.
- Payment form landscape: saved values remained visible, lower fields and Save were reachable by scrolling. Original emulator rotation settings were restored. Backend restart produced the expected offline screen; Retry connection recovered to Home after the server returned.
- Reference HTML Git blob remained identical to HEAD: `15ea10eb4d1db6a396963f224943fd152c440d89`. SHA256: `FF5554324E630F3B63235EC723E094BE6CDFF02DBF434898BED5D6F290F3AE28`.

## Candidate hashes

- Debug APK: `B2E06402834F1EEFEA34F052197A6B8D7F1647B088BE6BDC8D8EE2064B6CC1A2`.
- Executable backend JAR: `92EB69673BD0B878A5A31511AC28B56855CEAEECA89DE79A8DB636008A8AE69A`.

## Remaining acceptance gates

The seven previously missing reference routes now have native source (permissions, audit detail, payment help, support, subscription plans, referral offer and referral rules). Source presence is not functional or visual completion. Other merged/partial destinations, the full 203-route visual comparison, dark-mode coverage, tablet/physical-device validation, fresh/V1-clone migration paths and independent QA remain open. Live OTP recovery, phone push, checkout, subscription settlement and provider verification remain unconfigured and must not be represented as successful production integrations. Support requests are privately recorded locally; external support delivery is not configured.

## Later checkpoint: payment tracking and dedicated financial reports

Implemented dedicated native payment-status, proof and searchable history screens, plus an authenticated payment-detail endpoint. Status derives from persisted server review state, not a client success flag. Pending payments link from the bill using the authoritative pending ID. New declarations receive a fresh retry key; retries within the same form retain their key. Parent/tenant/flat checks remain enforced for proof access; approved declarations cannot silently receive replacement proof.

Added dedicated native expense breakdown, staff collections by flat and export-report screens. Existing authenticated finance APIs are reused. Resident expense breakdown includes only visible paid/unreversed entries; the transparency gate is checked first. Collections exclude reversed approved payments through the existing scoped bill API. CSV, PDF and plain-text sharing use Android choosers without automatic delivery. CSV cells are quoted and guarded against spreadsheet formula injection. Report links are reachable from the monthly summary, including the reference's Privacy → Shared totals resident entry.

Native tab selection now follows the frozen manifest groups for details and deep links. The selected icon uses the on-lime color for legibility in dark mode; theme control displays the appropriate sun/moon icon.

Executed against this backend source:

- Maven test/package: **26 unit tests**, zero failures/errors/skips.
- Android test/build checkpoint: **13 unit tests**, zero failures/errors; framework build passed.
- Core API suite: **60/60**, synthetic `TEST A/B 2d8c2604`.
- Operations/concurrency regression: **112/112**, `TEST02 A/B 72efd213`, report `test-results/operations-72efd213.json`.
- Support/consent/settings regression: **48/48**, `SUPPORT QA A/B 5a877d5f`.
- Invite regression: **33/33**, `INVITE QA A/B 2274682a`.
- Dedicated phone emulator only: approved synthetic resident declared a real ₹250 UPI payment, reached pending tracking and empty proof controls, searched history to a no-match state then cleared it, and reopened a real receipt after server-side approval. Native Back and Dues tab selection were observed.
- Resident Shared totals reached real monthly totals; expense breakdown rendered its truthful empty state in dark mode. Export CSV created a 681-byte real file containing the expected ₹250 approved receipts and ₹2,250 outstanding; the Android chooser opened. PDF generated a 40,749-byte file and opened a chooser with Print available. No destination was selected and nothing was sent externally.
- Receipt landscape was checked; original emulator rotation settings were restored. Earlier files named `payment-proof-dark` and `payment-receipt-dark` captured before the theme recreation finished and are actually light screenshots; they are **not** dark-mode evidence. Actual dark rendering was inspected in `expense-breakdown-20260926.png`.

Still open for this flow: all rejection/reversal states on Android, real attachment picker/download rendering, non-empty category chart and staff collection runtime, latest cosmetic-fix rerender, PDF visual-content inspection, exhaustive responsive/device testing, scored reference parity and independent QA. CSV/PDF file creation is not end-to-end release approval.

The source map now contains **49 partial/merged destinations**, **138 connected-source destinations without complete runtime acceptance**, **8 with bounded emulator evidence**, and **8 reference-only tools**. These categories are not a completion percentage. Whole-app completion is not claimed; independent QA remains on hold.

Final framework checkpoint after compactness, selected-icon contrast, theme-icon and Android minimum-version collection-helper repairs: Android `testDebugUnitTest assembleDebug` succeeded with **13/13 unit tests**, zero failures/errors. APK SHA256 `9FF4798549E7C6B4E2638A32D68CDD502A9D5CDB4AD0C0A99C74F738D70E2A76`, installed successfully on dedicated `emulator-5580`. Backend JAR SHA256 `FE6B251B7D8C00DDB38708CC893A0EEBCA315DA4BB21B3A626128FF695085C3C`. Reference hash/blob remained unchanged and `git diff --check` passed. Generated package inventories from earlier increments are historical; these current artifact hashes do not imply a release package has been approved.

## Latest checkpoint: recurring expense detail and vendor snapshot

Dedicated native recurring detail now provides Edit, confirmed Pause/Resume and confirmed Prepare expense actions. Setup has an optional existing-vendor selector. Server authentication supplies the tenant and staff permission. V7 adds nullable tenant-checked vendor links to recurring templates and expenses; existing records and migrations were not rewritten. A 520,051-byte custom-format development database backup was saved before applying V7. Flyway validated eight history entries and successfully applied V7 on the retained database.

Manual preparation creates or reopens one unpaid draft per template/cycle under the tenant transaction lock. It does not post cash. Explicit paid confirmation advances a still-current manually prepared cycle once; existing automation catch-up semantics remain unchanged. Request-key retries, concurrent preparation, optimistic pause/resume, foreign vendor/tenant denial, reversals and resident visibility were tested.

- Maven package: **33 unit tests**, zero failures/errors/skips.
- Android unit/build: **13 unit tests**, zero failures/errors; new APK installed on dedicated `emulator-5580` only.
- Recurring HTTP suite: **61/61**, synthetic `RECURRING QA A/B 0cb6f211` retained.
- Core regression: **60/60**, `TEST A/B bf1b8ce8`.
- Operations/concurrency: **112/112**, report `test-results/operations-844dc9dd.json`.
- Support/consent/settings: **48/48**, `SUPPORT QA A/B 889e51e6`.
- Invite regression: **33/33**, `INVITE QA A/B a205ae8f`.
- Total latest local HTTP checks: **314/314**. Initial fixture creation hit the existing registration rate limit; tests were rerun after its normal window elapsed. No limit was disabled, reset or bypassed. An initial report-write permission failure was also rerun successfully with local permission.
- Native emulator: Pause persisted and hid Prepare, Resume persisted and restored Prepare, confirmed preparation opened a real unpaid ₹125 draft with its linked vendor, Android Back returned to the template, Edit retained vendor/frequency/date and Save reopened detail. Landscape recreation retained the detail; lower controls still require scrolling and exhaustive responsiveness is not approved. Original emulator rotation settings were restored.
- APK SHA256: `34BEA9D00D4001D64948BBE836060385B6DA62ED7BCEE6CD516181E69F8CB37E`.
- Backend JAR SHA256: `192657FFBB7C4C1F1667FC79C4B77ED4AA68F57CD5F1628A7A848BA28A2AC15E`.

Recorded dedicated source coverage: **frontend 147/195 (75.4%)**, **backend 131/185 API-dependent routes (70.8%)**. The map retains 48 partial/merged routes, 139 connected-source routes without complete runtime acceptance, eight bounded-emulator routes and eight reference-only tools. These percentages are not release or visual/QA completion. Recurring detail remains in the incomplete-runtime category because exhaustive negative Android states, device/tablet coverage, scored reference parity and independent QA are still open. Independent QA remains on hold; no physical device or unrelated project was changed.
