#!/bin/bash

# MANUAL DEPLOY - Manual deployment script for production
# TECHNICAL CONCEPT: MANUAL DEPLOY

echo "🚀 Starting manual deployment process..."

# MANUAL DEPLOY - Set deployment variables
APP_NAME="realtime-chat-app"
VERSION="1.0.0"
DOCKER_IMAGE="chat-app:$VERSION"
CONTAINER_NAME="chat-app-container"

# MANUAL DEPLOY - Stop existing container if running
echo "📦 Stopping existing container..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# MANUAL DEPLOY - Build application
echo "🔨 Building application with Maven..."
mvn clean package -DskipTests

# MANUAL DEPLOY - Build Docker image
echo "🐳 Building Docker image..."
docker build -t $DOCKER_IMAGE .

# MANUAL DEPLOY - Start services with docker-compose
echo "🚀 Starting services..."
docker-compose down
docker-compose up -d

# MANUAL DEPLOY - Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# MANUAL DEPLOY - Health check
echo "🏥 Performing health check..."
curl -f http://localhost:8080/api/actuator/health || {
    echo "❌ Health check failed!"
    exit 1
}

echo "✅ Deployment completed successfully!"
echo "🌐 Application is running at: http://localhost:8080/api"
echo "📊 Health check: http://localhost:8080/api/actuator/health"
