-- Guided setup progress. Additive only: no existing table or record is changed.
-- user_id IS NULL  -> an apartment-level step (done once per apartment, shared by every Admin).
-- user_id NOT NULL -> a personal step for that account.
CREATE TABLE ap_setup_step (
 tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 user_id uuid REFERENCES ap_user(id),
 step_key varchar(40) NOT NULL,
 completed_at timestamptz NOT NULL DEFAULT now(),
 completed_by uuid REFERENCES ap_user(id)
);
CREATE UNIQUE INDEX ap_setup_step_apartment ON ap_setup_step(tenant_id,step_key) WHERE user_id IS NULL;
CREATE UNIQUE INDEX ap_setup_step_personal ON ap_setup_step(user_id,step_key) WHERE user_id IS NOT NULL;

-- Apartments that already exist were set up before guided setup existed: never send their Admins back through it.
INSERT INTO ap_setup_step(tenant_id,user_id,step_key)
SELECT t.id,NULL,k.step_key FROM ap_tenant t
CROSS JOIN (VALUES ('apartment'),('blocks'),('flats'),('payment'),('maintenance'),('late_fee'),('opening_balance'),('old_dues'),('invite'),('readiness')) AS k(step_key)
ON CONFLICT DO NOTHING;

-- Existing approved accounts keep working. Only the one-time Terms & privacy step remains for them.
INSERT INTO ap_setup_step(tenant_id,user_id,step_key)
SELECT u.tenant_id,u.id,k.step_key FROM ap_user u
CROSS JOIN (VALUES ('treasurer_rules'),('payment_check'),('late_fee_check'),('recurring'),('profile'),('vehicles'),('how_to_pay'),('stay_informed')) AS k(step_key)
WHERE u.status='ACTIVE'
ON CONFLICT DO NOTHING;
