-- Additive only: Google Play Billing subscriptions for apartments, and the 5-day free trial for new apartments.
-- The subscription belongs to the apartment: whoever bought it, a valid purchase keeps the whole apartment active,
-- including after the Admin role moves to someone else.
CREATE TABLE ap_play_purchase (
  token_hash char(64) PRIMARY KEY,
  purchase_token varchar(2000) NOT NULL,
  tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
  buyer_user_id uuid,
  buyer_name varchar(80) NOT NULL DEFAULT '',
  product_id varchar(100) NOT NULL,
  base_plan_id varchar(100) NOT NULL DEFAULT '',
  state varchar(40) NOT NULL,
  auto_renewing boolean NOT NULL DEFAULT false,
  expiry_at timestamptz,
  latest_order_id varchar(100),
  replaced_by char(64),
  acknowledged boolean NOT NULL DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ap_play_purchase_tenant ON ap_play_purchase(tenant_id, expiry_at);

-- Existing apartments keep the trial they already have: the stored end becomes the effective end that the old rule gave
-- (the whole first month, never less than the stored value). New apartments get 5 days.
UPDATE ap_tenant
SET trial_ends_at = GREATEST(trial_ends_at,
      (((created_at AT TIME ZONE 'Asia/Kolkata')::date + INTERVAL '1 month')::timestamp) AT TIME ZONE 'Asia/Kolkata');
ALTER TABLE ap_tenant ALTER COLUMN trial_ends_at SET DEFAULT (now() + INTERVAL '5 days');
