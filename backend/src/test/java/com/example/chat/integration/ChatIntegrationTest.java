package com.example.chat.integration;

import com.example.chat.dto.AuthResponse;
import com.example.chat.dto.LoginRequest;
import com.example.chat.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UAT - User Acceptance Testing with integration tests
 * TECHNICAL CONCEPT: UAT
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ChatIntegrationTest {

    @Autowired
    private MockMvc mockMvc;  // UAT - Mock MVC for testing

    @Autowired
    private ObjectMapper objectMapper;  // UAT - JSON mapper

    @Test
    void testUserRegistrationFlow() throws Exception {
        // UAT - Test complete user registration flow
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("integrationuser");
        registerRequest.setEmail("integration@example.com");
        registerRequest.setDisplayName("Integration User");
        registerRequest.setPassword("password123");

        // UAT - Perform registration
        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value("integrationuser"))
                .andExpect(jsonPath("$.user.email").value("integration@example.com"))
                .andReturn();

        // UAT - Verify response structure
        String responseContent = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseContent, AuthResponse.class);
        
        assert authResponse.getToken() != null;
        assert authResponse.getUser().getUsername().equals("integrationuser");
    }

    @Test
    void testUserLoginFlow() throws Exception {
        // UAT - Test complete user login flow
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        // UAT - Perform login
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andReturn();

        // UAT - Verify response structure
        String responseContent = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseContent, AuthResponse.class);
        
        assert authResponse.getToken() != null;
        assert authResponse.getUser().getUsername().equals("testuser");
    }

    @Test
    void testChatRoomCreationFlow() throws Exception {
        // UAT - Test complete chat room creation flow
        String roomData = "{\"name\":\"Integration Test Room\",\"description\":\"Test room for integration testing\"}";

        // UAT - Perform room creation
        mockMvc.perform(post("/chat/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Room"))
                .andExpect(jsonPath("$.description").value("Test room for integration testing"));
    }

    @Test
    void testMessageSendingFlow() throws Exception {
        // UAT - Test complete message sending flow
        String messageData = "{\"content\":\"Integration test message\",\"sender\":\"testuser\"}";

        // UAT - Perform message sending
        mockMvc.perform(post("/chat/rooms/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Integration test message"));
    }

    @Test
    void testHealthCheckEndpoint() throws Exception {
        // UAT - Test application health check
        mockMvc.perform(post("/actuator/health"))
                .andExpect(status().isOk());
    }
}
