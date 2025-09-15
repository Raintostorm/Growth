@echo off
echo Testing backend connection...
curl -X GET http://localhost:8080/api/chat/rooms
echo.
echo Test completed.
pause
