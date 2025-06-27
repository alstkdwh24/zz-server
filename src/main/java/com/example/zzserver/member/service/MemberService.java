package com.example.zzserver.member.service;

import com.example.zzserver.config.CustomUserInfoDto;
import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.entity.Member;
import com.example.zzserver.member.repository.MemberRepository;
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
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;

    public MemberService(JwtUtil jwtUtil, MemberRepository memberRepository, PasswordEncoder encoder, ModelMapper modelMapper) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.encoder = encoder;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public String login(LoginRequestDto dto){
        String userId = dto.getUserId();
        String userPw = dto.getUserPw();

        Optional<Member> member = memberRepository.findMemberByUserId(userId);
        if(member.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        if(!encoder.matches(userPw, member.get().getUserPw())){
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        CustomUserInfoDto info = modelMapper.map(member.get(),CustomUserInfoDto.class);
        return jwtUtil.createAccessToken(info);
    }

    @Transactional
    public UUID signup(Member member) {
        Optional<Member> validMember = memberRepository.findMemberByUserId(member.getUserId());

        if (validMember.isPresent()) {
            throw new IllegalArgumentException("This member UserId is already exist: " + member.getUserId());
        }
        member.setUserPw(encoder.encode(member.getUserPw())); // 비밀번호 암호화
        memberRepository.save(member);

        return member.getId();
    }
    }
