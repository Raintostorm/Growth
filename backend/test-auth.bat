@echo off
echo Testing authentication endpoints...

echo.
echo Testing registration...
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"displayName\":\"Test User\",\"password\":\"password123\"}"

echo.
echo.
echo Testing login...
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"password123\"}"

echo.
echo.
echo Test completed.
pause
