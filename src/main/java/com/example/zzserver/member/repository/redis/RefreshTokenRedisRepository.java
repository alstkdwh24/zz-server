package com.example.zzserver.member.repository.redis;

import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository

public interface RefreshTokenRedisRepository  extends CrudRepository<RedisRefreshToken, UUID> {
    Optional<RedisRefreshToken> findByEmail(String email);
    Optional<RedisRefreshToken> findMemberByEmail(String email);

}
