DROP TABLE IF EXISTS ACCOUNT;
CREATE TABLE Account (
       ID SERIAL NOT NULL
     , USERNAME VARCHAR(45) NOT NULL
     , PASSWORD CHARACTER(8) NOT NULL
     , ENABLED BOOLEAN DEFAULT 'true'
     , AUTHORITY VARCHAR(45) DEFAULT 'ROLE_USER'
     , PRIMARY KEY (ID)
);
