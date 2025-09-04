package com.example.zzserver.member.service;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("realRefreshTokenSevice")
public class RealRefreshTokenSevice {

    private JwtUtil jwtUtil;

    @Autowired
    private MemberRepository memberRepository;

    private StringRedisTemplate RedisTemplate;

    public RealRefreshTokenSevice(JwtUtil jwtUtil, StringRedisTemplate RedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.RedisTemplate = RedisTemplate;
    }
    public TokenResponseDTO reissueToken(String refreshToken) {
        if(!jwtUtil.isValidToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String id = jwtUtil.getEmail(refreshToken);
        String savedToken = RedisTemplate.opsForValue().get(id);
        if(savedToken == null || !savedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("저장된 토큰과 일치하지 않습니다.");
        }
        // CustomUserInfoDto는 실제로 DB 등에서 조회 필요


        Optional<Members> memberOpt = memberRepository.findMemberByEmail(id);
        Members member = memberOpt.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        CustomUserInfoDto customUserInfoDto = new CustomUserInfoDto(member);
        TokenResponseDTO tokenResponseDTO = jwtUtil.createAccessToken(customUserInfoDto);
        RedisTemplate.opsForValue().set(id, tokenResponseDTO.getRefresh_token());
        return tokenResponseDTO;

    }
}
