package com.example.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String MESSAGE_TOPIC = "chat-messages";
    private static final String USER_ACTIVITY_TOPIC = "user-activity";

    public void sendMessage(String message) {
        kafkaTemplate.send(MESSAGE_TOPIC, message);
    }

    public void sendUserActivity(String username, String activity) {
        String activityMessage = username + ":" + activity;
        kafkaTemplate.send(USER_ACTIVITY_TOPIC, activityMessage);
    }

    public void sendSystemNotification(String notification) {
        kafkaTemplate.send("system-notifications", notification);
    }
}
