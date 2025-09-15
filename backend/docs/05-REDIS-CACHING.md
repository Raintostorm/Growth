# Redis Caching - Caching và Session Management

## 🎯 Mục tiêu

Hiểu và sử dụng Redis để caching, session management và real-time features trong ứng dụng chat.

## 🔴 Redis là gì?

Redis (Remote Dictionary Server) là một in-memory data structure store được sử dụng như database, cache, message broker và streaming engine.

### Tính năng chính của Redis

- **In-memory storage**: Dữ liệu được lưu trong RAM
- **Data structures**: String, Hash, List, Set, Sorted Set
- **Persistence**: Có thể lưu dữ liệu vào disk
- **Pub/Sub**: Message publishing và subscribing
- **Clustering**: Hỗ trợ cluster mode
- **High performance**: Rất nhanh (100,000+ ops/sec)

## 🏗️ Redis Configuration trong Spring Boot

### 1. Dependencies

```xml
<!-- Spring Data Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 2. Application Configuration

```yaml
# application.yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

### 3. Redis Configuration Class

```java
@Configuration
@EnableCaching
public class RedisConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(
            new RedisStandaloneConfiguration("localhost", 6379)
        );
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        
        // Use Jackson2JsonRedisSerializer for JSON serialization
        Jackson2JsonRedisSerializer<Object> serializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        
        template.setDefaultSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        return template;
    }
    
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.Builder builder = RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(cacheConfiguration());
        
        return builder.build();
    }
    
    private RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(60))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
```

## 💾 Caching với Spring Cache

### 1. Service Layer Caching

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }
    
    @CacheEvict(value = "users", key = "#user.username")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public void clearUserCache() {
        // Clear all user cache entries
    }
    
    @CachePut(value = "users", key = "#user.username")
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
```

### 2. Chat Room Caching

```java
@Service
public class ChatService {
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Cacheable(value = "chatRooms", key = "#roomId")
    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new RoomNotFoundException("Room not found: " + roomId));
    }
    
    @Cacheable(value = "activeRooms")
    public List<ChatRoom> getActiveRooms() {
        return chatRoomRepository.findByIsActiveTrue();
    }
    
    @CacheEvict(value = {"chatRooms", "activeRooms"}, allEntries = true)
    public ChatRoom createRoom(ChatRoom room) {
        return chatRoomRepository.save(room);
    }
}
```

## 🔄 Session Management với Redis

### 1. Session Configuration

```java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400) // 24 hours
public class SessionConfig {
    
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(
            new RedisStandaloneConfiguration("localhost", 6379)
        );
    }
}
```

### 2. User Session Management

```java
@Service
public class SessionService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_SESSION_PREFIX = "user:session:";
    private static final String ONLINE_USERS_KEY = "online:users";
    
    public void setUserSession(String username, UserSession session) {
        String key = USER_SESSION_PREFIX + username;
        redisTemplate.opsForValue().set(key, session, Duration.ofHours(24));
    }
    
    public UserSession getUserSession(String username) {
        String key = USER_SESSION_PREFIX + username;
        return (UserSession) redisTemplate.opsForValue().get(key);
    }
    
    public void removeUserSession(String username) {
        String key = USER_SESSION_PREFIX + username;
        redisTemplate.delete(key);
    }
    
    public void addOnlineUser(String username) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
    }
    
    public void removeOnlineUser(String username) {
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
    }
    
    public Set<String> getOnlineUsers() {
        return redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }
}
```

### 3. User Session Model

```java
public class UserSession {
    private String username;
    private String sessionId;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private String ipAddress;
    private String userAgent;
    
    // Constructors, getters, setters
}
```

## 📨 Message Caching

### 1. Message Cache Service

```java
@Service
public class MessageCacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String ROOM_MESSAGES_PREFIX = "room:messages:";
    private static final int MAX_CACHED_MESSAGES = 100;
    
    public void cacheMessage(Long roomId, Message message) {
        String key = ROOM_MESSAGES_PREFIX + roomId;
        
        // Add message to the beginning of the list
        redisTemplate.opsForList().leftPush(key, message);
        
        // Keep only the last 100 messages
        redisTemplate.opsForList().trim(key, 0, MAX_CACHED_MESSAGES - 1);
        
        // Set expiration (1 hour)
        redisTemplate.expire(key, Duration.ofHours(1));
    }
    
    public List<Message> getCachedMessages(Long roomId) {
        String key = ROOM_MESSAGES_PREFIX + roomId;
        return (List<Message>) redisTemplate.opsForList().range(key, 0, -1);
    }
    
    public void clearRoomMessages(Long roomId) {
        String key = ROOM_MESSAGES_PREFIX + roomId;
        redisTemplate.delete(key);
    }
}
```

## 🚀 Redis Pub/Sub cho Real-time Features

### 1. Message Publisher

```java
@Service
public class MessagePublisher {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void publishMessage(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
    
    public void publishToRoom(Long roomId, Message message) {
        String channel = "room:" + roomId;
        publishMessage(channel, message);
    }
    
    public void publishUserStatus(String username, boolean isOnline) {
        UserStatus status = new UserStatus(username, isOnline);
        publishMessage("user:status", status);
    }
}
```

### 2. Message Subscriber

```java
@Component
public class MessageSubscriber {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private MessageService messageService;
    
    @RedisListener(channels = "room:*")
    public void handleRoomMessage(String channel, Message message) {
        // Extract room ID from channel
        String roomId = channel.substring(channel.lastIndexOf(':') + 1);
        
        // Save message to database
        messageService.saveMessage(message);
        
        // Broadcast to WebSocket clients
        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }
    
