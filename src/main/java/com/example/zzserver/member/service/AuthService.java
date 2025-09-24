package com.example.zzserver.member.service;

import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.config.jwt.JwtUtil;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    //로그인
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    private final ModelMapper modelMapper;
    private final RedisService redisService;

    public AuthService(MemberRepository memberRepository,  JwtUtil jwtUtil, BCryptPasswordEncoder encoder, RedisService redisService, ModelMapper modelMapper, MemberService memberService, RedisService redisService1) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
        this.modelMapper = modelMapper;
        this.redisService = redisService1;
    }

    //login 메서드
    public TokenResponseDTO login(LoginRequestDto dto) {
        String email = dto.getEmail();
        String userPw = dto.getUserPw();


        Members member = memberRepository.findMemberByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        if (!encoder.matches(userPw, member.getUserPw())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        dto.setId(member.getId());

        return this.accessTokenLogin(member);
    }
  // accessToken발급 메시드
    private TokenResponseDTO accessTokenLogin(Members member) {

        CustomUserInfoDto info = this.UserInfoLogin(member);
        if (info != null) {
            TokenResponseDTO tokenResponseDTO = this.createToken( info);
            String accessToken = tokenResponseDTO.getAccess_token();
            String refreshToken = tokenResponseDTO.getRefresh_token();
            log.debug("tokenResponseDTO {}" , tokenResponseDTO);

            RedisRefreshToken RedisUuid = redisService.RedisLoginSave(accessToken, refreshToken);
            String id = RedisUuid.getId();
            UUID uuid = UUID.fromString(id);
            TokenResponseDTO tokenResponseDTOs = new TokenResponseDTO();
            tokenResponseDTOs.setId(uuid);
            tokenResponseDTOs.setAccess_token(accessToken);
            tokenResponseDTOs.setRefresh_token(RedisUuid.getRefreshToken());
        return tokenResponseDTOs;
        } else {
            throw new IllegalArgumentException("사용자 정보가 올바르지 않습니다.");
        }
    }
    //위의 과정
    private TokenResponseDTO createToken( CustomUserInfoDto info) {

        return jwtUtil.createAccessToken(info);
    }
    //사용자 정보를 받는 바구니
    public CustomUserInfoDto UserInfoLogin(Members member) {
        return modelMapper.map(member, CustomUserInfoDto.class);
    }

}
