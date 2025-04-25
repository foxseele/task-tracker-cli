@echo off
   java -version 2>&1 | findstr "23." >nul
   if %ERRORLEVEL%==0 (
       exit /b 0
   ) else (
       echo Warning: JDK 23 not found.
       echo Please download and install JDK 23 from https://adoptium.net/temurin/releases/?version=23
       pause
       exit /b 1
   )