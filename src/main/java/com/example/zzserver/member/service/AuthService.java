package com.example.zzserver.member.service;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    //로그인
    private final MemberRepository memberRepository;
    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil ;
    private final BCryptPasswordEncoder encoder;
    private final RedisService redisService;
    private final ModelMapper modelMapper;

    public AuthService(MemberRepository memberRepository, RefreshRepository refreshRepository, JwtUtil jwtUtil, BCryptPasswordEncoder encoder, RedisService redisService, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.refreshRepository = refreshRepository;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
        this.redisService = redisService;
        this.modelMapper = modelMapper;
    }

    public TokenResponseDTO login(LoginRequestDto dto) {
        String email = dto.getEmail();
        String userPw = dto.getUserPw();
        // jpa
        Optional<Members> member = memberRepository.findMemberByEmail(email);
        // //redis
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        if (!encoder.matches(userPw, member.get().getUserPw())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        dto.setId( member.get().getId());


        CustomUserInfoDto info = modelMapper.map(member.get(), CustomUserInfoDto.class);

        TokenResponseDTO tokenResponse = jwtUtil.createAccessToken(info);
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(), member.get().getEmail(),
                tokenResponse.getRefresh_token());
        refreshRepository.save(refreshToken);

        String RedisAcess_token = tokenResponse.getAccess_token();
        String RedisRefresh_token = tokenResponse.getRefresh_token();
        RedisRefreshToken RedisUuid = redisService.RedisLoginSave(RedisAcess_token, RedisRefresh_token);
        String id = RedisUuid.getId();
        UUID uuid = UUID.fromString(id);
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setId(uuid);
        tokenResponseDTO.setAccess_token(RedisUuid.getAccessToken());
        tokenResponseDTO.setRefresh_token(RedisUuid.getRefreshToken());
        return tokenResponseDTO;
    }
    public UUID getUserIdFromAccessToken(String accessToken) {
        UUID id = jwtUtil.getUserIdFromAccessToken(accessToken);
        Optional<Members> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return member.get().getId();
        } else {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }
}
