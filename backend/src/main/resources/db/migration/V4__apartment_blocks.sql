-- Persistent apartment blocks. Existing flat block labels seed the new parent records.
CREATE TABLE ap_block (
 id uuid PRIMARY KEY,
 tenant_id uuid NOT NULL REFERENCES ap_tenant(id),
 name varchar(20) NOT NULL,
 UNIQUE(tenant_id,name),
 UNIQUE(tenant_id,id)
);

INSERT INTO ap_block(id,tenant_id,name)
SELECT gen_random_uuid(),tenant_id,block
FROM ap_flat
GROUP BY tenant_id,block;

ALTER TABLE ap_flat
 ADD CONSTRAINT ap_flat_block_parent
 FOREIGN KEY(tenant_id,block) REFERENCES ap_block(tenant_id,name)
 ON UPDATE CASCADE;
