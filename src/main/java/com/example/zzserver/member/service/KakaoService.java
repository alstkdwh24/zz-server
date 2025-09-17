package com.example.zzserver.member.service;

import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.KakaoRefreshTokenDto;
import com.example.zzserver.member.dto.response.KakaoRefreshTokenResDto;
import com.example.zzserver.member.dto.response.KakaoTokenDto;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("KakaoService")
public class KakaoService {
    private final RefreshRepository refreshRepository;
    private final ModelMapper modelMapper;

    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApi;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public KakaoService(RefreshRepository refreshRepository, ModelMapper modelMapper, RestTemplate restTemplate,
            RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.refreshRepository = refreshRepository;
        ;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.modelMapper = new ModelMapper();
    }

    public ResponseEntity<RedisRefreshToken> KakaoLogin_two(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoLoginRestApi); // 카카오 개발자 센터에 등록된 값
        body.add("redirect_uri", "http://localhost:9090/main"); // 카카오 개발자 센터에 등록된 값
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KakaoTokenDto> response = restTemplate.postForEntity(url, requestEntity,
                    KakaoTokenDto.class);
            String access_token = response.getBody().getAccess_token();
            String refresh_token = response.getBody().getRefresh_token();
            RefreshToken newToken = new RefreshToken();
            newToken.setRefresh_token(refresh_token);

            RefreshToken save = refreshRepository.save(newToken);
            String id = String.valueOf(save.getId());

            RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
            redisRefreshToken.setId(id);
            redisRefreshToken.setAccessToken(refresh_token);
            redisRefreshToken.setRefreshToken(access_token);

            refreshTokenRedisRepository.save(redisRefreshToken);

            return ResponseEntity.ok(redisRefreshToken); // Redis에 저장된 토큰 반환

        } catch (Exception e) {
            System.err.println(" 요청이 너무 많습니다. " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); // 에러 발생 시 500 상태 코드 반환
        }

    }

    public ResponseEntity<String> userInfo(TokenResponseDTO dto) {
        UUID id = dto.getId();
        String accessToken = dto.getAccess_token();
        String refreshToken = dto.getRefresh_token();


        RedisRefreshToken redisRefreshToken = this.RedisInsertSearch(id);
        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            if (accessToken == null || accessToken.isBlank()) {
                RestTemplate restTemplate = new RestTemplate();
                TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                tokenResponseDTO.setRefresh_token(refreshToken);
                ResponseEntity<TokenResponseDTO> responseEntity = restTemplate.postForEntity(
                        "http://localhost:9090/api/kakao/reGetToken?refresh_token=" + refreshToken, tokenResponseDTO,
                        TokenResponseDTO.class);
                return ResponseEntity.badRequest().body("access_token is missing");
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken); // "Bearer " 자동 추가, 직접 붙이지 마세요
            Map<String, Object> body = new HashMap<>();
            body.put("property_keys", List.of("kakao_account.email", "kakao_account.profile"));

            String url = "https://kapi.kakao.com/v2/user/me";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                return ResponseEntity.ok(response.getBody());
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 401) {
                    this.reissueAccessToken(refreshToken);
                }
                System.err.println("HTTP Status Code: " + e.getStatusCode());
                System.err.println("Response Headers: " + e.getResponseHeaders());
                System.err.println("Response Body: " + e.getResponseBodyAsString());
                e.printStackTrace();
                return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
            } catch (Exception e) {
                System.err.println("예상치 못한 에러: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body("Internal Server Error");
            }
        }
        return null;

    }

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

        ResponseEntity<KakaoRefreshTokenResDto> response = restTemplate.postForEntity(url, request,
                KakaoRefreshTokenResDto.class);

        KakaoRefreshTokenResDto body = response.getBody();

        String access_token = body.getAccessToken();

        String userInfoUrl = "http://localhost:9090/api/kakao/userinfo";
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> userInfoMap = new LinkedMultiValueMap<>();
        userInfoMap.add("access_token", access_token);

        HttpEntity<MultiValueMap<String, String>> userInfoRequest = new HttpEntity<>(userInfoMap, userInfoHeaders);
        ResponseEntity<String> userInfoResponse = restTemplate.postForEntity(userInfoUrl, userInfoRequest,
                String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
        } else {
            throw new RuntimeException("카카오 토큰 재발급 실패");
        }
        // 1. 리프레시 토큰 유효성 검증
        // 2. 카카오 API 등 외부 서비스에 새 액세스 토큰 요청
        // 3. 새 액세스 토큰 반환
    }

    public RedisRefreshToken RedisInsertSearch(UUID id) {
        RedisRefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰을 찾을 수 없습니다."));

       return redisRefreshToken;

    }

    public ResponseEntity<TokenResponseDTO> reGetToken(String refreshToken) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String url = "https://kauth.kakao.com/oauth/token";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoLoginRestApi); // 카카오 개발자 센터에 등록된 값
        body.add("refresh_token", refreshToken);// 실제 리프레시 토큰으로 교체해야 합니다.
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenDto> response;
        try {
            response = restTemplate.postForEntity(url, requestEntity, KakaoTokenDto.class);
            return ResponseEntity.ok(new TokenResponseDTO(null, response.getBody().getAccess_token(),
                    response.getBody().getRefresh_token()));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new TokenResponseDTO(null, null, null));
        }
        // 이 메서드는 단순히 리프레시 토큰을 받아서 로그를 출력하는 역할만 합니다.
        // 실제로 토큰을 재발급하는 로직은 이 메서드 외부에서 처리됩니다.
    }

    // 카카오 로그아웃
    public ResponseEntity<String> kakaoLogout(TokenResponseDTO dto) {
        String accessToken = dto.getAccess_token();
        String url = "https://kapi.kakao.com/v1/user/logout";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken); // "Bearer " 자동 추가, 직접 붙이지 마se
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("Logout successful");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Logout failed");
            }
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Logout failed: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

}
