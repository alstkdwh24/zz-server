package com.example.zzserver.member.dto.response;

import com.example.zzserver.member.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.logging.Messages;

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
    @Setter
    private Messages message;









}
