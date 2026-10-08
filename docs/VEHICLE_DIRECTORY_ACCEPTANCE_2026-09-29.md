# Vehicle directory Lead checkpoint — 29 September 2026

## Scope

Reference-aligned native vehicle directory search by registration, flat, or parking label. Existing add/edit/contact handlers and tenant scoping are preserved.

## Verification

- Android directory now filters live as the query changes and displays explicit empty/no-match messages. Search text is retained in the screen's in-memory page draft.
- Existing vehicle API behavior was reused; the backend was unchanged. The full local operations suite passed 120/120 before this UI-only change, including resident vehicle registration and surrounding tenant-isolation checks.
- Android `:app:assembleDebug` and `:app:testDebugUnitTest` passed: 40 Gradle tasks, 26/26 unit tests.
- Native virtual emulator: registered local synthetic `NATIVE12345` for flat A-101; directory showed the persisted row (`A-101 · UI-TEST`); an exact registration query retained it; an unmatched query produced `No matching vehicles`.
- Debug APK SHA-256: `5F641C84F0F25612BD202F4E01D5671A99D376474FA8755825FE5BD1BCCC0A79`.
- The synthetic vehicle is retained in the local development tenant; no physical vehicle or handset was touched.

## Still open

Independent QA remains on hold. Resident-versus-staff visibility, role permissions, UI parity score, tablet/landscape layout, and broader contact-owner delivery/error tests are not claimed as passed.
