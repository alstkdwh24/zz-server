package com.example.zzserver.member.restcontroller;

import com.example.zzserver.member.dto.request.NaverLoginInfoRDto;
import com.example.zzserver.member.dto.request.NaverLoginRDto;
import com.example.zzserver.member.dto.response.NaverLoginDto;
import com.example.zzserver.member.dto.response.NaverLoginInfoDto;
import com.example.zzserver.member.service.RefreshTokenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/naver")
public class NaverController {

    @Value("${naver.naverClientSecret}")
    private String naverClientSecret;

    @Value("${naver.naverClientId}")
    private String clientId;


    @Autowired
    @Qualifier("refreshTokenService")
    private RefreshTokenService refreshTokenService;


    @PostMapping("/token")
    public ResponseEntity<NaverLoginDto> getNaverToken(@ModelAttribute NaverLoginRDto dto, HttpSession session) {
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

        refreshTokenService.insertRefreshToken(
                response.getRefresh_token()
        );

        session.setAttribute("access_token",response.getAccess_token());


        System.out.println("Response: " + response);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<NaverLoginInfoDto> getUserInfo(@ModelAttribute NaverLoginInfoRDto dto, HttpSession session) {
        String accessToken = session.getAttribute("access_token").toString();
        String naverUserInfoUrl = "https://openapi.naver.com/v1/nid/me";


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // "Bearer " 자동 추가
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>( headers);

        ResponseEntity<NaverLoginInfoDto> response= restTemplate.exchange(naverUserInfoUrl, HttpMethod.GET,request, NaverLoginInfoDto.class);
        System.out.println("Response: " + response.getBody());


        return ResponseEntity.ok(response.getBody());
    }
}
