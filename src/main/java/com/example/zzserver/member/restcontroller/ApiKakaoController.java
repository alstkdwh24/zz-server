package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.response.KakaoTokenDto;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/kakao")
public class ApiKakaoController {

    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApi;

    @Autowired
    @Qualifier("refreshTokenService")
    private RefreshTokenService refreshTokenService;


    //토큰 받는 코드
    @PostMapping("/login_two")
    public ResponseEntity<RedisRefreshToken> kakaoGetToken(@RequestParam("code") String code) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoLoginRestApi);  // 카카오 개발자 센터에 등록된 값
        body.add("redirect_uri", "http://localhost:9090/main");  // 카카오 개발자 센터에 등록된 값
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KakaoTokenDto> response = restTemplate.postForEntity(url, requestEntity, KakaoTokenDto.class);
            System.out.println("Request Body: " + response.getBody());
            String access_token = response.getBody().getAccess_token();
            String refresh_token = response.getBody().getRefresh_token();
            RedisRefreshToken redisRefreshToken = refreshTokenService.insertRefreshToken(refresh_token, access_token);


            return ResponseEntity.ok(redisRefreshToken); // Redis에 저장된 토큰 반환


        } catch (Exception e) {
            System.err.println(" 요청이 너무 많습니다. " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); // 에러 발생 시 500 상태 코드 반환
        }
    }


    // 사용자 정보 받는 코드
    @PostMapping("/userinfo")
    public ResponseEntity<String> kakaoGetUserInfo(@RequestBody TokenResponseDTO dto) {
        UUID id = dto.getId();
        String accessToken = dto.getAccess_token();
        String refreshToken = dto.getRefresh_token();
        System.out.println("메서드 진입: " + accessToken);
        System.out.println("리프레시 토큰: " + refreshToken);

        RedisRefreshToken redisRefreshToken = refreshTokenService.RedisInsertSearch(id);
        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            if (accessToken == null || accessToken.isBlank()) {
                RestTemplate restTemplate = new RestTemplate();
                TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                tokenResponseDTO.setRefresh_token(refreshToken);
                System.out.println("리프레시 토큰: " + refreshToken);
                ResponseEntity<TokenResponseDTO> responseEntity = restTemplate.postForEntity("http://localhost:9090/api/kakao/reGetToken?refresh_token=" + refreshToken, tokenResponseDTO, TokenResponseDTO.class);
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
                System.out.println("Response Body: " + response.getBody());
                return ResponseEntity.ok(response.getBody());
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 401) {
                    refreshTokenService.reissueAccessToken(refreshToken);
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


    @PostMapping("/reGetToken")
    ResponseEntity<TokenResponseDTO> againGetToken(
            @RequestParam("refresh_token") String refreshToken
    ) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String url = "https://kauth.kakao.com/oauth/token";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoLoginRestApi);  // 카카오 개발자 센터에 등록된 값
        body.add("refresh_token", refreshToken);// 실제 리프레시 토큰으로 교체해야 합니다.
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenDto> response;
        try {
            response = restTemplate.postForEntity(url, requestEntity, KakaoTokenDto.class);
            return ResponseEntity.ok(new TokenResponseDTO(null, response.getBody().getAccess_token(), response.getBody().getRefresh_token()));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new TokenResponseDTO(null, null, null));
        }
    }
    // 카카오 로그아웃
    // 카카오 로그아웃은 access_token을 사용하여 로그아웃 요청을 보내는 방식으로 이루어집니다.
    // 로그아웃 후에는 해당 access_token이 더 이상 유효하지 않게 됩니다.
    // 따라서 로그아웃 후에는 클라이언트 측에서 access_token을 삭제하거나 갱신해야 합니다.
    @PostMapping("/kakaoLogout")
    public ResponseEntity<String> kakaoLogout(@RequestBody TokenResponseDTO dto) {
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
