package com.example.zzserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Member;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(MockConfig.class)
@AutoConfigureMockMvc
@Transactional
class ZzServerApplicationTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        protected ObjectMapper objectMapper;

        @MockitoBean // ✅ @Autowired에서 @MockitoBean으로 변경
        private MemberService memberService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private MemberRepository memberRepository;
        private final String username = "2";

        @BeforeEach
        void setUp() {
                // 테스트용 Member 데이터 생성
                Member member = new Member();
                member.setEmail(username);
                member.setUserPw(passwordEncoder.encode("testpassword"));
                member.setName("TestUser");
                member.setNickname("TestNickname"); // ✅ nickname 추가
                memberRepository.save(member);

                // ✅ MemberService Mock 설정 추가
                TokenResponseDTO mockToken = new TokenResponseDTO(UUID.randomUUID(), "accessToken", "refreshToken");
                when(memberService.login(any(LoginRequestDto.class))).thenReturn(mockToken);
        }

        @Test
        void TestMemberLogin() throws Exception {
                LoginRequestDto dto = new LoginRequestDto();
                dto.setEmail(username);
                dto.setUserPw("testpassword");

                TokenResponseDTO jwtToken = memberService.login(dto); // JWT 토큰 반환
                Optional<Member> memberOpt = memberRepository.findMemberByEmail(dto.getEmail());
                if (memberOpt.isEmpty()) {
                        throw new UsernameNotFoundException("User not found");
                }

                Member member = memberOpt.get();

                UserDetails userDetails = User.withUsername(member.getEmail()).password(member.getUserPw())
                                .roles("USER").build();

                mockMvc.perform(post("/test/member/login")
                                .contentType("application/json")

                                .content(objectMapper.writeValueAsString(dto))

                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                                .andExpect(status().isOk())
                                .andDo(MockMvcResultHandlers.print())
                                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                                                preprocessRequest(
                                                                modifyHeaders().remove("Content-Length").remove("Host"),
                                                                prettyPrint()),
                                                preprocessResponse(modifyHeaders().remove("Content-Length")
                                                                .remove("X-Content-Type-Options")
                                                                .remove("X-XSS-Protection").remove("Cache-Control")
                                                                .remove("Pragma")
                                                                .remove("Expires").remove("X-Frame-Options"),
                                                                prettyPrint()),
                                                responseFields(
                                                                fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                                                                                .description("로그인을 하고 토큰받는 코드"))));
        }

        @Test
        public void TestMemberSignup() throws Exception {
                MemberRequestDto memberdto = new MemberRequestDto();
                memberdto.setUserPw("newpassword");
                memberdto.setEmail("alkfqj2@naver.com");
                memberdto.setName("New User");
                // memberdto.setNickname("NewUserNickname"); // ✅ DTO에 nickname 필드가 있다면 추가

                Member member = new Member();
                member.setUserPw(memberdto.getUserPw());
                member.setEmail(memberdto.getEmail());
                member.setName(memberdto.getName());
                member.setNickname("NewUserNickname"); // ✅ nickname 추가

                memberRepository.findMemberByEmail(member.getEmail())
                                .ifPresent(existingMember -> {
                                        throw new IllegalArgumentException("이미 존재하는 회원입니다.");
                                });

                String response = mockMvc.perform(post("/test/member/signup")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(memberdto)))
                                .andExpect(status().isOk())
                                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                                                preprocessRequest(
                                                                modifyHeaders().remove("Content-Length").remove("Host"),
                                                                prettyPrint()),
                                                preprocessResponse(modifyHeaders().remove("Content-length")
                                                                .remove("X-Contetn-Type-Options")
                                                                .remove("X-XSS-Protection").remove("Cache-Control")
                                                                .remove("Pragma").remove("Expires")
                                                                .remove("X-Frame-Options"), prettyPrint()),
                                                responseFields(
                                                                fieldWithPath("id").type(JsonFieldType.STRING)
                                                                                .description("회원가입 성공 후 생성된 회원 ID"))))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                System.out.println(response); // 응답 결과 출력
        }

        // ...existi
        @Test
        void contextLoads() {
        }

}
