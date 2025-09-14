package com.example.zzserver;

import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 테스트 순서 관련 어노테이션

class MemberTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @Order(1)
        public void createUser() throws Exception {
                MemberRequestDto request = new MemberRequestDto(null, "alsalsals@naver.com", "password123", "만만만",
                                Role.ROLE_USER, "닉네임");

                // 회원가입
                mockMvc.perform(post("/member/signup").contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                                .andDo(print())
                                .andDo(document("create-user",
                                                PayloadDocumentation.requestFields(
                                                                fieldWithPath("id").description("유저 id"),
                                                                fieldWithPath("email").description("유저 이메일"),
                                                                fieldWithPath("userPw").description("유저 패스워드"),
                                                                fieldWithPath("nickname").description("유저 닉네임"),
                                                                fieldWithPath("name").description("유저 이름"),
                                                                fieldWithPath("role").description("유저 권한"))));

                Thread.sleep(100);

                // 로그인
                LoginRequestDto loginRequest = new LoginRequestDto(request.getEmail(), request.getUserPw());
                String loginResponse = mockMvc
                                .perform(post("/member/login").contentType("application/json")
                                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk()).andDo(print())
                                .andDo(document("login-user",
                                                PayloadDocumentation.requestFields(
                                                                fieldWithPath("email").description("유저 이메일"),
                                                                fieldWithPath("userPw").description("유저 패스워드"))))
                                .andReturn().getResponse().getContentAsString();

                TokenResponseDTO tokenResponse = objectMapper.readValue(loginResponse, TokenResponseDTO.class);
                String accessToken = tokenResponse.getAccess_token();
                String refreshToken = tokenResponse.getRefresh_token();
                String userId = tokenResponse.getId().toString();

                mockMvc.perform(get("/member/MemberUserInfo").contentType("application/json")
                                .param("access_token", accessToken).param("refresh_token", refreshToken)
                                .param("id", userId)).andDo(print())
                                .andDo(document("get-user-info",
                                                PayloadDocumentation.responseFields(
                                                                fieldWithPath("id").description("유저 id"),
                                                                fieldWithPath("userPw").description("유저 패스워드"),
                                                                fieldWithPath("email").description("유저 이메일"),
                                                                fieldWithPath("nickname").description("유저 닉네임"),
                                                                fieldWithPath("name").description("유저 이름"),
                                                                fieldWithPath("role").description("유저 권한"))));

                // 로그아웃 테스트
                mockMvc.perform(post("/member/logout").contentType("application/json")
                                .param("access_token", accessToken).param("id", userId)).andExpect(status().isOk())
                                .andDo(print()).andDo(result -> {
                                        // 실제 응답 구조 출력
                                        String responseContent = result.getResponse().getContentAsString();
                                        System.out.println("실제 로그아웃 응답: " + responseContent);
                                }).andDo(document("logout-user", PayloadDocumentation
                                                .responseFields(fieldWithPath("message").description("로그아웃 메시지"))));
        }

}
