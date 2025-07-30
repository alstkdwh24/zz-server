package com.example.zzserver.member.service;

import com.example.zzserver.member.entity.RedisRefreshToken;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.repository.jpa.NaverRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("naverService")
public class NaverService {


    private NaverRepository naverRepository;

    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    public NaverService(NaverRepository naverRepository,
                        RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.naverRepository = naverRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }

    public String insertRefreshToken(String refreshToken) {

        RefreshToken refreshTokenEntity = new RefreshToken(UUID.randomUUID(),refreshToken,"aslfmdqpdmqwkl@naver.com");

        System.out.println("insertRefreshToken: " + refreshTokenEntity.getRefresh_token());

        naverRepository.save(refreshTokenEntity);

        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
        redisRefreshToken.setId(UUID.randomUUID());
        redisRefreshToken.setEmail("test@naver.com");
        redisRefreshToken.setRefresh_token(refreshToken);


        refreshTokenRedisRepository.save(redisRefreshToken);
        return refreshToken;
    }
}
