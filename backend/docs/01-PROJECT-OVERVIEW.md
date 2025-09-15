# Real-time Chat Application - Tổng quan dự án

## 🎯 Mục tiêu dự án

Dự án này được thiết kế để học và áp dụng các technical concepts từ slide mà bạn đã chia sẻ, sử dụng **Java Spring Boot** làm nền tảng chính.

## 🏗️ Kiến trúc tổng thể

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (HTML/CSS/JS) │◄──►│   Spring Boot   │◄──►│   H2/PostgreSQL │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │     Redis       │
                       │   (Caching)     │
                       └─────────────────┘
```

## 📋 Các technical concepts được áp dụng

### ✅ CODING & SOURCE CONTROL
- **MAVEN/GRADLE**: ✅ Sử dụng Maven để quản lý dependencies
- **DESIGN PATTERN**: ✅ Repository, Service, DTO, Singleton patterns
- **JWT**: ✅ Authentication và authorization
- **STRUCTURE LAYER**: ✅ Controller → Service → Repository → Entity
- **DEBUGGING THINKING**: ✅ Logging, error handling, validation
- **KAFKA**: ⚠️ Có thể thay thế bằng Redis Pub/Sub
- **REDIS**: ✅ Caching, session management, real-time features
- **RESOLVE SOLUTION**: ✅ Exception handling, error responses
- **SESSION/COOKIES**: ✅ Spring Security session management
- **OAUTH**: ✅ Google/GitHub OAuth2 integration
- **WEBSOCKET**: ✅ Real-time messaging với Spring WebSocket

### ✅ TESTING & DEPLOYMENT
- **CI/CD**: ✅ GitHub Actions configuration
- **PMD**: ✅ Code quality analysis
- **DOCKER**: ✅ Containerization
- **UAT**: ✅ User acceptance testing setup
- **SONAR LINT**: ✅ Code quality tools
- **SONAR QUBE**: ✅ Code quality platform integration
- **MANUAL DEPLOY**: ✅ Deployment scripts
- **KUBERNETES**: ⚠️ Có thể thêm sau (advanced)
- **MOCKITO/JACOCO**: ✅ Testing frameworks và code coverage

## 🚀 Tính năng chính

### Phase 1: Core Features
1. **User Authentication**
   - JWT-based authentication
   - OAuth2 social login (Google, GitHub)
   - User registration và login

2. **Real-time Chat**
   - WebSocket-based messaging
   - Multiple chat rooms
   - Message types (text, image, file)

3. **User Management**
   - User profiles
   - Online/offline status
   - Role-based access control

### Phase 2: Advanced Features
1. **File Sharing**
   - Image upload
   - File attachment
   - File size validation

2. **Room Management**
   - Create/join/leave rooms
   - Room permissions
   - Member roles

3. **Message Features**
   - Message status (sent, delivered, read)
   - Message replies
   - Message history

## 🛠️ Tech Stack

### Backend
- **Java 17**: Ngôn ngữ lập trình
- **Spring Boot 3.1.2**: Framework chính
- **Spring Security**: Authentication & Authorization
- **Spring WebSocket**: Real-time communication
- **Spring Data JPA**: Database operations
- **Spring Data Redis**: Caching và session

### Database
- **H2**: In-memory database (development)
- **PostgreSQL**: Production database
- **Redis**: Caching và session storage

### Frontend
- **HTML5/CSS3**: UI structure và styling
- **JavaScript (ES6+)**: Client-side logic
- **WebSocket API**: Real-time communication
- **Fetch API**: HTTP requests

### DevOps & Tools
- **Maven**: Build tool
- **Docker**: Containerization
- **GitHub Actions**: CI/CD
- **JaCoCo**: Code coverage
- **PMD**: Code quality
- **SonarQube**: Code analysis

## 📁 Cấu trúc dự án

```
realtime-chat-app/
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
│   │       ├── static/                       # Static files (HTML, CSS, JS)
│   │       └── templates/                    # Thymeleaf templates
│   └── test/                                 # Test classes
├── docs/                                     # Documentation
├── docker/                                   # Docker configuration
├── pom.xml                                   # Maven configuration
└── README.md                                 # Project documentation
```

## 🎓 Mục tiêu học tập

Sau khi hoàn thành dự án này, bạn sẽ có thể:

1. **Hiểu và áp dụng** các design patterns trong Spring Boot
2. **Thực hiện** JWT authentication và OAuth2 integration
3. **Xây dựng** real-time applications với WebSocket
4. **Sử dụng** Redis cho caching và session management
5. **Viết** unit tests và integration tests
6. **Thiết lập** CI/CD pipeline với GitHub Actions
7. **Containerize** ứng dụng với Docker
8. **Áp dụng** code quality tools và best practices

## 📅 Timeline học tập (1 tháng)

### Tuần 1: Foundation
- **Ngày 1-2**: Spring Boot basics, project setup
- **Ngày 3-4**: JWT authentication, user management
- **Ngày 5-7**: OAuth2 integration, security configuration

### Tuần 2: Core Features
- **Ngày 1-3**: WebSocket implementation, real-time messaging
- **Ngày 4-5**: Redis integration, caching
- **Ngày 6-7**: Database design, JPA entities

### Tuần 3: Advanced Features
- **Ngày 1-3**: File upload, message types
- **Ngày 4-5**: Room management, permissions
- **Ngày 6-7**: Testing (unit, integration)

### Tuần 4: DevOps & Deployment
- **Ngày 1-3**: Docker containerization
- **Ngày 4-5**: CI/CD setup, code quality
- **Ngày 6-7**: Deployment, monitoring

## 🚀 Bắt đầu

1. **Clone repository** và setup môi trường
2. **Chạy ứng dụng** với `mvn spring-boot:run`
3. **Truy cập** http://localhost:8080/api
4. **Đọc documentation** trong thư mục `docs/`
5. **Thực hành** từng tính năng theo timeline

---

**Lưu ý**: Dự án này được thiết kế để học tập, có thể mở rộng và cải thiện theo nhu cầu thực tế.
