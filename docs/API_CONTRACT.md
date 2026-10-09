# API contract — cumulative increment 02

All ordinary APIs derive tenant/role from a bearer session. JSON bodies contain no trusted tenant or role assignment except explicitly guarded administrative operations. Use /status to confirm version0.2.0.

## Routes declared by the delivered controllers

### Changes — backend hardening 05 (auth/audit), 9 October 2026

- `POST /api/v1/auth/login`: additive response field `pinChangeRequired` (boolean): `true` when the PIN just used is shorter than six digits or weak (see PIN rule). Login still accepts 4–6 digits. Failed attempts are limited by `LoginGuard` instead of the old 10-per-15-minutes-per-mobile limit: 5 failures for the same mobile from the same client address, 20 for the mobile from any address, then backoff of 1, 2, 4… minutes (caps 60 and 15 minutes), and 30 failed logins per client address per 15 minutes across all mobiles. A request during a lock gets **429** with a `Retry-After` header (seconds) and `{"message":"Too many incorrect PIN attempts. Try again in N minutes."}`; it is refused before the PIN check and does not extend the lock, even with the correct PIN. A correct PIN clears the counters. Unknown mobiles get the same 401/429 answers as a wrong PIN. When the 20-failure lock first trips on a real account, the holder gets one inbox notice and the apartment audit gets one `LOGIN_LOCKED` row per lock episode. Thresholds are configurable under `app.login-guard.*`; `app.login-guard.enabled=false` restores the old per-mobile limit.
- PIN rule for `POST /auth/register`, `POST /auth/join` and `POST /ops/change-pin`: exactly six digits (400 `pin: Use a six-digit PIN.` from bean validation, or `Use a six-digit PIN.` from change-PIN) and not weak (400 `Choose a harder PIN. Avoid repeated digits, sequences such as 123456 and patterns such as 121212.`): all-same digits, ascending/descending runs and a short denylist are refused.
- `POST /api/v1/auth/join`: a `REJECTED` applicant (same apartment, same PIN) may resubmit for a **different** flat; the request returns to `PENDING` on the corrected flat and `RESIDENT_RESUBMITTED` records `Rejected · <old flat>` → `Pending · <new flat>`. If the corrected flat already has an active or pending resident: 409 `This flat already has a resident or a pending request…`. A `PENDING` request still keeps its flat (409 for a different flat).
- `GET /api/v1/audit`: optional `before` (`<createdAt>,<id>` of the last row of the previous page), `limit` (default 100, values above 200 are capped, below 1 → 400) and `action` (exact name, `[A-Z_]`, otherwise 400). Paged requests sort newest first with an id tie-break; start paging with `limit` (the no-parameter response keeps the old query exactly). Without parameters the rows and fields are as before. In `/audit` and `/audit/{id}`, `actor` is no longer `null` for rows without a person: `Automation` (scheduled automation), `Provider` (provider verification/settlement and their referral side effects) or `Security` (`LOGIN_LOCKED`).
- Scheduled automation writes its audit rows with no actor (`actor_id` null, shown as `Automation`) instead of the oldest Admin's id. A manual `POST /ops/automation/run` still records the person.
- `POST /api/v1/notices/{id}/pin`: a real change writes `NOTICE_PINNED` or `NOTICE_UNPINNED`; repeating the current state writes nothing.
- Edit commands with a record id in the URL (`/ops/flats/{id}`, `/ops/directory/{id}`, `/ops/committee/{id}`, `/ops/contacts/{id}`, `/ops/services/{id}`, `/ops/services/{id}/complete`, `/ops/tickets/{id}/status`, `/ops/tickets/{id}/comments`, `/ops/documents/{id}`, `/ops/resources/{id}`, `/ops/resources/{id}/blocks`, `/ops/bookings/{id}/decision`, `/ops/expenses/{id}`, `/ops/expenses/{id}/reverse`, `/ops/payments/{id}/reverse`, `/ops/recurring/{id}`, `/ops/vehicles/{id}`, `/ops/vehicles/{id}/contact`): the request key is bound to that id. Replaying the same key against a different id is **409** `Request key was already used for different details.` (it used to return the first record's result). A retry against the same id is still idempotent. Blocks, notices and recurring active/prepare were already bound.
- `POST /ops/members/{id}/role` and `POST /ops/members/{id}/end-access`: **409** `This is the last active Admin of the apartment. Make another member Admin first.` when the change would leave no active Admin (checked under the tenant lock, so two Admins demoting each other at once cannot both succeed).
- `POST /api/v1/provider/apartments/{id}/verify`: the member requirement is met by any active `RESIDENT` or `TREASURER` with a flat (was `RESIDENT` only).
- Framework client errors on authenticated routes now keep their real status with `{"message":...}` instead of a misleading 401: missing required query parameter or header → 400 (e.g. `GET /bills` without `month` → `month is required.`), wrong parameter type → 400, unsupported method → 405, unsupported content type → 415, unknown route → 404. Any other error that reaches the `/error` dispatch keeps its status and also carries a generic `message`. Requests without a valid session still get 401 first.

### Additions and verification guard — 27 September 2026

- `POST /api/v1/auth/register`: additive `address` (up to 300 characters) and `pinCode` (six digits) persist to the existing V2 tenant columns. The new native form requires both; legacy local test clients may omit them. Registration remains disabled outside the explicit local profile until verified-mobile onboarding is configured.
- `POST /api/v1/auth/join`: returns `status`, `apartmentName`, `flatLabel`, `reason`, a random `receipt` and `expiresAt`; it never issues a login token. Identical pending retries require the same PIN and tenant/flat identity and do not duplicate the resident. Rejected claims can be resubmitted with their existing PIN; old status receipts are revoked. Active/foreign accounts cannot be overwritten.
- `POST /api/v1/auth/join-status`: `{receipt}`; validates a 43-character opaque receipt, hashes it and reads only its bound request while unexpired. No account UUID/mobile lookup is accepted. Returns only status/apartment/flat/rejection reason; cannot authenticate or grant apartment access. Rate-limited to 60 requests per IP per 15-minute window. Android stores the receipt in a separate Keystore-encrypted vault, never the PIN.
- `POST /api/v1/members/{id}/approve`: now requires `{verified:true}` from an authenticated Admin. Missing/false verification is rejected; supplied client role/tenant never grants authority. Existing tenant lock, pending-only transition, tenant parent check, audit and notification remain. API callers must supply the explicit verification flag.
- `POST /api/v1/ops/apartment`: authenticated Admin only; requires `name` and `city` (nonblank, up to 80 characters), `address` (nonblank, up to 300 characters) and `pincode` (six digits). Values are trimmed and validated before mutation; supplied tenant/role cannot change the update scope. Legacy empty profiles must supply the required details when saving. Existing tenant lock and audit remain; no migration was added.

### Additions — 26 September 2026

- `GET /api/v1/ops/support`: only the authenticated requester's own records in their authenticated tenant.
- `POST /api/v1/ops/support`: `{topic, message, requestKey}`; topic is Payment approval, Account access, Flat mapping or Something else. Message is required, up to 1000 characters. Identical request-key retries return the original private record; changed retries return 409. Status RECORDED does not mean delivery to an external help desk.
- `GET /api/v1/audit/{id}`: Admin/Treasurer only, parent-checked within their tenant; foreign IDs return 404.
- `POST /api/v1/ops/subscription/offer`: Admin only, `{terms:true}` records offer consent and version, not qualification, verification or a reward. Repeat acceptance is idempotent.
- `POST /api/v1/ops/settings/{section}`: Admin/Treasurer only. Section is payment, billing, verification, reminders or booking. Only that section's fields can change. The server locks the authenticated tenant and merges current settings to avoid overwriting another focused form. Optional `requestKey` is automatically sent by Android; identical retries return the original result, changed retries return 409. A supplied `tenantId` or cross-section field is rejected. Existing full-document `/ops/settings` remains compatible.

| Method | Path | Controller |
|---|---|---|
| GET | `/api/v1/status` | `ApiController.java` |
| POST | `/api/v1/auth/register` | `ApiController.java` |
| POST | `/api/v1/auth/login` | `ApiController.java` |
| GET | `/api/v1/auth/invites/{code}` | `ApiController.java` |
| POST | `/api/v1/auth/join` | `ApiController.java` |
| POST | `/api/v1/auth/join-status` | `ApiController.java` |
| POST | `/api/v1/logout` | `ApiController.java` |
| GET | `/api/v1/me` | `ApiController.java` |
| GET | `/api/v1/flats` | `ApiController.java` |
| GET | `/api/v1/members` | `ApiController.java` |
| GET | `/api/v1/members/{id}` | `ApiController.java` |
| POST | `/api/v1/invites` | `ApiController.java` |
| GET | `/api/v1/invites/current` | `ApiController.java` |
| PUT | `/api/v1/invites/current/expiry` | `ApiController.java` |
| POST | `/api/v1/invites/current/revoke` | `ApiController.java` |
| POST | `/api/v1/invites/current/activate` | `ApiController.java` |
| POST | `/api/v1/members/{id}/approve` | `ApiController.java` |
| POST | `/api/v1/members/{id}/reject` | `ApiController.java` |
| POST | `/api/v1/members/{id}/treasurer` | `ApiController.java` |
| GET | `/api/v1/billing/rules` | `ApiController.java` |
| POST | `/api/v1/billing/rules` | `ApiController.java` |
| POST | `/api/v1/billing/generate` | `ApiController.java` |
| GET | `/api/v1/billing/preview` | `ApiController.java` |
| GET | `/api/v1/billing/reminders/preview` | `ApiController.java` |
| POST | `/api/v1/billing/reminders/send` | `ApiController.java` |
| GET | `/api/v1/bills` | `ApiController.java` |
| GET | `/api/v1/bills/{id}` | `ApiController.java` |
| GET | `/api/v1/payments` | `ApiController.java` |
| POST | `/api/v1/payments` | `ApiController.java` |
| POST | `/api/v1/payments/approve` | `ApiController.java` |
| POST | `/api/v1/payments/{id}/reject` | `ApiController.java` |
| GET | `/api/v1/receipts/{id}` | `ApiController.java` |
| GET | `/api/v1/expenses` | `ApiController.java` |
| POST | `/api/v1/expenses` | `ApiController.java` |
| GET | `/api/v1/reports/monthly` | `ApiController.java` |
| GET | `/api/v1/notices` | `ApiController.java` |
| GET | `/api/v1/notices/{id}` | `ApiController.java` |
| GET | `/api/v1/notices/{id}/preview` | `ApiController.java` |
| POST | `/api/v1/notices` | `ApiController.java` |
| POST | `/api/v1/notices/{id}` | `ApiController.java` |
| POST | `/api/v1/notices/{id}/publish` | `ApiController.java` |
| POST | `/api/v1/notices/{id}/schedule` | `ApiController.java` |
| POST | `/api/v1/notices/{id}/read` | `ApiController.java` |
| POST | `/api/v1/notices/{id}/pin` | `ApiController.java` |
| GET | `/api/v1/notifications` | `ApiController.java` |
| GET | `/api/v1/notifications/{id}` | `ApiController.java` |
| POST | `/api/v1/notifications/{id}/read` | `ApiController.java` |
| GET | `/api/v1/audit` | `ApiController.java` |
| GET | `/api/v1/ops/polls` | `ExtrasController.java` |
| GET | `/api/v1/ops/polls/{id}` | `ExtrasController.java` |
| POST | `/api/v1/ops/polls` | `ExtrasController.java` |
| POST | `/api/v1/ops/polls/{id}/vote` | `ExtrasController.java` |
| POST | `/api/v1/ops/polls/{id}/close` | `ExtrasController.java` |
| GET | `/api/v1/ops/vehicles` | `ExtrasController.java` |
| POST | `/api/v1/ops/vehicles` | `ExtrasController.java` |
| POST | `/api/v1/ops/vehicles/{id}` | `ExtrasController.java` |
| POST | `/api/v1/ops/vehicles/{id}/contact` | `ExtrasController.java` |
| GET | `/api/v1/ops/apartment` | `OperationsController.java` |
| POST | `/api/v1/ops/apartment` | `OperationsController.java` |
| GET | `/api/v1/ops/blocks` | `OperationsController.java` |
| POST | `/api/v1/ops/blocks` | `OperationsController.java` |
| POST | `/api/v1/ops/blocks/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/flats` | `OperationsController.java` |
| POST | `/api/v1/ops/flats` | `OperationsController.java` |
| POST | `/api/v1/ops/flats/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/directory` | `OperationsController.java` |
| POST | `/api/v1/ops/directory` | `OperationsController.java` |
| POST | `/api/v1/ops/directory/{id}` | `OperationsController.java` |
| POST | `/api/v1/ops/members/{id}/role` | `OperationsController.java` |
| POST | `/api/v1/ops/members/{id}/end-access` | `OperationsController.java` |
| POST | `/api/v1/ops/profile` | `OperationsController.java` |
| POST | `/api/v1/ops/change-pin` | `OperationsController.java` |
| GET | `/api/v1/ops/committee` | `OperationsController.java` |
| POST | `/api/v1/ops/committee` | `OperationsController.java` |
| POST | `/api/v1/ops/committee/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/contacts` | `OperationsController.java` |
| POST | `/api/v1/ops/contacts` | `OperationsController.java` |
| POST | `/api/v1/ops/contacts/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/settings` | `OperationsController.java` |
| POST | `/api/v1/ops/settings` | `OperationsController.java` |
| POST | `/api/v1/ops/opening` | `OperationsController.java` |
| GET | `/api/v1/ops/rate-overrides` | `OperationsController.java` |
| POST | `/api/v1/ops/rate-overrides` | `OperationsController.java` |
| POST | `/api/v1/ops/charges/preview` | `OperationsController.java` |
| POST | `/api/v1/ops/charges` | `OperationsController.java` |
| GET | `/api/v1/ops/income` | `OperationsController.java` |
| POST | `/api/v1/ops/income` | `OperationsController.java` |
| POST | `/api/v1/ops/expenses` | `OperationsController.java` |
| GET | `/api/v1/ops/expenses/{id}` | `OperationsController.java` |
| POST | `/api/v1/ops/expenses/{id}` | `OperationsController.java` |
| POST | `/api/v1/ops/expenses/{id}/reverse` | `OperationsController.java` |
| POST | `/api/v1/ops/payments/{id}/reverse` | `OperationsController.java` |
| GET | `/api/v1/ops/recurring` | `OperationsController.java` |
| POST | `/api/v1/ops/recurring` | `OperationsController.java` |
| POST | `/api/v1/ops/recurring/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/cashbook` | `OperationsController.java` |
| GET | `/api/v1/ops/tickets` | `OperationsController.java` |
| GET | `/api/v1/ops/tickets/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/tickets/{id}/affected` | `OperationsController.java` |
| POST | `/api/v1/ops/tickets` | `OperationsController.java` |
| POST | `/api/v1/ops/tickets/{id}/status` | `OperationsController.java` |
| POST | `/api/v1/ops/tickets/{id}/comments` | `OperationsController.java` |
| POST | `/api/v1/ops/tickets/{id}/follow` | `OperationsController.java` |
| GET | `/api/v1/ops/services` | `OperationsController.java` |
| GET | `/api/v1/ops/services/{id}` | `OperationsController.java` |
| POST | `/api/v1/ops/services` | `OperationsController.java` |
| POST | `/api/v1/ops/services/{id}` | `OperationsController.java` |
| POST | `/api/v1/ops/services/{id}/complete` | `OperationsController.java` |
| GET | `/api/v1/ops/documents` | `OperationsController.java` |
| GET | `/api/v1/ops/documents/{id}` | `OperationsController.java` |
| POST | `/api/v1/ops/documents` | `OperationsController.java` |
| POST | `/api/v1/ops/documents/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/files` | `OperationsController.java` |
| POST | `/api/v1/ops/files` | `OperationsController.java` |
| GET | `/api/v1/ops/files/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/resources` | `OperationsController.java` |
| POST | `/api/v1/ops/resources` | `OperationsController.java` |
| POST | `/api/v1/ops/resources/{id}` | `OperationsController.java` |
| POST | `/api/v1/ops/resources/{id}/release` | `OperationsController.java` |
| POST | `/api/v1/ops/resources/{id}/blocks` | `OperationsController.java` |
| DELETE | `/api/v1/ops/resource-blocks/{id}` | `OperationsController.java` |
| GET | `/api/v1/ops/availability` | `OperationsController.java` |
| GET | `/api/v1/ops/bookings` | `OperationsController.java` |
| GET | `/api/v1/ops/bookings/{id}` | `OperationsController.java` |
| POST | `/api/v1/ops/bookings` | `OperationsController.java` |
| POST | `/api/v1/ops/bookings/{id}/decision` | `OperationsController.java` |
| GET | `/api/v1/ops/subscription` | `OperationsController.java` |
| POST | `/api/v1/ops/subscription/install` | `OperationsController.java` |
| POST | `/api/v1/ops/referrals/claim` | `OperationsController.java` |
| POST | `/api/v1/ops/automation/run` | `OperationsController.java` |
| POST | `/api/v1/ops/notifications/read-all` | `OperationsController.java` |
| GET | `/api/v1/ops/tasks` | `OperationsController.java` |
| POST | `/api/v1/provider/apartments/{id}/verify` | `ProviderController.java` |
| POST | `/api/v1/provider/invoices/{id}/settle` | `ProviderController.java` |

## Command conventions
- Most new create/update financial/community commands require `requestKey`: a UUID retained on network retry. Reusing a key for different details is rejected; reusing the same successful command returns the existing result.
- Read response identifiers are UUID strings and SQL snake_case columns become camelCase JSON keys.
- Currency is exact decimal INR, never binary floating-point arithmetic on the server. JSON number or decimal string is validated explicitly in operational commands.
- Date fields: `YYYY-MM-DD`; billing month: `YYYY-MM`; event/notice timestamps: ISO8601 UTC instant. Android date/time entries are labelled IST and converted to UTC before submission.
- Failed access:401 unauthenticated,403 forbidden,404 scoped record not found. Validation400, optimistic/conflicting state409, request rate429 (login locks also send `Retry-After`). Missing/mistyped query parameters are 400, never 401. No stacktrace or credential response is intended.
- `/auth/register` and `/auth/join` require the explicit local profile. These are not verified production signup paths.

## Important shapes
`POST /auth/join` requires `residentType` as `OWNER` or `TENANT` and persists it with the pending account. `GET /members/{id}` returns the scoped flat, resident type, request timestamp and current decision state. Member detail and approval/rejection commands are Admin-only and tenant-scoped. Rejection requires a nonblank reason of at most 300 characters, moves only a pending request to `REJECTED`, retains the explanation, prevents sign-in and releases the flat for a corrected request. A repeated decision conflicts rather than silently rewriting the review.

`POST /ops/blocks` and `POST /ops/blocks/{id}`: name (1–8 characters),requestKey. Both are Admin-only and tenant-scoped. A rename cascades the block membership stored on flats but never rewrites a flat label; duplicate names are rejected. `POST /ops/flats` and `/ops/flats/{id}` require the supplied block to exist in the authenticated tenant.

`POST /ops/expenses`: title,category,amount,paidOn,mode,paid,visibleToResidents,notes,requestKey. Legacy POST/expenses remains for increment01 clients; the native increment02 expense form uses the operational endpoint to preserve payment mode.

`POST /ops/tickets`: kind ISSUE/COMPLAINT, title,description,category,scope,priority, optional staff-selected flatId,requestKey. Status command: status,note,optional vendorId/eta,notifyAll for staff/common issue only,requestKey. Explicit tenant checks apply to every ticket and its attachments. `GET /ops/tickets/{id}/affected` is staff-only, parent-checks the issue in the authenticated tenant and returns one active resident identity per distinct affected flat; private complaints are rejected.

`POST /ops/bookings`: title,eventType,guests,start,end,items [{resourceId,quantity}],acceptRules, optional flatId for Admin,requestKey. Decision: action APPROVE/REJECT/CANCEL/COMPLETE/OFFER/ACCEPT,revision,note,requestKey. OFFER also supplies proposed start/end/items. Requester must ACCEPT, and capacity is checked again inside the decision transaction.

`POST /ops/files`: kind DOCUMENT/TICKET/NOTICE/PAYMENT/EXPENSE,parentId,name,content (base64). No local filesystem path is sent. Content allowlist is PNG/JPEG/PDF; max5MB/max5 attachments per parent. Signature check is not a malware scan.

`POST /notices`: title,body,audience ALL/BLOCK/SELECTED/UNPAID,audienceValue,pinned,acknowledge,optional scheduledAt,publish,requestKey. Selected value is comma-separated existing flat labels; Block is an existing block label. Published notices are immutable except pin/read/acknowledgement.

Notice drafts also accept `noticeType` GENERAL/MAINTENANCE/SERVICE_ALERT/EMERGENCY, category and `phoneNotify`. `GET /notices/{id}/preview` is staff-only and returns the exact active-account recipient count derived inside the authenticated tenant plus `deliveryMode: IN_APP_ONLY` and `externalDeliveryStatus: NOT_CONFIGURED`. A future `scheduledAt` remains an unconfirmed draft until `POST /notices/{id}/schedule` receives a UUID `requestKey`; scheduling retries are idempotent. Only confirmed schedules are eligible for automation. Phone intent never reports external delivery success.

`POST /ops/settings`: full control set (payee,upi,bank,account,ifsc,billVacant,lateEnabled,lateFee,graceDays,dueNotify,reminders,reminderDays,expensesVisible,proofRequired,quietStart,quietEnd,bookingRules). This endpoint is not a partial PATCH. Read settings first before submitting an edited full body.

`POST /ops/charges/preview`: staff-only validation of kind `CONTRIBUTION`/`OPENING_DUE`, title,amount,month,dueDate and target flats. Use either one tenant-owned `flatId`, comma-separated active `flatLabels`, or scope `ALL`; `scope=SELECTED` requires labels. The response freezes the normalized labels, flat count and exact total for review. Submit those returned values to `POST /ops/charges` with a new `requestKey`; retries with that key do not duplicate bills. Opening dues require exactly one flat.

`GET /reports/monthly`: residents receive totals only while the authenticated tenant's `expensesVisible` setting is enabled. Staff retain access. Turning transparency off denies the entire resident report rather than leaking aggregate expense totals.

`GET /billing/preview?month=YYYY-MM`: staff-only, tenant-derived generation preview with the effective base rate, active/eligible flat count, flat-override-adjusted scheduled total, existing maintenance-bill count and due date. `GET /billing/reminders/preview` excludes paid and in-review bills. `POST /billing/reminders/send` requires `requestKey`; retries with the same key do not duplicate inbox records. Responses explicitly report external delivery `NOT_CONFIGURED` until a real provider is connected.

`POST /ops/services/{id}/complete`: scheduledOn,completedOn,note,cost,paid,requestKey. Each scheduled cycle creates at most one completion/linked expense.

`POST /ops/polls`: title,description,closesAt,options [2–6 distinct labels],requestKey. Vote body: optionId. One vote per flat, updateable until close.

`POST /ops/vehicles`: registration,kind CAR/BIKE/OTHER,parkingLabel,requestKey, optional Admin-selected flatId. Contact command:message,requestKey. Phone numbers are not exposed by this workflow.

Provider routes use an independent operator header, not apartment bearer privileges. See PROVIDER_BILLING.md. They are manual operator interfaces, not a connected payment gateway.

Invite controls are Admin-only and tenant-derived. `POST /invites` requires a UUID `requestKey`; a retry returns the same issuance, while a new key invalidates the previous code without revoking approved residents. `GET /invites/current` returns `exists`, `active`, `valid`, `codeAvailable`, `displayConfigured`, nullable `code`/`displayError`, `expiresAt` and inclusive Asia/Kolkata `expiresOn`. Legacy hash-only codes and ciphertext protected by a mismatched server key are not redisplayed. The latter returns an explicit recovery warning with no code; activation fails 503, while a separately confirmed rotation may replace it. `PUT /invites/current/expiry` accepts `{"expiry":"YYYY-MM-DD"}` from today through one year; the date remains valid through its local end of day. Issuance/activation without `APP_INVITE_DISPLAY_KEY` is `NOT_CONFIGURED`/503. Resident code lookup uses the existing hash and remains denied after revoke/rotation.

## Executable request examples
Recurring expenses are staff-only and tenant-derived. `GET /ops/recurring/{id}` returns the selected template and optional vendor name. Create/update accepts optional `vendorId`; a supplied ID must refer to an active vendor in the same tenant. Omission on update preserves the existing link; an empty value explicitly removes it. V7 snapshots the linked vendor on newly prepared expenses without rewriting existing records.

`POST /ops/recurring/{id}/active` accepts boolean `active`, boolean `expectedActive` and UUID `requestKey`. A stale expected state fails 409. `POST /ops/recurring/{id}/prepare` accepts `cycle` (YYYY-MM-DD, exactly the template's current `nextOn`) and UUID `requestKey`. It creates or returns one unpaid draft for that cycle under the tenant lock, never posts cash or verifies payment. A valid retry returns its original draft even if the template later pauses or advances; changing the target with the same key is rejected. Explicit paid confirmation advances a still-current manual cycle once. Existing scheduled automation catch-up behavior remains unchanged and reuses an already prepared cycle rather than duplicating it.

Payment tracking: authenticated `GET /payments/{id}` returns server-scoped declaration details, status, optional receipt/rejection/reversal information and `canAttach`. Residents are limited to their own flat; foreign tenants receive 404 and another flat in the same tenant is denied. Internal submitter IDs are not exposed. `GET /bills/{id}` includes the authoritative nullable `pendingPaymentId`. Payment proof continues through the existing parent-checked file API; proof upload is permitted only while pending and does not verify bank settlement.

See scripts/Test-Local-Api.ps1 and scripts/Test-Operations.ps1. These tests create only synthetic local records. Both have been run against the local PostgreSQL development database; the latest observed counts and exclusions are recorded in TEST_EVIDENCE.md.

## Backend hardening 05 (domain) behaviour
Details, files and tests: BACKEND_HARDENING_05_domain.md.
- `POST /payments/approve`: two-person rule. If the signed-in staff member (taken from the server session) submitted any selected payment that is still `PENDING`, the call returns **409** "You submitted a selected payment. Another Admin or Treasurer must approve it. No payments were approved." and nothing in the batch is approved. There is no single-staff exception: an apartment with only one Admin must appoint a Treasurer to approve payments the Admin records. Already-approved payments in the selection are still counted as `alreadyApproved` (retries unchanged). The submitter may still reject their own pending payment. The database enforces the same rule for new approvals (V12 check constraint).
- `POST /billing/generate?month=YYYY-MM`: a month after the current Asia/Kolkata month returns **400** "Bills can be generated only for the current month (YYYY-MM) or earlier." Scheduled automation only ever generates the current month.
- `POST /ops/opening`: `amount` is required (**400** "Enter the opening cash amount (0 is allowed)." when missing or blank). An explicit `0` is still accepted.
- `POST /ops/tickets/{id}/status` (staff): `vendorId` and `eta` are optional. **Omitting a key keeps the stored value.** Sending the key with `null` or `""` clears it (a vendor cannot be cleared on an `ASSIGNED` transition). A supplied `vendorId` must be an active contact in the tenant. Residents can never change either field.
- `GET /ops/services/{id}`: residents get **404** for an inactive service (the list already hides it). Staff still see it.
- `GET /ops/resources` and `GET /ops/availability`: every row has the additive boolean `privateSpace`. For a private space, `privateFlatId`, `ownerConsent`, `availableFrom` and `availableUntil` are returned only to Admin/Treasurer and to users of the owning flat. Other residents do not receive these keys. `/ops/availability` still never returns `privateFlatId`, and the `available` count is unchanged.
- `GET /ops/vehicles`: users of a flat also see their own flat's inactive vehicles (`active=false`). Other residents still see active vehicles only, and the Admin sees all.
- `POST /notices` SELECTED audience: labels are trimmed, upper-cased and de-duplicated before they are checked and stored (`" a-101 ,A-103"` is stored as `A-101,A-103`). Matching recipients is case-insensitive.
- `POST /provider/apartments/{id}/verify`: idempotent. A repeat (with a valid provider key) keeps the original `verified_at` and `verified_by`, writes no second audit row and awards nothing again. The response is `{verified:true, alreadyVerified, verifiedAt, verifiedBy}` (the last three fields are additive).
- `POST /ops/polls/{id}/close`: closing an already closed poll returns `{ok:true, closed:true, alreadyClosed:true}` and writes no second `POLL_CLOSED` audit row. A first close returns `alreadyClosed:false`.
- `POST /ops/contacts/{id}`: a `kind` change (`VENDOR`/`CONTACT`) is saved. Any other value returns 400.
- Payment account settings (`POST /ops/settings`, `POST /ops/settings/payment`): a non-blank `upi` must match `^[a-zA-Z0-9._-]{2,256}@[a-zA-Z]{2,64}$` (**400** "Enter a UPI ID like name@bank."), and a non-blank `account` must be 9–18 digits (**400** "Bank account number must be 9 to 18 digits."). Only new or changed values are checked, so saving another settings section never fails because of an older stored value.
- Vendor and contact `phone` is optional. When supplied it may contain digits, spaces, `+`, brackets and hyphens, and must have 8 to 15 digits, or be an emergency number such as 100, 101, 108, 112 or 1930. Otherwise it returns **400** "Enter a valid phone number (8 to 15 digits, or an emergency number such as 100, 101, 108 or 112)." The text is stored as entered.
- Subscription: after the three-day trial, an apartment without a covered current invoice gets **402** `{"code":"SUBSCRIPTION_REQUIRED",…}` on authenticated routes except logout and the Admin's subscription routes (see PROVIDER_BILLING.md).
