# Implementation handoff — reference 2.1

## Contract

Build a real Android application with Java/Spring Boot services and PostgreSQL, using `START.html` as the visual and interaction reference. This package is the design baseline, not a completed native application. Preserve the reviewed layouts rather than wrapping the HTML in a WebView and calling that a native implementation.

Maintain the reference light/dark tokens, icons, rounded cards, hierarchy, form controls, navigation, amount formatting and short labels. Screen content must remain readable at narrow mobile widths and with increased text size. Provide accessible labels for icon-only buttons. Keep the exact footer **Powered By @Rapolu`s** on authenticated screens, onboarding, error states and bottom sheets; it must not cover a button or input. Use a shared Android component and keep system insets separate from app content.

`reference/screen-manifest.json` maps 203 reference routes. Reference tools (demo roles, provider simulations, fixture reset, fake clock and the development screen library) are explicitly not production features. Use the inventory as traceability, not a reason to ship developer controls. Add loading, empty, validation, expired-session and offline states to each native workflow where appropriate.

## Identity, roles and tenancy

Use separate identities for the platform operator and each apartment's Admin/Treasurer/Resident. Backend authorization determines roles; selecting an Admin card on a sign-in screen must never grant access. Demo PINs, in-memory roles and JavaScript guards cannot be reused as security.

Each apartment is a tenant. Restrict every query, file, search result, notification recipient, export and command by the authenticated tenant and role. Never trust a client-supplied apartment ID alone. Enforce one active primary resident account per flat, with an explicit handover process that retains the flat's historical ledger and revokes the former resident's access.

An Admin may add a member record without creating an authenticated account. Mobile/account verification and an approved flat claim are separate operations. Committee titles such as President or Secretary are descriptive appointments, not automatically privileged application roles. Grant Admin/Treasurer powers separately and audit role changes.

Persist credentials securely; implement real verification, session lifecycle, recovery and throttling. Do not embed secrets or test accounts in release builds. Reference account selection and provider simulations must only exist in development/test fixtures.

## Two separate financial domains

1. **Apartment finance:** residents pay maintenance or contributions to their own apartment association.
2. **Software billing:** the association pays the application provider for software access.

Use separate ledgers and identifiers. Software fees, referral credits, provider payments and refunds must not modify maintenance receipts, outstanding flat dues or society cash balances.

### Maintenance and apartment accounting

The monthly maintenance amount is configured by the apartment, never hardcoded to ₹2,000. Initial setup must require a configured rate before automatic billing begins. Store effective-month versions so future changes do not silently rewrite existing invoices. Flat overrides require explicit scope and effective dates.

Use exact money types (for example Java BigDecimal and PostgreSQL numeric or integer minor units), immutable approved records and explicit reversals. Preserve receipt numbering and audit history. Do not silently edit or delete approved financial transactions.

Generate monthly dues idempotently for the eligible flats and billing period. Opening arrears are receivables, not newly collected cash. One-time contributions are separate bill entries rather than permanent changes to recurring maintenance. Opening cash balance, opening flat arrears and current-month collections must be distinct.

A resident pays externally and submits a declaration with amount, date, mode and applicable reference/proof. A declaration is not a confirmed payment. Only an authorized Admin/Treasurer's verified approval posts the receipt and collection. Partial approvals retain the unpaid balance. Duplicate approvals, reused references, negative/excessive amounts and conflicting concurrent approvals must fail safely. Bulk approval requires explicit verification and atomic or clearly reported per-item results; never partially mutate a batch without telling the user.

Approval must atomically update the invoice allocation, receipt, cashbook, audit and notification outbox. Dashboards and reports derive from the same records, not independent totals. Pause ordinary unpaid reminders for declarations being reviewed; rejected or partially paid balances follow the configured reminder rules. Recurring expense templates create drafts; they do not fabricate completed payments.

Run billing, reminders, service schedules and notice publishing in real background workers. Use idempotency keys and a controlled clock/time zone; the browser's “Run automation” is only a demonstration. Do not hardcode September 2026 or fixture IDs in production.

### Software plans and referral offer

The discussed launch plan is per apartment: A (5–10 flats) ₹99/month; B (11–30) ₹149/month; C (31–50) ₹249/month. Treat these as configurable product prices, not quotations of an external provider's pricing. Do not automatically put apartments above 50 flats in category C.

Distinguish a **resident invite** (join this apartment) from a **customer referral** (a different apartment adopts the product). One referred apartment has one attribution; self-referrals, duplicate buildings, duplicate rewards and repeated claims must be rejected server-side.

The reference models a one-time first-billing-cycle offer: two distinct, qualifying new apartment activations within the eligibility window unlock the first-month benefit for the referring apartment and the two qualified new apartments. Installation alone does not qualify. The demo checks separate identity/setup/activation facts through labelled provider controls. The real activation criteria and the party authorized to verify them must be finalized before launch and enforced on the server.

Month two onward is payable. Referrals do not create repeated free months, unlimited extensions or credits against residents' maintenance. Credit a first-period invoice explicitly; a first-period payment already collected requires a tracked provider refund/credit workflow rather than silently changing its payment record. Dates, eligibility deadlines and the next payable date must be visible. Subscription expiry must not erase financial records.

Do not ship the reference's “provider simulation” pages to apartment Admins as payment-verification or entitlement powers. Decide the distribution/payment implementation before production release and validate the applicable platform requirements; this UI reference is not a policy-compliance determination.

## Community, notices and services

Members/committee directories use names and flat mappings, with contact visibility controlled separately. Preserve identity through name changes or resident handover; do not use a person's displayed name as a primary key.

One notice composer supports all flats, a block, selected flats and unpaid-flat audiences within the tenant. Separate draft, scheduled and published states. A draft must not send notifications. Preview the target group before publishing. Store an in-app notice and inbox delivery independently of phone push delivery. A push request being accepted is not proof the member received or read the notice. “Read” and “Acknowledged” are different actions. Avoid duplicate notifications when a scheduled job retries.

Use a notification outbox, recipient records, retry handling and privacy-aware deep links. A disabled notification permission must not remove the notice from the app. An announcement to all flats reaches approved registered accounts, not arbitrary family members or other apartments. Do not expose private event notes, payment screenshots or private complaint details in push payloads.

Common incidents support affected-flat joining without duplicate counts, assignment, progress, resolution and appropriate reopening. Private complaints remain visible only to the relevant flat and authorized apartment staff. Planned services have completion history, next-date calculation and optional expense linkage; completion alone does not establish that a vendor payment was made.

## Events and temporary parking

A resident can request an event, guest parking, or both. Capture dates, time window, setup/cleanup buffers, common space, attendees, car/bike requirements and rules acceptance. Display conflicts before submission, but recheck them in a database transaction when approving. Multiple users may request the same resource simultaneously.

Only Admin-approved allocations reserve a resource. Display “Pending” until approval; do not label a request “Booked.” Use resource IDs and time-range constraints/locking to prevent overlapping approved allocations. Blocked maintenance dates and setup/cleanup windows also consume availability.

Parking bays remain distinct from the open parking area that contains them. Booking the area for an event blocks those bays; a conflicting bay reservation blocks use of the area. A physically empty bay is not automatically bookable. Private bays require the authorized holder's explicit consent for the relevant period. Protected access routes are never bookable.

If fewer spaces or a different time can be offered, the resident must accept the alternative. An edit to an approved booking must not erase the original reservation until the replacement is rechecked and approved. Cancellation releases resources with an audit record. Completion and cleanup end the booking without deleting its history. Free availability and a zero fee are separate concepts; the reference uses ₹0 event bookings.

Public calendar information should be limited to shared-space availability and permitted event details. Do not publish private guest names or phone numbers by default. Any event-related common-area announcement is separate from an invitation to the event.

## Extras and rollout controls

Polls are informal preferences, not legal voting/election mechanisms. Enforce one current vote per flat and an explicit close state. Vehicle-owner contact requests should not reveal the owner's phone number and need misuse/rate limits. The committee handover checklist transfers responsibility, not shared passwords.

A shared approval inbox is navigation into independent, authorized workflows; it must not bypass payment verification or event availability checks.

Keep optional feature modules configurable for small apartments. Paid space booking/deposits, visitor-gate hardware, AI chat, online maintenance payment integration, advanced tax accounting and professionally reviewed translations are not implemented by this browser reference.

## Completion evidence for the real application

Connect native screens to real API contracts, PostgreSQL migrations, transactions, file storage and background jobs. Provide repeatable local setup, configuration examples without secrets, tests, CI output and deployment instructions. Replace static sample charts and labels with server data.

Track each screen and action to its API and acceptance tests. Test tenant isolation, role escalation, duplicate writes, failed/retried requests, concurrent approvals, expired accounts, network loss and rejected permissions. Compare Android screenshots with this reference in both themes and at supported sizes; a matching color palette alone does not establish visual parity.

The included browser results are evidence only about this HTML prototype. They do not prove that an Android build exists, that production authentication is secure, that real payments reconcile, or that push delivery works. Report untested integrations and release blockers explicitly; do not claim “100% working” from mocked browser tests.
