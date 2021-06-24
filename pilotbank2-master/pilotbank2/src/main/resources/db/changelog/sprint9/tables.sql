--liquibase formatted sql

--changeSet taka.watanabe:add-account-creation-date-fields
ALTER TABLE "PUBLIC"."ACCOUNT" ADD "DATE_TIME" TIMESTAMP DEFAULT NULL;