# API contract — cumulative increment 02

All ordinary APIs derive tenant/role from a bearer session. JSON bodies contain no trusted tenant or role assignment except explicitly guarded administrative operations. Use /status to confirm version0.2.0.

## Routes declared by the delivered controllers

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
- Failed access:401 unauthenticated,403 forbidden,404 scoped record not found. Validation400, optimistic/conflicting state409, request rate429. No stacktrace or credential response is intended.
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
