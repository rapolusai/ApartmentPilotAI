# Test evidence — cumulative implementation 02

**This evidence includes successful local framework builds, PostgreSQL/Flyway validation and HTTP integration checks. It is not a claim of device/UI or external-provider acceptance.**

## Actually executed in this workspace

| Check | Observed result | What the result establishes |
|---|---|---|
| Maven test | **14 tests passed; 0 failures/errors/skips** | Spring Boot production and test sources compile on Java 21; current JUnit suite is green. |
| Maven package | **BUILD SUCCESS** | Executable backend jar was produced from current source. |
| PostgreSQL/Flyway startup | **PostgreSQL 18.6; schema V4; 5 migrations validated** | The existing `partmentpilotai_dev` database retained V1/V2/V3 checksums and applied additive V4 successfully. This is not the fresh/V1-clone migration matrix. |
| Live health/status | **health UP; version 0.2.0; localRegistration true; releaseReady false** | Current backend ran against PostgreSQL on localhost port 8081. Port 8080 was occupied by system `AgentService`. |
| Local API suite | **50 checks passed** | Authentication lifecycle, role-card non-escalation, tenant isolation, bill/reminder previews, in-app reminder idempotency, rejection persistence, atomic two-payment approval, receipts, notices and logout were exercised over HTTP. |
| Operations API suite with concurrency | **112 checks passed** | Owner/Tenant intake validation and persistence; join-review/rejection authorization, persistence and flat release; Admin-only block creation/rename/idempotency and tenant isolation; affected flats, notices, contributions, transparency, files, bookings, polls, vehicles, subscriptions and parallel one-winner approval were exercised over HTTP. |
| Android clean debug build | **BUILD SUCCESS; 41 tasks executed** | Current native Java/XML sources compiled, resources linked and a fresh debug APK was packaged. |
| Android debug unit tests | **2 tests passed; 0 failures/errors/skips** | Authenticated 401/403/404/5xx/network failures select dedicated states while unauthenticated login/invite and 400/409 failures remain inline. This is policy coverage, not device navigation evidence. |
| Real JDK21 pure production-rule tests | **580 assertions passed** | 21 original domain/token checks plus 559 new operational assertions. The latter include **500 deterministic occupancy scenarios**, so these are not 580 end-to-end feature tests. |
| Java syntax parsing | **40 compilation units; zero syntax errors** | Syntax only; does not resolve framework imports or all semantic types. |
| Literal JDBC SQL argument inspection | **284 calls; zero placeholder mismatches** | Counts `?` parameters against source arguments. Dynamic SQL/varargs arrays excluded. New block and join-review persistence SQL was also exercised live; the static scan alone does not execute SQL. |
| Literal native navigation inspection | **104 targets; zero missing switch destinations** | Recalculated from current literal `go(...)`/`openPage(...)` calls and 155 route dispatch identifiers. Only literal source links; no Android navigation/runtime test. |
| XML parsing | **44 XML files well-formed** | Not `aapt`/resource linking, layout inflation or screenshot testing. |
| Native resource-name references | Passed | Referenced project names exist; not SDK linking. |
| Restricted attachment configuration | Static checks passed | Non-exported FileProvider restricted to shared cache, app backup disabled. Not a security penetration test. |
| Original V1 migration | **Unchanged** | SHA256 equals Increment01 V1 bytes. |
| Frozen v2.1 HTML | **Unchanged** | SHA256 equals original frozen reference. This does not mean native screens visually match it. |

### Additional limited compilation checks
Backend services/controllers (27 source files, excluding security configuration, error handler and bootstrap) and Android `FeatureScreens`/`FeatureHost` were compiled against **private, hand-written approximation stubs**. Those checks found no errors in their covered signatures/types. They are weaker than builds against the actual libraries and are **NOT Maven or Android build passes**. The stubs are not included as application dependencies.

Raw outputs are in `docs/evidence/`; machine-readable scope and exclusions are in `TEST_EVIDENCE.json`.

## What still has NOT run here

- Fresh-database Flyway migration and a backed-up V1-only clone upgraded through V2/V3/V4. The existing development schema was migrated in place through V4 only.
- APK installation, emulator/physical-device testing, rotation, process recreation, accessibility or screenshot comparison.
- Instrumentation tests and broader Android view/session persistence tests; only the two HTTP failure-policy unit tests currently exist.
- `Apply-Update.ps1` remains source-reviewed, not runtime-tested.
- Real OTP, FCM, store purchase verification, payment settlement or installation attestation.

The Windows machine provided JDK 21.0.10, PostgreSQL 18.6 and Android SDK support. The checksum-verified repository tooling prepared Maven 3.9.11 and Gradle 8.13. No emulator or authorized physical-device run was performed in this checkpoint.

The user's earlier logs/screenshots confirm that Increment01 started locally, migrated V1 and launched on Android. They do not prove this increment's Android runtime or the still-unexecuted fresh/V1-clone migration matrix; the current development database's in-place upgrade through V4 is separately evidenced above.

## Source coverage — no completion percentage claim

Of the 203 frozen reference routes/states:

| Mapping | Count |
|---|---:|
| Direct native route source present, not runtime verified | 132 |
| Partial/merged subflow source, not a separately matched page | 55 |
| Not implemented as the reference route | 8 |
| Reference/demo-only simulation controls, excluded from production | 8 |

Some direct routes are limited forms and some reference pages are merged. All have `ui_parity: NOT_VERIFIED`. These numbers are **traceability, not percentage complete**. See `SCREEN_IMPLEMENTATION_MAP.json` and `SCREEN_COVERAGE.md`.

## Local acceptance evidence to collect

Next create a safe test backup/clone for the fresh and V1→V2→V3→V4 paths. Then install the freshly built APK, test real two-account approval and session-expiry/offline behavior, and inspect every production route in both themes on supported device sizes.

HTTP scripts never delete test tenants or reset databases. Runs `35a5bf91` (35 checks), `04a596d2` (60 checks), `3274c19f` (50 checks), `b43add73` (74 checks), `609d78dc` (84 checks), `1b9012a7` (86 checks), `94c613cf` (89 checks), `1ae51e73` (100 checks), `1ccd970e` (111 checks) and `bcb91bec` (112 checks) retained labelled synthetic tenants. The latest core API rerun retained tenants under `d6de74c8`. Their logs contain no passwords or bearer tokens. **Do not publish this development increment.**
