--liquibase formatted sql

--changeSet taka.watanabe:update-account-creation-date
UPDATE "PUBLIC"."ACCOUNT" SET "DATE_TIME" = parsedatetime('2020-11-01 00:00:00.00', 'yyyy-MM-dd hh:mm:ss.SS')
WHERE "DATE_TIME" IS NULL;