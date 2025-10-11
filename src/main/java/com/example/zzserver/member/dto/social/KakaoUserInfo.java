package com.example.zzserver.member.dto.social;

import lombok.*;

import java.util.Map;

@Setter
@Getter

public class KakaoUserInfo   {
    private Long id;
    private KakaoAccount kakaoAccount; // 👈 이 필드 반드시 선언해야 함


    public KakaoUserInfo(Map<String, Object> attributes) {
        this.id = ((Number) attributes.get("id")).longValue();

        Map<String, Object> accountMap = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profileMap = accountMap != null ? (Map<String, Object>) accountMap.get("profile") : null;

        KakaoAccount.Profile profile = null;
        if (profileMap != null) {
            profile = new KakaoAccount.Profile(
                    (String) profileMap.getOrDefault("profile_nickname", ""),
                    (String) profileMap.getOrDefault("profile_image", ""),
                    (String) profileMap.getOrDefault("account_email", "")
                     // thumbnail_image는 선택사항, 카카오가 안 줌
            );
        }

        this.kakaoAccount = new KakaoAccount(
                    id, accountMap.get("has_email") != null && (Boolean) accountMap.get("has_email"),
                    (String) accountMap.get("email"),
                    profile,
                    accountMap.get("has_profile") != null && (Boolean) accountMap.get("has_profile"));
        }





    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoAccount implements SocialUserInfo {
        private Long providerId; // 카카오 고유 ID

        private boolean hasEmail;
        private String email;
        private Profile profile;
        private boolean hasProfile;


        @Override
        public String getProviderId() {
            return String.valueOf(providerId); // id 필드를 추가해야 함
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getName() {
            return profile != null ? profile.getNickname() : null;
        }

        @Override
        public String getNickname() {
            return profile != null ? profile.getNickname() : null;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class Profile {
            private String nickname;
            private String profileImageUrl;
            private String thumbnailImageUrl;
        }
    }
}
