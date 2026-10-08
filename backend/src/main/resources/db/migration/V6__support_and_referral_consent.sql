-- Additive local support requests and explicit first-month offer consent.
CREATE TABLE ap_support_request (
 id uuid PRIMARY KEY,
 tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 requester_id uuid NOT NULL,
 request_key uuid NOT NULL,
 topic varchar(40) NOT NULL CHECK(topic IN ('Payment approval','Account access','Flat mapping','Something else')),
 message varchar(1000) NOT NULL,
 status varchar(16) NOT NULL DEFAULT 'RECORDED' CHECK(status IN ('RECORDED')),
 created_at timestamptz NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,requester_id) REFERENCES ap_user(tenant_id,id),
 UNIQUE(tenant_id,requester_id,request_key)
);
ALTER TABLE ap_subscription ADD COLUMN offer_accepted_at timestamptz;
ALTER TABLE ap_subscription ADD COLUMN offer_terms_version varchar(20);
