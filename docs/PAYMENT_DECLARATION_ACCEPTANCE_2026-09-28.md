# Payment declaration and review — Lead self-test

Date: 28 September 2026. This records local Lead checks only. Independent QA remains on hold; this is not a release or completion approval.

## Evidence

- Local HTTP/PostgreSQL workflow: `scripts/Test-Local-Api.ps1` passed 60/60 checks against the development backend on port 8081. This includes maintenance billing, resident declarations, approval/rejection, retries, proof permissions, receipts, bulk review, tenant/flat isolation and related balance/report behavior. The script retained labelled synthetic test tenants; it did not delete database records.
- Android unit tests: 26/26 passed; debug APK build succeeded (40 Gradle tasks). The project pins JetBrains JDK 21, which is not installed here, and Foojay download access was blocked. To avoid modifying that setting, the current Android source was built from a temporary copy with the daemon-JDK criteria file omitted, using installed Oracle JDK 21, the existing Android SDK and offline Gradle dependencies. This validates source/build compatibility but is not a build under the repository-pinned JetBrains JDK.
- Tested APK SHA-256: `D1B193439535B19E9D01F7F1924D7234967A7D5968D5BDF212AEF894732F7E95`. It was installed on emulator-5580 only; the physical device was not targeted.
- Native lifecycle: `scripts/Test-Payments-Emulator.ps1` passed 53/53 checks on emulator-5580 using a retained synthetic tenant. Coverage includes required resident confirmation (no unchecked write), pending status, proof action visibility, resident-scoped detail, Treasurer approval confirmation and receipt, proof lock after approval, bill balance synchronization, a distinct second declaration, required rejection message, persisted correction reason, resident history/status, resubmission entry and Android Back.
- The shared UIAutomator helper now redacts populated text inputs in the saved runtime XML artifact while retaining the unredacted screen only in memory for test assertions.

## Still open

- Independent QA is on hold per the user's instruction.
- Tablet runtime and reference UI parity scoring have not been completed for this flow.
- Native proof file-picker/download behavior, payment reversal UI, reversal-specific retry/race cases, broader session-expiry behavior and full regression remain open. No bank transfer or cash receipt was actually made; approval is a synthetic local test record.
- The full application accepted-completion percentage remains 0% until its declared acceptance inventory passes the required Lead and QA gates. The 60/60 API and 53/53 native figures are scoped test results, not an overall completion percentage.
