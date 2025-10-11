package com.example.zzserver.member.service;

import com.example.zzserver.member.dto.request.NaverLoginRDto;
import com.example.zzserver.member.dto.response.NaverLoginDto;
import com.example.zzserver.member.dto.response.NaverLoginInfoDto;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.NaverRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.HttpClientErrorException;
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
    private static final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";


    public NaverService(NaverRepository naverRepository, RefreshTokenRedisRepository refreshTokenRedisRepository,
                        RestTemplate restTemplate) {
        this.naverRepository = naverRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.restTemplate = restTemplate;
    }

    //토큰 요청
    private NaverLoginDto requestNaverToken(MultiValueMap<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.postForEntity(NAVER_TOKEN_URL, request, NaverLoginDto.class).getBody();
    }

    //조건에 따라  토큰 재발급 아니면 토큰 발급
    private MultiValueMap<String, String> createTokenRequestParams(String grantType, String code, String state, String refreshToken) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        if ("authorization_code".equals(grantType)) {
            params.add("code", code);
            params.add("state", state);
            params.add("redirect_uri", "http://localhost:9090/main");
        } else if ("refresh_token".equals(grantType)) {
            params.add("refresh_token", refreshToken);
        }
        return params;
    }

    //유저 정보 관련
    public NaverLoginInfoDto getNaverTokenInfo(@ModelAttribute NaverLoginRDto dto, HttpSession session) {
        Object accessTokenObj = session.getAttribute("access_token");
        if (accessTokenObj == null) {
            throw new IllegalStateException("세션에 access_token이 없습니다.");
        }
        String accessToken = accessTokenObj.toString();
        return getUserEmailFromNaver(accessToken);

    }

    //토큰 발급 메서드
    public NaverLoginDto getNaverToken(@ModelAttribute NaverLoginRDto dto, HttpSession session) {

        MultiValueMap<String, String> params = createTokenRequestParams(dto.getGrant_type(), dto.getCode(), dto.getState(), null);

        NaverLoginDto responseEntity = requestNaverToken(params);
        assert responseEntity != null;

        NaverLoginDto response = responseEntity;
        if (response != null) {
            session.setAttribute("access_token", response.getAccess_token());
            session.setAttribute("refresh_token", response.getRefresh_token());

            // 사용자 정보 조회 후 저장
            NaverLoginInfoDto userEmail =
                    getUserEmailFromNaver(response.getAccess_token());
            insertRefreshTokens(response.getRefresh_token(), userEmail.getResponse().getEmail());
        }

        return responseEntity;
    }

    private NaverLoginInfoDto getUserEmailFromNaver(String accessToken) {
        String naverUserInfoUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<NaverLoginInfoDto> response = restTemplate.exchange(
                    naverUserInfoUrl, HttpMethod.GET, request, NaverLoginInfoDto.class);

            NaverLoginInfoDto userInfo = response.getBody();
            if (userInfo == null || userInfo.getResponse() == null || userInfo.getResponse().getEmail() == null) {
                throw new IllegalStateException("네이버 사용자 정보를 가져올 수 없습니다");
            }

            return userInfo;
        } catch (Exception e) {
            throw new IllegalStateException("사용자 정보 없음", e);
        }
    }

//토큰 요청 로직 끝

    public void insertRefreshTokens(String refreshToken, String email) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalStateException("리프레시 토큰이 없습니다");
        }

        if (email == null || email.isEmpty()) {
            throw new IllegalStateException("사용자 정보 없음");
        }

        RefreshToken refreshTokenEntity = new RefreshToken(UUID.randomUUID(), email, refreshToken);


        naverRepository.save(refreshTokenEntity);

        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
        redisRefreshToken.setRefreshToken(refreshToken);
        redisRefreshToken.setEmail(email);

        refreshTokenRedisRepository.save(redisRefreshToken);
    }

    //유저 정보
    public NaverLoginInfoDto getUserInfo(@ModelAttribute NaverLoginRDto dto, HttpSession session) {
        Object accessTokenObj = session.getAttribute("access_token");
        Object refreshTokenObj = session.getAttribute("refresh_token");
        if (accessTokenObj == null) {
            throw new IllegalStateException("세션에 access_token이 없습니다.");
        }
        String accessToken = accessTokenObj.toString();
        String refreshToken = refreshTokenObj.toString();

        String naverUserInfoUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<NaverLoginInfoDto> response = restTemplate.exchange(naverUserInfoUrl, HttpMethod.GET, request, NaverLoginInfoDto.class);
            return response.getBody();

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode().value() == 401) {
                this.reissueAccessToken(refreshToken);

            }
            throw new RuntimeException(e);
        }

    }
    //유저 정보 요직 끝

    //토큰 재발급 로직
    public void reissueAccessToken(String refreshToken) {
        try {
            //토큰 재발급 요청
            MultiValueMap<String, String> params = createTokenRequestParams("refresh_token", null, null, refreshToken);
            NaverLoginDto tokenBody = requestNaverToken(params);

            if (tokenBody == null || tokenBody.getAccess_token() == null) {
                throw new IllegalStateException("토큰 재발급 실패: 응답이 비어있음");
            }

            String userInfoUrl = "http://localhost:9090/api/naver/userInfo";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tokenBody.getAccess_token());

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<NaverLoginInfoDto> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, NaverLoginInfoDto.class);
            String email = userInfoResponse.getBody().getResponse().getEmail();


            RedisRefreshToken redisRefreshToken = this.getRedisRefreshToken(email, refreshToken);

        } catch (Exception e) {
            throw new RuntimeException("네이버 토큰 재발급 또는 사용자 정보 요청 실패", e);
        }

    }

    //레디스에 리프레쉬 토큰 저장
    private RedisRefreshToken getRedisRefreshToken(String email, String refreshToken) {
        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
        redisRefreshToken.setId(String.valueOf(UUID.randomUUID()));
        redisRefreshToken.setRefreshToken(refreshToken);
        redisRefreshToken.setEmail(email);
        refreshTokenRedisRepository.save(redisRefreshToken);
        return redisRefreshToken;
    }

    //토큰 재발급 로직 끝
    //토큰 지우는 로직
    public NaverLoginDto deleteNaverToken(String accessToken) {
        String url = "https://nid.naver.com/oauth2.0/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "delete");
        body.add("client_id", naverClientId);  // 네이버 개발자 센터에
        body.add("client_secret", naverClientSecret); // 네이버 개발자 센터에 등록된 클라이언트 시크릿
        body.add("access_token", accessToken); // 실제 액세스 토큰으로 교de해야 합니다.

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        NaverLoginDto response = restTemplate.postForEntity(url, request, NaverLoginDto.class).getBody();
        return response;
    }

    //리프레쉬 토큰 재발급 로직
    public NaverLoginDto realRefreshNaverToken(String refreshToken) {
        String url = "https://nid.naver.com/oauth2.0/token";
        MultiValueMap<String, String> params = createTokenRequestParams("refresh_token", naverClientId, naverClientSecret, refreshToken);
        return getNaverLoginDtoResponseEntity(url, params, restTemplate);
    }

    public static NaverLoginDto getNaverLoginDtoResponseEntity(String url, MultiValueMap<String, String> params, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<NaverLoginDto> response = restTemplate.postForEntity(url, request, NaverLoginDto.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            NaverLoginDto responseBody = response.getBody();
            if (responseBody != null) {
                return responseBody;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


}
