-- Additive migration. V1 is intentionally immutable. Back up development data before applying.
ALTER TABLE ap_tenant ADD COLUMN address varchar(300) NOT NULL DEFAULT '';
ALTER TABLE ap_tenant ADD COLUMN pincode varchar(6) NOT NULL DEFAULT '';
ALTER TABLE ap_flat ADD COLUMN block varchar(20) NOT NULL DEFAULT 'A';
ALTER TABLE ap_flat ADD COLUMN floor integer NOT NULL DEFAULT 1;
ALTER TABLE ap_user ADD COLUMN email varchar(120) NOT NULL DEFAULT '';
ALTER TABLE ap_user ADD COLUMN resident_type varchar(10) NOT NULL DEFAULT 'OWNER' CHECK(resident_type IN ('OWNER','TENANT'));
CREATE TABLE ap_settings (
 tenant_id uuid PRIMARY KEY REFERENCES ap_tenant(id), payee varchar(100) NOT NULL DEFAULT '',
 upi varchar(100) NOT NULL DEFAULT '', bank varchar(100) NOT NULL DEFAULT '', account varchar(30) NOT NULL DEFAULT '', ifsc varchar(11) NOT NULL DEFAULT '',
 opening_amount numeric(12,2) NOT NULL DEFAULT 0, opening_date date,
 bill_vacant boolean NOT NULL DEFAULT true, late_enabled boolean NOT NULL DEFAULT false,
 late_fee numeric(12,2) NOT NULL DEFAULT 0 CHECK(late_fee>=0), grace_days integer NOT NULL DEFAULT 0 CHECK(grace_days BETWEEN 0 AND 30),
 due_notify boolean NOT NULL DEFAULT true, reminders boolean NOT NULL DEFAULT true,
 reminder_days varchar(80) NOT NULL DEFAULT '5,9,11,15', expenses_visible boolean NOT NULL DEFAULT true,
 proof_required boolean NOT NULL DEFAULT false, quiet_start varchar(5) NOT NULL DEFAULT '21:00',quiet_end varchar(5) NOT NULL DEFAULT '08:00',
 booking_rules varchar(2000) NOT NULL DEFAULT 'Keep access routes clear. Leave the space clean. Approval is required.'
);
INSERT INTO ap_settings(tenant_id) SELECT id FROM ap_tenant ON CONFLICT DO NOTHING;
ALTER TABLE ap_bill ADD COLUMN kind varchar(20) NOT NULL DEFAULT 'MAINTENANCE' CHECK(kind IN ('MAINTENANCE','CONTRIBUTION','OPENING_DUE'));
ALTER TABLE ap_bill ADD COLUMN charge_key varchar(80) NOT NULL DEFAULT 'MAINTENANCE';
ALTER TABLE ap_bill ADD COLUMN title varchar(100) NOT NULL DEFAULT 'Monthly maintenance';
ALTER TABLE ap_bill ADD COLUMN late_fee numeric(12,2) NOT NULL DEFAULT 0 CHECK(late_fee>=0);
ALTER TABLE ap_bill ADD COLUMN late_applied_at timestamptz;
ALTER TABLE ap_bill DROP CONSTRAINT ap_bill_tenant_id_flat_id_billing_month_key;
ALTER TABLE ap_bill ADD CONSTRAINT ap_bill_charge_unique UNIQUE(tenant_id,flat_id,billing_month,charge_key);
CREATE TABLE ap_rate_override (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),flat_id uuid NOT NULL,
 effective_month date NOT NULL CHECK(extract(day FROM effective_month)=1),amount numeric(12,2) NOT NULL CHECK(amount>0),reason varchar(300) NOT NULL,
 FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id),UNIQUE(tenant_id,flat_id,effective_month)
);
CREATE TABLE ap_command (
 tenant_id uuid NOT NULL REFERENCES ap_tenant(id),actor_id uuid NOT NULL,request_key uuid NOT NULL,
 operation varchar(60) NOT NULL,fingerprint char(64) NOT NULL,result_id uuid NOT NULL,
 PRIMARY KEY(tenant_id,actor_id,request_key),FOREIGN KEY(tenant_id,actor_id) REFERENCES ap_user(tenant_id,id)
);
CREATE TABLE ap_income (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),title varchar(100) NOT NULL,
 amount numeric(12,2) NOT NULL CHECK(amount>0),received_on date NOT NULL,mode varchar(20) NOT NULL,
 created_by uuid NOT NULL,created_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id)
);
ALTER TABLE ap_expense ADD COLUMN mode varchar(20) NOT NULL DEFAULT 'CASH';
ALTER TABLE ap_expense ADD COLUMN notes varchar(500) NOT NULL DEFAULT '';
ALTER TABLE ap_expense ADD COLUMN reversed_on date;
ALTER TABLE ap_expense ADD COLUMN reversal_reason varchar(300);
ALTER TABLE ap_expense ADD COLUMN recurring_id uuid;
ALTER TABLE ap_expense ADD COLUMN recurring_on date;
CREATE TABLE ap_recurring (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),title varchar(100) NOT NULL,category varchar(30) NOT NULL,
 amount numeric(12,2) NOT NULL CHECK(amount>0),frequency varchar(20) NOT NULL CHECK(frequency IN ('MONTHLY','QUARTERLY','HALF_YEARLY','YEARLY')),
 next_on date NOT NULL,active boolean NOT NULL DEFAULT true,created_by uuid NOT NULL,
 FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id),UNIQUE(tenant_id,id)
);
ALTER TABLE ap_expense ADD CONSTRAINT ap_expense_recurring_fk FOREIGN KEY(tenant_id,recurring_id) REFERENCES ap_recurring(tenant_id,id);
CREATE UNIQUE INDEX ap_recurring_expense_once ON ap_expense(tenant_id,recurring_id,recurring_on) WHERE recurring_id IS NOT NULL;
CREATE TABLE ap_member_record (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),flat_id uuid NOT NULL,name varchar(80) NOT NULL,
 mobile varchar(10) NOT NULL DEFAULT '',resident_type varchar(10) NOT NULL CHECK(resident_type IN ('OWNER','TENANT')),
 active boolean NOT NULL DEFAULT true,FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id),UNIQUE(tenant_id,id)
);
CREATE TABLE ap_committee (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),name varchar(80) NOT NULL,title varchar(60) NOT NULL,
 flat_id uuid,start_on date NOT NULL,end_on date,public boolean NOT NULL DEFAULT true,active boolean NOT NULL DEFAULT true,
 FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id),CHECK(end_on IS NULL OR end_on>=start_on)
);
CREATE TABLE ap_contact (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),kind varchar(10) NOT NULL CHECK(kind IN ('VENDOR','CONTACT')),
 name varchar(100) NOT NULL,category varchar(40) NOT NULL,phone varchar(20) NOT NULL DEFAULT '',notes varchar(500) NOT NULL DEFAULT '',
 public boolean NOT NULL DEFAULT true,active boolean NOT NULL DEFAULT true,amc_end date,UNIQUE(tenant_id,id)
);
CREATE TABLE ap_ticket (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),kind varchar(12) NOT NULL CHECK(kind IN ('ISSUE','COMPLAINT')),
 flat_id uuid,created_by uuid NOT NULL,title varchar(100) NOT NULL,description varchar(2000) NOT NULL,category varchar(40) NOT NULL,
 scope varchar(40) NOT NULL DEFAULT 'ALL',priority varchar(12) NOT NULL CHECK(priority IN ('NORMAL','HIGH','URGENT')),
 status varchar(20) NOT NULL DEFAULT 'OPEN' CHECK(status IN ('OPEN','ASSIGNED','IN_PROGRESS','RESOLVED','CLOSED')),
 vendor_id uuid,eta timestamptz,created_at timestamptz NOT NULL DEFAULT now(),updated_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id),FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id),
 FOREIGN KEY(tenant_id,vendor_id) REFERENCES ap_contact(tenant_id,id),UNIQUE(tenant_id,id)
);
CREATE TABLE ap_ticket_event (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL,ticket_id uuid NOT NULL,actor_id uuid NOT NULL,message varchar(2000) NOT NULL,
 status varchar(20),created_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,ticket_id) REFERENCES ap_ticket(tenant_id,id),FOREIGN KEY(tenant_id,actor_id) REFERENCES ap_user(tenant_id,id)
);
CREATE TABLE ap_ticket_follow (
 tenant_id uuid NOT NULL,ticket_id uuid NOT NULL,user_id uuid NOT NULL,
 PRIMARY KEY(ticket_id,user_id),FOREIGN KEY(tenant_id,ticket_id) REFERENCES ap_ticket(tenant_id,id),FOREIGN KEY(tenant_id,user_id) REFERENCES ap_user(tenant_id,id)
);
CREATE TABLE ap_service (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),title varchar(100) NOT NULL,category varchar(40) NOT NULL,
 vendor_id uuid,frequency varchar(20) NOT NULL CHECK(frequency IN ('MONTHLY','QUARTERLY','HALF_YEARLY','YEARLY')),
 next_on date NOT NULL,active boolean NOT NULL DEFAULT true,
 FOREIGN KEY(tenant_id,vendor_id) REFERENCES ap_contact(tenant_id,id),UNIQUE(tenant_id,id)
);
CREATE TABLE ap_service_visit (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL,service_id uuid NOT NULL,scheduled_on date NOT NULL,completed_on date NOT NULL,
 note varchar(1000) NOT NULL,cost numeric(12,2) NOT NULL DEFAULT 0 CHECK(cost>=0),expense_id uuid,
 FOREIGN KEY(tenant_id,service_id) REFERENCES ap_service(tenant_id,id),UNIQUE(service_id,scheduled_on)
);
ALTER TABLE ap_notice ADD COLUMN audience varchar(20) NOT NULL DEFAULT 'ALL' CHECK(audience IN ('ALL','BLOCK','SELECTED','UNPAID'));
ALTER TABLE ap_notice ADD COLUMN audience_value varchar(500) NOT NULL DEFAULT '';
ALTER TABLE ap_notice ADD COLUMN pinned boolean NOT NULL DEFAULT false;
ALTER TABLE ap_notice ADD COLUMN scheduled_at timestamptz;
ALTER TABLE ap_notice ADD COLUMN acknowledge boolean NOT NULL DEFAULT false;
ALTER TABLE ap_notice ADD CONSTRAINT ap_notice_tenant_id_unique UNIQUE(tenant_id,id);
CREATE TABLE ap_notice_recipient (
 tenant_id uuid NOT NULL,notice_id uuid NOT NULL,user_id uuid NOT NULL,read_at timestamptz,acknowledged_at timestamptz,
 PRIMARY KEY(notice_id,user_id),FOREIGN KEY(tenant_id,notice_id) REFERENCES ap_notice(tenant_id,id),FOREIGN KEY(tenant_id,user_id) REFERENCES ap_user(tenant_id,id)
);
INSERT INTO ap_notice_recipient(tenant_id,notice_id,user_id)
 SELECT n.tenant_id,n.id,u.id FROM ap_notice n JOIN ap_user u ON u.tenant_id=n.tenant_id
 WHERE n.status='PUBLISHED' AND u.status='ACTIVE' ON CONFLICT DO NOTHING;
