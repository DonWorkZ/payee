--liquibase formatted sql

--changeSet kalea.wolff:add-initial-seed-data
INSERT INTO USR_TABLE (USR_ID,USER_NAME, FIRST_NAME,LAST_NAME,email,USER_PASSWORD,Occupation) VALUES (USER_PK_SEQ.nextval,'Jay11','Jay','Wael','aa@gmail.com','123','Engineer');
INSERT INTO USR_TABLE (USR_ID,USER_NAME, FIRST_NAME,LAST_NAME,email,USER_PASSWORD,Occupation) VALUES (USER_PK_SEQ.nextval,'J1','Jane','Doe','aabb@gmail.com','1234','Doctor');