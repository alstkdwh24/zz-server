package com.example.zzserver.config.handler;

import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.config.jwt.JwtUtil;
import com.example.zzserver.member.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        System.out.println("OAuth2LoginSuccessHandler.onAuthenticationSuccess");
        logger.debug("123456890");
        SecurityContextHolder.getContext().setAuthentication(authentication); // ✅ 반드시 저장

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        logger.debug("123456890");
        String email = extractEmail(oAuth2User);
        CustomUserInfoDto userInfoDto = new CustomUserInfoDto();
        userInfoDto.setEmail(email);
        TokenResponseDTO tokenResponse = jwtUtil.createAccessToken(userInfoDto);

        refreshTokenService.insertRefreshToken(
                tokenResponse.getRefresh_token(),
                tokenResponse.getAccess_token()
        );

// Access Token 쿠키 추가
        response.addCookie(createCookie("Authorization", tokenResponse.getAccess_token()));

// Refresh Token도 쿠키로 추가 (선택사항)
        response.addCookie(createCookie("RefreshToken", tokenResponse.getRefresh_token()));

        // 중간 페이지로 리다이렉트 (토큰 없이!)
        response.sendRedirect("/mains/main");


    }

    private String extractEmail(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (attributes.get("kakao_account") != null) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        return (String) attributes.get("email");
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
