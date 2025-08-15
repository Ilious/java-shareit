-- liquibase formatted sql

-- changeset Ильнур:1754381035624-4
ALTER TABLE item_requests
    ADD title VARCHAR(255);

-- changeset Ильнур:1754381035624-1
ALTER TABLE item_requests DROP COLUMN created;

-- changeset Ильнур:1754381035624-2
ALTER TABLE item_requests
    ADD created TIMESTAMP WITHOUT TIME ZONE;

-- changeset Ильнур:1754381035624-3
ALTER TABLE item_requests
    ALTER COLUMN requester_id DROP NOT NULL;

