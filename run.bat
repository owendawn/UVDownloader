@echo off
title uvdownloader
java -jar %~dp0target/uvdownloader-0.0.1-SNAPSHOT.jar --spring.profiles.active=beta
pause