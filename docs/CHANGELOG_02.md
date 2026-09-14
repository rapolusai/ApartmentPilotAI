# Changes in cumulative increment 02

## Actual new source
- Apartment profile, flat add/edit/occupancy, public name directory, Admin-only phone visibility and manual member records separate from authenticated identities.
- Committee names/terms/public visibility; app roles remain separate. Staff access changes revoke tokens; resident handover disables old access without deleting flat bills.
- Profile editing and PIN change; encrypted on-device session persistence with Android Keystore and server-side expiry/revocation. No saved plaintext PIN.
- Vendors and contacts, AMC date, direct dialler action, public/private visibility and archive controls.
- Building incidents, private complaints, category selection, similar-issue suggestions, follow tracking by distinct flat, comments/history, vendor assignment, restoration estimate and controlled transitions.
- Admin-controlled whole-apartment in-app incident updates; private complaints cannot be broadcast through that control.
- Preventive schedules and completion history; a completed service may create an expense once and advance its schedule.
- Event requests for birthday/marriage/family/festival/meeting/other; shared spaces and guest car/bike quantities, calendar, availability, human approval, alternatives, organiser acceptance, rejection/cancellation and ended states.
- Resource capacity, blackout windows, private-flat owner release and linked physical conflicts. Approved bookings—not installations or empty-looking bays—consume availability. Overlap checks re-run inside the approval transaction.
- Manual member directory, role and committee changes do not automatically authenticate a new account.
- Targeted ALL/BLOCK/SELECTED/UNPAID notices, drafts, scheduled publication, pinned notices and recipient snapshots/read/acknowledgement tracking.
- Private attachment storage and server re-authorisation for each file: PNG/JPEG/PDF, 5MB per file, 5 files per record, SHA-256 deduplication. No external object-storage or malware scanning claim.
- Native PDF exports, file viewing/sharing through FileProvider, cache retention and attribution footer.
- Payee/bank/UPI details, one-time opening cash, opening flat dues, one-time contributions, flat rate overrides, optional late fee snapshots and grace period.
- Paid/draft expenses, correctly recorded payment modes, recurring draft templates, other income, full payment/expense reversals with audit evidence, cashbook and month-specific reports.
- In-app receipt visibility after reversal without deleting the original approval record. Reversal is a recorded adjustment, not an actual bank refund operation.
- Backend scheduled generation/reminders/fees/services/notices/booking completion. Paid and pending-verification bills are excluded from ordinary unpaid reminders; configured quiet hours apply to automated payment reminders.
- Informational software subscription ledger, monthly invoices, plan calculation, installation report and referral graph. Provider-only verification/settlement interfaces are disabled unless an external operator key is configured.
- Non-stacking first-month referral award, circular/self-referral protection and a refund-review record for previously paid first invoices. No fake cashback, extra free month or automatic bank refund.
- Simple polls with one mutable vote per flat until close, vehicle directory and privacy-preserving in-app contact-owner requests.
- Cumulative Android operational navigation, forms, confirmations, files and APIs; no simulated successful API fallback.

## Existing behaviour retained
- Admin/local setup, resident invite and approval, single active/pending flat association, existing financial APIs and receipt history.
- Backend Java21, localhost8080 default, PostgreSQL database settings and source-package directory layout.
- Original V1 migration and frozen v2.1 HTML bytes.
- **Powered By @Rapolu`s** on application page/footer and application confirmation/export UI.

## Local validation and build repairs — 15 September 2026

- Corrected `LocalFiles` PDF lifecycle handling for Android APIs where `PdfDocument` is not `AutoCloseable`; page finishing, output stream closure and the required attribution footer remain intact.
- Made the checksum-verified tooling bootstrap accept checksum responses returned as either text or bytes by Windows PowerShell.
- Corrected two Windows PowerShell operations-suite defects: generic result-list serialization and the case-insensitive `$pid` collision with PowerShell's built-in process ID.
- Current backend JUnit test/package, Android clean debug build, PostgreSQL/Flyway V3 validation, 50-check local API suite and 89-check operations/concurrency suite passed. Device/UI parity and external providers remain open.
- Added dedicated native Admin/Treasurer/Resident login destinations, invalid-invite, choose-flat and setup-complete routes. Added centralized stale-token invalidation and dedicated offline/session-expired/access-denied/empty/error/not-found/loading states with safe destination resume. Mobile OTP recovery remains visibly not configured rather than simulated.
- Split maintenance review into dedicated native selection, confirmation, rejection, unpaid-flat, reminder-preview and bill-preview routes. Added tenant-derived bill-generation previews and request-key-idempotent manual reminders that write only to the in-app inbox while external delivery remains `NOT_CONFIGURED`.
- Added dedicated native contribution review and resident-transparency routes. Contribution previews use server-resolved active tenant flats, exact totals and idempotent creation; residents cannot preview apartment-wide charges or use a guessed foreign flat ID. Disabling expense visibility now denies resident aggregate reports instead of leaking totals.
- Added dedicated native notice-template and notice-preview routes plus additive V3 notice metadata. Server previews resolve the exact authenticated-tenant audience, future timestamps require an explicit idempotent schedule confirmation, and only confirmed schedules run. Phone notification intent remains truthfully `NOT_CONFIGURED`; publishing creates in-app inbox records only.
- Added a dedicated native staff-only affected-flats route. The server derives distinct active follower flats inside the authenticated tenant, rejects resident enumeration and guessed cross-apartment issue IDs, and does not expose private complaints through this endpoint.

## Deliberately not claimed
Not a complete 203-route pixel-identical native app, a device-validated APK/AAB, a verified fresh/V1-clone migration matrix, a configured OTP/FCM/payment integration, or a Google Play release. See status and evidence for the remaining acceptance gates.
