@echo off
title uvdownloader - ����̨��ַ��http://127.0.0.1:8080
java -jar %~dp0target/uvdownloader-0.0.1-SNAPSHOT.jar --spring.profiles.active=beta
pause