# Resident approval verification — 27 September 2026

Status: Lead bounded functional checkpoint passed; independent QA remains on hold. This small flow follows the Registration bounded checkpoint under the user's automatic sequential authorization.

## Reference and implementation

The frozen reference's `approveClaim` opens a confirmation form with “I verified this resident and their flat.” `approve-claim` refuses unchecked submission. Existing code had only a generic confirmation.

- `FeatureScreens.java`: native confirmation with resident/flat details, explicit verification checkbox, initially disabled submit and Cancel. Submit is enabled only after verification, dismissed on submission and calls the real server with `{verified:true}`. Shared attribution is retained.
- `Requests.java`, `ApiController.java`, `AuthService.java`: explicit verification request flag; server refuses false/missing verification. Role, tenant parent checks, tenant lock, pending-only transition, audit and notification remain in force. This changes the approval request contract; callers must send the flag.
- `AuthApprovalTest.java`: three unit checks for no database mutation without verification, Resident denial and tenant-scoped audited approval.
- Test scripts: synthetic approvals now explicitly send `verified=true`; no production permission bypass. New emulator test checks dialog, cancellation, submit state, real persistence and audit.
- `API_CONTRACT.md`: new contract documented.

## Evidence

- Backend 47/47 unit regression tests passed; includes three approval tests.
- Android build and 25/25 unit tests passed.
- Updated Registration/approval HTTP+PostgreSQL 40/40 passed, retaining `REG QA c9d13a66`, including false-verification rejection, tenant/role denial and concurrent claim rollback.
- Updated Login/session HTTP+PostgreSQL 42/42 passed, retaining `LOGIN QA A/B a9fef5d3`.
- Updated Invite-control HTTP regression 33/33 passed, retaining `INVITE QA A/B c6367f2e`.
- Native approval: 10/10 checks passed on phone `emulator-5580` and 10/10 on tablet `emulator-5582`, both using the APK hash below. Includes disabled submit, explicit verification, Cancel preserving PENDING, real approval persistence and tenant-scoped audit. Retained fixtures: `APPROVAL UI fbf6073f` and `APPROVAL UI 8bc84dd7`. Screenshots: `test-results/approval-native-final.png` and `test-results/login-emulator-5582/approval-native-final.png`.
- First attempt stopped at the Members row because the test required the text child itself to be clickable; the locator now resolves its nearest clickable ancestor and follows the actual directory → Join requests route. That failed attempt is not counted as a UI pass. The tablet rerun retried a fresh XML capture after an initial null-root result; no stale capture was accepted.
- Visual >=95%, independent QA, wider rotation/accessibility/failure/retry scenarios remain pending. No whole-app release or completion claim.

Approval APK SHA256: `C2615F3265488E381178F81A2CF814514D2EA94E099C3141A877067DFD493A1E`.
The preceding Registration native phone/tablet 15/15 results belong to APK `35479E18...`, not this new build.

No new migration was needed for this approval guard. No records/schema history were reset. Physical devices and other projects were not modified. Bounded test suites above passed 100%; fully accepted overall inventory completion remains 0%.
