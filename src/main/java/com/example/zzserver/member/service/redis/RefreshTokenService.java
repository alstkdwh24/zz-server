package com.example.zzserver.member.service.redis;

import com.example.zzserver.member.dto.request.KakaoRefreshTokenDto;
import com.example.zzserver.member.dto.response.KakaoRefreshTokenResDto;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service("refreshTokenService")
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;

    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApi;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public RefreshTokenService(RefreshRepository refreshRepository, RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.refreshRepository = refreshRepository;
 ;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }

    public RedisRefreshToken insertRefreshToken(String refreshToken, String accessToken) {


        RefreshToken newToken = new RefreshToken();
        newToken.setRefresh_token(refreshToken);


        RefreshToken save=refreshRepository.save(newToken);
        String id= String.valueOf(save.getId());

        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
        redisRefreshToken.setId(id);
        redisRefreshToken.setAccessToken(accessToken);
        redisRefreshToken.setRefreshToken(refreshToken);

        return refreshTokenRedisRepository.save(redisRefreshToken);

    }

    ;

    // RefreshTokenService.java
    public void reissueAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        KakaoRefreshTokenDto kakaoRefreshTokenDto = new KakaoRefreshTokenDto();

        kakaoRefreshTokenDto.setRefresh_token(refreshToken);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("client_id", kakaoLoginRestApi);
        map.add("refresh_token", kakaoRefreshTokenDto.getRefresh_token()); // 카카오 개발자 센터에 등록된 클라이언트 시크릿


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<KakaoRefreshTokenResDto> response = restTemplate.postForEntity(url, request, KakaoRefreshTokenResDto.class);

        KakaoRefreshTokenResDto body = response.getBody();


        String access_token = body.getAccessToken();





        String userInfoUrl= "http://localhost:9090/api/kakao/userinfo";
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> userInfoMap = new LinkedMultiValueMap<>();
        userInfoMap.add("access_token", access_token);

        HttpEntity<MultiValueMap<String, String>> userInfoRequest = new HttpEntity<>(userInfoMap, userInfoHeaders);
        ResponseEntity<String> userInfoResponse = restTemplate.postForEntity(
                userInfoUrl,
                userInfoRequest,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
        } else {
            throw new RuntimeException("카카오 토큰 재발급 실패");
        }
        // 1. 리프레시 토큰 유효성 검증
        // 2. 카카오 API 등 외부 서비스에 새 액세스 토큰 요청
        // 3. 새 액세스 토큰 반환
    }
    public RedisRefreshToken RedisInsertSearch(UUID id){
        RedisRefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰을 찾을 수 없습니다."));

        return redisRefreshToken;


    }

}
