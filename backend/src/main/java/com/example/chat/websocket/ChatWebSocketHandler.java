package com.example.chat.websocket;

import com.example.chat.model.Message;
import com.example.chat.model.MessageType;
import com.example.chat.model.MessageStatus;
import com.example.chat.model.User;
import com.example.chat.repository.UserRepository;
import com.example.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * WEBSOCKET - WebSocket handler for real-time chat messaging
 * TECHNICAL CONCEPTS: WEBSOCKET, DESIGN PATTERN (Service), STRUCTURE LAYER
 */
@Controller
public class ChatWebSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;  // WEBSOCKET - Message template for sending

    @Autowired
    private UserRepository userRepository;  // DESIGN PATTERN - Repository pattern

    @Autowired
    private MessageService messageService;  // DESIGN PATTERN - Service pattern

    // WEBSOCKET - Handle real-time message sending
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        String content = (String) payload.get("content");
        String username = (String) payload.get("username");
        Long roomId = Long.valueOf(payload.get("roomId").toString());

        User sender = userRepository.findByUsername(username)  // DESIGN PATTERN - Repository pattern
                .orElseThrow(() -> new RuntimeException("User not found"));  // RESOLVE SOLUTION - Exception handling

        Message message = new Message();
        message.setContent(content);
        message.setType(MessageType.TEXT);
        message.setStatus(MessageStatus.SENT);
        message.setSender(sender);
        message.setChatRoomId(roomId);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        // DESIGN PATTERN - Service pattern - Save message to database
        messageService.saveMessage(message);

        // WEBSOCKET - Send to room subscribers via STOMP
        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }

    // WEBSOCKET - Handle real-time typing indicators
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Boolean isTyping = (Boolean) payload.get("isTyping");
        Long roomId = Long.valueOf(payload.get("roomId").toString());

        Map<String, Object> typingData = Map.of(
            "username", username,
            "isTyping", isTyping,
            "timestamp", LocalDateTime.now().toString()
        );

        // WEBSOCKET - Send typing indicator to room subscribers
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/typing", typingData);
    }

    // WEBSOCKET - Handle user joining chat room
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) payload.get("username");
        Long roomId = Long.valueOf(payload.get("roomId").toString());

        Map<String, Object> systemMessage = Map.of(
            "content", username + " joined the chat",
            "type", "SYSTEM",
            "timestamp", LocalDateTime.now().toString()
        );

        // WEBSOCKET - Send system message to room subscribers
        messagingTemplate.convertAndSend("/topic/room/" + roomId, systemMessage);
    }
}
