package com.example.zzserver;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.zzserver.config.JpaConfig;
import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.RedisConfig;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.service.RealRefreshTokenSevice;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import({ TestRestTemplateConfig.class, JpaConfig.class, RedisConfig.class })
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired // ✅ 추가
    private ObjectMapper objectMapper;

    @MockitoBean // ✅ @Autowired에서 @MockitoBean으로 변경
    private RealRefreshTokenSevice refreshToken;

    @MockitoBean // ✅ @Autowired에서 @MockitoBean으로 변경
    private JwtUtil jwtUtil;

    @Test
    void refreshToken_문서생성() throws Exception {
        // Mock 설정: JwtUtil의 refreshBothTokens 메서드가 정상적으로 TokenResponseDTO를 반환하도록 설정
        Mockito.when(jwtUtil.refreshBothTokens(anyString()))
                .thenReturn(new TokenResponseDTO(null, "access", "refresh"));

        String requestJson = "{\"refreshToken\":\"validRefreshToken\"}";

        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("auth-refresh"));
    }
}
