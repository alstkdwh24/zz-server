package com.example.zzserver.member.dto.response;

import com.example.zzserver.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
@Getter
@Builder
public class MemberResponseDto {
    private UUID id;
//    private String userId;
    private String userPw;
    private String email;
    private String name;
    private Role role;

    public MemberResponseDto() {}

    public MemberResponseDto(UUID id, String userPw, String email, String name, Role role) {
        this.id = id;
        this.userPw = userPw;
        this.email = email;
        this.name = name;
        this.role = role;
    }







}
