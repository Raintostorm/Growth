@echo off
echo Testing backend...
mvn clean compile
if %errorlevel% equ 0 (
    echo Build successful!
    mvn spring-boot:run
) else (
    echo Build failed!
)
pause
