-- Dedicated application schema, managed by Flyway. No DROP/TRUNCATE or demo records.
CREATE TABLE ap_tenant (
 id uuid PRIMARY KEY, name varchar(80) NOT NULL, city varchar(80) NOT NULL,
 created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE ap_flat (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 label varchar(20) NOT NULL, occupied boolean NOT NULL DEFAULT true,
 active boolean NOT NULL DEFAULT true,
 UNIQUE(tenant_id,label), UNIQUE(tenant_id,id)
);
CREATE TABLE ap_user (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 flat_id uuid, name varchar(80) NOT NULL, mobile varchar(10) NOT NULL UNIQUE,
 pin_hash varchar(100) NOT NULL,
 role varchar(12) NOT NULL CHECK(role IN ('ADMIN','TREASURER','RESIDENT')),
 status varchar(12) NOT NULL CHECK(status IN ('ACTIVE','PENDING','REJECTED','DISABLED')),
 rejection_reason varchar(300), created_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id),
 UNIQUE(tenant_id,id), CHECK(role <> 'RESIDENT' OR flat_id IS NOT NULL)
);
CREATE UNIQUE INDEX one_primary_resident_per_flat ON ap_user(tenant_id,flat_id)
 WHERE flat_id IS NOT NULL AND status IN ('ACTIVE','PENDING');
CREATE TABLE ap_session (
 token_hash char(64) PRIMARY KEY, user_id uuid NOT NULL REFERENCES ap_user(id),
 expires_at timestamptz NOT NULL, created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ap_session_user ON ap_session(user_id);
CREATE TABLE ap_rate_limit (
 bucket_key char(64) NOT NULL, window_start bigint NOT NULL, uses integer NOT NULL,
 PRIMARY KEY(bucket_key,window_start)
);
CREATE TABLE ap_invite (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 code_hash char(64) NOT NULL UNIQUE, expires_at timestamptz NOT NULL,
 active boolean NOT NULL DEFAULT true, created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE ap_billing_rule (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 effective_month date NOT NULL CHECK(extract(day FROM effective_month)=1),
 amount numeric(12,2) NOT NULL CHECK(amount>0),
 billing_day integer NOT NULL CHECK(billing_day BETWEEN 1 AND 28),
 due_day integer NOT NULL CHECK(due_day BETWEEN 1 AND 28),
 UNIQUE(tenant_id,effective_month), CHECK(due_day>=billing_day)
);
CREATE TABLE ap_bill (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 flat_id uuid NOT NULL, billing_month date NOT NULL CHECK(extract(day FROM billing_month)=1),
 amount numeric(12,2) NOT NULL CHECK(amount>0), due_date date NOT NULL,
 created_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id),
 UNIQUE(tenant_id,flat_id,billing_month), UNIQUE(tenant_id,id)
);
CREATE TABLE ap_payment (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 bill_id uuid NOT NULL, amount numeric(12,2) NOT NULL CHECK(amount>0),
 mode varchar(16) NOT NULL CHECK(mode IN ('UPI','CASH','BANK_TRANSFER','CHEQUE')),
 reference varchar(80), paid_on date NOT NULL, note varchar(300) NOT NULL DEFAULT '',
 status varchar(12) NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','APPROVED','REJECTED')),
 submitted_by uuid NOT NULL, submitted_at timestamptz NOT NULL DEFAULT now(),
 request_key uuid NOT NULL, reviewed_by uuid, reviewed_at timestamptz,
 reason varchar(300), receipt_number varchar(64) UNIQUE,
 FOREIGN KEY(tenant_id,bill_id) REFERENCES ap_bill(tenant_id,id),
 FOREIGN KEY(tenant_id,submitted_by) REFERENCES ap_user(tenant_id,id),
 FOREIGN KEY(tenant_id,reviewed_by) REFERENCES ap_user(tenant_id,id),
 UNIQUE(tenant_id,submitted_by,request_key),
 CHECK(mode='CASH' OR reference IS NOT NULL),
 CHECK((status='APPROVED' AND receipt_number IS NOT NULL) OR (status<>'APPROVED' AND receipt_number IS NULL))
);
CREATE UNIQUE INDEX unique_payment_reference ON ap_payment(tenant_id,reference)
 WHERE reference IS NOT NULL AND status<>'REJECTED';
CREATE INDEX ap_payment_bill ON ap_payment(tenant_id,bill_id,status);
CREATE INDEX ap_bill_tenant_month ON ap_bill(tenant_id,billing_month);
CREATE TABLE ap_expense (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 title varchar(100) NOT NULL, category varchar(30) NOT NULL,
 amount numeric(12,2) NOT NULL CHECK(amount>0), paid_on date NOT NULL,
 paid boolean NOT NULL DEFAULT false, public boolean NOT NULL DEFAULT true,
 created_by uuid NOT NULL, request_key uuid NOT NULL, created_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id),
 UNIQUE(tenant_id,created_by,request_key)
);
CREATE TABLE ap_notice (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 title varchar(100) NOT NULL, body varchar(2000) NOT NULL,
 status varchar(12) NOT NULL CHECK(status IN ('DRAFT','PUBLISHED')),
 created_by uuid NOT NULL, created_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id)
);
CREATE TABLE ap_inbox (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 recipient_id uuid NOT NULL, event_key varchar(150) NOT NULL,
 title varchar(100) NOT NULL, body varchar(500) NOT NULL, target varchar(150) NOT NULL,
 created_at timestamptz NOT NULL DEFAULT now(), read_at timestamptz,
 FOREIGN KEY(tenant_id,recipient_id) REFERENCES ap_user(tenant_id,id),
 UNIQUE(recipient_id,event_key)
);
CREATE TABLE ap_audit (
 id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 actor_id uuid, action varchar(60) NOT NULL, record_id varchar(80) NOT NULL,
 before_value varchar(500), after_value varchar(500),
 created_at timestamptz NOT NULL DEFAULT now()
);
