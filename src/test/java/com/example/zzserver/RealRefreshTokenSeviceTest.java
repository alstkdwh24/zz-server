package com.example.zzserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.example.zzserver.config.JpaConfig;
import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.RedisConfig;
import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.entity.Member;
import com.example.zzserver.member.entity.Role;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.service.RealRefreshTokenSevice;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import({ TestRestTemplateConfig.class, JpaConfig.class, RedisConfig.class })
@AutoConfigureMockMvc
@Transactional
public class RealRefreshTokenSeviceTest {

    private JwtUtil jwtUtil;
    private MemberRepository memberRepository;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;
    @MockitoBean // ✅ 새로 권장되는 애노테이션

    private RealRefreshTokenSevice realRefreshTokenSevice;

    @BeforeEach
    public void setUp() {
        jwtUtil = mock(JwtUtil.class);
        memberRepository = mock(MemberRepository.class);
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        realRefreshTokenSevice = new RealRefreshTokenSevice(jwtUtil, redisTemplate);

        java.lang.reflect.Field field = null;
        try {
            field = RealRefreshTokenSevice.class.getDeclaredField("memberRepository");
            field.setAccessible(true);
            field.set(realRefreshTokenSevice, memberRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void reissueToken_seccess() {
        String refreshToken = "validRefreshToken";
        String userId = "testUserId";
        Member member = new Member();
        member.setRole(Role.USER); // Role.USER 등 실제 enum 값으로 세팅
        member.setEmail(userId); // 이메일을 userId로 설정

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(null, "access", "refresh"); // 수정

        when(jwtUtil.isValidToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserId(refreshToken)).thenReturn(userId);
        when(valueOps.get(userId)).thenReturn(refreshToken);
        when(memberRepository.findMemberByEmail(userId)).thenReturn(java.util.Optional.of(member));
        when(jwtUtil.createAccessToken(any(CustomUserInfoDto.class))).thenReturn(tokenResponseDTO);
        TokenResponseDTO tokenResponseDTO1 = realRefreshTokenSevice.reissueToken(refreshToken);
        // Assertions to verify the behavior

        assertEquals("access", tokenResponseDTO1.getAccess_token());
        assertEquals("refresh", tokenResponseDTO1.getRefresh_token());
        verify(valueOps).set(userId, "refresh"); // 여기서 "refresh"로 수정
    }

    @Test
    void reissueToken_inValid() {
        String refreshToken = "validRefreshToken";
        when(jwtUtil.isValidToken(refreshToken)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            realRefreshTokenSevice.reissueToken(refreshToken);
        });
    }

    @Test
    void reissueToken_notMatch() {
        String refreshToken = "valid";
        String userId = "user@example.com";

        when(jwtUtil.isValidToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserId(refreshToken)).thenReturn(userId);
        when(valueOps.get(userId)).thenReturn("differentRefreshToken");

        assertThrows(IllegalArgumentException.class, () -> {
            realRefreshTokenSevice.reissueToken(refreshToken);
        });
    }

    @Test
    void reissueToken_notExist() {
        String refreshToken = "validRefreshToken";
        String userId = "user@example.com";

        when(jwtUtil.isValidToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserId(refreshToken)).thenReturn(userId);
        when(valueOps.get(userId)).thenReturn(null);
        when(memberRepository.findMemberByEmail(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            realRefreshTokenSevice.reissueToken(refreshToken);
        });
    }
}
