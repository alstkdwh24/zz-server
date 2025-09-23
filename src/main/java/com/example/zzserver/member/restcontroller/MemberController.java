package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.CustomUserDetails;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.dto.request.MemberUpdateDTO;
import com.example.zzserver.member.dto.response.MemberResponseDto;
import com.example.zzserver.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {



    private final MemberService memberService;


    public MemberController(MemberService memberService)
                            {
        this.memberService = memberService;

    }


    @PostMapping("/signup")
    public ResponseEntity<UUID> signup(@Valid @RequestBody MemberRequestDto memberDto) {

        String id = memberService.signup(memberDto); // 서비스
        return ResponseEntity.status(HttpStatus.OK).body(UUID.fromString(id));
    }




    @PutMapping("/{id}")
    public ResponseEntity<String> updateMember(@PathVariable("id") UUID id, @AuthenticationPrincipal CustomUserDetails token, @Valid @RequestBody MemberUpdateDTO dto) {


        memberService.updateMember(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("Member updated successfully");
    }


    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMemberDetail(@PathVariable("id") UUID id, @AuthenticationPrincipal CustomUserDetails token) {
        MemberResponseDto memberDetail = memberService.getMemberById(id);

        return ResponseEntity.ok(memberDetail);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable("id") UUID id) {

        String message ="회원이 삭제되었습니다.";
        memberService.deleteMember(id);

        return  ResponseEntity.status(HttpStatus.OK).body(message);

    }


}
