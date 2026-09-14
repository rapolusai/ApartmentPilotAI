# Implementation status — cumulative increment 02

**FRAMEWORK BUILDS AND LOCAL API CHECKPOINT VERIFIED. NOT PRODUCTION READY. NOT FULLY END-TO-END OR UI VERIFIED.**

On 15 September 2026 the current increment02 source compiled with Java 21, produced a fresh debug APK, validated Flyway V1/V2/V3/V4 on the existing PostgreSQL 18.6 development database, and passed the available local HTTP suites. This is meaningful framework/API evidence, but it does not establish device behavior, every acceptance scenario, a fresh-database/V1-clone upgrade path, complete 203-route parity, or live provider integrations.

## Verified local checkpoint — 15 September 2026

- `backend\mvnw.cmd test`: **14 JUnit tests passed**, 30 production source files compiled with Java 21.
- `backend\mvnw.cmd package`: **BUILD SUCCESS**, executable `0.2.0-SNAPSHOT` jar produced.
- Android clean debug build with the repository-prepared Gradle 8.13 runtime: **BUILD SUCCESS**, 41 tasks executed and a fresh debug APK produced.
- Android `:app:testDebugUnitTest`: **2 tests passed** for authenticated/unauthenticated HTTP failure routing policy.
- PostgreSQL `partmentpilotai_dev` reported version **18.6**. Flyway successfully validated five history entries and additively migrated the existing schema through version **4**.
- `/actuator/health`: `UP`; `/api/v1/status`: version `0.2.0`, local registration enabled, `releaseReady: false`.
- `Test-Local-Api.ps1`: **50/50 HTTP checks passed**, including finance previews, reminder idempotency, rejection persistence and atomic two-payment approval.
- `Test-Operations.ps1 -Concurrency`: **112/112 checks passed**, including validated and persisted Owner/Tenant intake, reasoned one-time join rejection and flat release, scoped join-review details, persistent block creation/rename/idempotency/tenant isolation, staff-only affected-flat enumeration, notice scheduling, contribution/transparency boundaries and one-winner concurrent booking approval.
- The local backend used port **8081** because port 8080 was occupied by the system `AgentService`; the checked-in Android endpoint was not changed.
- Test suites intentionally retained their clearly labelled synthetic tenants for inspection. No database records were deleted.

The first post-build Android module is now source-complete and clean-build verified: dedicated Admin/Treasurer/Resident login routes, invalid invite, separate flat choice, setup completion, centralized 401 invalidation, safe destination resume, and offline/session-expired/access-denied/empty/error/not-found/loading states. OTP-backed recovery remains explicitly `NOT_CONFIGURED`, and none of these routes is marked device-runtime or visual-parity verified.

The maintenance review flow now has dedicated native `bulk-approval`, `bulk-review`, `payment-reject`, `outstanding`, `reminder-preview` and `bill-preview` destinations. Server-backed previews derive eligible flats, effective rates/overrides, existing bills, balances and recipients from the authenticated tenant. Manual reminders are request-key idempotent and create only in-app inbox records; external push remains `NOT_CONFIGURED`. These routes clean-build successfully but remain device-runtime and light/dark visual-parity unverified.

The contribution flow now has a dedicated native `contribution-preview` destination. The backend resolves only active flats inside the authenticated tenant, validates selected comma-separated labels or a single parent-checked flat ID, returns the exact count/total for review, and creates the frozen selection idempotently. The native `transparency` destination previews actual monthly totals and edits the full server settings document; disabling visibility now denies resident aggregate reports as well as individual expense rows. The expanded 86-check live suite passed these boundaries, while both native routes remain device-runtime and visual-parity unverified.

Notice authoring now has dedicated native `notice-templates` and `notice-preview` destinations. Additive V3 stores type/category/phone intent and separates a future draft timestamp from explicit idempotent schedule confirmation. Preview recipients are resolved by the backend within the authenticated tenant, residents and foreign tenants cannot open the preview, and automation only publishes confirmed schedules. External phone delivery is explicitly `NOT_CONFIGURED`; successful publishing means authenticated in-app inbox delivery only. The expanded 89-check live suite passed these server boundaries, including a due confirmed schedule becoming resident-visible and clearing its scheduled state, while the two native routes remain device-runtime and visual-parity unverified.

The issue flow now has a dedicated native staff-only `affected` destination. The backend parent-checks the issue within the authenticated tenant, rejects resident and foreign-tenant enumeration, excludes inactive or unassigned accounts and returns only one active identity per distinct affected flat. The expanded 89-check live suite passed these boundaries; the native route remains device-runtime and visual-parity unverified.

Apartment administration now has dedicated native `blocks` and `block-form` destinations. Additive V4 seeds persistent block parents from existing flat labels and adds a tenant-scoped parent constraint. Admins can create or rename blocks idempotently; a rename updates each flat's block membership without changing its label. Residents, duplicate names, unknown flat parents and foreign-tenant block IDs are rejected. The expanded 100-check live suite passed these boundaries; both native routes remain device-runtime and visual-parity unverified.

Resident intake now has dedicated native `join-requests`, `join-review` and `join-reject` destinations. The request list provides the reference Pending/Approved/Rejected states, signup persists the selected Owner/Tenant type, and review displays that type with the request timestamp. Member detail is fetched through an Admin-only tenant-scoped endpoint. Rejection requires and retains a resident-facing reason, rejects guessed foreign-tenant IDs, prevents repeated decisions and releases the flat for a corrected request while the rejected account remains unable to sign in. The expanded 112-check live suite passed these boundaries; the native routes remain device-runtime and visual-parity unverified.

