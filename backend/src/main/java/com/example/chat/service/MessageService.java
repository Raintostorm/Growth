package com.example.chat.service;

import com.example.chat.model.Message;
import com.example.chat.model.MessageType;
import com.example.chat.model.MessageStatus;
import com.example.chat.model.User;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Message> getMessagesByRoomId(Long roomId, int page, int size) {
        // Return all messages for the room (simple implementation)
        return messageRepository.findAll().stream()
                .filter(message -> message.getChatRoomId().equals(roomId))
                .toList();
    }

    public Message sendMessage(Long roomId, String content, String username) {
        // Try to find user, if not found, use the first available user
        User sender = userRepository.findByUsername(username)
                .orElse(userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No users found in database")));

        Message message = new Message();
        message.setContent(content);
        message.setType(MessageType.TEXT);
        message.setStatus(MessageStatus.SENT);
        message.setSender(sender);
        message.setChatRoomId(roomId);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
