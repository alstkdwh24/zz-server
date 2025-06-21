package com.example.zzserver.member.restcontroller;

import com.example.zzserver.member.dto.request.KakaoTokenRDto;
import com.example.zzserver.member.dto.response.KakaoTokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/kakao")
public class KakaoController {

    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApi;



//토큰 받는 코드
    @PostMapping("/login_two")
    public ResponseEntity<String> kakaoGetToken(@RequestParam("code") String code) {

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

        System.out.println("Request Body: " + body);

        try {
            return restTemplate.postForEntity(url, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Status Code: " + e.getStatusCode());
            System.err.println("Response Headers: " + e.getResponseHeaders());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
            e.printStackTrace();
            throw e;
        } catch (RestClientException e) {
            System.err.println("RestClientException: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
// 사용자 정보 받는 코드
    @PostMapping("/userinfo")
    public ResponseEntity<String> kakaoGetUserInfo(@ModelAttribute KakaoTokenRDto dto) {
        String accessToken = dto.getAccess_token();
        System.out.println("메서드 진입"+accessToken);
//        if (accessToken == null || accessToken.isBlank()) {
//            return ResponseEntity.badRequest().body("access_token is missing");
//        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken); // "Bearer " 자동 추가

        Map<String, Object> body = new HashMap<>();
        body.put("property_keys", List.of("kakao_account.email", "kakao_account.profile"));


        String url = "https://kapi.kakao.com/v2/user/me";

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        System.out.println(url + "wwwwww");

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("wwwwwwww"+ response);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            // 에러 응답 바디 출력
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @PostMapping("/kakaoRefreshToken")
    public ResponseEntity<KakaoTokenDto> kakaoTokenDtoResponseEntity(@ModelAttribute KakaoTokenRDto dto) {
        String refresh_token = dto.getRefresh_token();
        System.out.println("리프레시 토큰: " + dto.getRefresh_token());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);



        String url = "https://kauth.kakao.com/oauth/token";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoLoginRestApi);  // 카카오 개발자 센터에 등록된 값
        body.add("refresh_token",refresh_token );  // 실제 리프레시 토큰으로 교체해야 합니다.

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<KakaoTokenDto> response = restTemplate.postForEntity(url, requestEntity, KakaoTokenDto.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            // 에러 응답 바디 출력
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (RestClientException e) {
            System.err.println("RestClientException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
