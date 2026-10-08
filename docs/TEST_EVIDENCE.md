# Test evidence — cumulative implementation 02

Latest execution evidence is recorded separately in [LOCAL_VALIDATION_2026-09-26.md](LOCAL_VALIDATION_2026-09-26.md), with candidate hashes and explicit coverage limitations. The older checkpoints below must not be treated as full acceptance of subsequent source changes.

**This evidence includes successful local framework builds, PostgreSQL/Flyway validation, HTTP integration checks and bounded resident-join/invite-controls emulator runs. It is not a claim of whole-app device/UI or external-provider acceptance.**

## Actually executed in this workspace

| Check | Observed result | What the result establishes |
|---|---|---|
| Maven test | **16 tests passed; 0 failures/errors/skips** | Spring Boot production and test sources compile on Java 21; current JUnit suite including invite encryption is green. |
| Maven package | **BUILD SUCCESS** | Executable backend jar was produced from current source. |
| PostgreSQL/Flyway startup | **PostgreSQL 18.6; schema V5; 6 migrations validated** | The retained `partmentpilotai_dev` database applied additive V5 successfully. This is not the fresh/V1-clone migration matrix. |
| Live health/status | **health UP; version 0.2.0; localRegistration true; releaseReady false** | Current backend ran against PostgreSQL on localhost port 8081. Port 8080 was occupied by system `AgentService`. |
| Local API suite | **50 checks passed** | Authentication lifecycle, role-card non-escalation, tenant isolation, bill/reminder previews, in-app reminder idempotency, rejection persistence, atomic two-payment approval, receipts, notices and logout were exercised over HTTP. |
| Operations API suite with concurrency | **112 checks passed** | Owner/Tenant intake validation and persistence; join-review/rejection authorization, persistence and flat release; Admin-only block creation/rename/idempotency and tenant isolation; affected flats, notices, contributions, transparency, files, bookings, polls, vehicles, subscriptions and parallel one-winner approval were exercised over HTTP. |
| Invite controls API suite | **33 checks passed** | Admin-only scope, cross-tenant denial, request-key retry, two-request concurrent rotation/one valid code, inclusive expiry, revoke/activate and approved-account retention were exercised against retained synthetic tenants. |
| Wrong-key localhost probe | **4/4 assertions passed** | A second localhost backend with an ephemeral different key returned code unavailable and explicit recovery guidance; primary key still displayed the synthetic code; activation under the wrong key returned HTTP 503. This is a bounded server probe, not full key-rotation operations. |
| Independent invite QA API checks | **49/49 bounded checks passed** | QA independently exercised role/tenant negatives, required request key, expiry/retry, revoke/reactivate, old-code denial, approved-resident retention and a two-request race against retained synthetic localhost tenants. Wrong-key, true session expiry and server-failure cases were not independently run. |
| Android clean debug build | **BUILD SUCCESS; 41 tasks executed** | Current native Java/XML sources compiled, resources linked and a fresh debug APK was packaged. |
| Android debug unit tests | **2 tests passed; 0 failures/errors/skips** | Authenticated 401/403/404/5xx/network failures select dedicated states while unauthenticated login/invite and 400/409 failures remain inline. This is policy coverage, not device navigation evidence. |
| Current invite/login-recovery rebuild | **Gradle testDebugUnitTest + assembleDebug BUILD SUCCESS; 7 tasks executed, 33 up-to-date; 2 unit tests passed** | Current APK SHA256 `542E950ED741B7A25697D61F5223851733C3D0CCA3A6FB0779819E77ADF9EA38`. The current native sources/APK compile; this is an incremental build, separate from the earlier clean build. |
| Shared-shell emulator regression | **Lead and independent QA bounded checks passed** | Current rebuilt APK installed on Pixel_10_Pro emulator; Home notifications opened and Android Back returned Home; Community/Members/Join requests tabs and Android Back passed; portrait Invite controls showed Back/title/theme, date and actions; landscape Save/New code/Revoke were reachable after three swipes. All five nav icons/labels were visible. The first fixed-height nav clipped icons and was replaced with content-measured height before QA. |
| Resident join flow on Android emulator | **Passed on Pixel_10_Pro, Android 37** | Fresh debug APK installed; local backend test, Admin login, Pending/Approved/Rejected filters, Owner/Tenant display, review, blank-reason validation, persisted rejection, Android Back, light/dark themes and portrait/landscape rendering were exercised. This is a bounded flow result, not whole-app/device-family acceptance. |
| Invite controls on Android emulator | **Bounded flow passed on Pixel_10_Pro, Android 37; current UI transfer PASS** | Admin saved 22 Sep expiry on APK `42766BC...` through the live local backend and returned to persisted `Expires 22 Sep 2026`. An earlier bounded candidate rotated a legacy invite, redisplayed the code after app relaunch, opened Android Share chooser without sending, revoked/reactivated, and reached actions in dark theme/landscape. Current APK `542E950E...` includes an auth-resume fix; independent QA passed code-free visual/navigation transfer, not repeated live Save/rotate/revoke. |
| Independent invite visual QA | **PASS, bounded light-phone screen ~96.3% on current APK** | QA checksum-matched APK `542E950E...`; fresh code-free Home/Community entry screenshots transferred the ~96.3% frozen-v2.1 score unchanged: geometry mean error ~5.93%, nominal type/icon mean ~2.37%, categorical color/order 8/8. Fully legible date and Community selected from both paths passed hard gates. Picker/Back, phone landscape actions after a contained swipe, prior Home notification→Members and Join requests filters passed. Taller date field remains the largest visual mismatch. This is one phone-sized emulator and this flow, not whole-app parity. See `INVITE_VISUAL_PARITY_SCORE.md`. |
| Pixel Tablet native invite-settings | **Lead + independent QA PASS, bounded; full acceptance OPEN** | Lead and QA checked current APK `542E950E...` on a new local Pixel Tablet Android 37 emulator while the phone stayed connected. QA passed code-free portrait/landscape, light/dark, both entry paths, picker/Back, cancelled New code/Revoke confirmations, nav/footer/readability and restored original tablet light/landscape. Lead separately verified local backend mapping and live Save/persisted expiry on a retained synthetic invite; two labelled synthetic test apartments remain. No physical device or tablet-wide/app-wide visual score. |
| Xiaomi Pad 7 Android 16 physical-tablet Invite controls | **Lead + independent QA PASS, bounded; full acceptance OPEN** | The connected device itself reports market name `Xiaomi Pad 7`, serial `a04cd04e`, 2136×3200 portrait/3200×2136 landscape. Current checksum-matched APK `542E950E...` installed as separate `.debug` package without clearing data. ADB reverse `8080→8081`; the debug-app Local server test showed `Successful`. Lead signed in only to the retained synthetic Admin, opened Invite controls from Home via Invite residents → Expiry date, confirmed fully legible saved date and selected Community, opened/cancelled the native date picker and New code/Revoke dialogs. QA independently passed code-free light/dark portrait/landscape layout and legibility, both Home/Community entry paths, Community nav cue, picker Cancel, cancelled New code/Revoke confirmations, Android Back and restoration of original auto-rotation/light portrait settings. QA captured `test-results/qa-invite-542-physical-controls.png`, `qa-invite-542-physical-pad7-dark-portrait.png`, `qa-invite-542-physical-pad7-dark-landscape.png`, `qa-invite-542-physical-pad7-light-landscape.png` and `qa-invite-542-physical-pad7-restored.png`; no invite code was captured. No code rotation/revocation, physical live Save/persistence, measured physical-tablet 95% visual score or whole-app parity claim. Physical phone NOT_RUN. |
| Controlled Offline-login recovery | **Lead runtime PASS; independent QA static PASS/runtime NOT_RUN** | Only the tablet debug app was set to unreachable localhost port18080; synthetic Admin sign-in showed `Connection paused`. The local backend remained UP. Restoring 127.0.0.1:8080 produced a Successful UI backend test; retrying the same synthetic Admin on current APK returned Home rather than stale Admin login. QA inspected safe resumable-field/token handling in source, but did not independently induce the runtime case. This is not true session expiry or actual backend outage. |
| Real JDK21 pure production-rule tests | **580 assertions passed** | 21 original domain/token checks plus 559 new operational assertions. The latter include **500 deterministic occupancy scenarios**, so these are not 580 end-to-end feature tests. |
| Java syntax parsing | **40 compilation units; zero syntax errors** | Syntax only; does not resolve framework imports or all semantic types. |
| Literal JDBC SQL argument inspection | **284 calls; zero placeholder mismatches** | Counts `?` parameters against source arguments. Dynamic SQL/varargs arrays excluded. New block and join-review persistence SQL was also exercised live; the static scan alone does not execute SQL. |
| Literal native navigation inspection | **104 targets; zero missing switch destinations** | Recalculated from current literal `go(...)`/`openPage(...)` calls and 155 route dispatch identifiers. Only literal source links; no Android navigation/runtime test. |
| XML parsing | **47 source XML files well-formed** | Not `aapt`/resource linking, layout inflation or screenshot testing. |
| Native resource-name references | Passed | Referenced project names exist; not SDK linking. |
| Restricted attachment configuration | Static checks passed | Non-exported FileProvider restricted to shared cache, app backup disabled. Not a security penetration test. |
| Original V1 migration | **Unchanged** | SHA256 equals Increment01 V1 bytes. |
| Frozen v2.1 HTML | **Unchanged** | SHA256 equals original frozen reference. This does not mean native screens visually match it. |

