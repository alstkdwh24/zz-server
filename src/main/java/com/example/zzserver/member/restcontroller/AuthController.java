package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.service.RealRefreshTokenSevice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Qualifier("realRefreshTokenSevice")
    private RealRefreshTokenSevice refreshToken;
    private final JwtUtil jwtUtil;

    public AuthController(RealRefreshTokenSevice refreshToken, JwtUtil jwtUtil) {
        this.refreshToken = refreshToken;
        this.jwtUtil = jwtUtil;
    }




    @PostMapping("/refresh")
    public TokenResponseDTO refreshToken(@RequestBody TokenResponseDTO refreshTokens) {
        String refreshToken = refreshTokens.getRefresh_token();
        TokenResponseDTO tokenResponse = jwtUtil.refreshBothTokens(refreshToken);
        return tokenResponse;
    }

}
