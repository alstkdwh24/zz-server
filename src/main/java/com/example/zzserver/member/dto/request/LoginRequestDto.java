package com.example.zzserver.member.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class LoginRequestDto {
    private UUID id;



    @NotNull(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotNull(message = "비밀번호 입력은 필수입니다.")
    private String userPw;

    public LoginRequestDto() {}


    public LoginRequestDto(UUID id,String email, String userPw) {
        this.id = id;
        this.email = email;
//        this.userId = userId;
        this.userPw = userPw;
    }

//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
