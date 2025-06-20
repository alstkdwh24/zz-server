package com.example.zzserver.member.restcontroller;

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
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/kakao")
public class KakaoController {

    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApi;




    @PostMapping("/login_two")
    public ResponseEntity<String> kakaoGetCode(@RequestParam("code") String code) {

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

}
