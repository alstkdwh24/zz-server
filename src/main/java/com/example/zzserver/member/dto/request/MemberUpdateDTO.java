package com.example.zzserver.member.dto.request;

import com.example.zzserver.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberUpdateDTO {

    private UUID id;

    // private String userId;
    private String userPw;

    private String email;

    private String nickname;

    private String name;

    private Role role;


}
