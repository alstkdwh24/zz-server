package com.example.zzserver.member.dto.response;

import java.util.UUID;

public class MemberResponseDto {
    private UUID id;
//    private String userId;
    private String userPw;
    private String email;
    private String name;
    private String role;

    public MemberResponseDto() {}

    public MemberResponseDto(UUID id, String userPw, String email, String name, String role) {
        this.id = id;
//        this.userId = userId;
        this.userPw = userPw;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }




}
