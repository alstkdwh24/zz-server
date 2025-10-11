package com.example.zzserver.config.dto;

import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;
@EqualsAndHashCode(callSuper = true) //이 코드는 부모필드를 비교에 포함시킴
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CustomUserInfoDto extends MemberRequestDto {
    private UUID id;
    private String email;

    private String userPw;
    private String name;
    private Role role;
    private String nickname;




}
