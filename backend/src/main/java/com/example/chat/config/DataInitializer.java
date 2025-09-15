package com.example.chat.config;

import com.example.chat.model.ChatRoom;
import com.example.chat.model.Message;
import com.example.chat.model.MessageType;
import com.example.chat.model.MessageStatus;
import com.example.chat.model.RoomType;
import com.example.chat.model.User;
import com.example.chat.model.Role;
import com.example.chat.repository.ChatRoomRepository;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create sample users
        if (userRepository.count() == 0) {
            User user1 = new User();
            user1.setUsername("testuser");
            user1.setEmail("test@example.com");
            user1.setDisplayName("Test User");
            user1.setPassword(passwordEncoder.encode("password123"));
            user1.setRole(Role.USER);
            user1.setOnline(true);
            userRepository.save(user1);

            User user2 = new User();
            user2.setUsername("admin");
            user2.setEmail("admin@example.com");
            user2.setDisplayName("Admin User");
            user2.setPassword(passwordEncoder.encode("admin123"));
            user2.setRole(Role.ADMIN);
            user2.setOnline(true);
            userRepository.save(user2);

            User user3 = new User();
            user3.setUsername("defaultuser");
            user3.setEmail("default@example.com");
            user3.setDisplayName("Default User");
            user3.setPassword(passwordEncoder.encode("password"));
            user3.setRole(Role.USER);
            user3.setOnline(true);
            userRepository.save(user3);

            System.out.println("Created sample users");
        }

        // Create sample chat rooms
        if (chatRoomRepository.count() == 0) {
            ChatRoom room1 = new ChatRoom();
            room1.setName("General");
            room1.setDescription("General discussion room");
            room1.setType(RoomType.PUBLIC);
            room1.setCreatedAt(LocalDateTime.now());
            room1.setUpdatedAt(LocalDateTime.now());
            chatRoomRepository.save(room1);

            ChatRoom room2 = new ChatRoom();
            room2.setName("Tech Talk");
            room2.setDescription("Technology discussions");
            room2.setType(RoomType.PUBLIC);
            room2.setCreatedAt(LocalDateTime.now());
            room2.setUpdatedAt(LocalDateTime.now());
            chatRoomRepository.save(room2);

            System.out.println("Created sample chat rooms");
        }

        // Create sample messages
        if (messageRepository.count() == 0) {
            User user1 = userRepository.findByUsername("testuser").orElse(null);
            User user2 = userRepository.findByUsername("admin").orElse(null);
            ChatRoom room1 = chatRoomRepository.findAll().get(0);

            if (user1 != null && room1 != null) {
                Message message1 = new Message();
                message1.setContent("Hello everyone! Welcome to the chat!");
                message1.setType(MessageType.TEXT);
                message1.setStatus(MessageStatus.SENT);
                message1.setSender(user1);
                message1.setChatRoomId(room1.getId());
                message1.setCreatedAt(LocalDateTime.now().minusMinutes(10));
                message1.setUpdatedAt(LocalDateTime.now().minusMinutes(10));
                messageRepository.save(message1);

                Message message2 = new Message();
                message2.setContent("This is a sample message to test the chat system.");
                message2.setType(MessageType.TEXT);
                message2.setStatus(MessageStatus.SENT);
                message2.setSender(user1);
                message2.setChatRoomId(room1.getId());
                message2.setCreatedAt(LocalDateTime.now().minusMinutes(5));
                message2.setUpdatedAt(LocalDateTime.now().minusMinutes(5));
                messageRepository.save(message2);
            }

            System.out.println("Created sample messages");
        }

        System.out.println("Database initialization completed!");
    }
}
