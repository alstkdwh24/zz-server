package com.example.zzserver.member.dto.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
// 네이버 로그인 정보 응답 DTO
public class SocialInfoNaverDto implements SocialUserInfo {

    private String resultcode;
    private String message;
    private NaverUser response; // 필드 존재해야 함


    public SocialInfoNaverDto(Map<String, Object> attributes) {
        this.resultcode = (String) attributes.get("resultcode");
        this.message = (String) attributes.get("message");
        Object obj = attributes.get("response");
        if (obj instanceof Map<?, ?>) {

            Map<String, Object> responseMap = (Map<String, Object>) attributes.get("response");
            if (responseMap != null) {
                this.response =  NaverUser.builder()
                        .id((String) responseMap.get("id"))
                        .nickname((String) responseMap.get("nickname"))
                        .profileImage((String) responseMap.get("profile_image"))
                        .age((String) responseMap.get("age"))
                        .gender((String) responseMap.get("gender"))
                        .email((String) responseMap.get("email"))
                        .name((String) responseMap.get("name"))
                        .birthday((String) responseMap.get("birthday"))
                        .birthyear((String) responseMap.get("birthyear"))
                        .mobile((String) responseMap.get("mobile"))
                        .build();

            }
        }
    }
    //내부 클래스 타입 사용 가능, NaverLoginInfoDto. 없어도 됨

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return response != null ? response.getId() : null;
    }

    @Override
    public String getEmail() {
        return response != null ? response.getEmail() : null;
    }

    @Override
    public String getName() {
        return response != null ? response.getName() : null;
    }

    @Override
    public String getNickname() {
        return response != null ? response.getNickname() : null;
    }
    @Getter
    @AllArgsConstructor
    @Builder
    public static class NaverUser {
        private String id;
        private String nickname;

        @JsonProperty("profile_image") // JSON 필드와 다르면 꼭 써야 함
        private String profileImage;

        private String age;
        private String gender;
        private String email;
        private String name;
        private String birthday;
        private String birthyear;
        private String mobile;


    }


}