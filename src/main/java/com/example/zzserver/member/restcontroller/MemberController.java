package com.example.zzserver.member.restcontroller;

import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final RedisTemplate<Object, Object> redisTemplate;
    @Value("${kakao.kakaoLoginJavaScriptKey}")
    private String kakaoLoginJavaScriptKey;

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    private JwtUtil jwtUtil;

    public MemberController(MemberService memberService, ModelMapper modelMapper, JwtUtil jwtUtil,
            RedisTemplate<Object, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.memberService = memberService;
        this.modelMapper = modelMapper;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<TokenResponseDTO> getMemberLogin(Model model, @Valid @RequestBody LoginRequestDto dto) {
        model.addAttribute("kakaoLoginJavaScriptKey", kakaoLoginJavaScriptKey);
        TokenResponseDTO tokenResponseDTO = memberService.login(dto); // 반환 타입 수정

        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody MemberRequestDto memberDto) {
        Members member = modelMapper.map(memberDto, Members.class);
        String id = memberService.signup(member); // 서비스
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @GetMapping("/MemberUserInfo")
    public ResponseEntity<?> getUserInfo(@RequestParam("access_token") String accessToken,
            @RequestParam("refresh_token") String refreshToken, @RequestParam("id") UUID id) {
        System.out.println("refreshToken = " + refreshToken);

        try {
            ResponseEntity<?> response = memberService.getRedisMemberById(id, accessToken, refreshToken);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestParam("access_token") String token,
            @RequestParam("id") UUID id, HttpServletRequest request) {
        ResponseEntity<Map<String, String>> response = memberService.getLogout(id, token);
        return response;

    }

}
