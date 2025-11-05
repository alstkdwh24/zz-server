package com.example.zzserver.member.service.oauth2;

import com.example.zzserver.member.dto.social.*;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.Role;
import com.example.zzserver.member.factory.SocialUserInfoFactory;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service("oAuth2UserService")
@Slf4j
@RequiredArgsConstructor

public class OAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        //프로바이더에서 사용자 정보를 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //어떤 소셜인지 식별
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //프러바이더가 반환한 attribute들을 맴으로 추출

        //provider별로 attributes 구조가 다르니 파싱 / 정규화(팩토리 / 어뎁터)
        SocialUserInfo socialUserInfo = null;
        if(registrationId.equals("naver")) {
            socialUserInfo= new SocialInfoNaverDto(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            socialUserInfo= new KakaoUserInfo(oAuth2User.getAttributes()).getKakaoAccount();
        }else{
            return null;
        }
        String email = socialUserInfo.getEmail(); // ✅ 실제 이메일
        Members existData = memberRepository.findByEmail(email);

        if(existData == null){

            Members userEntity = new Members();
            userEntity.ChangeEmail(socialUserInfo.getEmail());
            userEntity.ChangeName(socialUserInfo.getName());
            userEntity.ChangeRole(Role.valueOf("ROLE_USER"));

            memberRepository.save(userEntity);

            UserDTO userDTO= new UserDTO();
            userDTO.setEmail(socialUserInfo.getEmail());
            userDTO.setName(socialUserInfo.getName());
            userDTO.setRole("ROLE_USER");
            return new CustomOAuth2User(userDTO, oAuth2User.getAttributes());
        } else {


            UserDTO userDTO= new UserDTO();
            userDTO.setEmail(socialUserInfo.getEmail());
            userDTO.setName(socialUserInfo.getName());
            userDTO.setRole("ROLE_USER");
            return new CustomOAuth2User(userDTO, oAuth2User.getAttributes());
        }
        //DB에 해당 소셜로 가입한 사용자가 있는지 확인
    }
}
