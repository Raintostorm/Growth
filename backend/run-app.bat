@echo off
echo Starting Spring Boot Application...
cd /d "%~dp0"
mvn spring-boot:run
echo.
echo Application stopped. Exit code: %ERRORLEVEL%
pause
