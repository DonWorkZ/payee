--liquibase formatted sql

--changeSet neha.pal:drop-unique-constraint-from-phone-number
ALTER TABLE "PUBLIC"."USR_TABLE" DROP CONSTRAINT "PUBLIC"."UK_L4V3X784L6SYE8AFMPW7B1OS7";

--changeSet kalea.wolff:drop-email-constraint-and-index-from-user-table
ALTER TABLE "PUBLIC"."USR_TABLE" DROP CONSTRAINT "PUBLIC"."UK_LGGBFTBH7XHD0I9MU40XRDRNA";
DROP INDEX IF EXISTS "PUBLIC"."UK_LGGBFTBH7XHD0I9MU40XRDRNA_INDEX_F";

--changeSet kalea.wolff:change-fk-customer-id-to-opened_by_cust_id
ALTER TABLE "PUBLIC"."ACCOUNT" ALTER COLUMN "FK_CUSTOMER_ID" RENAME TO "OPENED_BY_CUST_ID";

--changeSet kalea.wolff:add-fk-columns-for-acct-summaries
ALTER TABLE "PUBLIC"."ACCOUNT" ADD "AUTH_CUST_ID" BIGINT;

--changeSet kalea.wolff:add-fk-column-for-owned-accts
ALTER TABLE "PUBLIC"."ACCOUNT" ADD "OWNING_CUST_ID" BIGINT;

--changeSet kalea.wolff:add-fk-main-acct-id-to-customer
ALTER TABLE "PUBLIC"."CUSTOMER" ADD "MAIN_ACCT_ID" BIGINT;

--changeSet kalea.wolff:add-fk-user-id-to-address
ALTER TABLE "PUBLIC"."ADDRESS" ADD "USR_ID" BIGINT;
ALTER TABLE "PUBLIC"."USR_TABLE" DROP COLUMN "FK_ADDRESS_ID";

--changeSet kalea.wolff:alter-transaction-fk-account-id-to-bigint
ALTER TABLE "PUBLIC"."TRANSACTION" ALTER COLUMN "FK_ACCOUNT_ID" SET DATA TYPE BIGINT;