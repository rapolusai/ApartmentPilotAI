-- Two-person rule for maintenance payments: the person who submitted a payment cannot approve it.
-- FinanceService.approve enforces this first with a clear 409; this constraint is defence in depth.
-- NOT VALID: historic self-approved rows are neither checked nor rewritten; every new or updated row is checked.
-- Approved rows are never updated later (reversals live in ap_payment_reversal), so old rows do not trip it.
-- Rollback is a new additive migration that drops the constraint, never an edit of this file.
ALTER TABLE ap_payment ADD CONSTRAINT ap_payment_approver_not_submitter
  CHECK (status <> 'APPROVED' OR reviewed_by IS DISTINCT FROM submitted_by) NOT VALID;
