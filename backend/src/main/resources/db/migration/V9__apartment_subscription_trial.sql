-- New apartments receive a three-day trial. Existing apartments receive at least
-- three days from rollout so this additive policy does not unexpectedly lock them out.
ALTER TABLE ap_tenant ADD COLUMN trial_ends_at timestamptz;

UPDATE ap_tenant
SET trial_ends_at = GREATEST(created_at + INTERVAL '3 days', now() + INTERVAL '3 days')
WHERE trial_ends_at IS NULL;

ALTER TABLE ap_tenant
    ALTER COLUMN trial_ends_at SET DEFAULT (now() + INTERVAL '3 days'),
    ALTER COLUMN trial_ends_at SET NOT NULL;
