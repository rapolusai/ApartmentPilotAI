# Login and session handling — 27 September 2026

Status: **IN PROGRESS**, not QA-approved or release-ready. The user subsequently authorized automatic sequential implementation, so remaining Login acceptance/provider gates are deferred while Registration progresses. Independent QA remains on hold. Evidence below belongs to its named Login APK, not automatically to later registration builds.

## Scope and existing functionality reused

Native Admin, Treasurer and Resident login; input validation; role switching; honest unavailable-recovery state; encrypted session persistence; server expiry/revocation; safe reauthentication; confirmed logout. Reuses AuthService, BCrypt, hashed PostgreSQL sessions, server-derived Account/tenant, database-backed RateGate, Android Keystore SessionVault and HTTP failure routing. No backend production logic or applied migration was rewritten.

The frozen HTML remains unchanged. Demo autofill is reference-only, not a production credential bypass. Verified mobile ownership and real Forgot PIN recovery remain NOT_CONFIGURED and separately pending; this checkpoint does not implement them.

## Changes

- `MainActivity.java`: immediate mobile/PIN validation; account-bound destination recovery after expiry; discard old account drafts/selections on a different login; complete sign-out cleanup; role heading/subtitle/icons, compact login tools and setup/join links; role-specific return from unavailable recovery.
- `data/LoginPolicy.java`: pure validation, role-route and same-account resume rules.
- `data/LoginPolicyTest.java`: six unit tests.
- `ui/Ui.java`: password transformation is applied after single-line configuration so the PIN remains masked; native snapshot changed from password=false to password=true. Native Material placeholders avoid overlapping the field labels.
- `security/AuthLoginTest.java`: six backend unit tests for denied/approved authentication, server identity, hash-only sessions and logout.
- `scripts/Test-Login-Sessions.ps1`: localhost-only retained synthetic HTTP/PostgreSQL checks, including actual expiry and role-change revocation.
- `scripts/Test-Login-Emulator.ps1`: dedicated emulator-only native checks, synthetic credentials held in memory; fresh hierarchy captures.

## Executed evidence

| Gate | Result | Boundary |
|---|---|---|
| Reference requirements/source review | Passed | Inspected renderSignIn and session-expired definitions; not a rendered visual score |
| Backend unit regression | 39/39 passed | Includes six new Login tests; no real database in unit tests |
| Android unit/build | 23/23 passed; debug APK built | Includes six LoginPolicy tests; no claim that unit tests prove UI/runtime |
| Real HTTP + PostgreSQL Login/session suite | 42/42 passed | LOGIN QA A/B 16e7c3df retained; correct PIN/role, denial, expiry, logout, role revocation and rate limit |
| Native phone emulator runtime | 46/46 bounded checks passed on the frozen final APK | Core 11, outage 3, role/layout 16, real Resident/Treasurer 10, Android Back/rotation 6; emulator-5580 only |
| Native tablet emulator runtime | 37/37 bounded checks passed on the same frozen APK | Role/layout 16, real Resident/Treasurer 10, same-account expiry/recovery suite 11; emulator-5582 only |
| Phone/tablet portrait/landscape and both themes | Bounded reachability and role/layout checks passed | Fresh phone and tablet screenshots reviewed for overlap; exhaustive accessibility and scored parity remain open |
| Scored >=95% visual parity | Blocked / not measured | Browser policy rejects local file:// reference; screenshots requested, no bypass attempted |
| Independent QA | On hold | Not handed over as complete |

Frozen final placeholder/masking candidate APK SHA256: `E3B897C9CDDCAA383014979D9F403CCBA7AB12E9F7C0EB5D923C6A4ECE551025`.

Reference SHA256: `FF5554324E630F3B63235EC723E094BE6CDFF02DBF434898BED5D6F290F3AE28`.

The first database script run omitted the dedicated schema qualifier and stopped after ten HTTP checks. The script was corrected, and the full 42-check run passed. A later Android build caught an accidentally duplicated helper declaration; it was removed and the final build/tests passed. These failed attempts are not counted as successful evidence.

Earlier native fixtures: `LOGIN UI A/B fcf2f5b7`. That initial retest printed eleven passes, but two process-restart assertions used an old hierarchy after Android returned an empty root. Those two were discarded and independently rerun successfully with unique fresh dump files. The helper now fails/retries missing captures rather than consuming stale XML. The first outage attempt did not interrupt the existing request path; a fresh-process controlled outage then showed Connection paused and recovered to Home. Both attempts restored the emulator's original 8080-to-8081 reverse mapping. No other device mappings were changed.

After the role-screen test detected password=false on the PIN input, the shared field builder was corrected. The final candidate reports password=true. All eleven Login/session runtime checks were then rerun successfully on that exact APK with **fresh captures**, retaining `LOGIN UI A/B 2c0dd739`. The earlier candidate's results are not substituted for this retest.

Candidate `570B4E16...` then passed 16 bounded role/layout checks and ten real native Resident/Treasurer checks (`LOGIN ROLE UI fbcff73d`). Screenshot inspection nevertheless caught overlapping mobile/PIN labels and placeholders. Material placeholders replaced EditText hints; final candidate `E3B897C9...` built with 23/23 unit tests. Its complete bounded phone rerun passed 46 checks, retaining `LOGIN UI A/B 301e6ce3` and `LOGIN ROLE UI 1470dd89`. The same APK passed 26 bounded tablet checks, retaining `LOGIN ROLE UI 459e415b`. Fresh portrait/landscape phone and tablet screenshot inspection confirms the overlap is removed. Presence/reachability assertions alone are not a visual-parity pass.

The physical phone and ParkingPilotAI were not modified. Only emulator-5580 and the dedicated headless tablet emulator-5582 received the APK. No schema/data reset occurred; labelled test fixtures remain available. Secrets are not recorded in this report.

The additional tablet same-account run initially stopped at process restart when the hierarchy was transiently empty; it did not reach the recovery assertion and is not counted as a pass. Expected-screen assertions now wait for up to three fresh captures, rather than accepting an empty hierarchy. The full rerun passed 11 checks with `LOGIN UI A/B 069234a4`: actual database expiry led to reauthentication, and the same account resumed the original private Invite destination with authenticated controls reloaded. This does not prove restoration of a private form draft.

## Progress definitions

All 42 executed focused backend/database checks passed (100% of that bounded test set). This is **not** 100% functionality completion. Overall fully accepted inventory completion remains **0%** while acceptance gates remain open. The last recorded source-coverage baseline is frontend 75.4%, backend 70.8%; these percentages are not whole-flow acceptance.

## Acceptance still required

- Scored frozen-reference comparison for all three login types; reference screenshots requested because local-file browser access was rejected.
- Broader accessibility checks beyond the bounded phone/tablet role/layout suite.
- Further native negatives: long timeout/HTTP 5xx, repeated taps while a request is slow, background/rotation during an in-flight login, and same-account private-draft recovery. Android Back and non-secret form restoration passed six native checks. Current policy/unit/API evidence is not a substitute for the remaining native scenarios.
- Independent QA after the Lead's remaining gates pass; no knowingly incomplete flow has been handed over.
- Verified mobile recovery requires a real approved provider/configuration; provider choice was requested. The unavailable page is an honest limitation, not a completed Forgot PIN implementation.
