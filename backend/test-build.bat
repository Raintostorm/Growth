@echo off
echo Testing Maven build...
mvn clean compile
echo.
echo Build completed. Exit code: %ERRORLEVEL%
pause
