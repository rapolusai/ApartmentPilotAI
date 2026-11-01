-- Additive only: platform super users ("ApartmentCare team") and a last sign-in time per apartment login.
-- Operators are not apartment members, so they live outside ap_user (which requires a tenant and an apartment role).
-- Their sessions have their own table, so an operator token is never accepted by apartment endpoints and vice versa.
CREATE TABLE ap_operator (
  id uuid PRIMARY KEY,
  name varchar(80) NOT NULL,
  mobile varchar(10) NOT NULL UNIQUE,
  pin_hash varchar(100) NOT NULL,
  status varchar(12) NOT NULL DEFAULT 'ACTIVE' CHECK(status IN ('ACTIVE','DISABLED')),
  last_login_at timestamptz,
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE ap_operator_session (
  token_hash char(64) PRIMARY KEY,
  operator_id uuid NOT NULL REFERENCES ap_operator(id),
  expires_at timestamptz NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ap_operator_session_operator ON ap_operator_session(operator_id);

-- Sessions are purged after they expire, so the last sign-in is kept on the login itself.
ALTER TABLE ap_user ADD COLUMN last_login_at timestamptz;
UPDATE ap_user u SET last_login_at = s.latest
FROM (SELECT user_id, max(created_at) AS latest FROM ap_session GROUP BY user_id) s
WHERE s.user_id = u.id;
