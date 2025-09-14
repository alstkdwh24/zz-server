package com.example.zzserver.member.dto.request;

import com.example.zzserver.member.entity.Role;

import java.util.UUID;

public class MemberUpdateDTO {

    private UUID id;

    // private String userId;
    private String userPw;

    private String email;

    private String nickname;

    private String name;

    private Role role;
    public MemberUpdateDTO(){

    }
    public MemberUpdateDTO(UUID id, String email, String userPw, String name, Role role, String nickname) {
        this.id = id;
        this.email = email;

        // this.userId = userId;
        this.userPw = userPw;
        this.name = name;

        this.role = role;

        this.nickname = nickname;

    }
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
    public String getName() {
        return name;

    }
    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
