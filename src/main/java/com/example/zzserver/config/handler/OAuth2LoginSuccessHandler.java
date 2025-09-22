package com.example.zzserver.config.handler;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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
                                        Authentication authentication) throws IOException{
        System.out.println("OAuth2LoginSuccessHandler.onAuthenticationSuccess");
        logger.debug("123456890");
        SecurityContextHolder.getContext().setAuthentication(authentication); // ✅ 반드시 저장

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        logger.debug("123456890");
        String email = extractEmail(oAuth2User);
        CustomUserInfoDto userInfoDto = new CustomUserInfoDto();
        userInfoDto.setEmail(email);
        TokenResponseDTO accessToken = jwtUtil.createAccessToken(userInfoDto);

        refreshTokenService.insertRefreshToken(accessToken.getRefresh_token(),accessToken.getAccess_token());

        Map<String, String> tokens = Map.of(
                "accessToken", accessToken.getAccess_token(),
                "refreshToken", accessToken.getRefresh_token()
        );
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getWriter(), tokens);

    }

    private String extractEmail(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (attributes.get("kakao_account") != null) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        return (String) attributes.get("email");
    }
}
