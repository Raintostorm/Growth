# Testing Strategy - Chiến lược Testing

## 🎯 Mục tiêu

Hiểu và thực hiện testing strategy toàn diện cho ứng dụng chat, bao gồm unit tests, integration tests và end-to-end tests.

## 🧪 Testing Pyramid

```
        /\
       /  \
      / E2E \     <- 10% - End-to-End Tests
     /______\
    /        \
   /Integration\ <- 20% - Integration Tests
  /____________\
 /              \
/   Unit Tests   \ <- 70% - Unit Tests
/________________\
```

### Tỷ lệ Testing
- **70% Unit Tests**: Test individual components
- **20% Integration Tests**: Test component interactions
- **10% End-to-End Tests**: Test complete user workflows

## 🔧 Testing Dependencies

### 1. Maven Dependencies

```xml
<!-- Testing Dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>redis</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito for mocking -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ for assertions -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. JaCoCo Configuration

```xml
<!-- JaCoCo Plugin for Code Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 🧩 Unit Testing

### 1. Service Layer Testing

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        User savedUser = new User("testuser", "test@example.com", "encodedPassword");
        savedUser.setId(1L);
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        User result = userService.createUser(user);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
    }
    
    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        User user = new User("existinguser", "test@example.com", "password");
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(user))
            .isInstanceOf(UsernameAlreadyExistsException.class)
            .hasMessage("Username already exists: existinguser");
    }
    
    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        String username = "testuser";
        User user = new User(username, "test@example.com", "password");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        
        // When
        User result = userService.findByUsername(username);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        verify(userRepository).findByUsername(username);
    }
}
```

### 2. Controller Testing

```java
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    
    @Mock
    private AuthService authService;
    
    @InjectMocks
    private AuthController authController;
    
    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        UserDto userDto = new UserDto(1L, "testuser", "test@example.com", "Test User", Role.USER);
        AuthResponse authResponse = new AuthResponse("jwt-token", 86400L, userDto);
        
        when(authService.login(loginRequest)).thenReturn(authResponse);
        
        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("Should return unauthorized for invalid credentials")
    void shouldReturnUnauthorizedForInvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest("invaliduser", "wrongpassword");
        when(authService.login(loginRequest)).thenThrow(new BadCredentialsException("Invalid credentials"));
        
        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getError()).isEqualTo("Invalid credentials");
    }
}
```

### 3. WebSocket Handler Testing

```java
@ExtendWith(MockitoExtension.class)
class ChatWebSocketHandlerTest {
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    
    @Mock
    private MessageService messageService;
    
    @Mock
    private SimpMessageHeaderAccessor headerAccessor;
    
    @Mock
    private Principal principal;
    
    @InjectMocks
    private ChatWebSocketHandler chatHandler;
    
    @Test
    @DisplayName("Should send message successfully")
    void shouldSendMessageSuccessfully() {
        // Given
        Message message = new Message();
        message.setContent("Hello World");
        message.setType(MessageType.TEXT);
        
        Message savedMessage = new Message();
        savedMessage.setId(1L);
        savedMessage.setContent("Hello World");
        savedMessage.setType(MessageType.TEXT);
        
        when(headerAccessor.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("testuser");
        when(messageService.saveMessage(any(Message.class))).thenReturn(savedMessage);
        
        // When
        Message result = chatHandler.sendMessage(message, headerAccessor);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Hello World");
        verify(messageService).saveMessage(message);
    }
}
```

## 🔗 Integration Testing

### 1. Repository Integration Tests

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> found = userRepository.findByUsername("testuser");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("Should find online users")
    void shouldFindOnlineUsers() {
        // Given
        User user1 = new User("user1", "user1@example.com", "password");
        user1.setIsOnline(true);
        User user2 = new User("user2", "user2@example.com", "password");
        user2.setIsOnline(false);
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        
        // When
        List<User> onlineUsers = userRepository.findByIsOnlineTrue();
        
        // Then
        assertThat(onlineUsers).hasSize(1);
        assertThat(onlineUsers.get(0).getUsername()).isEqualTo("user1");
    }
}
```

### 2. Service Integration Tests

```java
@SpringBootTest
@Transactional
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("Should create and find user")
    void shouldCreateAndFindUser() {
        // Given
        User user = new User("integrationuser", "integration@example.com", "password");
        
        // When
        User created = userService.createUser(user);
        User found = userService.findByUsername("integrationuser");
        
        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("integrationuser");
    }
}
```

### 3. Controller Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class AuthControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    @DisplayName("Should register and login user")
    void shouldRegisterAndLoginUser() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest(
            "integrationuser", 
            "integration@example.com", 
            "password", 
            "Integration User"
        );
        
        // When - Register
        ResponseEntity<UserDto> registerResponse = restTemplate.postForEntity(
            "/api/auth/register", registerRequest, UserDto.class);
        
