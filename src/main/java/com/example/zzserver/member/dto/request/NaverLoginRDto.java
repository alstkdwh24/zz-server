package com.example.zzserver.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverLoginRDto {
    private String code;
    private String state;

    private String client_id;

    private String grant_type;

    private String redirect_uri;

}
