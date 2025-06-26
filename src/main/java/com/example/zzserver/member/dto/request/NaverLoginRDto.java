package com.example.zzserver.member.dto.request;

public class NaverLoginRDto {
    private String code;
    private String state;

    private String client_id;

    private String grant_type;

    private String redirect_uri;

    public NaverLoginRDto() {}

    public NaverLoginRDto(String code, String state, String client_id, String grant_type, String redirect_uri) {
        this.code = code;
        this.state = state;
        this.client_id=client_id;
        this.grant_type = grant_type;
        this.redirect_uri = redirect_uri; // 실제 리다이렉트 URI로 변경 필요
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }
    public String getClient_id() {
        return client_id;
    }
    public String getGrant_type() {
        return grant_type;
    }
    public String getRedirect_uri() {
        return redirect_uri;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }
    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }
}
