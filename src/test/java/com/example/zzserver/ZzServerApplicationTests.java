package com.example.zzserver;

import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Member;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RestTemplate restTemplate;



    private final String username = "2";

    @BeforeEach
    public void setUp() {
        LoginRequestDto dto = new LoginRequestDto();
        try {
            dto.setEmail(username);
            memberService.login(dto);
        } catch (Exception e) {
            Member member = new Member();
            member.setEmail(username);
            member.setUserPw(passwordEncoder.encode("testpassword"));
            member.setEmail("testuser@example.com");
            member.setName("TestUser");
            memberRepository.save(member);
        }
    }

    @Test
    public void TestMemberLogin() throws Exception {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(username);
        dto.setUserPw("testpassword");

        TokenResponseDTO jwtToken = memberService.login(dto); // JWT 토큰 반환
        Optional<Member> memberOpt = memberRepository.findMemberByEmail(dto.getEmail());
        if (memberOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        Member member = memberOpt.get();

        UserDetails userDetails = User.withUsername(member.getEmail()).password(member.getUserPw()).roles("USER").build();


        mockMvc.perform(post("/test/member/login")
                        .contentType("application/json")

                        .content(objectMapper.writeValueAsString(dto))

                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-Length").remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma")
                                .remove("Expires").remove("X-Frame-Options"), prettyPrint()),
                        responseFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("로그인을 하고 토큰받는 코드")
                        )));
    }

    @Test
    public void TestMemberSignup() throws Exception {
        MemberRequestDto memberdto = new MemberRequestDto();

        memberdto.setUserPw("newpassword");
        memberdto.setEmail("alkfqj2@naver.com");
        memberdto.setName("New User");
        Member member = new Member();
        member.getId();
        member.setUserPw(memberdto.getUserPw());
        member.setEmail(memberdto.getEmail());
        member.setName(memberdto.getName());


        memberRepository.findMemberByEmail(member.getEmail())
                .ifPresent(existingMember -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다.");
                });

        String response = mockMvc.perform(post("/test/member/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(memberdto)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        preprocessRequest(modifyHeaders().remove("Content-Length").remove("Host"), prettyPrint()),
                        preprocessResponse(modifyHeaders().remove("Content-length").remove("X-Contetn-Type-Options")
                                .remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma").remove("Expires").remove("X-Frame-Options"), prettyPrint()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("회원가입 성공 후 생성된 회원 ID")
                        )))
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(response); // 응답 결과 출력


    }


    @Test
    void contextLoads() {
    }

}
