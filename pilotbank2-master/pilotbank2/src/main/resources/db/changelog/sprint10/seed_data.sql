--liquibase formatted sql

--changeSet taka.watanabe:update-security-question-and-answer
UPDATE "PUBLIC"."USR_TABLE" SET SECURITY_QUESTION = 'What is your favourite food?'
WHERE SECURITY_QUESTION IS NULL;
UPDATE "PUBLIC"."USR_TABLE" SET SECURITY_ANSWER = 'pizza'
WHERE SECURITY_ANSWER IS NULL;

--changeSet taka.watanabe:update-temp-lockout-expiration
UPDATE "PUBLIC"."USR_TABLE" SET "TEMP_LOCK_OUT_EXP" = PARSEDATETIME('1970-01-01 00:00:00.00', 'yyyy-MM-dd hh:mm:ss.SS')
WHERE "TEMP_LOCK_OUT_EXP" IS NULL;

--changeSet kris.case:update-security-code
UPDATE "PUBLIC"."USR_TABLE" SET "SECURITY_CODE" = 0
WHERE "SECURITY_CODE" IS NULL;

--changeSet kris.case:update-account-number
UPDATE "PUBLIC"."ACCOUNT" SET "ACCOUNT_NUMBER" = 418728318
WHERE "ACCOUNT_NUMBER" IS NULL;