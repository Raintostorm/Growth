package com.example.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setOnlineUser(String username) {
        redisTemplate.opsForSet().add("online_users", username);
        redisTemplate.expire("online_users", 30, TimeUnit.MINUTES);
    }

    public void setOfflineUser(String username) {
        redisTemplate.opsForSet().remove("online_users", username);
    }

    public Set<Object> getOnlineUsers() {
        return redisTemplate.opsForSet().members("online_users");
    }

    public void cacheMessage(String roomId, Object message) {
        String key = "room_messages:" + roomId;
        redisTemplate.opsForList().leftPush(key, message);
        redisTemplate.opsForList().trim(key, 0, 99); // Keep last 100 messages
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    public void cacheUserSession(String username, Object sessionData) {
        String key = "user_session:" + username;
        redisTemplate.opsForValue().set(key, sessionData, 24, TimeUnit.HOURS);
    }

    public Object getUserSession(String username) {
        String key = "user_session:" + username;
        return redisTemplate.opsForValue().get(key);
    }
}
