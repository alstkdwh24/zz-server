package com.example.zzserver.member.restcontroller;

import com.example.zzserver.member.dto.request.NaverLoginRDto;
import com.example.zzserver.member.dto.response.NaverLoginDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/naver")
public class NaverController {

    @Value("${naver.naverClientSecret}")
    private String naverClientSecret;

    @Value("${naver.naverClientId}")
    private String clientId;

    @PostMapping("/token")
    public ResponseEntity<NaverLoginDto> getNaverToken(@ModelAttribute NaverLoginRDto dto) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", dto.getGrant_type());
        params.add("client_id", clientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", dto.getCode());
        params.add("state", dto.getState());
        params.add("redirect_uri", "http://localhost:9090/main");  // 네이버 개발자센터에 등록된 값과 동일해야 합니다

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverLoginDto> responseEntity = restTemplate.postForEntity(tokenUrl, request, NaverLoginDto.class);

        NaverLoginDto response = responseEntity.getBody();
        System.out.println("Response: " + response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<NaverLoginDto> getUserInfo(@ModelAttribute NaverLoginRDto dto) {return null;}
}
