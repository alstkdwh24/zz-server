package com.example.zzserver.member.service;

import com.example.zzserver.member.dto.social.SocialUserInfo;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.Role;
import com.example.zzserver.member.factory.SocialUserInfoFactory;
import com.example.zzserver.member.repository.jpa.MemberRepository;
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

@Service
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    public OAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        //프로바이더에서 사용자 정보를 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //어떤 소셜인지 식별
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //프러바이더가 반환한 attribute들을 맴으로 추출
        Map<String,Object> attributes = oAuth2User.getAttributes();

        //provider별로 attributes 구조가 다르니 파싱 / 정규화(팩토리 / 어뎁터)
        SocialUserInfo info = SocialUserInfoFactory.getSocialUserInfo(registrationId, attributes);
        //DB에 해당 소셜로 가입한 사용자가 있는지 확인
        Members userOptional = memberRepository.findMemberByEmail(info.getEmail());
        Members user;
        if(userOptional!= null){
            user =Members.builder()
                    .email(info.getEmail())
                    .role(Role.ROLE_USER)
                    .nickname(info.getName())
                    .name(info.getName())
                    .build();
            memberRepository.save(user);
        }else{
            user = null;
        }


        assert user != null;
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())), // DB Role과 일치
                attributes,
                "id"
        );
    }
}
