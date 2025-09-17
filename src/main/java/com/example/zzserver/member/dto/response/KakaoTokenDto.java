package com.example.zzserver.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoTokenDto {


    private UUID id;
    private String access_token;
    private String refresh_token;

    private Integer refresh_token_expires_in;
    private String scope;
    private String tokenType;
    private Integer expiresIn;


}
