# Spring Boot Basics - Kiến thức cơ bản

## 🎯 Mục tiêu

Hiểu được các khái niệm cơ bản của Spring Boot và cách chúng áp dụng vào dự án Real-time Chat Application.

## 🏗️ Spring Boot là gì?

Spring Boot là một framework Java giúp tạo ra các ứng dụng Spring một cách nhanh chóng và dễ dàng. Nó cung cấp:

- **Auto-configuration**: Tự động cấu hình các components
- **Starter dependencies**: Quản lý dependencies dễ dàng
- **Embedded server**: Chạy ứng dụng mà không cần deploy
- **Production-ready features**: Monitoring, health checks, metrics

## 📦 Dependencies trong pom.xml

### Core Dependencies

```xml
<!-- Spring Boot Web Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
**Giải thích**: Cung cấp REST API, HTTP handling, JSON serialization

```xml
<!-- Spring Boot WebSocket Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```
**Giải thích**: Hỗ trợ WebSocket cho real-time communication

```xml
<!-- Spring Boot Data JPA Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
**Giải thích**: ORM (Object-Relational Mapping) với Hibernate

```xml
<!-- Spring Boot Security Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
**Giải thích**: Authentication, authorization, security features

### Database Dependencies

```xml
<!-- H2 Database (Development) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

```xml
<!-- PostgreSQL (Production) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### JWT Dependencies

```xml
<!-- JWT API -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

## ⚙️ Configuration (application.yml)

### Server Configuration

```yaml
server:
  port: 8080
  servlet:
    context-path: /api
```

**Giải thích**:
- `port: 8080`: Ứng dụng chạy trên port 8080
- `context-path: /api`: Tất cả endpoints bắt đầu với `/api`

### Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:chatdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

**Giải thích**:
- `ddl-auto: create-drop`: Tự động tạo/xóa tables khi start/stop
- `show-sql: true`: Hiển thị SQL queries trong console

### Redis Configuration

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
```

## 🏛️ Kiến trúc Layered Architecture

### 1. Controller Layer (Presentation Layer)

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Handle HTTP requests
        // Validate input
        // Call service layer
        // Return response
    }
}
```

**Trách nhiệm**:
- Nhận HTTP requests
- Validate input data
- Gọi service layer
- Trả về HTTP responses

### 2. Service Layer (Business Logic Layer)

```java
@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public AuthResponse login(LoginRequest request) {
        // Business logic
        // Data processing
        // Call repository layer
        // Return business objects
    }
}
```

**Trách nhiệm**:
- Chứa business logic
- Xử lý transactions
- Gọi repository layer
- Validate business rules

### 3. Repository Layer (Data Access Layer)

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByIsOnlineTrue();
}
```

**Trách nhiệm**:
- Truy cập database
- CRUD operations
- Custom queries
- Data mapping

### 4. Entity Layer (Data Model Layer)

```java
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    // Getters, setters, constructors
}
```

**Trách nhiệm**:
- Định nghĩa data model
- Database mapping
- Relationships
- Validation

## 🔧 Auto-Configuration

Spring Boot tự động cấu hình các components dựa trên:

### 1. Classpath Dependencies

```java
// Nếu có spring-boot-starter-web trong classpath
// Spring Boot tự động cấu hình:
// - DispatcherServlet
// - Jackson (JSON serialization)
// - Embedded Tomcat server
```

### 2. Application Properties

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:mem:chatdb
# Spring Boot tự động cấu hình DataSource
```

### 3. Conditional Annotations

```java
@Configuration
@ConditionalOnClass(DataSource.class)
public class DatabaseAutoConfiguration {
    // Chỉ load khi có DataSource class
}
```

## 🚀 Application Startup

### Main Class

```java
@SpringBootApplication
@EnableCaching
@EnableAsync
public class ChatApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}
```

**Giải thích**:
- `@SpringBootApplication`: Kết hợp của `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan`
- `@EnableCaching`: Bật caching với Spring Cache
- `@EnableAsync`: Bật asynchronous processing

### Startup Process

1. **Load Application Context**
2. **Auto-configure Components**
3. **Initialize Beans**
4. **Start Embedded Server**
5. **Ready to Accept Requests**

## 📊 Profiles

### Development Profile

```yaml
spring:
  config:
    activate:
      on-profile: dev
  
  datasource:
    url: jdbc:h2:mem:chatdb
  jpa:
    show-sql: true
```

### Production Profile

```yaml
spring:
  config:
    activate:
      on-profile: prod
  
  datasource:
    url: ${DATABASE_URL}
  jpa:
    show-sql: false
```

## 🔍 Actuator Endpoints

Spring Boot Actuator cung cấp production-ready features:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

**Available Endpoints**:
- `/actuator/health`: Health check
- `/actuator/info`: Application info
- `/actuator/metrics`: Application metrics

## 🎯 Best Practices

### 1. Configuration Management

```java
@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
    private String jwtSecret;
    private long jwtExpiration;
    
    // Getters and setters
}
```

### 2. Exception Handling

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        // Handle exception globally
    }
}
```

### 3. Validation

```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    // @Valid triggers validation
}
```

## 🧪 Testing

### Unit Testing

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldCreateUser() {
        // Test implementation
    }
}
```

### Integration Testing

```java
@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateUser() {
        // Integration test
    }
}
```

## 📚 Tài liệu tham khảo

- [Spring Boot Official Documentation](https://spring.io/projects/spring-boot)
- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Boot Guides](https://spring.io/guides)

## 🎯 Bài tập thực hành

1. **Tạo một REST endpoint** đơn giản trả về "Hello World"
2. **Cấu hình database** với H2 và tạo một Entity
3. **Viết unit test** cho Service class
4. **Sử dụng profiles** để cấu hình dev/prod khác nhau
5. **Thêm Actuator endpoints** và kiểm tra health status

---

**Lưu ý**: Đây là kiến thức nền tảng, hãy thực hành nhiều để hiểu sâu hơn về Spring Boot!
