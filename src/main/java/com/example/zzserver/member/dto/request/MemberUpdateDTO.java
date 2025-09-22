package com.example.zzserver.member.dto.request;

import com.example.zzserver.member.entity.Role;
import lombok.*;

import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberUpdateDTO {

    private UUID id;

    private String userPw;

    private String email;

    private String nickname;

    private String name;

    private Role role;


}
