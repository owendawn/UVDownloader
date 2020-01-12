@echo off
title uvdownloader - 控制台地址：http://127.0.0.1:8080
java -jar %~dp0target/uvdownloader-0.0.1-SNAPSHOT.jar --spring.profiles.active=beta
pause