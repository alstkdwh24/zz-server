package com.example.zzserver.member.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverRefreshTokenResDto {

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("refresh_token")
    private String refresh_token;  // refresh_token이 응답에 없으면 null이 될 수 있음

    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("expires_in")
    private String expires_in;

}