CREATE TABLE ap_document (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),title varchar(100) NOT NULL,category varchar(40) NOT NULL,
 public boolean NOT NULL DEFAULT false,archived boolean NOT NULL DEFAULT false,created_by uuid NOT NULL,created_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id),UNIQUE(tenant_id,id)
);
CREATE TABLE ap_file (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),parent_kind varchar(20) NOT NULL,parent_id uuid NOT NULL,
 name varchar(120) NOT NULL,mime varchar(60) NOT NULL,bytes bytea NOT NULL,sha256 char(64) NOT NULL,created_by uuid NOT NULL,
 created_at timestamptz NOT NULL DEFAULT now(),FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id),CHECK(octet_length(bytes) BETWEEN 1 AND 5242880),
 UNIQUE(tenant_id,parent_kind,parent_id,sha256)
);
CREATE TABLE ap_resource (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),name varchar(80) NOT NULL,
 kind varchar(12) NOT NULL CHECK(kind IN ('SPACE','CAR','BIKE')),capacity integer NOT NULL CHECK(capacity BETWEEN 1 AND 100),
 bookable boolean NOT NULL DEFAULT true,private_flat_id uuid,owner_consent boolean NOT NULL DEFAULT false,
 available_from timestamptz,available_until timestamptz,
 FOREIGN KEY(tenant_id,private_flat_id) REFERENCES ap_flat(tenant_id,id),UNIQUE(tenant_id,id),
 CHECK(available_until IS NULL OR available_until>available_from)
);
CREATE TABLE ap_resource_block (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL,resource_id uuid NOT NULL,starts_at timestamptz NOT NULL,ends_at timestamptz NOT NULL,reason varchar(300) NOT NULL,
 FOREIGN KEY(tenant_id,resource_id) REFERENCES ap_resource(tenant_id,id),CHECK(ends_at>starts_at)
);
CREATE TABLE ap_booking (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),flat_id uuid,created_by uuid NOT NULL,
 title varchar(100) NOT NULL,event_type varchar(40) NOT NULL,guests integer NOT NULL CHECK(guests BETWEEN 1 AND 1000),
 starts_at timestamptz NOT NULL,ends_at timestamptz NOT NULL,notes varchar(1000) NOT NULL DEFAULT '',
 status varchar(20) NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','ALTERNATIVE','APPROVED','REJECTED','CANCELLED','COMPLETED')),
 decision_note varchar(500) NOT NULL DEFAULT '',offered_start timestamptz,offered_end timestamptz,revision integer NOT NULL DEFAULT 1,
 created_at timestamptz NOT NULL DEFAULT now(),FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id),FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id),
 UNIQUE(tenant_id,id),CHECK(ends_at>starts_at)
);
CREATE TABLE ap_booking_item (
 tenant_id uuid NOT NULL,booking_id uuid NOT NULL,resource_id uuid NOT NULL,quantity integer NOT NULL CHECK(quantity>0),offered boolean NOT NULL DEFAULT false,
 PRIMARY KEY(booking_id,resource_id,offered),FOREIGN KEY(tenant_id,booking_id) REFERENCES ap_booking(tenant_id,id),FOREIGN KEY(tenant_id,resource_id) REFERENCES ap_resource(tenant_id,id)
);
CREATE TABLE ap_booking_event (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL,booking_id uuid NOT NULL,actor_id uuid NOT NULL,status varchar(20) NOT NULL,note varchar(1000) NOT NULL,
 created_at timestamptz NOT NULL DEFAULT now(),FOREIGN KEY(tenant_id,booking_id) REFERENCES ap_booking(tenant_id,id)
);
CREATE INDEX ap_booking_overlap ON ap_booking(tenant_id,status,starts_at,ends_at);
CREATE TABLE ap_subscription (
 tenant_id uuid PRIMARY KEY REFERENCES ap_tenant(id),activated_at date NOT NULL,first_end date NOT NULL,
 referral_code varchar(24) NOT NULL UNIQUE,installed_at timestamptz,verified_at timestamptz,verified_by varchar(80),
 CHECK(first_end>activated_at)
);
CREATE TABLE ap_subscription_invoice (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL REFERENCES ap_tenant(id),starts_on date NOT NULL,ends_on date NOT NULL,
 amount numeric(12,2) NOT NULL CHECK(amount>0),credit numeric(12,2) NOT NULL DEFAULT 0 CHECK(credit>=0),
 paid numeric(12,2) NOT NULL DEFAULT 0 CHECK(paid>=0),status varchar(12) NOT NULL DEFAULT 'DUE' CHECK(status IN ('DUE','PAID','WAIVED')),
 UNIQUE(tenant_id,starts_on),UNIQUE(tenant_id,id),CHECK(ends_on>starts_on),CHECK(credit+paid<=amount)
);
CREATE TABLE ap_referral (
 id uuid PRIMARY KEY,referrer_id uuid NOT NULL REFERENCES ap_tenant(id),referred_id uuid NOT NULL UNIQUE REFERENCES ap_tenant(id),
 created_at timestamptz NOT NULL DEFAULT now(),rewarded boolean NOT NULL DEFAULT false,CHECK(referrer_id<>referred_id)
);
CREATE TABLE ap_provider_payment (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL,invoice_id uuid NOT NULL,provider_reference varchar(100) NOT NULL UNIQUE,
 amount numeric(12,2) NOT NULL CHECK(amount>0),verified_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,invoice_id) REFERENCES ap_subscription_invoice(tenant_id,id)
);

