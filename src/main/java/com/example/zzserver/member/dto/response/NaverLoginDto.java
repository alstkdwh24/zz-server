package com.example.zzserver.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NaverLoginDto {
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("refresh_token")
    private String refresh_token;
    @JsonProperty("token_type")
    private String token_type;
    @JsonProperty("expires_in")
    private String expires_in;
    public NaverLoginDto() {}
    public NaverLoginDto(String access_token, String refresh_token, String token_type, String expires_in) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getExpires_in() {
        return expires_in;
    }
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }


}
