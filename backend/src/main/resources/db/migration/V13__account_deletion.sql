-- Additive only: account deletion for Google Play. V1-V12 are unchanged and no existing row is rewritten.
-- A deleted account keeps its ap_user row (payments, expenses and audit rows reference it) but every personal
-- field is replaced: name becomes "Former resident · <flat>", mobile becomes 'deleted:<user id>' (never a dialable
-- number, so it can never sign in and the real number may register again), email is blanked and the PIN hash is
-- replaced with one nothing can match. deleted_at marks the row.
ALTER TABLE ap_user ADD COLUMN deleted_at timestamptz;
ALTER TABLE ap_user ALTER COLUMN mobile TYPE varchar(40);
CREATE INDEX ap_user_deleted ON ap_user(tenant_id) WHERE deleted_at IS NOT NULL;

-- Requests made through the public web page (required by Play for people who no longer have the app). The
-- operator verifies the request before executing it; nothing here grants access to any record.
CREATE TABLE ap_deletion_request (
  id uuid PRIMARY KEY,
  mobile varchar(10) NOT NULL,
  apartment_name varchar(80) NOT NULL,
  note varchar(300) NOT NULL DEFAULT '',
  client_hash char(64) NOT NULL,
  status varchar(12) NOT NULL DEFAULT 'RECEIVED' CHECK(status IN ('RECEIVED','DONE','REJECTED')),
  outcome varchar(300) NOT NULL DEFAULT '',
  created_at timestamptz NOT NULL DEFAULT now(),
  handled_at timestamptz
);
CREATE INDEX ap_deletion_request_open ON ap_deletion_request(created_at) WHERE status='RECEIVED';