        // Then - Register
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody().getUsername()).isEqualTo("integrationuser");
        
        // When - Login
        LoginRequest loginRequest = new LoginRequest("integrationuser", "password");
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
            "/api/auth/login", loginRequest, AuthResponse.class);
        
        // Then - Login
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody().getAccessToken()).isNotNull();
        assertThat(loginResponse.getBody().getUser().getUsername()).isEqualTo("integrationuser");
    }
}
```

## 🗄️ Database Testing với Testcontainers

### 1. PostgreSQL Testcontainer

```java
@SpringBootTest
@Testcontainers
class DatabaseIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("Should persist user to PostgreSQL")
    void shouldPersistUserToPostgreSQL() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        
        // When
        User saved = userRepository.save(user);
        
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }
}
```

### 2. Redis Testcontainer

```java
@SpringBootTest
@Testcontainers
class RedisIntegrationTest {
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Test
    @DisplayName("Should store and retrieve data from Redis")
    void shouldStoreAndRetrieveDataFromRedis() {
        // Given
        String key = "test:key";
        String value = "test:value";
        
        // When
        redisTemplate.opsForValue().set(key, value);
        String retrieved = (String) redisTemplate.opsForValue().get(key);
        
        // Then
        assertThat(retrieved).isEqualTo(value);
    }
}
```

## 🌐 End-to-End Testing

### 1. WebSocket E2E Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketE2ETest {
    
    @LocalServerPort
    private int port;
    
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    
    @BeforeEach
    void setUp() throws Exception {
        stompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));
        
        stompSession = stompClient.connect(
            "ws://localhost:" + port + "/ws", 
            new StompSessionHandlerAdapter() {}
        ).get(1, TimeUnit.SECONDS);
    }
    
    @AfterEach
    void tearDown() throws Exception {
        if (stompSession != null) {
            stompSession.disconnect();
        }
    }
    
    @Test
    @DisplayName("Should send and receive messages via WebSocket")
    void shouldSendAndReceiveMessagesViaWebSocket() throws Exception {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Message> receivedMessage = new AtomicReference<>();
        
        // Subscribe to messages
        stompSession.subscribe("/topic/public", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }
            
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessage.set((Message) payload);
                latch.countDown();
            }
        });
        
        // When
        Message message = new Message();
        message.setContent("Hello WebSocket!");
        message.setType(MessageType.TEXT);
        
        stompSession.send("/app/chat.sendMessage", message);
        
        // Then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(receivedMessage.get()).isNotNull();
        assertThat(receivedMessage.get().getContent()).isEqualTo("Hello WebSocket!");
    }
}
```

### 2. Complete Chat Flow E2E Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatFlowE2ETest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @LocalServerPort
    private int port;
    
    @Test
    @DisplayName("Should complete full chat flow")
    void shouldCompleteFullChatFlow() throws Exception {
        // 1. Register user
        RegisterRequest registerRequest = new RegisterRequest(
            "e2euser", "e2e@example.com", "password", "E2E User"
        );
        
        ResponseEntity<UserDto> registerResponse = restTemplate.postForEntity(
            "/api/auth/register", registerRequest, UserDto.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // 2. Login user
        LoginRequest loginRequest = new LoginRequest("e2euser", "password");
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
            "/api/auth/login", loginRequest, AuthResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        String token = loginResponse.getBody().getAccessToken();
        
        // 3. Create chat room
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        ChatRoomDto roomDto = new ChatRoomDto("Test Room", RoomType.PUBLIC, null);
        HttpEntity<ChatRoomDto> roomRequest = new HttpEntity<>(roomDto, headers);
        
        ResponseEntity<ChatRoomDto> roomResponse = restTemplate.exchange(
            "/api/chat/rooms", HttpMethod.POST, roomRequest, ChatRoomDto.class);
        assertThat(roomResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        Long roomId = roomResponse.getBody().getId();
        
        // 4. Send message
        MessageDto messageDto = new MessageDto("Hello E2E Test!", MessageType.TEXT, null, roomId);
        HttpEntity<MessageDto> messageRequest = new HttpEntity<>(messageDto, headers);
        
        ResponseEntity<MessageDto> messageResponse = restTemplate.exchange(
            "/api/chat/rooms/" + roomId + "/messages", 
            HttpMethod.POST, messageRequest, MessageDto.class);
        assertThat(messageResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // 5. Get messages
        ResponseEntity<Page<MessageDto>> messagesResponse = restTemplate.exchange(
            "/api/chat/rooms/" + roomId + "/messages", 
            HttpMethod.GET, new HttpEntity<>(headers), 
            new ParameterizedTypeReference<Page<MessageDto>>() {});
        assertThat(messagesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(messagesResponse.getBody().getContent()).hasSize(1);
    }
}
```

## 📊 Test Coverage và Quality

### 1. JaCoCo Configuration

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 2. Test Execution Commands

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn clean test jacoco:report

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Run integration tests only
mvn test -Dtest="*IntegrationTest"

# Run with coverage check
mvn clean test jacoco:report jacoco:check
```

## 🎯 Best Practices

### 1. Test Naming Convention

```java
// Good naming
@Test
@DisplayName("Should return user when valid username is provided")
void shouldReturnUserWhenValidUsernameIsProvided() {
    // Test implementation
}

// Bad naming
@Test
void test1() {
    // Test implementation
}
```

### 2. Test Data Builders

```java
public class UserTestDataBuilder {
    
    private String username = "defaultuser";
    private String email = "default@example.com";
    private String password = "defaultpassword";
    private Role role = Role.USER;
    
    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }
    
    public UserTestDataBuilder withUsername(String username) {
        this.username = username;
        return this;
    }
    
    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public User build() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}

// Usage
@Test
void shouldCreateUser() {
    User user = UserTestDataBuilder.aUser()
        .withUsername("testuser")
        .withEmail("test@example.com")
        .build();
    
    // Test implementation
}
```

### 3. Test Configuration

```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneOffset.UTC);
    }
}
```

## 📚 Tài liệu tham khảo

- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Testcontainers](https://www.testcontainers.org/)

## 🎯 Bài tập thực hành

1. **Viết unit tests** cho tất cả service classes
2. **Viết integration tests** cho repositories
3. **Viết controller tests** với MockMvc
4. **Setup Testcontainers** cho database testing
5. **Viết WebSocket tests** với StompClient
6. **Setup JaCoCo** và đạt 80% code coverage

---

**Lưu ý**: Testing là investment dài hạn, hãy viết tests ngay từ đầu để tránh technical debt!
