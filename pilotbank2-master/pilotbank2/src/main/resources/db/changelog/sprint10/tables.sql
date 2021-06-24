--liquibase formatted sql

--changeSet taka.watanabe:add-security-question-and-answer-fields
ALTER TABLE "PUBLIC"."USR_TABLE" ADD "SECURITY_QUESTION" VARCHAR(255);
ALTER TABLE "PUBLIC"."USR_TABLE" ADD "SECURITY_ANSWER" VARCHAR(255);

--changeSet taka.watanabe:add-temp-lockout-and-incorrect-answer-fields
ALTER TABLE "PUBLIC"."USR_TABLE" ADD "TEMP_LOCK_OUT_EXP" TIMESTAMP;
ALTER TABLE "PUBLIC"."USR_TABLE" ADD "INCORRECT_ANSWER_COUNT" NUMBER(1) DEFAULT 0;

--changeSet taka.watanabe:add-correct-answer-flag-field
ALTER TABLE "PUBLIC"."USR_TABLE" ADD "CORRECT_ANSWER_FL" BOOLEAN DEFAULT FALSE;

--changeSet taka.watanabe:add-security-code-verification-flag-field
ALTER TABLE "PUBLIC"."USR_TABLE" ADD "SECURITY_CODE_VERIFICATION_FL" BOOLEAN DEFAULT FALSE;

--changeSet taka.watanabe:add-device-info-field
ALTER TABLE "PUBLIC"."USR_TABLE" ADD "DEVICE_INFO" VARCHAR(255) DEFAULT NULL;

--changeSet kris.case:add-security-code-info-field
ALTER TABLE "PUBLIC"."USR_TABLE" ADD "SECURITY_CODE" NUMBER(6) DEFAULT 0;

--changeSet kris.case:add-account-number-field-into-account-table (needs to be updated to default 0 eventually)
ALTER TABLE "PUBLIC"."ACCOUNT" ADD "ACCOUNT_NUMBER" NUMBER(10) DEFAULT 10;