# Events & bookings Lead checkpoint — 29 September 2026

## Scope

Bounded checkpoint for the Events & bookings list, native event request, explicit approval confirmation, and reservation persistence. It does not claim that every booking subflow (alternatives, cancellation, resource blocks, parking release, or calendar parity) is complete.

## Changes

- Android Events list now has reference-aligned `Upcoming`, `Mine`, `Past`, and `All` filters, a search field covering event title/type/flat/notes, empty states, and direct Calendar, Availability, Spaces, and Booking Rules access.
- Filter/search state is retained in the in-memory page draft during navigation. `Mine` compares the authenticated account/flat identifiers returned by the server; it does not accept a client-selected identity.
- Successful request submission now clears the stale form from the Back stack. Opening the created detail uses Events as its back target; alternative offers keep their existing decision path.
- Existing backend booking behavior was reused: tenant-scoped records, resident visibility, requests do not reserve capacity, staff approval rechecks capacity under a tenant lock, conflicting approval is rejected, and cancellation releases capacity.

## Verification

- Local API/PostgreSQL operations suite: 120/120 passed, including resident request creation, pending requests not locking availability, approval-time reservation, double-book rejection, cancellation releasing capacity, and a subsequent approval. Evidence: `C:\Users\rapol\AppData\Local\Temp\ap-booking-test-evidence-20260929\operations-5499d828.json`.
- Android: `:app:assembleDebug` and `:app:testDebugUnitTest` passed; 40 Gradle tasks, 26/26 unit tests.
- Native virtual emulator: created a synthetic common space; submitted `NATIVE_UI_Event_Birthday` for 2026-10-01 17:00–21:00 through the confirmation prompt; confirmed the persisted request was pending and explicitly approved it; detail then showed Approved and the selected space. The list rendered all four filters; `Past` excluded this upcoming row, `Mine` included the authenticated creator's row, and a deliberately unmatched search showed the empty state.
- Debug APK SHA-256: `26CDF67CDDF70F97B3030EF4D64E61D9C8D0D68FD9F1E007A75826FE3BC502B7`.
- Synthetic space and booking are retained in the local development tenant. No live payment or external delivery is part of this flow.

## Still open

Independent QA remains on hold. UI parity score, tablet/landscape, resident-role native booking, search behavior under a live typed query, rejected/alternative/cancellation native paths, and broader failure/session regressions are not claimed as passed. Full Events & bookings acceptance remains open.
