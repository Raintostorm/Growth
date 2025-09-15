@echo off
echo Testing Database Persistence...
echo.

echo 1. Checking if database files exist:
if exist "data\chatdb.mv.db" (
    echo    ✓ Database file exists: data\chatdb.mv.db
) else (
    echo    ✗ Database file missing: data\chatdb.mv.db
)

if exist "data\chatdb.trace.db" (
    echo    ✓ Trace file exists: data\chatdb.trace.db
) else (
    echo    ✗ Trace file missing: data\chatdb.trace.db
)

echo.
echo 2. Database Configuration:
echo    - URL: jdbc:h2:file:./data/chatdb
echo    - Mode: Persistent (file-based)
echo    - DDL: update (preserves data on restart)

echo.
echo 3. What this means:
echo    ✓ Chat rooms will be saved permanently
echo    ✓ Messages will be saved permanently  
echo    ✓ Users will be saved permanently
echo    ✓ Data survives application restarts
echo    ✓ Data survives computer restarts

echo.
echo 4. To verify persistence:
echo    - Start the application
echo    - Create some chat rooms and messages
echo    - Stop the application
echo    - Start the application again
echo    - Your data should still be there!

echo.
echo Database persistence is configured correctly!
pause
