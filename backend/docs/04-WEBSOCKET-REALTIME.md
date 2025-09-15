# WebSocket Real-time Communication - Giao tiếp thời gian thực

## 🎯 Mục tiêu

Hiểu và thực hiện WebSocket để tạo ra giao tiếp thời gian thực trong ứng dụng chat.

## 🔌 WebSocket là gì?

WebSocket là một giao thức truyền thông hai chiều (full-duplex) cho phép client và server giao tiếp với nhau trong thời gian thực thông qua một kết nối TCP duy nhất.

### So sánh với HTTP

| HTTP | WebSocket |
|------|-----------|
| Request-Response | Full-duplex |
| Stateless | Stateful |
| Overhead cao | Overhead thấp |
| Không real-time | Real-time |
| Một chiều | Hai chiều |

## 🏗️ Spring WebSocket Implementation

### 1. Dependencies

```xml
<!-- WebSocket Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### 2. WebSocket Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple message broker for destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic", "/queue");
        
        // Set application destination prefix to "/app"
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for private messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register WebSocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Enable SockJS fallback
    }
}
```

**Giải thích**:
- `enableSimpleBroker`: Bật message broker đơn giản cho các destination có prefix `/topic`, `/queue`
- `setApplicationDestinationPrefixes`: Set prefix `/app` cho các message từ client
- `setUserDestinationPrefix`: Set prefix `/user` cho private messages
- `addEndpoint`: Đăng ký endpoint `/ws` cho WebSocket connections

## 📡 STOMP Protocol

STOMP (Simple Text Oriented Messaging Protocol) là một giao thức messaging đơn giản được sử dụng với WebSocket.

### STOMP Message Types

1. **CONNECT**: Kết nối đến server
2. **SEND**: Gửi message
3. **SUBSCRIBE**: Đăng ký nhận message
4. **UNSUBSCRIBE**: Hủy đăng ký
5. **DISCONNECT**: Ngắt kết nối

### Message Destinations

```
/topic/public          - Public messages (broadcast)
/topic/room/{roomId}   - Room-specific messages
/queue/private         - Private messages
/user/{username}/queue/private - User-specific private messages
```

## 🎮 WebSocket Handler

### 1. Message Mapping

```java
@Controller
public class ChatWebSocketHandler {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message, 
                             SimpMessageHeaderAccessor headerAccessor) {
        // Handle incoming message
        message.setCreatedAt(LocalDateTime.now());
        
        // Save to database
        Message savedMessage = messageService.saveMessage(message);
        
        // Broadcast to all subscribers
        return savedMessage;
    }
}
```

**Giải thích**:
- `@MessageMapping`: Map incoming messages từ client
- `@SendTo`: Broadcast message đến subscribers
- `@Payload`: Message payload
- `SimpMessageHeaderAccessor`: Access message headers

### 2. Private Messages

```java
@MessageMapping("/chat.sendPrivateMessage")
public void sendPrivateMessage(@Payload Message message, 
                              SimpMessageHeaderAccessor headerAccessor) {
    // Save message
    Message savedMessage = messageService.saveMessage(message);
    
    // Send to specific user
    String destination = "/user/" + message.getChatRoom().getId() + "/queue/private";
    messagingTemplate.convertAndSend(destination, savedMessage);
}
```

### 3. Room-specific Messages

```java
@MessageMapping("/chat.sendMessage/{roomId}")
@SendTo("/topic/room/{roomId}")
public Message sendMessageToRoom(@DestinationVariable String roomId, 
                                @Payload Message message) {
    // Handle room-specific message
    return messageService.saveMessage(message);
}
```

## 🖥️ Frontend Implementation

### 1. WebSocket Connection

```javascript
// Connect to WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to public messages
    stompClient.subscribe('/topic/public', function(message) {
        showMessage(JSON.parse(message.body));
    });
    
    // Subscribe to room messages
    stompClient.subscribe('/topic/room/' + roomId, function(message) {
        showMessage(JSON.parse(message.body));
    });
    
    // Subscribe to private messages
    stompClient.subscribe('/user/queue/private', function(message) {
        showPrivateMessage(JSON.parse(message.body));
    });
});
```

