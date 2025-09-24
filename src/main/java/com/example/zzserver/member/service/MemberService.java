package com.example.zzserver.member.service;

import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.dto.request.MemberUpdateDTO;
import com.example.zzserver.member.dto.response.MemberResponseDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.Role;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;


    ;

    private final BCryptPasswordEncoder encoder;

    public MemberService( MemberRepository memberRepository, BCryptPasswordEncoder encoder) {
        this.memberRepository = memberRepository;
        this.encoder = encoder;
    }


    //회원가입
    public UUID signup(MemberRequestDto memberDto) {
        signUpPrivate(memberDto);
        Members newMember = Members.builder()
                .email(memberDto.getEmail())
                .userPw(encoder.encode(memberDto.getUserPw()))
                .name(memberDto.getName())
                .nickname(memberDto.getNickname())
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(newMember);
        UUID id=newMember.getId();
        memberDto.setId(id);
        return memberDto.getId();
    }


    private void signUpPrivate(MemberRequestDto member) {

        boolean validMember = memberRepository.existsByEmail(member.getEmail());

        if (validMember) {
            throw new IllegalArgumentException("This member UserId is already exist: " + member.getEmail());
        }

    }


    //직접 유저정보 가져오기
    public MemberResponseDto getMemberById(UUID id) {
        Members member = memberRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Member Id: " + id));
        return toDto(member);
    }


//수정
    // 유저 정보 가져오기


    // 로그아웃
    public void updateMember(UUID id, MemberUpdateDTO dto) {
        Members member = getMembersById(id);

        member.ChangeName(dto.getName());
        member.ChangeNickname(dto.getNickname());
        memberRepository.save(member);
    }


    public Members getMembersById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
    }


    public void deleteMember(UUID id) {
        Members member = getMembersById(id);
        memberRepository.deleteById(member.getId());
    }


    private MemberResponseDto toDto(Members member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .userPw(member.getUserPw())
                .build();
    }
}
