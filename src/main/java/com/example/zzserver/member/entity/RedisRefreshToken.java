package com.example.zzserver.member.entity;

import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;


@RedisHash(value = "refreshToken", timeToLive = 2592000) // 30일(초 단위)
public class RedisRefreshToken {


    @Id
    private UUID id;

    private String refresh_token;

    @Indexed
    private String email;

    public RedisRefreshToken() {}

    public RedisRefreshToken(UUID id, String refresh_token, String email) {
        this.id = id;
        this.refresh_token = refresh_token;
        this.email = email;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getRefreshToken() {
        return refresh_token;
    }
    public void setRefreshToken(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
