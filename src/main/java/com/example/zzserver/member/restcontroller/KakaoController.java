package com.example.zzserver.member.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.service.KakaoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/kakao")
public class KakaoController {

    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApi;

    @Autowired
    @Qualifier("KakaoService")
    private KakaoService kakaoService;

    // 토큰 받는 코드
    @PostMapping("/login_two")
    public ResponseEntity<RedisRefreshToken> login_two(@RequestParam("code") String code) {
        ResponseEntity<RedisRefreshToken> redisRefreshToken = kakaoService.KakaoLogin_two(code);
        return redisRefreshToken;

    }

    // 사용자 정보 받는 코드
    @PostMapping("/userinfo")
    public ResponseEntity<String> userInfo(@RequestBody TokenResponseDTO dto) {
        ResponseEntity<String> userInfo = kakaoService.userInfo(dto);

        return userInfo;
    }

    @PostMapping("/reGetToken")
    ResponseEntity<TokenResponseDTO> reGetToken(@RequestParam("refresh_token") String refreshToken) {

        ResponseEntity<TokenResponseDTO> reGetTokenToken = kakaoService.reGetToken(refreshToken);
        return reGetTokenToken;

    }

    // 카카오 로그아웃
    // 카카오 로그아웃은 access_token을 사용하여 로그아웃 요청을 보내는 방식으로 이루어집니다.
    // 로그아웃 후에는 해당 access_token이 더 이상 유효하지 않게 됩니다.
    // 따라서 로그아웃 후에는 클라이언트 측에서 access_token을 삭제하거나 갱신해야 합니다.
    @PostMapping("/kakaoLogout")
    public ResponseEntity<String> kakaoLogout(@RequestBody TokenResponseDTO dto) {
        kakaoService.kakaoLogout(dto);
        return ResponseEntity.ok("로그아웃 되었습니다.");

    }
}
