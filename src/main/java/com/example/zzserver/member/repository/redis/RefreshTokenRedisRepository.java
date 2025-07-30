package com.example.zzserver.member.repository.redis;

import com.example.zzserver.member.entity.RedisRefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRedisRepository  extends CrudRepository<RedisRefreshToken, UUID> {
    Optional<RedisRefreshToken> findByEmail(String email);
    Optional<RedisRefreshToken> findMemberByEmail(String email);

}
