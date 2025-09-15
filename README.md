# Real-time Chat Application

A comprehensive real-time chat application built with **Java Spring Boot**, demonstrating various technical concepts and modern development practices.

## 🎯 Project Overview

This project is designed to learn and apply technical concepts from the provided slides, using **Java Spring Boot** as the main platform. It showcases a complete real-time chat application with modern architecture and best practices.

## 🏗️ Architecture

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

## 📋 Technical Concepts Applied

### ✅ CODING & SOURCE CONTROL
- **MAVEN/GRADLE**: ✅ Maven for dependency management
- **DESIGN PATTERN**: ✅ Repository, Service, DTO, Singleton patterns
- **JWT**: ✅ Authentication and authorization
- **STRUCTURE LAYER**: ✅ Controller → Service → Repository → Entity
- **DEBUGGING THINKING**: ✅ Logging, error handling, validation
- **KAFKA**: ⚠️ Can be replaced with Redis Pub/Sub
- **REDIS**: ✅ Caching, session management, real-time features
- **RESOLVE SOLUTION**: ✅ Exception handling, error responses
- **SESSION/COOKIES**: ✅ Spring Security session management
- **OAUTH**: ✅ Google/GitHub OAuth2 integration
- **WEBSOCKET**: ✅ Real-time messaging with Spring WebSocket

### ✅ TESTING & DEPLOYMENT
- **CI/CD**: ✅ GitHub Actions configuration
- **PMD**: ✅ Code quality analysis
- **DOCKER**: ✅ Containerization
- **UAT**: ✅ User acceptance testing setup
- **SONAR LINT**: ✅ Code quality tools
- **SONAR QUBE**: ✅ Code quality platform integration
- **MANUAL DEPLOY**: ✅ Deployment scripts
- **KUBERNETES**: ⚠️ Can be added later (advanced)
- **MOCKITO/JACOCO**: ✅ Testing frameworks and code coverage

## 🚀 Features

### Phase 1: Core Features
1. **User Authentication**
   - JWT-based authentication
   - OAuth2 social login (Google, GitHub)
   - User registration and login

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
- **Java 17**: Programming language
- **Spring Boot 3.1.2**: Main framework
- **Spring Security**: Authentication & Authorization
- **Spring WebSocket**: Real-time communication
- **Spring Data JPA**: Database operations
- **Spring Data Redis**: Caching and session

### Database
- **H2**: In-memory database (development)
- **PostgreSQL**: Production database
- **Redis**: Caching and session storage

### Frontend
- **HTML5/CSS3**: UI structure and styling
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

## 📁 Project Structure

```
realtime-chat-app/
├── backend/                                  # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/chat/
│   │   │   │   ├── ChatApplication.java      # Main application class
│   │   │   │   ├── config/                   # Configuration classes
│   │   │   │   ├── controller/               # REST controllers
│   │   │   │   ├── service/                  # Business logic
│   │   │   │   ├── repository/               # Data access layer
│   │   │   │   ├── model/                    # Entity classes
│   │   │   │   ├── dto/                      # Data Transfer Objects
│   │   │   │   ├── security/                 # Security configuration
│   │   │   │   ├── websocket/                # WebSocket handlers
│   │   │   │   └── exception/                # Exception handling
│   │   │   └── resources/
│   │   │       ├── application.yml           # Application configuration
│   │   │       └── static/                   # Static files
│   │   └── test/                             # Test classes
│   ├── docs/                                 # Documentation
│   ├── pom.xml                               # Maven configuration
│   └── README.md                             # Backend documentation
├── frontend/                                 # React frontend
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── README.md
└── README.md                                 # Project documentation
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Redis (optional, for caching)
- PostgreSQL (optional, for production)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd realtime-chat-app
   ```

2. **Set up the backend**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

3. **Set up the frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - H2 Console: http://localhost:8080/api/h2-console
   - Health Check: http://localhost:8080/api/actuator/health

### Configuration

1. **Copy environment file**
   ```bash
   cp env.example .env
   ```

