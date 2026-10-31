-- Additive only: per-account PIN failure counting with backoff (LoginGuard). V1-V10 are unchanged and no data is rewritten.
-- Keys are SHA-256 digests of tier + mobile (+ client IP); the raw mobile and IP are never stored here.
CREATE TABLE ap_login_guard (
  key_hash char(64) PRIMARY KEY,
  failures integer NOT NULL DEFAULT 0,
  locked_until timestamptz,
  last_failure_at timestamptz NOT NULL DEFAULT now(),
  notified_at timestamptz
);
CREATE INDEX ap_login_guard_last_failure ON ap_login_guard(last_failure_at);
