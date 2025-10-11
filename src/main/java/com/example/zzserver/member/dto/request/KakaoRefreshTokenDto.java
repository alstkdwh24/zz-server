package com.example.zzserver.member.dto.request;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoRefreshTokenDto {

    private String refresh_token;


}
