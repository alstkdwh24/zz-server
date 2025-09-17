package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.dto.request.MemberUpdateDTO;
import com.example.zzserver.member.dto.response.MemberResponseDto;
import com.example.zzserver.member.service.MemberService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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


    @PostMapping("/signup")
    public ResponseEntity<UUID> signup(@Valid @RequestBody MemberRequestDto memberDto) {

        String id = memberService.signup(memberDto); // 서비스
        return ResponseEntity.status(HttpStatus.OK).body(UUID.fromString(id));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/MemberUserInfo")
    public ResponseEntity<?> getUserInfo(@RequestParam("access_token") String accessToken,
                                         @RequestParam("refresh_token") String refreshToken, @RequestParam("id") UUID id) {

        try {
            ResponseEntity<?> response = memberService.getRedisMemberById(id, accessToken, refreshToken);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/update")
    public ResponseEntity<String> updateMember(@RequestHeader("Authorization") String token, @Valid @RequestBody MemberUpdateDTO dto) {
        String cleanToken = token.replace("Bearer ", "");

        UUID id = jwtUtil.getUserIdFromAccessToken(cleanToken);
        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        memberService.updateMember(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("Member updated successfully");
    }

    @PutMapping("/userDetail")
    public ResponseEntity<MemberResponseDto> getMemberDetail(@RequestHeader("Authorization") String token) {
        ResponseEntity<MemberResponseDto> memberDetail = memberService.userDetail(token);
        return ResponseEntity.ok(memberDetail.getBody());

    }


}
