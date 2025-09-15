@echo off
echo Testing backend endpoints...

echo.
echo Testing chat rooms...
curl -X GET http://localhost:8080/api/chat/rooms

echo.
echo.
echo Testing send message...
curl -X POST http://localhost:8080/api/chat/rooms/1/messages -H "Content-Type: application/json" -d "{\"content\":\"Hello World\",\"type\":\"TEXT\",\"sender\":{\"username\":\"testuser\"}}"

echo.
echo.
echo Test completed.
pause