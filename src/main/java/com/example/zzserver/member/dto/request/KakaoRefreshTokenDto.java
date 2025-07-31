package com.example.zzserver.member.dto.request;

public class KakaoRefreshTokenDto {

    private String refresh_token;

    public KakaoRefreshTokenDto() {}

    public KakaoRefreshTokenDto( String refresh_token) {

        this.refresh_token = refresh_token;
    }



    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
