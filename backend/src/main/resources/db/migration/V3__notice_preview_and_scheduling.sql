-- Additive notice-authoring metadata and explicit schedule confirmation.
-- V1/V2 remain immutable; existing scheduled drafts retain their prior behavior.
ALTER TABLE ap_notice ADD COLUMN notice_type varchar(20) NOT NULL DEFAULT 'GENERAL'
 CHECK(notice_type IN ('GENERAL','MAINTENANCE','SERVICE_ALERT','EMERGENCY'));
ALTER TABLE ap_notice ADD COLUMN category varchar(60) NOT NULL DEFAULT 'Other';
ALTER TABLE ap_notice ADD COLUMN phone_notify boolean NOT NULL DEFAULT false;
ALTER TABLE ap_notice ADD COLUMN schedule_confirmed boolean NOT NULL DEFAULT false;
UPDATE ap_notice SET schedule_confirmed=true WHERE scheduled_at IS NOT NULL;
CREATE INDEX ap_notice_schedule_due_idx ON ap_notice(tenant_id,scheduled_at)
 WHERE status='DRAFT' AND schedule_confirmed=true AND scheduled_at IS NOT NULL;