### 2. Sending Messages

```javascript
// Send public message
function sendMessage() {
    const message = {
        content: document.getElementById('messageInput').value,
        type: 'TEXT',
        sender: currentUser
    };
    
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
}

// Send private message
function sendPrivateMessage(recipientId) {
    const message = {
        content: document.getElementById('messageInput').value,
        type: 'TEXT',
        recipientId: recipientId
    };
    
    stompClient.send("/app/chat.sendPrivateMessage", {}, JSON.stringify(message));
}

// Send room message
function sendRoomMessage(roomId) {
    const message = {
        content: document.getElementById('messageInput').value,
        type: 'TEXT',
        chatRoomId: roomId
    };
    
    stompClient.send("/app/chat.sendMessage/" + roomId, {}, JSON.stringify(message));
}
```

### 3. Typing Indicators

```javascript
// Send typing indicator
function sendTypingIndicator(isTyping) {
    const typingIndicator = {
        username: currentUser.username,
        isTyping: isTyping
    };
    
    stompClient.send("/app/chat.typing", {}, JSON.stringify(typingIndicator));
}

// Subscribe to typing indicators
stompClient.subscribe('/topic/public', function(message) {
    const data = JSON.parse(message.body);
    if (data.type === 'TYPING') {
        showTypingIndicator(data);
    }
});
```

## 🔐 WebSocket Security

### 1. Authentication

```java
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, 
                                 ServerHttpResponse response,
                                 WebSocketHandler wsHandler, 
                                 Map<String, Object> attributes) throws Exception {
        
        // Extract JWT token from query parameters or headers
        String token = extractToken(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            attributes.put("username", username);
            return true;
        }
        
        return false;
    }
    
    @Override
    public void afterHandshake(ServerHttpRequest request, 
                             ServerHttpResponse response,
                             WebSocketHandler wsHandler, 
                             Exception exception) {
        // Post-handshake logic
    }
}
```

### 2. User Authentication in Messages

```java
@MessageMapping("/chat.sendMessage")
public Message sendMessage(@Payload Message message, 
                         SimpMessageHeaderAccessor headerAccessor) {
    
    // Get authenticated user
    Principal principal = headerAccessor.getUser();
    if (principal == null) {
        throw new SecurityException("Unauthorized");
    }
    
    // Set sender from authenticated user
    User sender = userService.findByUsername(principal.getName());
    message.setSender(sender);
    
    return messageService.saveMessage(message);
}
```

## 📊 Message Broadcasting

### 1. Broadcasting to All Users

```java
@Autowired
private SimpMessagingTemplate messagingTemplate;

// Broadcast to all connected users
public void broadcastMessage(Message message) {
    messagingTemplate.convertAndSend("/topic/public", message);
}
```

### 2. Broadcasting to Room Members

```java
// Broadcast to specific room
public void broadcastToRoom(Long roomId, Message message) {
    messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
}
```

### 3. Sending to Specific User

```java
// Send to specific user
public void sendToUser(String username, Message message) {
    messagingTemplate.convertAndSendToUser(username, "/queue/private", message);
}
```

## 🎯 Advanced Features

### 1. User Presence

```java
@EventListener
public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    // User connected
    String username = getUsernameFromEvent(event);
    userService.updateUserOnlineStatus(username, true);
    
    // Notify other users
    Message systemMessage = new Message();
    systemMessage.setContent(username + " joined the chat!");
    systemMessage.setType(MessageType.SYSTEM);
    
    messagingTemplate.convertAndSend("/topic/public", systemMessage);
}

@EventListener
public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    // User disconnected
    String username = getUsernameFromEvent(event);
    userService.updateUserOnlineStatus(username, false);
    
    // Notify other users
    Message systemMessage = new Message();
    systemMessage.setContent(username + " left the chat!");
    systemMessage.setType(MessageType.SYSTEM);
    
    messagingTemplate.convertAndSend("/topic/public", systemMessage);
}
```

### 2. Message Status Updates

