package com.example.zzserver.member.dto.request;

import jakarta.validation.constraints.NotNull;

public class LoginRequestDto {



    @NotNull(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotNull(message = "비밀번호 입력은 필수입니다.")
    private String userPw;

    public LoginRequestDto() {}


    public LoginRequestDto( String userPw) {
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
