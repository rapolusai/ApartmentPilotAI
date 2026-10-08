-- Optional recurring vendor; retain existing templates/expenses and their IDs.
ALTER TABLE ap_recurring ADD COLUMN vendor_id uuid;
ALTER TABLE ap_recurring ADD CONSTRAINT ap_recurring_vendor_fk
    FOREIGN KEY (tenant_id,vendor_id) REFERENCES ap_contact(tenant_id,id);
ALTER TABLE ap_expense ADD COLUMN vendor_id uuid;
ALTER TABLE ap_expense ADD CONSTRAINT ap_expense_vendor_fk
    FOREIGN KEY (tenant_id,vendor_id) REFERENCES ap_contact(tenant_id,id);