### Additional limited compilation checks
Backend services/controllers (27 source files, excluding security configuration, error handler and bootstrap) and Android `FeatureScreens`/`FeatureHost` were compiled against **private, hand-written approximation stubs**. Those checks found no errors in their covered signatures/types. They are weaker than builds against the actual libraries and are **NOT Maven or Android build passes**. The stubs are not included as application dependencies.

Raw outputs are in `docs/evidence/`; machine-readable scope and exclusions are in `TEST_EVIDENCE.json`.

## What still has NOT run here

- Fresh-database Flyway migration and a backed-up V1-only clone upgraded through V2/V3/V4/V5. The retained development schema was migrated in place through V5 only.
- Broader physical-device testing, accessibility, tablet-wide behavior and a whole-app screenshot comparison. Resident join was exercised on one phone-sized emulator; Invite controls received bounded Lead and independent QA emulator-tablet checks plus Lead/QA physical Pad 7 tablet checks in portrait/landscape/light/dark. No physical phone was connected or tested.
- Instrumentation tests and broader Android view/session persistence tests; only the two HTTP failure-policy unit tests currently exist.
- `Apply-Update.ps1` remains source-reviewed, not runtime-tested.
- Real OTP, FCM, store purchase verification, payment settlement or installation attestation.

The Windows machine provided JDK 21.0.10, PostgreSQL 18.6 and Android SDK support. The checksum-verified repository tooling prepared Maven 3.9.11 and Gradle 8.13. The Pixel_10_Pro Android 37 emulator and new local Pixel Tablet Android 37 emulator used `adb reverse tcp:8080 tcp:8081` without changing the checked-in endpoint; the emulator-tablet debug URL was set to `http://127.0.0.1:8080/api/v1`. An initial emulator-phone System UI startup dialog recovered after selecting Wait. Separately, the physical Xiaomi Pad 7 Android 16 tablet `a04cd04e` installed the checksum-matched `.debug` APK after user approval; its debug-only URL was set to `http://127.0.0.1:8080/api/v1` with pinned ADB reverse, and its Local server test reported `Successful`. No physical phone was connected.

