package com.example.zzserver.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;


@RedisHash(value = "refreshToken", timeToLive = 2592000) // 30일(초 단위)

public class RedisRefreshToken {


    @Id
    private UUID id = UUID.randomUUID();


    @Column
    private String refresh_token;

    @Column
    @Indexed
    private String email; // 이메일 필드 추가

    public RedisRefreshToken() {}
    public RedisRefreshToken(UUID id, String refresh_token, String email) {
        this.id = id;



        this.refresh_token = refresh_token;
        this.email = email; // 이메일 초기화
    }

    public void setRefreshToken(String refreshToken) {

        this.refresh_token = refreshToken;

    }
    public String getRefreshToken() {
        return this.refresh_token;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
