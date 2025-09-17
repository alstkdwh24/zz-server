package com.example.zzserver.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverLoginInfoRDto {
    private String resultcode;
    private String message;
    private NaverUser response; // 네이버 유저 정보는 여기 안에 있음

    public String getResultcode() {
        return resultcode;
    }
    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public NaverUser getResponse() {
        return response;
    }
    public void setResponse(NaverUser response) {
        this.response = response;
    }

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