The user's earlier logs/screenshots confirm that Increment01 started locally, migrated V1 and launched on Android. They do not prove this increment's runtime or the still-unexecuted fresh/V1-clone migration matrix; the retained development database's in-place upgrade through V5 is separately evidenced above.

## Source coverage — no completion percentage claim

Of the 203 frozen reference routes/states:

| Mapping | Count |
|---|---:|
| Direct native route source present, not runtime verified | 128 |
| Direct native route, bounded local emulator flow verified | 5 |
| Partial/merged subflow source, not a separately matched page | 55 |
| Not implemented as the reference route | 7 |
| Reference/demo-only simulation controls, excluded from production | 8 |

Some direct routes are limited forms and some reference pages are merged. The three resident join routes have bounded emulator evidence; Invite controls additionally have bounded physical Pad 7 tablet QA. Route-map labels remain source/emulator-oriented and do not assert whole-app parity; all other routes remain `NOT_VERIFIED`. These numbers are **traceability, not percentage complete**. See `SCREEN_IMPLEMENTATION_MAP.json` and `SCREEN_COVERAGE.md`.

## Local acceptance evidence to collect

Next create a safe test backup/clone for the fresh and V1→V2→V3→V4→V5 paths. Then retest the corrected Offline-login return path, true session expiry and induced server outage, extend APK testing to real two-account approval, and inspect every production route in both themes on supported device sizes. Bounded Invite-controls tablet checks are not tablet-wide acceptance.

HTTP scripts never delete test tenants or reset databases. Historical runs retained labelled synthetic tenants. The latest invite suite retained `INVITE QA A/B c762e7cf` (33 checks), core API retained `TEST A/B c7a882a5` (50 checks), operations retained `TEST02 A/B 4c2af10d` (112 checks), and the wrong-key probe retained `INVITE KEY QA 0d1ba4e0` (4 assertions). Their logs contain no passwords or bearer tokens. **Do not publish this development increment.**
