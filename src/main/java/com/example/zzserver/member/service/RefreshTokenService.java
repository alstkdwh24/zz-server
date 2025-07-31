package com.example.zzserver.member.service;

import com.example.zzserver.member.dto.request.KakaoRefreshTokenDto;
import com.example.zzserver.member.dto.response.KakaoRefreshTokenResDto;
import com.example.zzserver.member.entity.RedisRefreshToken;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service("refreshTokenService")
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;
    private final ModelMapper modelMapper;

    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApi;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public RefreshTokenService(RefreshRepository refreshRepository, ModelMapper modelMapper, RestTemplate restTemplate, RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.refreshRepository = refreshRepository;
 ;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.modelMapper = new ModelMapper();
    }

    public int insertRefreshToken(String refreshToken) {

        RefreshToken newToken = new RefreshToken();
        newToken.setRefresh_token(refreshToken);


        System.out.println("RefreshTokenService: Inserting new refresh token: " + newToken.getRefresh_token());
        refreshRepository.save(newToken);

        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
        redisRefreshToken.setId(newToken.getId());
        redisRefreshToken.setEmail("test@naver.com");
        redisRefreshToken.setRefreshToken(refreshToken);
        System.out.println("RefreshTokenService: Saving to Redis: " + redisRefreshToken.getRefreshToken());
        refreshTokenRedisRepository.save(redisRefreshToken);
        return 1; // Token inserted successfully

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

        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();

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
        refreshTokenRedisRepository.save(redisRefreshToken);

        if (response.getStatusCode() == HttpStatus.OK) {
        } else {
            throw new RuntimeException("카카오 토큰 재발급 실패");
        }
        // 1. 리프레시 토큰 유효성 검증
        // 2. 카카오 API 등 외부 서비스에 새 액세스 토큰 요청
        // 3. 새 액세스 토큰 반환
    }


}
