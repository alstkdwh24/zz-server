package com.example.zzserver;

import com.example.zzserver.config.JpaConfig;
import com.example.zzserver.config.RedisConfig;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.service.RealRefreshTokenSevice;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import({TestRestTemplateConfig.class, JpaConfig.class, RedisConfig.class})
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private  RealRefreshTokenSevice refreshToken;



    @Test
    void refreshToken_문서생성() throws Exception {
        Mockito.when(refreshToken.reissueToken(anyString()))
                .thenReturn(new TokenResponseDTO(null,"access", "refresh"));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"validRefreshToken\""))
                .andExpect(status().isOk())
                .andDo(document("auth-refresh"));
    }
}
