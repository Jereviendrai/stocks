# Upgrading the server

It is only possible to upgrade the server version to the next each. For a 
successful upgrade just follow the instructions listed here in the order they
are listed, from your current version, to the version you want to upgrade to.

## 1.0.0

Just perform the distribution's upgrade procedure. 

## 0.5.7

Changes in the database model require a database migration. Shut down the server
tomcat server before upgrading. Then execute the SQL script provided in 
/usr/share/stocks-server/db-migration.sql. This adds new tables and triggers
to the database. Once this is complete, start the new server. 

## 0.5.6

It is recommended to do a full server migration as described in maintainance.md
section "Stocks migration". Reason for this are the changes in the container:
Before 5.6.0 there was a Jetty container, which is exchanged by tomcat8. 

## Before 0.5

No instructions are provided. Best do a full migration as described in 
maintainance.md section "Stocks migration" or contact the developers for indi-
vidual support. 
