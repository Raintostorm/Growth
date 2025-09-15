package com.example.chat.dto;

public class UserDto {
    
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private boolean isOnline;

    public UserDto() {}

    public UserDto(Long id, String username, String email, String displayName, boolean isOnline) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.isOnline = isOnline;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
