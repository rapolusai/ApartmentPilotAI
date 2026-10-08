# Referral reward acceptance — 29 September 2026

## Lead checkpoint

The first-month referral reward is **locally verified at backend/API level**, but is not independently QA-approved or production billing-ready.

The approved reference requires the referring apartment Admin to accept the offer, then requires two distinct referred apartments to be installed and independently verified with an active resident, all within their first billing month. The one-time first invoice credit applies to the referrer and those two apartments; paid invoices go to manual refund review and no later invoice is credited.

## Implementation change

- `SubscriptionService.acceptOffer` serializes consent/reward evaluation with the same PostgreSQL promotion lock as referral linking and provider activation.
- `award` now requires the referrer’s persisted `offer_accepted_at`, in addition to the existing install, independent verification, active-resident and first-month checks. Referred apartments do not need to accept the referrer’s offer.
- Accepting consent re-evaluates eligibility, matching the reference behavior if activation already exists. Repeated acceptance remains idempotent and can safely re-evaluate the one-time award.
- `scripts/Test-Operations.ps1` adds Admin-only, explicit-consent, non-premature-award and retry checks.
- `scripts/Test-Referral-Rewards.ps1` exercises the full local positive path and the corresponding no-consent negative path, including a repeated-consent retry. It only targets localhost, requires an explicit synthetic-data switch and a temporary local provider key, records no credentials, performs no real charge and does not delete test records.

## Evidence

- `backend\\mvnw.cmd test`: **56/56 Java unit tests passed**.
- `backend\\mvnw.cmd package`: **BUILD SUCCESS**.
- Local PostgreSQL 18.6, Flyway schema V8, temporary localhost backend and ephemeral test-only provider/invite keys: **56/56 referral checks passed**.
- Positive case: root Admin consent, two linked apartments, active resident in each, all three installations, and independent local provider-verification events resulted in exactly two rewarded referrals and waived first invoices for all three apartments.
- Negative case: the same two activated referrals with no referrer consent left all three first invoices due with zero credit.
- Synthetic records are retained in the local development database by design. The successful suite created six QA-labelled apartments; two earlier interrupted setup attempts also left two QA-labelled root apartments without residents. No existing data was removed.
- Evidence JSON: `%TEMP%\\ApartmentPilotAI-test-results\\referrals-<run>.json` (check names/results only; no auth tokens or provider keys).
- PowerShell parser and `git diff --check` passed for the changed implementation/test scripts (Git emitted only its existing LF→CRLF notices).

## Still open

- QA remains on hold; no independent tester or device validation was performed for this flow.
- Android native consent UI exists, but this checkpoint did not perform a fresh Android build, visual comparison, tablet/phone UI run or full navigation/error-state QA.
- The provider verification was a synthetic localhost service-provider event, not actual production identity/installation verification. Live checkout, Google Play verification/webhooks, actual settlement/refund and production entitlement enforcement are not configured or validated.
- This is a bounded backend referral checkpoint, not whole-application completion or release approval.
