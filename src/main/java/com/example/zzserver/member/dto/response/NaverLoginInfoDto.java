package com.example.zzserver.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverLoginInfoDto {

    private String resultcode;
    private String message;
    private NaverUser response; // 내부 클래스 타입 사용 가능, NaverLoginInfoDto. 없어도 됨


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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
