@echo off
REM MANUAL DEPLOY - Manual deployment script for Windows
REM TECHNICAL CONCEPT: MANUAL DEPLOY

echo 🚀 Starting manual deployment process...

REM MANUAL DEPLOY - Set deployment variables
set APP_NAME=realtime-chat-app
set VERSION=1.0.0
set DOCKER_IMAGE=chat-app:%VERSION%
set CONTAINER_NAME=chat-app-container

REM MANUAL DEPLOY - Stop existing container if running
echo 📦 Stopping existing container...
docker stop %CONTAINER_NAME% 2>nul
docker rm %CONTAINER_NAME% 2>nul

REM MANUAL DEPLOY - Build application
echo 🔨 Building application with Maven...
mvn clean package -DskipTests

REM MANUAL DEPLOY - Build Docker image
echo 🐳 Building Docker image...
docker build -t %DOCKER_IMAGE% .

REM MANUAL DEPLOY - Start services with docker-compose
echo 🚀 Starting services...
docker-compose down
docker-compose up -d

REM MANUAL DEPLOY - Wait for services to be ready
echo ⏳ Waiting for services to be ready...
timeout /t 30 /nobreak >nul

REM MANUAL DEPLOY - Health check
echo 🏥 Performing health check...
curl -f http://localhost:8080/api/actuator/health
if %errorlevel% neq 0 (
    echo ❌ Health check failed!
    exit /b 1
)

echo ✅ Deployment completed successfully!
echo 🌐 Application is running at: http://localhost:8080/api
echo 📊 Health check: http://localhost:8080/api/actuator/health