```java
@MessageMapping("/chat.messageRead")
public void markMessageAsRead(@Payload MessageReadRequest request) {
    // Update message status
    messageService.markAsRead(request.getMessageId(), request.getUserId());
    
    // Notify sender
    Message statusUpdate = new Message();
    statusUpdate.setType(MessageType.SYSTEM);
    statusUpdate.setContent("Message read by " + request.getUserId());
    
    messagingTemplate.convertAndSendToUser(
        request.getSenderId(), 
        "/queue/status", 
        statusUpdate
    );
}
```

### 3. File Sharing

```java
@MessageMapping("/chat.sendFile")
public void sendFile(@Payload FileMessage fileMessage) {
    // Save file message
    Message savedMessage = messageService.saveFileMessage(fileMessage);
    
    // Broadcast to room
    messagingTemplate.convertAndSend(
        "/topic/room/" + fileMessage.getRoomId(), 
        savedMessage
    );
}
```

## 🧪 Testing WebSocket

### 1. Unit Testing

```java
@ExtendWith(MockitoExtension.class)
class ChatWebSocketHandlerTest {
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    
    @Mock
    private MessageService messageService;
    
    @InjectMocks
    private ChatWebSocketHandler chatHandler;
    
    @Test
    void shouldSendMessage() {
        // Given
        Message message = new Message();
        message.setContent("Hello World");
        
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");
        when(headerAccessor.getUser()).thenReturn(principal);
        
        when(messageService.saveMessage(any())).thenReturn(message);
        
        // When
        Message result = chatHandler.sendMessage(message, headerAccessor);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello World");
    }
}
```

### 2. Integration Testing

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldConnectToWebSocket() throws Exception {
        // Test WebSocket connection
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));
        
        StompSession stompSession = stompClient.connect(
            "ws://localhost:" + port + "/ws", 
            new StompSessionHandlerAdapter() {}
        ).get(1, TimeUnit.SECONDS);
        
        assertThat(stompSession).isNotNull();
        assertThat(stompSession.isConnected()).isTrue();
    }
}
```

## 🎯 Best Practices

### 1. Error Handling

```java
@MessageMapping("/chat.sendMessage")
public Message sendMessage(@Payload Message message, 
                         SimpMessageHeaderAccessor headerAccessor) {
    try {
        // Process message
        return messageService.saveMessage(message);
    } catch (Exception e) {
        logger.error("Error processing message: {}", e.getMessage());
        
        // Send error message to sender
        Message errorMessage = new Message();
        errorMessage.setContent("Error: " + e.getMessage());
        errorMessage.setType(MessageType.SYSTEM);
        
        messagingTemplate.convertAndSendToUser(
            headerAccessor.getUser().getName(),
            "/queue/errors",
            errorMessage
        );
        
        return null;
    }
}
```

### 2. Rate Limiting

```java
@Component
public class WebSocketRateLimiter {
    
    private final Map<String, AtomicInteger> userMessageCounts = new ConcurrentHashMap<>();
    private final int MAX_MESSAGES_PER_MINUTE = 30;
    
    public boolean isAllowed(String username) {
        String key = username + ":" + (System.currentTimeMillis() / 60000);
        AtomicInteger count = userMessageCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        
        return count.incrementAndGet() <= MAX_MESSAGES_PER_MINUTE;
    }
}
```

### 3. Message Validation

```java
@MessageMapping("/chat.sendMessage")
public Message sendMessage(@Payload @Valid Message message, 
                         SimpMessageHeaderAccessor headerAccessor) {
    // Validation is automatically handled by @Valid annotation
    return messageService.saveMessage(message);
}
```

## 📚 Tài liệu tham khảo

- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [STOMP Protocol Specification](https://stomp.github.io/stomp-specification-1.2.html)
- [WebSocket API](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)

## 🎯 Bài tập thực hành

1. **Tạo WebSocket connection** từ frontend
2. **Implement real-time messaging** giữa users
3. **Thêm typing indicators** cho chat
4. **Implement user presence** (online/offline)
5. **Thêm file sharing** qua WebSocket
6. **Test WebSocket** với Postman hoặc browser

---

**Lưu ý**: WebSocket cần được test kỹ lưỡng vì nó duy trì kết nối lâu dài và có thể gây memory leak nếu không quản lý tốt!
