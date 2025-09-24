package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.service.KakaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/kakao")
public class KakaoController {

    @Value("${kakao.kakaoLoginRestApi}")
    private String kakaoLoginRestApis;

    @Autowired
    @Qualifier("KakaoService")
    private KakaoService kakaoService;

    // 토큰 받는 코드
    @PostMapping("/login_two")
    public ResponseEntity<RedisRefreshToken> login_two(@RequestParam("code") String code) {
        RedisRefreshToken redisRefreshToken = kakaoService.KakaoLogin_two(code);
        return ResponseEntity.ok(redisRefreshToken);

    }

    // 사용자 정보 받는 코드
    @PostMapping("/userinfo")
    public ResponseEntity<String> userInfo(@RequestBody TokenResponseDTO dto) {
        String userInfo = kakaoService.userInfo(dto);

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/reGetToken")
    ResponseEntity<TokenResponseDTO> reGetToken(@RequestParam("refresh_token") String refreshToken) {

        TokenResponseDTO reGetTokenToken = kakaoService.reGetToken(refreshToken);
        return ResponseEntity.ok(reGetTokenToken);

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
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 필요"));
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", principal.getAttribute("id"));
        info.put("name", principal.getAttribute("nickname"));
        info.put("email", principal.getAttribute("email"));

        return ResponseEntity.ok(info);
    }
    //OAuth2 인증 코드 받는 코드
    @GetMapping("/user")
    public String kakaoLogin(@RequestParam String code) {
        // code → 카카오 토큰 → JWT 발급
        return kakaoService.callKakaoApi(code);
    }
}
