package com.example.zzserver.member.dto.request;

import java.util.UUID;

public class KakaoTokenRDto {

    private UUID id;
    private String access_Token;
    private String refresh_token;

    private Integer refresh_token_expires_in;
    private String scope;
    private String tokenType;
    private Integer expiresIn;

    public KakaoTokenRDto(){}
    public KakaoTokenRDto(UUID id, String access_Token, String refresh_token, Integer refresh_token_expires_in, String scope, String tokenType, Integer expiresIn) {
        this.id = id;
        this.access_Token = access_Token;
        this.refresh_token = refresh_token;
        this.refresh_token_expires_in = refresh_token_expires_in;
        this.scope = scope;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccess_token() {
        return access_Token;
    }

    public void setAccess_token(String access_Token) {
        this.access_Token = access_Token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Integer getRefresh_token_expires_in() {
        return refresh_token_expires_in;
    }

    public void setRefresh_token_expires_in(Integer refresh_token_expires_in) {
        this.refresh_token_expires_in = refresh_token_expires_in;
    }

    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }





}
