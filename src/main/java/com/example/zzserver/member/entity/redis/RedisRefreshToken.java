package com.example.zzserver.member.entity.redis;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


@RedisHash(value = "refreshToken", timeToLive = 2592000) // 30일(초 단위)
@Getter
@Builder
public class RedisRefreshToken {


    @Id
    private String id;

    private String refresh_token;

    private String access_token;

    @Indexed
    private String email;

    public RedisRefreshToken() {
    }

    public RedisRefreshToken(String id, String refresh_token, String email, String access_token) {
        this.access_token = access_token;
        this.id = id;
        this.refresh_token = refresh_token;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
