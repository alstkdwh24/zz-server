package com.example.zzserver.member.dto.social;

public interface SocialUserInfo {
    String getProvider();
    String getProviderId();   // 소셜에서 주는 고유 ID
    String getEmail();        // 이메일
    String getName();
    String getNickname();
}
