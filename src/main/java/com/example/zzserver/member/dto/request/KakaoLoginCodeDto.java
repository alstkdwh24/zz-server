package com.example.zzserver.member.dto.request;

public class KakaoLoginCodeDto {
    private String code;
    private String token;
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String redirect_uri;

    public KakaoLoginCodeDto() {}
    public KakaoLoginCodeDto(String code, String token, String grant_type, String client_id, String client_secret, String redirect_uri) {
        this.code = code;
        this.token = token;
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.redirect_uri = redirect_uri;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

}
