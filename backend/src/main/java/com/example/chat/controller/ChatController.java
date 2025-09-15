package com.example.chat.controller;

import com.example.chat.dto.ChatRoomDto;
import com.example.chat.dto.MessageDto;
import com.example.chat.model.ChatRoom;
import com.example.chat.model.Message;
import com.example.chat.service.ChatService;
import com.example.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getRooms() {
        List<ChatRoom> rooms = chatService.getAllRooms();
        List<ChatRoomDto> roomDtos = rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roomDtos);
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDto> createRoom(@RequestBody ChatRoomDto roomDto) {
        ChatRoom room = chatService.createRoom(roomDto.getName(), roomDto.getDescription());
        return ResponseEntity.ok(convertToDto(room));
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomDto> getRoomById(@PathVariable Long roomId) {
        ChatRoom room = chatService.getRoomById(roomId);
        return ResponseEntity.ok(convertToDto(room));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<MessageDto>> getRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Message> messages = messageService.getMessagesByRoomId(roomId, page, size);
        List<MessageDto> messageDtos = messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messageDtos);
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable Long roomId,
            @RequestBody MessageDto messageDto) {
        // For now, use a default username since we don't have authentication yet
        String username = "defaultuser";
        if (messageDto.getSender() != null && messageDto.getSender().getUsername() != null) {
            username = messageDto.getSender().getUsername();
        }
        Message message = messageService.sendMessage(roomId, messageDto.getContent(), username);
        return ResponseEntity.ok(convertToDto(message));
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        chatService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/online")
    public ResponseEntity<List<String>> getOnlineUsers() {
        List<String> onlineUsers = chatService.getOnlineUsers();
        return ResponseEntity.ok(onlineUsers);
    }

    private ChatRoomDto convertToDto(ChatRoom room) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setDescription(room.getDescription());
        dto.setType(room.getType().toString());
        dto.setCreatedAt(room.getCreatedAt().toString());
        return dto;
    }

    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setType(message.getType().toString());
        dto.setStatus(message.getStatus().toString());
        dto.setCreatedAt(message.getCreatedAt().toString());
        
        // Convert sender
        com.example.chat.dto.UserDto senderDto = new com.example.chat.dto.UserDto();
        senderDto.setId(message.getSender().getId());
        senderDto.setUsername(message.getSender().getUsername());
        senderDto.setDisplayName(message.getSender().getDisplayName());
        dto.setSender(senderDto);
        
        return dto;
    }
}
