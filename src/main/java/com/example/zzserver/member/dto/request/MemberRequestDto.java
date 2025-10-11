package com.example.zzserver.member.dto.request;

import com.example.zzserver.member.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MemberRequestDto {
    private UUID id;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String userPw;
    @NotBlank(message = "이메일은 필수 입력값입니다.")

    private String email;

    private String nickname;

    private String name;

    private Role role;



}
