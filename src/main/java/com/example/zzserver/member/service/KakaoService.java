package com.example.zzserver.member.service;

import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.config.message.Messages;
import com.example.zzserver.member.dto.response.KakaoTokenDto;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
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
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApi;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public KakaoService(RefreshRepository refreshRepository,
                        RefreshTokenRedisRepository refreshTokenRedisRepository, OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        this.refreshRepository = refreshRepository;
        ;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }

    public String callKakaoApi(String principalName) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("kakao", principalName);
        String accessToken = client.getAccessToken().getTokenValue();

        // WebClient/RestTemplate로 API 호출
        return accessToken;
    }

    //카카오 토큰 받기
    public RedisRefreshToken KakaoLogin_two(String code) {
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
            KakaoTokenDto response = restTemplate.postForObject(url, requestEntity, KakaoTokenDto.class);
            RefreshToken save = this.kakaoTokens(response);
            String id = String.valueOf(save.getId());
            return this.redisRefreshToken(id, save); // Redis에 저장된 토큰 반환

        } catch (Exception e) {
            System.err.println(" 요청이 너무 많습니다. " + e.getMessage());
            return null; // 에러 발생 시 500 상태 코드 반환
        }

    }

    //레디스에 카카오 토큰 저장
    private RedisRefreshToken redisRefreshToken(String id, RefreshToken save) {
        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
        redisRefreshToken.setId(id);
        redisRefreshToken.setRefreshToken(save.getRefresh_token());

        refreshTokenRedisRepository.save(redisRefreshToken);
        return redisRefreshToken;
    }

    //카카오 리프레시 토큰 저장
    private RefreshToken kakaoTokens(KakaoTokenDto response) {
        String refresh_token = response.getRefresh_token();
        RefreshToken newToken = new RefreshToken();
        newToken.setRefresh_token(refresh_token);
        return refreshRepository.save(newToken);

    }

    //유저 정보
    public String userInfo(TokenResponseDTO dto) {
        UUID id = dto.getId();
        String accessToken = dto.getAccess_token();
        String refreshToken = dto.getRefresh_token();
        RedisRefreshToken redisRefreshToken = this.RedisInsertSearch(id);
        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            if (accessToken == null || accessToken.isBlank()) {
                return httpPost(refreshToken);
            }
            return httpRequestPost(accessToken);
        }
        return null;
    }

    //사용자 정보 받는 것
    private String httpRequestPost(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken); // "Bearer " 자동 추가, 직접 붙이지 마세요
        Map<String, Object> body = new HashMap<>();
        body.put("property_keys", List.of("kakao_account.email", "kakao_account.profile"));

        String url = "https://kapi.kakao.com/v2/user/me";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String response = String.valueOf(restTemplate.postForEntity(url, request, String.class));
        return response;
    }

    //토큰 재발급
    private String httpPost(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setRefresh_token(refreshToken);
        TokenResponseDTO responseEntity = restTemplate.postForEntity(
                "http://localhost:9090/api/kakao/reGetToken?refresh_token=" + refreshToken, tokenResponseDTO,
                TokenResponseDTO.class).getBody();
        assert responseEntity != null;
        return responseEntity.getRefresh_token();
    }


    //사용자 정보 찾기

    public RedisRefreshToken RedisInsertSearch(UUID id) {

        return refreshTokenRedisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰을 찾을 수 없습니다."));

    }

    //토큰 재발급을 controller로 요청
    public TokenResponseDTO reGetToken(String refreshToken) {

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
            return new TokenResponseDTO(null, response.getBody().getAccess_token(),
                    response.getBody().getRefresh_token(), Messages.TOKEN_CREATED);
        } catch (HttpClientErrorException e) {
            return new TokenResponseDTO(null, null, null, Messages.BAD_REQUEST);
        }
        // 이 메서드는 단순히 리프레시 토큰을 받아서 로그를 출력하는 역할만 합니다.
        // 실제로 토큰을 재발급하는 로직은 이 메서드 외부에서 처리됩니다.
    }

    // 카카오 로그아웃
    public void kakaoLogout(TokenResponseDTO dto) {
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
            } else {
            }
        } catch (HttpClientErrorException e) {
            e.getResponseBodyAsString();
        } catch (Exception e) {
        }
    }

}

