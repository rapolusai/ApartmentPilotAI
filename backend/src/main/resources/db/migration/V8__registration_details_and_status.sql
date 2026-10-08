-- Address and pincode already exist in V2 and are reused, not duplicated.
-- Existing tenant records and applied schema history are retained.

-- A receipt grants only read access to one join request, never application authentication.
CREATE TABLE ap_join_receipt (
 token_hash char(64) PRIMARY KEY,
 user_id uuid NOT NULL REFERENCES ap_user(id),
 expires_at timestamptz NOT NULL,
 created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ap_join_receipt_user ON ap_join_receipt(user_id);
