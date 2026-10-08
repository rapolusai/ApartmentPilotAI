# Building issues board — Lead checkpoint

Date: 29 September 2026  
Scope: native Building issues board shortcuts, search and Active/Following/Resolved/All filters, plus report-category/form/detail, staff resolution and issue-update timeline checks.

## Result

- Local API/PostgreSQL operations regression: **111/111 checks passed** on rebuilt backend `http://127.0.0.1:18081/api/v1`; evidence: `C:\Users\rapol\AppData\Local\Temp\ap-issues-test-evidence-20260929\operations-1a0a9eaa.json`. The new assertions verify a reporter is marked as following, while a staff account sees the same issue as not followed; affected-flat counts remain tenant-scoped. Existing cross-apartment issue and affected-flat denial checks also passed.
- Backend Java suite: **56/56 tests passed**. Spring Boot started against the local PostgreSQL database, validated all 9 migrations, and found schema version 8 already current; no migration was added.
- Android build: **40 Gradle tasks succeeded; 26/26 unit tests passed**. Candidate installed only on virtual `emulator-5554`. APK SHA-256: `622AC5BFF10302F0D708A4C4B7B3A89A840DD461F56BB684DCC1B94B73273163`.
- Native emulator checks: issue categories and Water form opened; one synthetic issue was submitted through the actual form and reached its backend-backed detail. The Active board displayed it, title search found it, Following included its reporter, and Resolved excluded the open issue. Staff resolved that issue with a reason through the native status form; the Resolved filter then displayed it and Active no longer did. A second synthetic comment/update was posted and both status and comment messages appeared in the detail timeline. Synthetic data was retained.
- The existing API list query did not expose account-specific follow state. Returning a boolean derived from the authenticated account enables Following without accepting a client-supplied user or tenant identifier.

## Source changes

- `backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java` — expose authenticated-user follow state in the tenant-scoped issue list and keep affected-flat aggregation tenant-bound.
- `android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java` — add live issue/request search, role-appropriate status filters, matching empty states, and the Contacts shortcut.
- `scripts/Test-Operations.ps1` — add resident/staff assertions for per-account follow state.
- `docs/SEQUENTIAL_IMPLEMENTATION_PLAN.md` — record this bounded Lead checkpoint.

## Remaining gates

- Independent QA remains on hold at the user's direction. No 95% visual score, tablet acceptance, or whole Issues-flow acceptance is claimed.
- Native checks do not yet cover all status transitions and error cases, resident follow/close/reopen behavior, tenant/role denial UI, attachments, or scheduled service workflows. The backend suite covers cross-apartment denial and the positive assign/resolve/close path; independent QA and wider UI regression remain pending.
- No production data or physical device was used. No records were deleted.
