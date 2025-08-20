package com.example.zzserver.config.dto;

import java.util.UUID;

public class TokenResponseDTO {
    private UUID id;
    private String access_token;
    private String refresh_token;


    public TokenResponseDTO() {
    }
    public TokenResponseDTO(UUID id,String access_token, String refresh_token ) {
        this.id = id;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public String getRefresh_token() {
        return refresh_token;
    }
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
}
