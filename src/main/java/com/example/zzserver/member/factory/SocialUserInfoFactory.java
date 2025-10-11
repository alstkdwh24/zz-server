package com.example.zzserver.member.factory;

import com.example.zzserver.member.dto.social.KakaoUserInfo;
import com.example.zzserver.member.dto.social.SocialInfoNaverDto;
import com.example.zzserver.member.dto.social.SocialUserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
@Slf4j
public class SocialUserInfoFactory {
    public static SocialUserInfo getSocialUserInfo(String registration, Map<String,Object> attributes){
        log.debug("getSocialUserInfo");
        if(registration.equals("kakao")){
            System.out.println("attributes = " + attributes);
            log.debug("info = " + attributes);

            return new KakaoUserInfo(attributes).getKakaoAccount();
        } else if (registration.equals("naver")) {
            return new SocialInfoNaverDto(attributes);
        }else{
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        }
    }
}
