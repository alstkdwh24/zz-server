package com.example.zzserver;

import com.example.zzserver.config.JpaConfig;
import com.example.zzserver.config.RedisConfig;
import com.example.zzserver.member.dto.response.NaverLoginDto;
import com.example.zzserver.member.dto.response.NaverLoginInfoDto;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import com.example.zzserver.member.service.MemberService;
import com.example.zzserver.member.service.NaverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import({TestRestTemplateConfig.class,JpaConfig.class, RedisConfig.class})
@AutoConfigureMockMvc
@Transactional
public class NaverTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NaverService naverService;
    @Autowired

    private RefreshTokenRedisRepository refreshTokenRedisRepository;
    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void apiNaverToken() throws Exception {
        NaverLoginDto dto = new NaverLoginDto();
        dto.setAccess_token("testAccessToken");
        dto.setRefresh_token("testRefreshToken");
        dto.setExpires_in("3600");
        dto.setToken_type("Bearer");

        ResponseEntity<NaverLoginDto> response = new ResponseEntity<>(dto, HttpStatus.OK);

        Mockito.when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(NaverLoginDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/naver/token").param("grant_type", "authorization_code")
                        .param("code", "testCode")
                        .param("state", "testState"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
                        responseFields(
                                fieldWithPath("access_token").type(JsonFieldType.STRING).description("네이버 액세스 토큰"),
                                fieldWithPath("refresh_token").type(JsonFieldType.STRING).description("네이버 리프레시 토큰"),
                                fieldWithPath("expires_in").type(JsonFieldType.STRING).description("토큰 만료 시간"),
                                fieldWithPath("token_type").type(JsonFieldType.STRING).description("토큰 타입")
                        )));
    }

    @Test
    public void apiNaverUserInfo() throws Exception {
        String testAccessToken = "testAccessToken";
        String testRefreshToken = "testRefreshToken"; // 추가
        NaverLoginInfoDto dto = new NaverLoginInfoDto();
        NaverLoginInfoDto.NaverUser user = new NaverLoginInfoDto.NaverUser(
                "123", "홍길동", "20-29", "male", "hong@example.com", "홍길동", "01-01", "1990", "010-1234-5678"
        );
        dto.setResponse(user);

        ResponseEntity<NaverLoginInfoDto> response = new ResponseEntity<>(dto, HttpStatus.OK);

        Mockito.when(restTemplate.exchange(anyString(), any(), any(), eq(NaverLoginInfoDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/naver/userInfo")
                        .sessionAttr("access_token", testAccessToken)
                .sessionAttr("refresh_token", testRefreshToken) // 이 부분 추가
)

                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
                        responseFields(
                                fieldWithPath("resultcode").type(JsonFieldType.STRING).optional().description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).optional().description("응답 메시지"),
                                fieldWithPath("response.id").type(JsonFieldType.STRING).description("네이버 사용자 ID"),
                                fieldWithPath("response.nickname").type(JsonFieldType.STRING).description("네이버 사용자 닉네임"),
                                fieldWithPath("response.email").type(JsonFieldType.STRING).description("네이버 사용자 이메일"),
                                fieldWithPath("response.gender").type(JsonFieldType.STRING).description("네이버 사용자 성별"),
                                fieldWithPath("response.age").type(JsonFieldType.STRING).description("네이버 사용자 연령대"),
                                fieldWithPath("response.name").type(JsonFieldType.STRING).optional().description("네이버 사용자 이름"),
                                fieldWithPath("response.birthday").type(JsonFieldType.STRING).optional().description("생일"),
                                fieldWithPath("response.birthyear").type(JsonFieldType.STRING).optional().description("출생년도"),
                                fieldWithPath("response.mobile").type(JsonFieldType.STRING).optional().description("휴대폰 번호"),
                                fieldWithPath("response.profile_image").type(JsonFieldType.STRING).optional().description("프로필 이미지")
                        )));
    }

    @Test
    void reissueAccessToken_실제호출_및_저장확인() {
        String refreshToken ="HipisS5m0TViizV9mipROJPv6ZhWta6F4isgDCM6IeUDk9miiQeFQ5QQWfksuXWIeVFXYVUwqXoESbFHQIyW9fhjKkyipQSyfUs5MWYxiiI6fQGYDIooQOu6Ibzisp82iptGXkD3ZZ";

        String email = naverService.reissueAccessToken(refreshToken);

        Assertions.assertNotNull(email);
        boolean exists = StreamSupport.stream(refreshTokenRedisRepository.findAll().spliterator(), false)
                .anyMatch(token -> refreshToken.equals(token.getRefreshToken()));
        Assertions.assertTrue(exists, "Redis에 토큰이 저장되어야 합니다.");
    }
}