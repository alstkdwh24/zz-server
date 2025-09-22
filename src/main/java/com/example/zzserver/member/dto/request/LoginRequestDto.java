package com.example.zzserver.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDto {
    private UUID id;

    @NotNull(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotNull(message = "비밀번호 입력은 필수입니다.")
    private String userPw;


}