CREATE TABLE ap_resource_conflict (
 tenant_id uuid NOT NULL,resource_id uuid NOT NULL,other_id uuid NOT NULL,
 PRIMARY KEY(resource_id,other_id),FOREIGN KEY(tenant_id,resource_id) REFERENCES ap_resource(tenant_id,id),
 FOREIGN KEY(tenant_id,other_id) REFERENCES ap_resource(tenant_id,id),CHECK(resource_id<>other_id)
);
ALTER TABLE ap_bill ADD COLUMN penalty_amount numeric(12,2) NOT NULL DEFAULT 0 CHECK(penalty_amount>=0);
ALTER TABLE ap_bill ADD COLUMN penalty_grace integer NOT NULL DEFAULT 0 CHECK(penalty_grace BETWEEN 0 AND 30);

ALTER TABLE ap_payment ADD CONSTRAINT ap_payment_tenant_id_unique UNIQUE(tenant_id,id);
CREATE TABLE ap_payment_reversal (
 id uuid PRIMARY KEY,tenant_id uuid NOT NULL,payment_id uuid NOT NULL UNIQUE,reversed_on date NOT NULL,
 amount numeric(12,2) NOT NULL CHECK(amount>0),reason varchar(300) NOT NULL,created_by uuid NOT NULL,
 FOREIGN KEY(tenant_id,payment_id) REFERENCES ap_payment(tenant_id,id),FOREIGN KEY(tenant_id,created_by) REFERENCES ap_user(tenant_id,id)
);

