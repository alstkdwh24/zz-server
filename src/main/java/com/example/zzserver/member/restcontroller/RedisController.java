package com.example.zzserver.member.restcontroller;

import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService, MemberRepository memberRepository) {
        this.redisService = redisService;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestHeader("Authorization") String token){

        redisService.deleteMember(token);
        return ResponseEntity.status(HttpStatus.OK).body("Member deleted successfully");
    }
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestParam("access_token") String token,
                                                      @RequestParam("id") UUID id, HttpServletRequest request) {
        ResponseEntity<Map<String, String>> response = redisService.getLogout(id, token);
        return response;

    }
}
