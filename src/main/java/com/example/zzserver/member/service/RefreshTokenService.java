package com.example.zzserver.member.service;

import com.example.zzserver.member.entity.RedisRefreshToken;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service("refreshTokenService")
public class RefreshTokenService {
    private final RefreshRepository refreshRepository;
    private final ModelMapper modelMapper;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public RefreshTokenService(RefreshRepository refreshRepository, ModelMapper modelMapper, RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.refreshRepository = refreshRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.modelMapper = new ModelMapper();
    }
    public int insertRefreshToken(String refreshToken) {

        RefreshToken newToken = new RefreshToken();
        newToken.setRefresh_token(refreshToken);


        System.out.println("RefreshTokenService: Inserting new refresh token: " + newToken.getRefresh_token());
        refreshRepository.save(newToken);

        RedisRefreshToken redisRefreshToken = new RedisRefreshToken();
        redisRefreshToken.setId(newToken.getId());
        redisRefreshToken.setEmail("test@naver.com");
        redisRefreshToken.setRefresh_token(refreshToken);
        System.out.println("RefreshTokenService: Saving to Redis: " + redisRefreshToken.getRefresh_token());
       refreshTokenRedisRepository.save(redisRefreshToken);
        return 1; // Token inserted successfully

    };
}