-- A referrer can unlock exactly one group; retries cannot award later children.
create table ap_referral_award (
 referrer_id uuid primary key references ap_tenant(id), first_child uuid not null references ap_tenant(id),
 second_child uuid not null references ap_tenant(id), awarded_at timestamptz not null default now(),
 check(first_child<>second_child and referrer_id<>first_child and referrer_id<>second_child)
);
create table ap_subscription_refund_review (
 invoice_id uuid primary key references ap_subscription_invoice(id), tenant_id uuid not null references ap_tenant(id),
 reason varchar(300) not null, created_at timestamptz not null default now(), resolved_at timestamptz
);

create table ap_poll(id uuid primary key,tenant_id uuid not null references ap_tenant(id),title varchar(160) not null,
 description varchar(1000) not null default '',closes_at timestamptz not null,closed boolean not null default false,
 created_by uuid not null references ap_user(id),created_at timestamptz not null default now());
create table ap_poll_option(id uuid primary key,poll_id uuid not null references ap_poll(id),label varchar(120) not null,
 position integer not null,unique(poll_id,position),unique(poll_id,label));
create table ap_poll_vote(tenant_id uuid not null references ap_tenant(id),poll_id uuid not null references ap_poll(id),
 flat_id uuid not null references ap_flat(id),option_id uuid not null references ap_poll_option(id),user_id uuid not null references ap_user(id),
 created_at timestamptz not null default now(),primary key(poll_id,flat_id));
