package com.example.zzserver.member.dto.request;

public class NaverRefreshTokenDto {

    private String refreshToken;

    public NaverRefreshTokenDto() {
    }

    public NaverRefreshTokenDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
