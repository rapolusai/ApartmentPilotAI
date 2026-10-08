# Expense attachments — Lead checkpoint

Date: 29 September 2026  
Scope: authenticated expense-proof selection, upload, preview, save and open on Android; tenant and visibility rules through the local API.

## Result

- Local API/PostgreSQL synthetic attachment suite: **39/39 checks passed** with `scripts/Test-Expense-Attachments.ps1 -AllowSyntheticRecords`. It covered staff/resident/foreign-tenant boundaries, public/private visibility, MIME/signature checks, sanitized names, duplicate-content reuse, byte round-trip and the five-file limit. Synthetic records were retained; no rows were deleted.
- Native virtual-emulator checks on `emulator-5554`: **passed** for the empty state, Android document picker, selecting/uploading an image, selecting a second file, previewing a real 108,305-byte PNG, invoking the save destination picker, saving the file, and opening it in Android Photos.
- Download integrity: saved image SHA-256 exactly matched its original bytes: `DB51D92C57D3B8088086DFCA73891C339AD715A1BCE31269AA10515D26C1158A`.
- Physical phone was not touched. The emulator received only labeled synthetic image fixtures; the test-created apartment and expense records remain in the local development database.

## Observation and boundary

The 68-byte PNG fixture used by the API suite contains a PNG signature but cannot be decoded as an image. Upload is accepted by the current signature-based file validation, and the native screen correctly shows its fallback (“Preview unavailable. You can still open or save the original file.”). A real emulator-generated PNG previewed and opened successfully. Stronger image-decoding validation is not currently part of this checkpoint; attachment malware scanning and tenant storage quotas are also not implemented.

This is a Lead self-test only. Expense create/edit/report/cashbook UI acceptance, 95% visual parity measurement, tablet coverage, independent QA and broader regression are still open. QA remains on hold.

## Files and test evidence

- `android/app/src/main/java/com/rapolus/apartmentpilotai/ui/AttachmentScreens.java` — native list, selected-file preview and staff upload entry.
- `android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java` — native document selection, upload, preview, save and open handoffs.
- `scripts/Test-Expense-Attachments.ps1` — API/PostgreSQL synthetic checks (39/39).
- Native picker/save/open exploration used the existing virtual emulator and helper routines; no dedicated repeatable attachment-emulator test script has been added yet.
