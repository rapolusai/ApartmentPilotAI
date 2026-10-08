-- Additive indexes only. No data is changed; V1-V9 remain immutable.
-- Purge support for SessionCleanupJob.
CREATE INDEX ap_session_expires_idx ON ap_session(expires_at);
CREATE INDEX ap_rate_limit_window_idx ON ap_rate_limit(window_start);
-- Newest-first tenant lists: audit trail, inbox and issues.
CREATE INDEX ap_audit_tenant_created_idx ON ap_audit(tenant_id,created_at DESC);
CREATE INDEX ap_inbox_recipient_created_idx ON ap_inbox(tenant_id,recipient_id,created_at DESC);
CREATE INDEX ap_ticket_tenant_kind_updated_idx ON ap_ticket(tenant_id,kind,updated_at DESC);
