package com.example.zzserver.member.restcontroller;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.entity.Member;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.service.MemberService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Value("${kakao.KakaoLoginJavaScriptKey}")
    private String kakaoLoginJavaScriptKey;

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    private JwtUtil jwtUtil;


    public MemberController(MemberService memberService, ModelMapper modelMapper, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.memberService = memberService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<TokenResponseDTO> getMemberLogin(Model model, @Valid @RequestBody LoginRequestDto dto) {
        model.addAttribute("kakaoLoginJavaScriptKey", kakaoLoginJavaScriptKey);
        TokenResponseDTO tokenDto = memberService.login(dto); // 반환 타입 수정
        String RedisAcess_token = tokenDto.getAccess_token();
        String RedisRefresh_token = tokenDto.getRefresh_token();
        RedisRefreshToken RedisUuid = memberService.RedisLoginSave(RedisAcess_token, RedisRefresh_token);
        UUID id = RedisUuid.getId();
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setId(id);
        tokenResponseDTO.setAccess_token(RedisUuid.getAccessToken());
        tokenResponseDTO.setRefresh_token(RedisUuid.getRefreshToken());

        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<UUID> signup(@Valid @RequestBody MemberRequestDto memberDto) {
        Member member = modelMapper.map(memberDto, Member.class);
        UUID id = memberService.signup(member);  // 서비스
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @GetMapping("/MemberUserInfo")
    public ResponseEntity<Object> getUserInfo(@RequestParam("access_token") String accessToken,
                                              @RequestParam("refresh_token") String refreshToken,
                                              @RequestParam("id") UUID id) {
        System.out.println("refreshToken = " + refreshToken);

        try {
            String RedisfreshToken = memberService.getRedisMemberById(id);
            if (refreshToken.equals(RedisfreshToken)) {

                if (accessToken == null) {


                    TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                    tokenResponseDTO.setRefresh_token(refreshToken);
                    System.out.println(refreshToken);

                    ResponseEntity.status(HttpStatus.OK).body(tokenResponseDTO);
                    RestTemplate restTemplate = new RestTemplate();
                    TokenResponseDTO response = restTemplate.getForObject("http://localhost:9090/auth/refresh?refresh_token=" + refreshToken
                            , TokenResponseDTO.class);
                    UUID ids = memberService.getUserIdFromAccessToken(response.getAccess_token());
                    Member member = memberService.getMemberById(ids);


                    if (member == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    }
                    return ResponseEntity.ok(member);


                } else {

                    TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                    tokenResponseDTO.setRefresh_token(refreshToken);
                    System.out.println(refreshToken);

                    ResponseEntity.status(HttpStatus.OK).body(tokenResponseDTO);
                    UUID ids = memberService.getUserIdFromAccessToken(accessToken);
                    Member member = memberService.getMemberById(ids);
                    if (member == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    }
                    return ResponseEntity.ok(member);

                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }


}
