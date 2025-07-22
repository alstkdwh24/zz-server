package com.example.zzserver;

import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Member;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class ControllerTest {

    private final ModelMapper modelMapper;

    public ControllerTest(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostMapping("/member/login")
    public ResponseEntity<Map<String, Object>> removeToken(@RequestBody LoginRequestDto loginRequestDto, Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        // 예시: memberId 반환
        result.put("memberId", 1L); // 예시 값, 실제 로직에 맞게 변경


        return ResponseEntity.ok(result);
    }

    @PostMapping("/member/signup")
    public ResponseEntity<Map<String, UUID>> signup(@Valid @RequestBody MemberRequestDto memberDto) {
        Member member = modelMapper.map(memberDto, Member.class);
        UUID id = member.getId() != null ? member.getId() : UUID.randomUUID();


        return ResponseEntity.ok(Map.of("id", id));
    }

}
