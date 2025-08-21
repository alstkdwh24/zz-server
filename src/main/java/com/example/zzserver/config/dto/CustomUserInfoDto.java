package com.example.zzserver.config.dto;

import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Member;
import com.example.zzserver.member.entity.Role;

import java.util.UUID;

public class CustomUserInfoDto extends MemberRequestDto {
    private String email;
    private String name;
    private UUID id;
    private String userId;
    private String userPw;

    private Role role;

    public CustomUserInfoDto() {}

    public CustomUserInfoDto(Member member) {
        this.id = member.getId();
        this.userId = member.getUserId(); // 파라미터 없는 메서드만 남기세요.
        this.userPw = member.getUserPw();
        this.role = Role.valueOf(member.getRole().name()); // Role을 String으로 변환
        this.email = member.getEmail();
        this.name = member.getName();
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
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
