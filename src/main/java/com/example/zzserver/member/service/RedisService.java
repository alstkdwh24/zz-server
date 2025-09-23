package com.example.zzserver.member.service;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<Object, Object> redisTemplate2;
    private final MemberRepository memberRepository;
    public RedisService(RefreshTokenRedisRepository refreshTokenRedisRepository, JwtUtil jwtUtil, RedisTemplate<Object, Object> redisTemplate2, MemberRepository memberRepository) {
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.jwtUtil = jwtUtil;
        this.redisTemplate2 = redisTemplate2;
        this.memberRepository = memberRepository;
    }

    public RedisRefreshToken RedisLoginSave(String accessToken, String refreshToken) {
        RedisRefreshToken refreshTokenEntity = new RedisRefreshToken();
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setAccessToken(accessToken);
        return refreshTokenRedisRepository.save(refreshTokenEntity);
    }


    //유저 삭제
    public void deleteMember(String token) {
        UUID id = jwtUtil.getUserIdFromAccessToken(token);
        redisTemplate2.delete("refresh_token:" + id);

    logger.debug("ssssss" +String.valueOf(id));
    //MemberService 부분으로 옳기기

    }
//
 //수정ResponseEntity수정
    public ResponseEntity<Map<String, String>> getLogout(UUID id, String token) {
        Map<String, String> response = new HashMap<>();
        try {//try 수정
            String jwtToken = token.replace("Bearer ", "");

            redisTemplate2.delete("refreshToken:" + id);

            try {
                UUID userId = jwtUtil.getUserIdFromAccessToken(jwtToken);
                redisTemplate2.delete("refresh_token:" + userId);
            } catch (Exception e) {
            }
            long expiration = jwtUtil.getExpirationFromToken(jwtToken);
            long currentTime = System.currentTimeMillis();
            long timeToLive = Math.max(expiration - currentTime, 60000); // 최소 1분은 블랙리스트에 유지

            if (timeToLive > 0) {
                redisTemplate2.opsForValue().set("blacklist:" + jwtToken, "logout", Duration.ofMillis(timeToLive));
            }
            //레디스 헤시로
            refreshTokenRedisRepository.deleteById(id);
            response.put("message", "로그아웃 완료");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "로그아웃 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
