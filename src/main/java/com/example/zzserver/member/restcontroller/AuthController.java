package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.service.AuthService;
import com.example.zzserver.member.service.RealRefreshTokenSevice;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Qualifier("realRefreshTokenSevice")
    private RealRefreshTokenSevice refreshTokens;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController( JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<TokenResponseDTO> getMemberLogin(Model model, @Valid @RequestBody LoginRequestDto dto) {
        TokenResponseDTO tokenResponseDTO = authService.login(dto); // 반환 타입 수정

        return ResponseEntity.ok(tokenResponseDTO);
    }


    @PostMapping("/refresh")
    public TokenResponseDTO refreshToken(@RequestBody TokenResponseDTO refreshTokens) {
        String refreshToken = refreshTokens.getRefresh_token();
        TokenResponseDTO tokenResponse = jwtUtil.refreshBothTokens(refreshToken);
        return tokenResponse;
    }

}