    @RedisListener(channels = "user:status")
    public void handleUserStatus(UserStatus status) {
        // Broadcast user status to all clients
        messagingTemplate.convertAndSend("/topic/user:status", status);
    }
}
```

### 3. User Status Model

```java
public class UserStatus {
    private String username;
    private boolean isOnline;
    private LocalDateTime timestamp;
    
    // Constructors, getters, setters
}
```

## 📊 Redis Data Structures Usage

### 1. String Operations

```java
@Service
public class StringOperations {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void setUserToken(String username, String token) {
        String key = "user:token:" + username;
        redisTemplate.opsForValue().set(key, token, Duration.ofHours(24));
    }
    
    public String getUserToken(String username) {
        String key = "user:token:" + username;
        return (String) redisTemplate.opsForValue().get(key);
    }
    
    public void incrementMessageCount(Long roomId) {
        String key = "room:message:count:" + roomId;
        redisTemplate.opsForValue().increment(key);
    }
}
```

### 2. Hash Operations

```java
@Service
public class HashOperations {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void setUserProfile(String username, UserProfile profile) {
        String key = "user:profile:" + username;
        redisTemplate.opsForHash().putAll(key, profile.toMap());
        redisTemplate.expire(key, Duration.ofHours(24));
    }
    
    public UserProfile getUserProfile(String username) {
        String key = "user:profile:" + username;
        Map<Object, Object> profileMap = redisTemplate.opsForHash().entries(key);
        return UserProfile.fromMap(profileMap);
    }
}
```

### 3. Set Operations

```java
@Service
public class SetOperations {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void addRoomMember(Long roomId, String username) {
        String key = "room:members:" + roomId;
        redisTemplate.opsForSet().add(key, username);
    }
    
    public Set<String> getRoomMembers(Long roomId) {
        String key = "room:members:" + roomId;
        return redisTemplate.opsForSet().members(key);
    }
    
    public boolean isRoomMember(Long roomId, String username) {
        String key = "room:members:" + roomId;
        return redisTemplate.opsForSet().isMember(key, username);
    }
}
```

### 4. Sorted Set Operations

```java
@Service
public class SortedSetOperations {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void addUserActivity(String username, double score) {
        String key = "user:activity";
        redisTemplate.opsForZSet().add(key, username, score);
    }
    
    public Set<String> getMostActiveUsers(int limit) {
        String key = "user:activity";
        return redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
    }
    
    public Long getUserRank(String username) {
        String key = "user:activity";
        return redisTemplate.opsForZSet().reverseRank(key, username);
    }
}
```

## 🔧 Redis Configuration Properties

```java
@ConfigurationProperties(prefix = "spring.data.redis")
@Component
public class RedisProperties {
    
    private String host = "localhost";
    private int port = 6379;
    private String password;
    private int timeout = 2000;
    private Pool pool = new Pool();
    
    public static class Pool {
        private int maxActive = 8;
        private int maxIdle = 8;
        private int minIdle = 0;
        private long maxWait = -1;
        
        // Getters and setters
    }
    
    // Getters and setters
}
```

## 🧪 Testing Redis

### 1. Unit Testing

```java
@ExtendWith(MockitoExtension.class)
class RedisServiceTest {
    
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private ValueOperations<String, Object> valueOperations;
    
    @InjectMocks
    private SessionService sessionService;
    
    @Test
    void shouldSetUserSession() {
        // Given
        String username = "testuser";
        UserSession session = new UserSession();
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        
        // When
        sessionService.setUserSession(username, session);
        
        // Then
        verify(valueOperations).set("user:session:" + username, session, Duration.ofHours(24));
    }
}
```

### 2. Integration Testing

```java
@SpringBootTest
@Testcontainers
class RedisIntegrationTest {
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Test
    void shouldStoreAndRetrieveData() {
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

## 🎯 Best Practices

### 1. Key Naming Convention

```java
public class RedisKeyGenerator {
    
    public static String userSessionKey(String username) {
        return "user:session:" + username;
    }
    
    public static String roomMessagesKey(Long roomId) {
        return "room:messages:" + roomId;
    }
    
    public static String onlineUsersKey() {
        return "online:users";
    }
    
    public static String userTokenKey(String username) {
        return "user:token:" + username;
    }
}
```

### 2. Error Handling

```java
@Service
public class RedisService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void safeSet(String key, Object value, Duration timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout);
        } catch (Exception e) {
            logger.error("Failed to set Redis key {}: {}", key, e.getMessage());
            // Fallback to database or other storage
        }
    }
    
    public Object safeGet(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Failed to get Redis key {}: {}", key, e.getMessage());
            return null;
        }
    }
}
```

### 3. Cache Warming

```java
@Component
public class CacheWarmer {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ChatService chatService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void warmCache() {
        // Warm up frequently accessed data
        List<User> activeUsers = userService.getActiveUsers();
        activeUsers.forEach(user -> userService.findByUsername(user.getUsername()));
        
        List<ChatRoom> activeRooms = chatService.getActiveRooms();
        activeRooms.forEach(room -> chatService.getRoomById(room.getId()));
    }
}
```

## 📚 Tài liệu tham khảo

- [Redis Documentation](https://redis.io/documentation)
- [Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)
- [Redis Commands](https://redis.io/commands)

## 🎯 Bài tập thực hành

1. **Setup Redis** và test connection
2. **Implement user session** caching
3. **Cache chat messages** for quick access
4. **Use Redis Pub/Sub** for real-time notifications
5. **Implement online users** tracking
6. **Add cache warming** on application startup

---

**Lưu ý**: Redis là in-memory database, cần backup và monitoring để tránh mất dữ liệu!
