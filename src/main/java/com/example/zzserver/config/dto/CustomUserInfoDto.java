package com.example.zzserver.config.dto;

import java.util.UUID;

import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.Role;

public class CustomUserInfoDto extends MemberRequestDto {
    private UUID id;
    private String email;

    private String userPw;
    private String name;
    private Role role;
    private String nickname;

    public CustomUserInfoDto() {
    }

    public CustomUserInfoDto(Members member) {
        this.id = member.getId();
        this.userPw = member.getUserPw();
        this.role = Role.valueOf(member.getRole().name()); // Role을 String으로 변환
        this.email = member.getEmail();
        this.name = member.getName();
        this.nickname = member.getNickname();
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
}
