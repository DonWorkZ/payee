--liquibase formatted sql

--changeSet kalea.wolff:update-initially-seeded-users-to-have-an-entry-in-customers-table-and-a-role
UPDATE "PUBLIC"."USR_TABLE" SET ROLE = 'CUSTOMER' WHERE ROLE IS NULL;

INSERT INTO "PUBLIC"."CUSTOMER" (USR_ID) VALUES(1);
INSERT INTO "PUBLIC"."CUSTOMER" (USR_ID) VALUES(2);
