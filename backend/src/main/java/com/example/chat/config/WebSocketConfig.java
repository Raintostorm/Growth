package com.example.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WEBSOCKET - WebSocket configuration for real-time communication
 * TECHNICAL CONCEPT: WEBSOCKET
 */
@Configuration
@EnableWebSocketMessageBroker  // WEBSOCKET - Enable WebSocket message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // WEBSOCKET - Configure message broker for real-time messaging
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");  // WEBSOCKET - Enable simple broker for topics and queues
        config.setApplicationDestinationPrefixes("/app");  // WEBSOCKET - Set application destination prefix
    }

    // WEBSOCKET - Register STOMP endpoints for WebSocket connections
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")  // WEBSOCKET - WebSocket endpoint
                .setAllowedOriginPatterns("*")  // WEBSOCKET - Allow all origins
                .withSockJS();  // WEBSOCKET - Enable SockJS fallback
    }
}
