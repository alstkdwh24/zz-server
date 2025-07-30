package com.example.zzserver;

import com.example.zzserver.config.JpaConfig;
import com.example.zzserver.config.RedisConfig;
import com.example.zzserver.member.entity.RedisRefreshToken;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import({MockConfig.class, JpaConfig.class, RedisConfig.class})
@AutoConfigureMockMvc

public class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    void 리프레시토큰_저장_조회() {
        RedisRefreshToken token = new RedisRefreshToken(UUID.randomUUID(), "vqvqevqwqwvqwqwqwvqwqvwqvw", "test@email.com");
        refreshTokenRedisRepository.save(token);

        Set<String> keys = redisTemplate.keys("refreshToken*");
        System.out.println("Redis 저장된 키들: " + keys);

        Optional<RedisRefreshToken> found = refreshTokenRedisRepository.findByEmail("test@email.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@email.com");
    }
}