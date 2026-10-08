# Recurring expenses — Lead checkpoint

Date: 28 September 2026  
Scope: tenant-scoped recurring templates, due-cycle draft preparation, pause/resume, explicit payment posting and scheduled preparation.

## Result

- API/PostgreSQL synthetic lifecycle suite: **61/61 checks passed** (`scripts/Test-Recurring-Expenses.ps1 -AllowSyntheticRecords`). Test apartment records are retained; nothing was deleted.
- Native emulator checks: **8/8 passed** on `emulator-5580`: Money menu exposes Recurring expenses; its list exposes Add recurring expense; setup form opens; a synthetic template is created; pause and resume persist; Prepare expense opens Expense detail; the prepared recurring entry remains an unpaid draft.
- Integrated native Android/API checks: **14/14 passed** on the restarted Pixel_10_Pro virtual device (`emulator-5554`): synthetic Admin login, open/edit recurring template, persist title and amount to PostgreSQL, prepare a cycle draft, confirm payment through the native form, verify paid state, exactly-once monthly advancement, and report total changes by the expense amount.
- Android build after adding the missing Money-menu entry: **40 Gradle tasks succeeded**, Android unit tests **26/26 passed**, APK installed and launched on `emulator-5580`, then reinstalled on `emulator-5554` for the integrated run (SHA-256 `69C658A54F1E0A786C49F66F75FD3EE0F082A08364AA388F458699FF3FC82703`).
- Current tested APK: `C:\Users\rapol\AppData\Local\Temp\ApartmentPilotAI-debug-2026-09-29.apk` (SHA-256 `69C658A54F1E0A786C49F66F75FD3EE0F082A08364AA388F458699FF3FC82703`).
- Physical phone `5ee2fea` was not targeted. No real vendor payment occurred; recurring cycles create drafts and money is posted only after explicit confirmation.
- Recurring-expense functional Lead self-tests are complete. Independent QA remains on hold; visual parity is not measured and the wider expense/accounting step remains open.

## API behaviors exercised

Tenant isolation and staff-only access; optional tenant vendor binding; idempotent template creation; legacy update compatibility; unauthorized/foreign template access; invalid, stale and client-overridden cycles; request-key idempotency and concurrent preparation; pause/resume and stale-state conflicts; one unpaid draft per cycle; amount/vendor/cycle snapshot; draft visibility; explicit paid confirmation; one-time cycle advancement; monthly cash movement; audited reversal rather than deletion; scheduled draft preparation and catch-up advancement.

## Source change

- `android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java` — add a direct “Recurring expenses” entry to the staff Money menu, pointing to the existing native recurring-template screen.
- `docs/SEQUENTIAL_IMPLEMENTATION_PLAN.md` — record this checkpoint and remaining gates.

## Still open

- Recurring-template edit and explicit paid-confirmation interactions now pass on emulator; the synthetic paid record and test apartment are retained.
- Expense create/edit/attachment/download and reporting/cashbook UI gates are separate pending checks.
- No 95% visual-parity measurement, tablet acceptance, or independent QA was performed.
- No changes were made to pinned repository JDK/Gradle configuration. Build used a temporary source copy without `gradle-daemon-jvm.properties`, local Gradle 8.13, installed Oracle JDK 21, and downloaded Android build dependencies after sandbox networking blocked the initial attempt.
