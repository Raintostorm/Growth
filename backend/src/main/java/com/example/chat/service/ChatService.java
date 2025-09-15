package com.example.chat.service;

import com.example.chat.model.ChatRoom;
import com.example.chat.model.RoomType;
import com.example.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public ChatRoom createRoom(String name, String description) {
        ChatRoom room = new ChatRoom();
        room.setName(name);
        room.setDescription(description);
        room.setType(RoomType.PUBLIC);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        return chatRoomRepository.save(room);
    }

    public void deleteRoom(Long roomId) {
        ChatRoom room = getRoomById(roomId);
        chatRoomRepository.delete(room);
    }

    public List<String> getOnlineUsers() {
        // For now, return a mock list of online users
        // In a real application, you would track online users
        List<String> onlineUsers = new ArrayList<>();
        onlineUsers.add("Luncantat");
        return onlineUsers;
    }
}