create table ap_vehicle(id uuid primary key,tenant_id uuid not null references ap_tenant(id),flat_id uuid not null references ap_flat(id),
 registration varchar(20) not null,kind varchar(10) not null check(kind in ('CAR','BIKE','OTHER')),
 parking_label varchar(40) not null default '',active boolean not null default true,created_by uuid not null references ap_user(id),
 unique(tenant_id,registration));

-- Cross-tenant integrity is also enforced at the database layer for added financial/community records.
ALTER TABLE ap_expense ADD CONSTRAINT ap_expense_tenant_id_unique UNIQUE(tenant_id,id);
ALTER TABLE ap_service_visit ADD CONSTRAINT ap_service_visit_expense_fk FOREIGN KEY(tenant_id,expense_id) REFERENCES ap_expense(tenant_id,id);
ALTER TABLE ap_poll ADD CONSTRAINT ap_poll_tenant_id_unique UNIQUE(tenant_id,id);
ALTER TABLE ap_poll_option ADD CONSTRAINT ap_poll_option_poll_id_unique UNIQUE(poll_id,id);
ALTER TABLE ap_poll_vote ADD CONSTRAINT ap_poll_vote_poll_fk FOREIGN KEY(tenant_id,poll_id) REFERENCES ap_poll(tenant_id,id);
ALTER TABLE ap_poll_vote ADD CONSTRAINT ap_poll_vote_option_fk FOREIGN KEY(poll_id,option_id) REFERENCES ap_poll_option(poll_id,id);
ALTER TABLE ap_poll_vote ADD CONSTRAINT ap_poll_vote_flat_fk FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id);
ALTER TABLE ap_poll_vote ADD CONSTRAINT ap_poll_vote_user_fk FOREIGN KEY(tenant_id,user_id) REFERENCES ap_user(tenant_id,id);
ALTER TABLE ap_vehicle ADD CONSTRAINT ap_vehicle_flat_fk FOREIGN KEY(tenant_id,flat_id) REFERENCES ap_flat(tenant_id,id);
