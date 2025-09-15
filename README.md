# 🚀 Real-time Chat Application

## 📋 Tổng quan
Real-time Chat Application là một ứng dụng chat thời gian thực được xây dựng với Spring Boot backend và React frontend, implement đầy đủ **18+ technical concepts** hiện đại.

## 🎯 Technical Concepts Implemented

### ✅ Core Technologies
- **MAVEN** - Build tool và dependency management
- **DESIGN PATTERN** - Repository, Service, DTO, Singleton patterns
- **JWT** - JSON Web Token authentication
- **STRUCTURE LAYER** - Layered architecture
- **DEBUGGING THINKING** - Logging, error handling, validation

### ✅ Advanced Features
- **KAFKA** - Message streaming platform
- **REDIS** - In-memory data store và caching
- **RESOLVE SOLUTION** - Global exception handling
- **SESSION/COOKIES** - Session management và CORS
- **OAUTH** - OAuth2 social login (Google, GitHub)
- **WEBSOCKET** - Real-time communication với STOMP

### ✅ DevOps & Testing
- **CI/CD** - GitHub Actions pipeline
- **DOCKER** - Containerization với docker-compose
- **UAT** - User Acceptance Testing
- **SONAR LINT/QUBE** - Code quality analysis
- **MANUAL DEPLOY** - Deployment scripts
- **MOCKITO/JACOCO** - Testing frameworks
- **PERSISTENT DATABASE** - H2 và PostgreSQL

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │ Spring Boot BE  │
│                 │◄──►│                 │
│ - Real-time UI  │    │ - REST APIs     │
│ - WebSocket     │    │ - WebSocket     │
│ - JWT Auth      │    │ - JWT Security  │
└─────────────────┘    └─────────────────┘
                                │
                       ┌────────┴────────┐
                       │                 │
                ┌──────▼──────┐  ┌──────▼──────┐
                │   Redis     │  │   Kafka     │
                │   Cache     │  │  Messaging  │
                └─────────────┘  └─────────────┘
                       │
                ┌──────▼──────┐
                │ PostgreSQL  │
                │  Database   │
                └─────────────┘
```

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm start
```

### Docker Setup
```bash
docker-compose up -d
```

## 📁 Project Structure

```
growth/
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/         # Java source code
│   │   ├── config/            # Configuration classes
│   │   ├── controller/        # REST controllers
│   │   ├── service/           # Business logic
│   │   ├── repository/        # Data access layer
│   │   ├── model/             # Entity classes
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── security/          # Security configuration
│   │   ├── websocket/         # WebSocket handlers
│   │   └── exception/         # Exception handling
│   ├── src/test/              # Test classes
│   ├── src/main/resources/    # Configuration files
│   ├── Dockerfile             # Docker configuration
│   ├── docker-compose.yml     # Multi-container setup
│   ├── deploy.sh              # Linux deployment script
│   ├── deploy.bat             # Windows deployment script
│   └── TECHNICAL-CONCEPTS-GUIDE.md  # Technical documentation
├── frontend/                   # React Frontend
│   ├── src/                   # React source code
│   ├── public/                # Static files
│   └── package.json           # Dependencies
└── README.md                  # This file
```

## 🔧 Features

### 🔐 Authentication & Security
- JWT-based authentication
- OAuth2 social login (Google, GitHub)
- Password encryption với BCrypt
- CORS configuration
- Session management

### 💬 Real-time Chat
- WebSocket-based messaging
- Multiple chat rooms
- Typing indicators
- User presence (online/offline)
- Message history
- Real-time notifications

### 🏗️ Backend Features
- RESTful APIs
- Layered architecture
- Global exception handling
- Input validation
- Database persistence
- Redis caching
- Kafka messaging

### 🎨 Frontend Features
- Modern React UI
- Real-time updates
- Responsive design
- TypeScript support
- Axios HTTP client
- WebSocket integration

## 🧪 Testing

### Unit Tests
```bash
cd backend
mvn test
```

### Integration Tests
```bash
cd backend
mvn verify
```

### Code Coverage
```bash
cd backend
mvn jacoco:report
```

## 🚀 Deployment

### Manual Deployment
```bash
# Linux/Mac
./deploy.sh

# Windows
deploy.bat
```

### Docker Deployment
```bash
docker-compose up -d
```

### CI/CD Pipeline
- GitHub Actions tự động build, test, và deploy
- Code quality checks với PMD
- Test coverage reports với JaCoCo

## 📚 Documentation

- **[Technical Concepts Guide](backend/TECHNICAL-CONCEPTS-GUIDE.md)** - Chi tiết về 18+ technical concepts
- **[API Documentation](backend/docs/)** - API endpoints documentation
- **[Deployment Guide](backend/deploy.sh)** - Deployment instructions

## 🛠️ Tech Stack

### Backend
- **Java 21** - Programming language
- **Spring Boot 3.1.2** - Main framework
- **Spring Security** - Authentication & Authorization
- **Spring WebSocket** - Real-time communication
- **Spring Data JPA** - Database operations
- **Spring Data Redis** - Caching
- **Maven** - Build tool

### Frontend
- **React 18** - UI framework
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **Axios** - HTTP client
- **Socket.io** - WebSocket client

### Database & Cache
- **H2** - Development database
- **PostgreSQL** - Production database
- **Redis** - Caching và session storage

### DevOps & Tools
- **Docker** - Containerization
- **GitHub Actions** - CI/CD
- **JaCoCo** - Code coverage
- **PMD** - Code quality
- **SonarQube** - Code analysis

## 🎓 Learning Objectives

Sau khi hoàn thành project này, bạn sẽ có thể:

1. **Hiểu và áp dụng** các design patterns trong Spring Boot
2. **Thực hiện** JWT authentication và OAuth2 integration
3. **Xây dựng** real-time applications với WebSocket
4. **Sử dụng** Redis cho caching và session management
5. **Viết** unit tests và integration tests
6. **Thiết lập** CI/CD pipeline với GitHub Actions
7. **Containerize** ứng dụng với Docker
8. **Áp dụng** code quality tools và best practices

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Raintostorm** - [GitHub](https://github.com/Raintostorm)

## 🙏 Acknowledgments

- Spring Boot community
- React community
- All open source contributors

---

**⭐ Nếu project này hữu ích, hãy star repository này!**

**🚀 Happy Coding!**
