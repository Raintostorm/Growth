@echo off
echo Testing PMD check...
mvn pmd:check
echo.
echo PMD check completed. Exit code: %ERRORLEVEL%
pause
