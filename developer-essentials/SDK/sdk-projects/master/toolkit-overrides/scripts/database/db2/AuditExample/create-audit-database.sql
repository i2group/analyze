-- Using a suitably privileged user account, run with the command:
--   db2 -s -td; -v -f create-audit-database.sql

create database Audit;

connect to Audit;

grant dataaccess on database to user db2user;

create bufferpool Audit_BP
  pagesize 32k;
  
create tablespace Audit_TS
  pagesize 32k
  bufferpool Audit_BP;
  
create schema i2Audit authorization db2user;

create table i2Audit.Quick_Search (
    ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE NO CYCLE NO CACHE),
    User VARCHAR(100) NOT NULL,
    Event_Time TIMESTAMP NOT NULL,
    User_Security_Groups VARCHAR(1000) NOT NULL,
    User_Security_Permissions VARCHAR(32672) NOT NULL,
    Client_User_Agent VARCHAR(1000) NOT NULL,
    Client_IP_Address VARCHAR(100) NOT NULL,
    Expression VARCHAR(1000) NOT NULL,
    Filters VARCHAR(1000) NOT NULL,
    Datastores VARCHAR(1000) NOT NULL
  )
  in Audit_TS;
  
create table i2Audit.Expand (
    ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE NO CYCLE NO CACHE),
    User VARCHAR(100) NOT NULL,
    Event_Time TIMESTAMP NOT NULL,
    User_Security_Groups VARCHAR(1000) NOT NULL,
    User_Security_Permissions VARCHAR(32672) NOT NULL,
    Client_User_Agent VARCHAR(1000) NOT NULL,
    Client_IP_Address VARCHAR(100) NOT NULL,
    Seeds VARCHAR(32672) NOT NULL
  )
  in Audit_TS;

create view i2Audit.User as
  (select qs.user as user from i2Audit.Quick_Search qs)
  union
  (select ex.user as user from i2Audit.Expand ex);

disconnect current;
