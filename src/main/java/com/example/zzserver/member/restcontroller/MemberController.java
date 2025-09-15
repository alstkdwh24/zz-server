package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.dto.request.MemberUpdateDTO;
import com.example.zzserver.member.dto.response.MemberResponseDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<TokenResponseDTO> getMemberLogin(Model model, @Valid @RequestBody LoginRequestDto dto) {
        model.addAttribute("kakaoLoginJavaScriptKey", kakaoLoginJavaScriptKey);
        TokenResponseDTO tokenResponseDTO = memberService.login(dto); // 반환 타입 수정

        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<UUID> signup(@Valid @RequestBody MemberRequestDto memberDto) {
        Members member = modelMapper.map(memberDto, Members.class);
        String id = memberService.signup(member); // 서비스
        System.out.println("Created member ID: " + id);
        return ResponseEntity.status(HttpStatus.OK).body(member.getId());
    }
    @PreAuthorize("hasRole('ROLE_USER')")
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

    @PostMapping("/update")
    public ResponseEntity<String> updateMember(@RequestHeader("Authorization") String token, @Valid @RequestBody MemberUpdateDTO dto) {
        String cleanToken = token.replace("Bearer ", "");
        System.out.println("cleanToken = " + cleanToken);

        UUID id = jwtUtil.getUserIdFromAccessToken(cleanToken);
        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        memberService.updateMember(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("Member updated successfully");
    }
    @PutMapping("/userDetail")
    public ResponseEntity<MemberResponseDto> getMemberDetail(@RequestHeader("Authorization") String token) {
        ResponseEntity<MemberResponseDto> memberDetail=memberService.userDetail(token);
        return ResponseEntity.ok(memberDetail.getBody());

    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestHeader("Authorization") String token){

        memberService.deleteMember(token);
        return ResponseEntity.status(HttpStatus.OK).body("Member deleted successfully");
    }

}
