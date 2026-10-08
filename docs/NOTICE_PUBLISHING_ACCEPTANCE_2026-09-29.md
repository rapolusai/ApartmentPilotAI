# Community notices — Lead checkpoint

Date: 29 September 2026  
Scope: staff notice list search and filters, template entry, notice form, saved-draft preview, explicit publish confirmation, and published detail.

## Result

- Local Android build: **40 Gradle tasks succeeded; 26/26 Android unit tests passed**. The final Notices/Issues candidate was installed only on virtual emulator `emulator-5554`. APK SHA-256: `622AC5BFF10302F0D708A4C4B7B3A89A840DD461F56BB684DCC1B94B73273163`.
- The existing local API/PostgreSQL operations regression passed **109/109 checks** (evidence: `C:\Users\rapol\AppData\Local\Temp\ap-money-test-evidence-20260929\operations-c0f5dcf6.json`). The suite exercises the existing notice save, preview, publish/schedule, recipient, acknowledgement and idempotency behaviors among its broader operations checks; it is not a notice-only suite.
- Native emulator checks confirmed Notices search, All/Pinned/Drafts/Scheduled filters, a matching title/body query, intersection of search with Pinned and Drafts, and exclusion of an immediate publish from Scheduled. A synthetic Water template was saved as a draft, previewed with its audience/recipient count, acknowledgement, pin and in-app delivery mode, explicitly published through a confirmation dialog, and opened at the persisted `published` detail. The notice remains pinned and synthetic local records were retained.
- Phone delivery was off. No push/SMS provider delivery is claimed or configured; publish created in-app notice state only.

## Source change

- `android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java` — add live search and role-sensitive notice filters to the list; render matching notice summaries and clear empty results; label the staff entry action `Create notice`.
- `android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureHost.java` and `android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java` — provide an explicit post-publish/schedule back target so Android Back returns to Notices instead of a preview the server no longer permits.
- `docs/SEQUENTIAL_IMPLEMENTATION_PLAN.md` — record this bounded Lead checkpoint.

## Remaining gates

- Independent QA is still on hold at the user's direction. No 95% visual score, tablet acceptance, resident acknowledgement runtime acceptance, scheduled-time execution timing, or complete role/error/regression acceptance is claimed.
- The publishing flow uses the existing authenticated backend handlers; no new provider integration or schema change was made.
- This Lead checkpoint is not full Notices acceptance and does not count as overall application completion. Visual review, tablet checks and QA remain pending.
- Final-candidate native check created and published a second synthetic in-app notice, confirmed the detail state `all · published`, then verified Android Back returned directly to the Notices list. No external notification was sent.
