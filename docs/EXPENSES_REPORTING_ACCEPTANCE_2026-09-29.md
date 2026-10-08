# Expenses and reporting — Lead checkpoint

Date: 29 September 2026  
Scope: ordinary expense list/create/edit/confirm, attachment route, monthly expense totals, cashbook and report export/navigation.

## Result

- Local backend/PostgreSQL regression: `scripts/Test-Operations.ps1` **109/109 checks passed** against `http://127.0.0.1:8081/api/v1`. It covered the ordinary draft/paid/reversal lifecycle, tenant and role boundaries, resident transparency and monthly-summary behavior, alongside the broader local operations suite. The passing evidence JSON is `C:\Users\rapol\AppData\Local\Temp\ap-money-test-evidence-20260929\operations-c0f5dcf6.json`; synthetic records remain. The initial run also passed all checks, but its default evidence write to the repository `test-results` directory was denied, so the suite was rerun with a Temp output directory.
- Attachment API suite: **39/39 checks passed**; native picker/upload/preview/save/open evidence is recorded in `docs/EXPENSE_ATTACHMENT_ACCEPTANCE_2026-09-29.md`.
- Final Android build: **40 Gradle tasks succeeded; 26/26 Android unit tests passed**. Debug APK installed only on virtual emulator `emulator-5554`; SHA-256 `89489B9A50700B8C823B95D7CA1931FC2588A94ABDA7BA7817862B87BF0177FC`. Candidate: `C:\Users\rapol\AppData\Local\Temp\ApartmentPilotAI-debug-expenses-2026-09-29.apk`.
- Final native APK checks: expense list search and All/Paid/Drafts filtering; monthly paid total; empty-description, zero-amount and future-date validation (each replaces the previous message); valid draft confirmation and detail; attachment picker/save/open (see linked checkpoint); report summary and Expense breakdown totals; cashbook transaction rows; CSV/PDF/text-share chooser handoffs. No report was sent to an external recipient.
- Saved synthetic paid amounts reconcile across Expenses, Monthly report, Expense breakdown and Cashbook: **₹105.50 total** (₹90 Security + ₹15.50 Other). A final-build synthetic unpaid draft was also created and remains clearly labeled in the local database.

## Source change

- `android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java` — add native expense search and status filters, authoritative monthly expense summary, and live result rendering; replace stale inline validation errors rather than accumulating them.
- `android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java` — validate expense description, positive amount and non-future date with specific messages before posting.
- `docs/EXPENSE_ATTACHMENT_ACCEPTANCE_2026-09-29.md` — record native attachment evidence and its fixture limitation.
- `docs/SEQUENTIAL_IMPLEMENTATION_PLAN.md` — update this bounded Lead checkpoint.

## Remaining gates

- Independent QA remains on hold at the user's direction. No 95% visual score, tablet acceptance or screen-by-screen parity claim is made.
- Cashbook CSV/PDF export and report share choices were not sent to any destination; only chooser handoff was observed. Provider receipt/delivery is not claimed.
- The 109-check backend suite is broad local operations coverage, not proof that every report role/device/error scenario is complete. Whole-step and whole-app acceptance remain open.
- The build used installed Gradle 8.13 and JetBrains JDK 21 from a temporary source copy omitting only the generated daemon-JDK download criteria; repository toolchain/configuration files were not changed.