2. **Update configuration**
   Edit `backend/src/main/resources/application.yml` or set environment variables:
   ```yaml
   server:
     port: 8080
   
   spring:
     datasource:
       url: jdbc:h2:mem:chatdb
     data:
       redis:
         host: localhost
         port: 6379
   
   jwt:
     secret: your-secret-key
     expiration: 86400000
   ```

## 📚 Documentation

Comprehensive documentation is available in the `backend/docs/` directory:

- [01-PROJECT-OVERVIEW.md](backend/docs/01-PROJECT-OVERVIEW.md) - Project overview and goals
- [02-SPRING-BOOT-BASICS.md](backend/docs/02-SPRING-BOOT-BASICS.md) - Spring Boot fundamentals
- [03-JWT-AUTHENTICATION.md](backend/docs/03-JWT-AUTHENTICATION.md) - JWT authentication guide
- [04-WEBSOCKET-REALTIME.md](backend/docs/04-WEBSOCKET-REALTIME.md) - WebSocket real-time communication
- [05-REDIS-CACHING.md](backend/docs/05-REDIS-CACHING.md) - Redis caching and session management
- [06-TESTING-STRATEGY.md](backend/docs/06-TESTING-STRATEGY.md) - Comprehensive testing strategy

## 🧪 Testing

### Backend Tests
```bash
cd backend
# Run all tests
mvn test

# Run tests with coverage
mvn clean test jacoco:report

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run integration tests
mvn test -Dtest="*IntegrationTest"
```

### Frontend Tests
```bash
cd frontend
# Run all tests
npm test

# Run tests with coverage
npm run test:coverage
```

### Test Coverage
- **Backend**: JaCoCo for code coverage. Reports are generated in `backend/target/site/jacoco/`.
- **Frontend**: Jest for code coverage. Reports are generated in `frontend/coverage/`.

## 🐳 Docker

### Build Docker Image
```bash
docker build -t realtime-chat-app .
```

### Run with Docker Compose
```bash
docker-compose up -d
```

## 🔧 Development

### Backend Code Quality
```bash
cd backend
# Run PMD analysis
mvn pmd:check

# Run SonarQube analysis
mvn sonar:sonar

# Format code
mvn spotless:apply
```

### Frontend Code Quality
```bash
cd frontend
# Run ESLint
npm run lint

# Fix ESLint issues
npm run lint:fix

# Run TypeScript check
npm run type-check
```

### Database Migration
```bash
cd backend
# Generate migration script
mvn flyway:migrate
```

## 📅 Learning Timeline (1 Month)

### Week 1: Foundation
- **Day 1-2**: Spring Boot basics, project setup
- **Day 3-4**: JWT authentication, user management
- **Day 5-7**: OAuth2 integration, security configuration

### Week 2: Core Features
- **Day 1-3**: WebSocket implementation, real-time messaging
- **Day 4-5**: Redis integration, caching
- **Day 6-7**: Database design, JPA entities

### Week 3: Advanced Features
- **Day 1-3**: File upload, message types
- **Day 4-5**: Room management, permissions
- **Day 6-7**: Testing (unit, integration)

### Week 4: DevOps & Deployment
- **Day 1-3**: Docker containerization
- **Day 4-5**: CI/CD setup, code quality
- **Day 6-7**: Deployment, monitoring

## 🎓 Learning Objectives

After completing this project, you will be able to:

1. **Understand and apply** design patterns in Spring Boot
2. **Implement** JWT authentication and OAuth2 integration
3. **Build** real-time applications with WebSocket
4. **Use** Redis for caching and session management
5. **Write** unit tests and integration tests
6. **Setup** CI/CD pipeline with GitHub Actions
7. **Containerize** applications with Docker
8. **Apply** code quality tools and best practices

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Redis team for the powerful caching solution
- All contributors and the open-source community

## 📞 Support

If you have any questions or need help, please:

1. Check the [documentation](backend/docs/)
2. Search existing [issues](../../issues)
3. Create a new [issue](../../issues/new)

---

**Note**: This project is designed for learning purposes and can be extended and improved according to real-world needs.
