package com.example.zzserver.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoLoginCodeDto {
    private String code;
    private String token;
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String redirect_uri;

}
