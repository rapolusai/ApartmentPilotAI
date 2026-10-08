# Audited payment and expense reversal — Lead acceptance checkpoint

Date: 28 September 2026  
Scope: locally authenticated Admin/Treasurer/Resident workflows for reversing an approved synthetic maintenance payment and a paid synthetic expense. No real money was collected, sent, refunded, or settled.

## Result

- API/PostgreSQL self-test: **19/19 checks passed** using retained synthetic records.
- Native Android emulator self-test: **33/33 checks passed** on `emulator-5580`.
- Android build: **40 Gradle tasks succeeded**; Android unit tests **26/26 passed**.
- APK installed on the approved emulator only. Physical device `5ee2fea` was not targeted.
- Independent QA remains on hold; this is a Lead checkpoint, not feature/flow release acceptance.

## Verified behaviors

- Pending declarations cannot be reversed; a resident cannot reverse a payment.
- A Treasurer can reverse only an eligible approved payment; an ID from another tenant is not disclosed.
- Reversal requires an audit reason and explicit confirmation/verification. Blank or whitespace-only reasons and missing verification are rejected before submission.
- Request-key retries are idempotent; conflicting key reuse and a second distinct reversal attempt are rejected.
- Canceling Android confirmation with Back does not change the record.
- Confirming reversal preserves the original payment and receipt, marks the payment reversed, restores the bill balance, writes the reversal to monthly reporting/cashbook, and records the audit reason.
- The same audited form supports a paid expense. Its history remains present with the reversal reason, and the UI does not offer a second reversal afterward.

## Evidence and changed files in this checkpoint

- `scripts/Test-Payment-Reversals.ps1` — API/PostgreSQL acceptance checks.
- `scripts/Test-Payment-Reversals-Emulator.ps1` — synthetic account setup and native UI checks.
- `android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java` — payment history heading.
- `android/app/src/main/java/com/rapolus/apartmentpilotai/data/NavigationPolicy.java` — reversal routes open Money navigation.
- `android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java` — reason and verification guards before explicit reversal confirmation.
- `android/app/src/test/java/com/rapolus/apartmentpilotai/data/NavigationPolicyTest.java` — route test coverage.
- `docs/SEQUENTIAL_IMPLEMENTATION_PLAN.md` — sequential checkpoint status.

## Boundaries still open

- Independent QA has not run, per the user's hold instruction.
- No 95% visual-parity measurement was performed for this flow.
- Tablet, real handset, provider-backed, production, and real payment-settlement behavior are not validated by this checkpoint.
- Overall ApartmentPilotAI accepted-inventory completion remains **0%** until the declared end-to-end acceptance and QA gates are passed; these scoped checks do not change that aggregate.

## Build environment note

The repository-pinned JetBrains daemon JDK was unavailable. The Android source was copied to a temporary build directory and built offline with installed Oracle JDK 21 and the existing Gradle/SDK installation, without changing the repository's Gradle/JDK configuration. This is build/unit evidence for this source snapshot; it does not replace an in-repository build under the user's pinned configuration.
