--
-- MIT License
--
-- Copyright (c) 2023, N. Harris Computer Corporation
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in all
-- copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
-- SOFTWARE.
--

-- Using a suitably privileged user account, run with the command:
--   psql -f create-audit-database.sql

create database audit;

\c audit;

grant all privileges on database audit to pguser;

create schema i2Audit authorization pguser;

create table i2Audit.Quick_Search (
    ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE NO CYCLE),
    User_Name VARCHAR(100) NOT NULL,
    Event_Time TIMESTAMP NOT NULL,
    User_Security_Groups VARCHAR(1000) NOT NULL,
    User_Security_Permissions VARCHAR(32672) NOT NULL,
    Client_User_Agent VARCHAR(1000) NOT NULL,
    Client_IP_Address VARCHAR(100) NOT NULL,
    Expression VARCHAR(1000) NOT NULL,
    Filters VARCHAR(1000) NOT NULL,
    Datastores VARCHAR(1000) NOT NULL
  );

create table i2Audit.Expand (
    ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE NO CYCLE),
    User_Name  VARCHAR(100) NOT NULL,
    Event_Time TIMESTAMP NOT NULL,
    User_Security_Groups VARCHAR(1000) NOT NULL,
    User_Security_Permissions VARCHAR(32672) NOT NULL,
    Client_User_Agent VARCHAR(1000) NOT NULL,
    Client_IP_Address VARCHAR(100) NOT NULL,
    Seeds TEXT NOT NULL
  );

create table i2Audit.Visual_Query (
    ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE NO CYCLE),
    User_Name VARCHAR(100) NOT NULL,
    Event_Time TIMESTAMP NOT NULL,
    User_Security_Groups VARCHAR(1000) NOT NULL,
    User_Security_Permissions VARCHAR(32672) NOT NULL,
    Client_User_Agent VARCHAR(1000) NOT NULL,
    Client_IP_Address VARCHAR(100) NOT NULL,
    Datastores VARCHAR(1000) NOT NULL,
    Query VARCHAR(32672) NOT NULL
  );

create table i2Audit.Record_Retrieval (
    ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE NO CYCLE),
    User_Name VARCHAR(100) NOT NULL,
    Event_Time TIMESTAMP NOT NULL,
    User_Security_Groups VARCHAR(1000) NOT NULL,
    User_Security_Permissions VARCHAR(32672) NOT NULL,
    Client_User_Agent VARCHAR(1000) NOT NULL,
    Client_IP_Address VARCHAR(100) NOT NULL,
    Records TEXT NOT NULL
  );

create index Quick_Search_Idx
  on i2Audit.Quick_Search (User_Name);

create index Expand_Idx
  on i2Audit.Expand (User_Name);

create index Visual_Query_Idx
  on i2Audit.Visual_Query (User_Name);

create index Record_Retrieval_Idx
  on i2Audit.Record_Retrieval (User_Name);
  
create view i2Audit.Users as
  (select distinct User_Name from i2Audit.Quick_Search)
  union
  (select distinct User_Name from i2Audit.Expand)
  union
  (select distinct User_Name from i2Audit.Visual_Query)
  union
  (select distinct User_Name from i2Audit.Record_Retrieval);
