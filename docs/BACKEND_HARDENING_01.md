# Backend hardening 01 (branch `APAI1`)

Backend-only. No Android, layout or reference changes. No endpoint, request or response shape changed. Existing V1–V9 migrations are untouched; V10 only adds indexes.

| Change | Files | Behaviour |
|---|---|---|
| Stale bearer token no longer blocks public routes | `security/SecurityConfig.java` | Before: any request carrying an expired/unknown token got 401, even `/auth/login` and `/status`. After: public routes work; protected routes still return the same JSON 401 message, so Android session-expiry handling is unchanged. |
| Expired sessions and old rate-limit windows are purged hourly | `security/SessionCleanupJob.java` (new), V10 indexes | `ap_session` rows past `expires_at` and `ap_rate_limit` windows older than one day are deleted. Live sessions are not touched. Disable with `app.cleanup-enabled=false`. |
| Scheduled automation audits only real changes | `operations/AutomationService.java`, `operations/OperationsJob.java` | The 15-minute job no longer writes an `AUTOMATION_RUN` row when it created nothing, so human actions stay visible in the 100-row audit list. Manual runs from Admin/Treasurer are always audited; individual late fees etc. are audited as before. |
| Resident monthly report before settings exist | `finance/FinanceService.java` | A new apartment with no `ap_settings` row now treats transparency as visible (the default), matching the expense list. Turning transparency off still returns 403 to residents. |
| Explicit column lists on INSERT | Accounting/Booking/Notice/Ticket services | Seven `INSERT … VALUES` statements relied on column order and would break when a later migration adds a column. Same data is written. |
| Proxy-aware client address | `application.yml` | `server.forward-headers-strategy: native`. Behind a private-network load balancer, per-IP rate limits use the real client address instead of the balancer's. No effect when no proxy header is sent. |
| V10 indexes | `db/migration/V10__housekeeping_and_list_indexes.sql` | Session/rate purge, audit, inbox and issue lists. Index-only; no data change. |
| Database integration tests and CI | `src/test/.../ApiIntegrationTest.java`, `.github/workflows/backend-tests.yml` | 8 HTTP tests against real PostgreSQL. They run only when `APT_TEST_DB_URL` is set to a disposable database; CI provides one. |

## Evidence (cloud container, 8 Oct 2026, on top of main 10a3425)
- `mvn clean test` with PostgreSQL 16: **67/67 passed** (59 existing unit tests + 8 new integration tests; fresh database migrated V1→V10).
- With the main versions of `FinanceService`/`SecurityConfig`, the two matching integration tests **failed** (403 and 401), confirming they detect the bugs.
- Upgrade path: a database created and populated by the main 10a3425 build (V9, 4-digit PIN admin) migrated to V10 with this build; the existing admin logged in, a stale token got 200 on `/status` and 401 on `/me`.
- Packaged jar ran 75 s with the `local` profile and jobs enabled: no errors logged.

## Not done here (needs a decision)
- Requiring 6-digit PINs at registration/join and stronger lockout: changes what users can enter.
- Preventing a Treasurer/Admin from approving a payment they submitted: business rule.
- Per-step transactions in automation, S3 file storage, multi-instance job locking.
- Not run: Windows PowerShell HTTP suites, Android, PostgreSQL 18 (CI and this run used 16).
