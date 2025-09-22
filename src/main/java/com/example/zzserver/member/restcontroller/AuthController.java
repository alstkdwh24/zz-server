package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.service.AuthService;
import com.example.zzserver.member.service.MemberService;
import com.example.zzserver.member.service.RealRefreshTokenSevice;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Qualifier("realRefreshTokenSevice")
    private RealRefreshTokenSevice refreshTokens;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final MemberService memberService;

    public AuthController( JwtUtil jwtUtil, AuthService authService, MemberService memberService) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<TokenResponseDTO> getMemberLogin( @Valid @RequestBody LoginRequestDto dto) {
        TokenResponseDTO tokenResponseDTO = authService.login(dto); // 반환 타입 수정

        return ResponseEntity.ok(tokenResponseDTO);
    }


    @PostMapping("/refresh")
    public TokenResponseDTO refreshToken(@RequestBody TokenResponseDTO refreshTokens) {
        String refreshToken = refreshTokens.getRefresh_token();
        TokenResponseDTO tokenResponse = jwtUtil.refreshBothTokens(refreshToken);
        return tokenResponse;
    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/MemberUserInfo")
    public ResponseEntity<?> getUserInfo(@RequestParam("access_token") String accessToken,
                                         @RequestParam("refresh_token") String refreshToken, @RequestParam("id") UUID id) {

        try {
            ResponseEntity<?> response = memberService.getRedisMemberById(id, accessToken, refreshToken);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
