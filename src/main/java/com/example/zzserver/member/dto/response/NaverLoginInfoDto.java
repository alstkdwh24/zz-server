package com.example.zzserver.member.dto.response;

import com.example.zzserver.member.dto.request.NaverLoginInfoRDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NaverLoginInfoDto {

    private String resultcode;
    private String message;
    private NaverLoginInfoDto.NaverUser response; // 네이버 유저 정보는 여기 안에 있음

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

    public NaverLoginInfoDto.NaverUser getResponse() {
        return response;
    }
    public void setResponse(NaverLoginInfoDto.NaverUser response) {
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

        public NaverUser(){}

        public NaverUser(String id, String nickname, String  age, String gender, String email, String name, String birthday, String birthyear, String mobile) {
            this.id = id;
            this.nickname = nickname;
            this.age = age;
            this.gender = gender;
            this.email = email;
            this.name = name;
            this.birthday = birthday;
            this.birthyear = birthyear;
            this.mobile = mobile;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getBirthyear() {
            return birthyear;
        }

        public void setBirthyear(String birthyear) {
            this.birthyear = birthyear;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }









    }
}
