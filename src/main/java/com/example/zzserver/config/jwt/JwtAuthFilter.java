package com.example.zzserver.config.jwt;

import com.example.zzserver.member.service.auth.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);


    public JwtAuthFilter(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }


    /**
     * JWT 검증 필터 수행
     */

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

       log.debug("request: {}",String.valueOf(request.getUserPrincipal()));
        String authorizationHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();

        log.debug("token: {}", authorizationHeader);

        //JWT 헤더가 있는 경우
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            String token = authorizationHeader.substring(7);

            if (jwtUtil.isValidToken(token)) {
                String emailString = jwtUtil.getEmail(token);
                System.out.println("emailString = " + emailString);

                if (emailString != null && !emailString.isEmpty()) {
                    try {
                        // 토큰에서 유저와 토큰이 일치시에 userDetails 생성
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(emailString);
                        if (userDetails != null) {
                            // UserDetails, Password, Role -> 접근 권한 인증 토큰을 생성
                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    } catch (NumberFormatException e) {
                        logger.error("Invalid userId format in token: " + emailString, e);
                    }
                } else {
                    logger.error("UserId is null or empty in token");
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
