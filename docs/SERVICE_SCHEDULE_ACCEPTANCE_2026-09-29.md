# Service schedule Lead checkpoint — 29 September 2026

## Scope

Native Services schedule list and detail behavior from the approved ApartmentCare reference: active/due/scheduled state, staff completion, cycle advancement, and expense-draft linkage. This is a bounded Lead checkpoint, not independent QA or whole-feature acceptance.

## Implementation and evidence

- Android: schedule rows show `Active`, `Paused`, `Due soon`, or `Scheduled` from the service state and next date; detail shows active/paused state. Completion input validates date format, rejects future dates, rejects negative cost, and requires a work note before submitting.
- Backend behavior reused: tenant-scoped service/vendor access, staff-only writes, confirmed billing-cycle requirement, future-date rejection, idempotent completion, next-cycle advancement, and draft expense creation for a positive unpaid service cost. Resident responses hide financial fields and inactive records.
- Local API/PostgreSQL operations regression: 120/120 passed. Evidence: `C:\Users\rapol\AppData\Local\Temp\ap-services-test-evidence-20260929\operations-bf4ec9e6.json`.
- Android build: 40 Gradle tasks; unit tests 26/26. Candidate APK SHA-256: `CEB24E8361CFFF03CBCC804E3D307904D8DFAC93109941FA685B3F2792324404`.
- Native virtual-emulator path: created a monthly Lift schedule; list displayed `Due soon`; future completion date was rejected without a write; valid completion on 2026-09-29 with ₹125.50 and an unpaid work note advanced next date to 2026-10-29; history showed the completion and cost; linked expense opened as Draft; returning to the schedule list displayed `2026-10-29 · monthly · Scheduled`.
- Test fixture `NATIVE_UI_Lift_Inspection` and related synthetic expense were retained in the local development database.

## Still open

Independent QA remains on hold. UI parity has not been scored against the reference; tablet/landscape coverage, provider behavior, broader role/error/session cases, and application-wide regression are not claimed as passed. The draft expense is not a payment or settlement.
