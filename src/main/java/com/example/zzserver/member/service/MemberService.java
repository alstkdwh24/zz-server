package com.example.zzserver.member.service;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.entity.Member;
import com.example.zzserver.member.entity.RedisRefreshToken;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {


    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    private final RefreshRepository refreshRepository;;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;

    public MemberService(JwtUtil jwtUtil, MemberRepository memberRepository, RefreshRepository refreshRepository, RefreshTokenRedisRepository refreshTokenRedisRepository, PasswordEncoder encoder, ModelMapper modelMapper) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.refreshRepository = refreshRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.encoder = encoder;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public TokenResponseDTO login(LoginRequestDto dto){
        String email = dto.getEmail();
        String userPw = dto.getUserPw();
//jpa
        Optional<Member> member = memberRepository.findMemberByEmail(email);
     //   //redis
        Optional<RedisRefreshToken> members = refreshTokenRedisRepository.findMemberByEmail(email);

        if(member.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        if(!encoder.matches(userPw, member.get().getUserPw())){
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        CustomUserInfoDto info = modelMapper.map(member.get(),CustomUserInfoDto.class);
        TokenResponseDTO tokenResponse = jwtUtil.createAccessToken(info);
        System.out.println("  1234  "+tokenResponse.getRefresh_token() + "  1234  ");
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(),"alstdqdlqdq@naver.com",tokenResponse.getRefresh_token()); // 필요한 필드 채우기
        refreshRepository.save(refreshToken);
        return jwtUtil.createAccessToken(info);
    }

    @Transactional
    public UUID signup(Member member) {
        Optional<Member> validMember = memberRepository.findMemberByEmail(member.getEmail());

        if (validMember.isPresent()) {
            throw new IllegalArgumentException("This member UserId is already exist: " + member.getEmail());
        }
        member.setUserPw(encoder.encode(member.getUserPw())); // 비밀번호 암호화
        memberRepository.save(member);

        return member.getId();
    }
    }
