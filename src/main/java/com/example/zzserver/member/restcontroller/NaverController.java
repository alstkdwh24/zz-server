package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.AppConfig;
import com.example.zzserver.member.dto.request.NaverLoginRDto;
import com.example.zzserver.member.dto.response.NaverLoginDto;
import com.example.zzserver.member.dto.response.NaverLoginInfoDto;
import com.example.zzserver.member.service.NaverService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/naver")
@Import(AppConfig.class)
public class NaverController {

    @Value("${naver.naverClientSecret}")
    private String naverClientSecret;

    @Value("${naver.naverClientId}")
    private String clientId;

    @Autowired
    @Qualifier("naverService")
    private NaverService naverService;

    @Autowired
    private RestTemplate restTemplate;

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

        ResponseEntity<NaverLoginDto> responseEntity = restTemplate.postForEntity(tokenUrl, request, NaverLoginDto.class);

        NaverLoginDto response = responseEntity.getBody();
        System.out.println("Response: " + response);

        session.setAttribute("access_token", response.getAccess_token());
        session.setAttribute("refresh_token", response.getRefresh_token());

        naverService.insertRefreshToken(response.getRefresh_token());


        return ResponseEntity.ok(response);
    }


    @GetMapping("/userInfo")
    public ResponseEntity<NaverLoginInfoDto> getUserInfo(@ModelAttribute NaverLoginRDto dto,  HttpSession session) {

        Object accessTokenObj = session.getAttribute("access_token");
        Object refreshTokenObj = session.getAttribute("refresh_token");

        if (accessTokenObj == null || refreshTokenObj == null) {
            throw new IllegalStateException("세션에 access_token 또는 refresh_token이 없습니다.");
        }

        String accessToken = accessTokenObj.toString();
        String refreshToken = refreshTokenObj.toString();
          String naverUserInfoUrl = "https://openapi.naver.com/v1/nid/me";


          HttpHeaders headers = new HttpHeaders();
          headers.setBearerAuth(accessToken); // "Bearer " 자동 추가
          headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

          HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
          ResponseEntity<NaverLoginInfoDto> response = restTemplate.exchange(naverUserInfoUrl, HttpMethod.GET, request, NaverLoginInfoDto.class);
          System.out.println("Response: " + response.getBody());
          return ResponseEntity.ok(response.getBody());

      } catch (HttpClientErrorException e) {

          if(e.getStatusCode().value() == 401){
              naverService.reissueAccessToken(refreshToken);

          }
          throw new RuntimeException(e);
      }

    }
}
