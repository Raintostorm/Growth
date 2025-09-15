# Real-time Chat Backend

Spring Boot backend cho ứng dụng chat thời gian thực.

## 🚀 Features

- **JWT Authentication**: Xác thực và phân quyền với JWT
- **Real-time Messaging**: WebSocket với STOMP protocol
- **REST API**: API đầy đủ cho chat functionality
- **Database**: H2 (development) / PostgreSQL (production)
- **Security**: Spring Security với JWT
- **WebSocket**: Real-time communication

## 🛠️ Tech Stack

- **Java 17**: Ngôn ngữ lập trình
- **Spring Boot 3.1.2**: Framework chính
- **Spring Security**: Authentication & Authorization
- **Spring WebSocket**: Real-time communication
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory database (development)
- **Maven**: Build tool

## 📁 Cấu trúc dự án

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/chat/
│   │   │   ├── ChatApplication.java          # Main application class
│   │   │   ├── config/                       # Configuration classes
│   │   │   ├── controller/                   # REST controllers
│   │   │   ├── service/                      # Business logic
│   │   │   ├── repository/                   # Data access layer
│   │   │   ├── model/                        # Entity classes
│   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   ├── security/                     # Security configuration
│   │   │   ├── websocket/                    # WebSocket handlers
│   │   │   └── exception/                    # Exception handling
│   │   └── resources/
│   │       ├── application.yml               # Application configuration
│   │       └── static/                       # Static files
│   └── test/                                 # Test classes
├── docs/                                     # Documentation
├── pom.xml                                   # Maven configuration
└── README.md                                 # This file
```

## 🏃‍♂️ Chạy ứng dụng

### 1. Yêu cầu hệ thống

- Java 17+
- Maven 3.6+

### 2. Cài đặt và chạy

```bash
# Di chuyển vào thư mục backend
cd backend

# Chạy ứng dụng
mvn spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080/api`

### 3. Test endpoints

```bash
# Health check
curl http://localhost:8080/api/actuator/health

# H2 Console (development)
http://localhost:8080/api/h2-console
```

## 🔧 Configuration

### Database (H2 - Development)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:chatdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
```

### JWT Configuration

```yaml
app:
  jwt:
    secret: mySecretKey
    expiration: 86400000 # 24 hours
```

## 📡 API Endpoints

### Authentication

- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký
- `GET /api/auth/me` - Lấy thông tin user hiện tại
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Đăng xuất

### Chat

- `GET /api/chat/rooms` - Lấy danh sách phòng chat
- `POST /api/chat/rooms` - Tạo phòng chat mới
- `GET /api/chat/rooms/{roomId}` - Lấy thông tin phòng
- `POST /api/chat/rooms/{roomId}/join` - Tham gia phòng
- `POST /api/chat/rooms/{roomId}/leave` - Rời phòng
- `GET /api/chat/rooms/{roomId}/messages` - Lấy tin nhắn
- `POST /api/chat/rooms/{roomId}/messages` - Gửi tin nhắn
- `GET /api/chat/users/online` - Lấy danh sách user online

### WebSocket

- **Endpoint**: `/ws`
- **Public messages**: `/topic/public`
- **Room messages**: `/topic/room/{roomId}`
- **Private messages**: `/user/queue/private`

## 🧪 Testing

```bash
# Chạy tests
mvn test

# Chạy với coverage
mvn test jacoco:report
```

## 📚 Documentation

Chi tiết về các technical concepts được giải thích trong thư mục `docs/`:

- [01-PROJECT-OVERVIEW.md](docs/01-PROJECT-OVERVIEW.md) - Tổng quan dự án
- [02-SPRING-BOOT-BASICS.md](docs/02-SPRING-BOOT-BASICS.md) - Spring Boot cơ bản
- [03-JWT-AUTHENTICATION.md](docs/03-JWT-AUTHENTICATION.md) - JWT Authentication
- [04-WEBSOCKET-REALTIME.md](docs/04-WEBSOCKET-REALTIME.md) - WebSocket Real-time
- [05-REDIS-CACHING.md](docs/05-REDIS-CACHING.md) - Redis Caching
- [06-TESTING-STRATEGY.md](docs/06-TESTING-STRATEGY.md) - Testing Strategy

## 🎯 Learning Objectives

Sau khi hoàn thành dự án này, bạn sẽ có thể:

1. **Hiểu và áp dụng** các design patterns trong Spring Boot
2. **Thực hiện** JWT authentication và authorization
3. **Xây dựng** real-time applications với WebSocket
4. **Sử dụng** Spring Data JPA cho database operations
5. **Viết** unit tests và integration tests
6. **Thiết lập** Spring Security configuration
7. **Áp dụng** best practices trong Spring Boot development

## 🔗 Liên kết

- [Frontend](../frontend/) - React TypeScript frontend
- [Usage Guide](USAGE-GUIDE.md) - Hướng dẫn sử dụng tổng thể
