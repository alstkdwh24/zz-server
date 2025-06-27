package com.example.zzserver.member.restcontroller;

import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Member;
import com.example.zzserver.member.service.MemberService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Value("${kakao.KakaoLoginJavaScriptKey}")
    private String kakaoLoginJavaScriptKey;

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    public MemberController(MemberService memberService, ModelMapper modelMapper) {
        this.memberService = memberService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> getMemberLogin(Model model, @Valid @RequestBody LoginRequestDto dto) {
        model.addAttribute("kakaoLoginJavaScriptKey", kakaoLoginJavaScriptKey);
        String token = memberService.login(dto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<UUID> signup(@Valid @RequestBody MemberRequestDto memberDto) {
        Member member = modelMapper.map(memberDto, Member.class);
        UUID id = memberService.signup(member);  // 서비스
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }




}
