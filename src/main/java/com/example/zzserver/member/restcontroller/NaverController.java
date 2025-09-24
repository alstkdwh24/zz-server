package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.config.AppConfig;
import com.example.zzserver.member.dto.request.NaverLoginRDto;
import com.example.zzserver.member.dto.response.NaverLoginDto;
import com.example.zzserver.member.dto.response.NaverLoginInfoDto;
import com.example.zzserver.member.service.NaverService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/naver")
@Import(AppConfig.class)
public class NaverController {
    @Value("${naver.naverClientId}")
    private String clientId;

    @Value("${naver.naverClientSecret}")
    private String naverClientSecret;

    @Autowired
    @Qualifier("naverService")
    private NaverService naverService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/token")
    public ResponseEntity<NaverLoginDto> getNaverToken(@ModelAttribute NaverLoginRDto dto, HttpSession session) {
        NaverLoginDto result = naverService.getNaverToken(dto, session);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<NaverLoginInfoDto> getUserInfo(@ModelAttribute NaverLoginRDto dto, HttpSession session) {
        NaverLoginInfoDto naverLoginDto = naverService.getNaverTokenInfo(dto, session);
        return ResponseEntity.ok(naverLoginDto);
    }

    @PostMapping("/deleteNaverToken")
    public ResponseEntity<NaverLoginDto> deleteNaverToken(@RequestParam("access_token") String accessToken) {
        NaverLoginDto result = naverService.deleteNaverToken(accessToken);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/realRefreshNaverToken")
    public ResponseEntity<NaverLoginDto> realRefreshNaverToken(@RequestParam("refresh_token") String refreshToken) {
        NaverLoginDto result = naverService.realRefreshNaverToken(refreshToken);
        return ResponseEntity.ok(result);
    }

}
