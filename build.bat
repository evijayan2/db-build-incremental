@echo off
@set cpath=.\lib\ant-1.6.5.jar;.\lib\ojdbc-5.jar;.\lib\stax-1.2.0_rc2-dev.jar;.\lib\stax-api-1.0.1.jar;.\lib\db-incremental-1.0-SNAPSHOT.jar
rem echo java -cp %cpath% org.vijay.db.incremental.BuildIncremental d:/tmp/incremental.xml d:/tmp/incremental.xml
java -cp %cpath% org.vijay.db.incremental.BuildIncremental d:/tmp/incremental.xml d:/tmp/ise-incremental-password.property