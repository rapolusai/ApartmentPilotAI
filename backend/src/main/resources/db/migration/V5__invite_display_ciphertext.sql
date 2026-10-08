-- The code hash remains the lookup authority. This encrypted display copy is
-- Admin-only and is written only for newly issued invites; legacy hashes cannot
-- be reversed and must be rotated before the current code can be displayed.
ALTER TABLE ap_invite ADD COLUMN code_ciphertext text;
ALTER TABLE ap_invite ADD COLUMN request_key uuid;
CREATE UNIQUE INDEX ap_invite_request_once_per_tenant ON ap_invite(tenant_id, request_key) WHERE request_key IS NOT NULL;
CREATE INDEX ap_invite_current_by_tenant ON ap_invite(tenant_id, created_at DESC);
