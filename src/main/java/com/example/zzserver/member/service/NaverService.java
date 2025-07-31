package com.example.zzserver.member.service;

import com.example.zzserver.member.dto.response.NaverLoginInfoDto;
import com.example.zzserver.member.dto.response.NaverRefreshTokenResDto;
import com.example.zzserver.member.entity.RedisRefreshToken;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.repository.jpa.NaverRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service("naverService")
public class NaverService {

    @Value("${naver.naverClientId}")
    private String naverClientId;

    @Value("${naver.naverClientSecret}")
    private String naverClientSecret;


    private final RestTemplate restTemplate;
    private final NaverRepository naverRepository;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public NaverService(NaverRepository naverRepository,
                        RefreshTokenRedisRepository refreshTokenRedisRepository,
                        RestTemplate restTemplate) {
        this.naverRepository = naverRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.restTemplate = restTemplate;
    }

    public String insertRefreshToken(String refreshToken) {

        RefreshToken refreshTokenEntity = new RefreshToken(UUID.randomUUID(), refreshToken, "aslfmdqpdmqwkl@naver.com");

        System.out.println("insertRefreshToken: " + refreshTokenEntity.getRefresh_token());

        naverRepository.save(refreshTokenEntity);

        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
        redisRefreshToken.setId(UUID.randomUUID());
        redisRefreshToken.setEmail("test@naver.com");
        redisRefreshToken.setRefreshToken(refreshToken);


        refreshTokenRedisRepository.save(redisRefreshToken);
        return refreshToken;
    }

    public String reissueAccessToken(String refreshToken) {
        System.out.println("reissueAccessToken:" + refreshToken);
        String reissueAccessToken = refreshToken;
        RestTemplate newToken = new RestTemplate();

        try {
            // 1. 토큰 재발급 요청
            String tokenUrl = "https://nid.naver.com/oauth2.0/token";
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("grant_type", "refresh_token");
            tokenParams.add("client_id", naverClientId);
            tokenParams.add("client_secret", naverClientSecret);
            tokenParams.add("refresh_token", reissueAccessToken);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);

            ResponseEntity<NaverRefreshTokenResDto> tokenResponse ;
            try {
                tokenResponse = newToken.postForEntity(tokenUrl, tokenRequest, NaverRefreshTokenResDto.class);
                if (tokenResponse == null) {
                    throw new IllegalStateException("tokenResponse가 null입니다. 네이버 서버가 응답하지 않았을 수 있습니다.");
                }

                if (tokenResponse.getBody() == null || tokenResponse.getBody().getAccessToken() == null) {
                    System.err.println("응답 본문: " + tokenResponse);
                    throw new IllegalStateException("토큰 재발급 실패: 응답이 비어있음");
                }
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                System.err.println("HTTP 오류: " + e.getStatusCode() + " / " + e.getResponseBodyAsString());
                throw new RuntimeException("네이버 토큰 재발급 실패", e);
            } catch (RestClientException e) {
                throw new RuntimeException("네이버 서버 호출 실패", e);
            }
            if (tokenResponse.getBody() == null || tokenResponse.getBody().getAccessToken() == null) {
                throw new IllegalStateException("토큰 재발급 실패: 응답이 비어있음");
            }

            String accessToken = tokenResponse.getBody().getAccessToken();

            String userInfoUrl = "http://localhost:9090/api/naver/userinfo";
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.setBearerAuth(accessToken);

            HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

            ResponseEntity<NaverLoginInfoDto> userInfoResponse = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    userInfoRequest,
                    NaverLoginInfoDto.class
            );

            if (userInfoResponse == null || userInfoResponse.getBody() == null || userInfoResponse.getBody().getResponse() == null) {
                throw new IllegalStateException("네이버 사용자 정보 응답이 비어있음");
            }
            String email = userInfoResponse.getBody().getResponse().getEmail();

// 3. Redis 저장
            RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
            redisRefreshToken.setId(UUID.randomUUID());
            redisRefreshToken.setEmail(email);
            redisRefreshToken.setRefreshToken(reissueAccessToken);
            refreshTokenRedisRepository.save(redisRefreshToken);

            return email;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("네이버 토큰 재발급 또는 사용자 정보 요청 실패", e);
        }
    }
}