## Written and connected at source level

| Area | Code present in increment02 | Validation boundary |
|---|---|---|
| Identity | Local registration, invitation/approval, account role checks, PIN change, encrypted session storage, role revocation, basic handover | No live phone verification, recovery or refresh-token implementation |
| Community | Apartment/flat records, members, committee titles, vendors/contacts, public/private documents, polls and vehicles | No multi-apartment account membership or reviewed local-language translations |
| Money | Existing maintenance/payments plus overrides, late fees, opening cash/dues, contributions, other income, recurring drafts, reversals, reports/cashbook | Full accounting reconciliation/security/runtime testing remains |
| Attachments | Database-backed file service, ownership checks, deduplication and native view/share/PDF exports | File-signature checks are not malware scanning; cloud object storage and quotas are future operational work |
| Communication | Targeted/scheduled notices, recipient snapshots, read/acknowledgement, in-app inbox | No real FCM/SMS/WhatsApp transport, notification-permission flow or delivery receipt from an external provider |
| Incidents/services | Common issue/private complaint lifecycle, follow/history/vendor/ETA, preventive schedules, completion-linked expenses | No emergency response guarantee; no vendor login/app |
| Events/parking | Availability, configured capacity, private owner release, physical conflicts, request/approve/offer/accept/cancel/complete, calendar | No live sensors, visitor gate system, payment/deposit integration or guaranteed native pixel parity |
| Software subscription | Plan/invoices/status, referral code and graph, one-time first-period award, independent-operator interfaces | No Google Play purchase verification/webhook integration; overdue status does NOT yet enforce production access restrictions |
| Automation | Server-scheduled jobs with duplicate-safe database commands and manual run from Admin/Treasurer | Needs real PostgreSQL/concurrency and missed-run/catch-up validation |
| Android | Native Java/XML components and connected operational pages, encrypted sessions, date/time pickers, local-server configuration | Fresh debug APK build passed; device/accessibility tests and final screen-by-screen UI matching have not run |

## Critical remaining work before public launch
1. Verify phone ownership and implement secure recovery/number change; no universal/test OTP may become a production bypass. Reconcile one-person/multiple-apartment and owner/tenant lifecycle requirements.
2. Connect real push delivery with device token handling/retries and user notification preferences. In-app inbox writes are not push delivery.
3. Choose and integrate a compliant live subscription purchase flow and backend provider verification. Define cancellation/refund/reconciliation handling and server-enforced subscription entitlements/grace/read-only policies. Existing subscription status is informational, not a production paywall.
4. Replace manual operator activation checks with the agreed independently verifiable onboarding/install signal where required; use no client-side self-approval or hardcoded paid state.
5. Finish the native screen inventory and visual/accessibility matching. Some related reference subpages are combined into forms or inline details in this increment; this is NOT proof that every subpage matches the frozen design.
6. Complete the remaining database/device gates: fresh database plus backed-up V1-only clone upgrade through V4, broader tenant/concurrency suites, Android install/runtime tests, process recreation, rotation, accessibility and screenshot comparison.
7. Production secrets/least-privilege database roles, TLS, data retention/deletion, backup/restore verification, upload scanning/storage quotas, monitoring, rate/load tests and release signing/privacy review.

## Important operational limitations
- Still local-only registration. Release build guard remains; there is no seeded production account or universal password.
- One phone number identifies one global account; multiple apartments per identity are not supported. Disabled accounts cannot re-register the same phone into a different apartment without a future managed lifecycle flow.
- Handover revokes old access but does not delete financial records. Paid records are corrected through explicit full reversals; partial refunds/credit notes and a complete bank-reconciliation engine are not implemented.
- Opening cash is set once, with an effective first-of-month date. Signed bank statements/reconciliation are outside scope. Opening dues are per-flat arrears, not income already received.
- Free event bookings only in this increment. No guest payment, deposit or venue charge is collected. Requested times must include setup/cleanup; maximum booking duration is seven days.
- Bookings/resident requests that are pending do not hold capacity. Acceptance/approval must recheck availability. Admin decisions are separate from treasurer financial permissions.
- Same-category incident suggestions are not machine-learning deduplication or an automatic ticket merge. Follow count reflects active distinct flats; subscribers without a flat do not inflate it.
- Recurring service/expense dates use calendar-month addition with short-month clamping. Monthly subscription cycles use the original activation anniversary anchor. Recurring expense catch-up is bounded to twelve cycles per run.
- Referral reward window is **one calendar month from apartment creation/activation in this local model**, not thirty extra days from the second referral. All qualifying apartments must still be within their own first period; only the first invoice can be waived once. These commercial assumptions require review before deployment.
- Attachments are bytea in PostgreSQL for local simplicity. They are capped per file and record, but total-tenant quotas and malware scanning are not implemented.
- Runtime errors, empty states and related subpages use shared components. The HTML remains unchanged; native UI parity is separately **NOT VERIFIED**.

## Evidence discipline
`TEST_EVIDENCE.md` separates executed pure-rule/static checks from supplied HTTP/Maven/Android suites. Do not convert route counts or generated Java file counts into a percentage of finished, tested product.
