--liquibase formatted sql

--changeSet kalea.wolff:update-users-password-expired-fl
UPDATE "PUBLIC"."USR_TABLE" SET "PASSWORD_EXPIRES_DT" = DATEADD('DAY', +90, CURRENT_DATE)
WHERE "PASSWORD_EXPIRES_DT" IS NULL;