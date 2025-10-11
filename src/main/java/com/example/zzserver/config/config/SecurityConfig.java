package com.example.zzserver.config.config;


import com.example.zzserver.config.handler.CustomAccessDeniedHandler;
import com.example.zzserver.config.handler.CustomAuthenticationEntryPoint;
import com.example.zzserver.config.handler.OAuth2LoginSuccessHandler;
import com.example.zzserver.config.jwt.JwtAuthFilter;
import com.example.zzserver.config.jwt.JwtUtil;
import com.example.zzserver.member.service.CustomUserDetailsService;
import com.example.zzserver.config.service.OAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final OAuth2UserService oAuth2UserService;

    private static final String[] AUTH_WHITELIST = {"/**", "/member/login", "/member/signup",
            "/swagger-ui/**", "/api-docs", "swagger-ui-custom.html", "**/h2-console/**", "/api/**"
,"/api/kakao/**"  ,"/member/logout","/login/login"  };



    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil, CustomAccessDeniedHandler customAccessDeniedHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, OAuth2UserService oAuth2UserService) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           OAuth2LoginSuccessHandler successHandler) throws Exception {
        //CSRF, CORS 설정

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors((Customizer.withDefaults()));

        // h2-console 에서 사용하는 X-FRAME-OPTIONS 허용
        http.headers(headers -> headers

            .frameOptions(FrameOptionsConfig::sameOrigin)

        );
        //추가로 설정한 OAuth2에 관한 것
        http.oauth2Login(oauth -> oauth
                .loginPage("/login/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                .successHandler(successHandler));
        //세션 관리 상태 없음으로 구성, Spring Security가 세션을 생성하지 않도록 설정
        http.sessionManagement(sessionMangement -> sessionMangement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
        ));

        //FOrmLogin, BasicHttp 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter
                .class);

        //권한 규칙 작성
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(AUTH_WHITELIST).permitAll()


                //@PreAuthorization 사용 -> 모든 경로에 대한 인증처리는 pass
                .anyRequest().permitAll()
        );
            log.debug("httpsss"+http.toString());
        return http.build();

    }

}
