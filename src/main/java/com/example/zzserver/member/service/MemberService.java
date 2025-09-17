package com.example.zzserver.member.service;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.MemberRequestDto;
import com.example.zzserver.member.dto.request.MemberUpdateDTO;
import com.example.zzserver.member.dto.response.MemberResponseDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.Role;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final AuthService authService;

    ;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final BCryptPasswordEncoder encoder;
    @Qualifier("redisTemplate")

    private final ModelMapper modelMapper;

    public MemberService(JwtUtil jwtUtil, MemberRepository memberRepository, AuthService authService,
                         RefreshTokenRedisRepository refreshTokenRedisRepository, BCryptPasswordEncoder encoder,
                         ModelMapper modelMapper) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.authService = authService;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.encoder = encoder;
        this.modelMapper = modelMapper;
    }


    //회원가입

    public String signup(MemberRequestDto memberDto) {
        signUpPrivate(memberDto);

        return memberDto.getEmail();
    }

    private void signUpPrivate(MemberRequestDto member) {

        Optional<Members> validMember = memberRepository.findMemberByEmail(member.getEmail());

        if (validMember.isPresent()) {
            throw new IllegalArgumentException("This member UserId is already exist: " + member.getEmail());
        }
        Members newMember = Members.builder()
                .email(member.getEmail())
                .userPw(encoder.encode(member.getUserPw()))
                .name(member.getName())
                .nickname(member.getNickname())
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(newMember);
    }

    //직접 유저정보 가져오기
    public Members getMemberById(UUID id) {
        Optional<Members> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return member.get();
        } else {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }




    // 유저 정보 가져오기
    public ResponseEntity<?> getRedisMemberById(UUID id, String accessToken, String refreshToken) {
        Optional<RedisRefreshToken> redisRefreshToken = refreshTokenRedisRepository.findById(id);
        RedisRefreshToken redisRefreshTokenEntity = redisRefreshToken.get();
        String RedisfreshToken = redisRefreshTokenEntity.getRefreshToken();
        if (refreshToken.equals(RedisfreshToken)) {

            if (accessToken == null) {

                TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                tokenResponseDTO.setRefresh_token(refreshToken);

                ResponseEntity.status(HttpStatus.OK).body(tokenResponseDTO);
                RestTemplate restTemplate = new RestTemplate();
                TokenResponseDTO response = restTemplate.getForObject(
                        "http://localhost:9090/auth/refresh?refresh_token=" + refreshToken, TokenResponseDTO.class);
                UUID ids = authService.getUserIdFromAccessToken(response.getAccess_token());
                Members member = this.getMemberById(ids);

                if (member == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                return ResponseEntity.ok(member);

            } else {

                TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                tokenResponseDTO.setRefresh_token(refreshToken);
                tokenResponseDTO.setAccess_token(accessToken);


                ResponseEntity.status(HttpStatus.OK).body(tokenResponseDTO);
                UUID ids = authService.getUserIdFromAccessToken(accessToken);
                Members member = this.getMemberById(ids);
                if (member == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                return ResponseEntity.ok(member);

            }

        }
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setRefresh_token(refreshToken);

        return tokenResponseDTO != null ? ResponseEntity.ok(tokenResponseDTO)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }

    // 로그아웃


    public void updateMember(UUID id, MemberUpdateDTO dto) {
        System.out.println("Updating member with ID: " + id);
        Members member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));

        member.ChangeName(dto.getName());
        member.ChangeNickname(dto.getNickname());
        memberRepository.save(member);
    }


    public ResponseEntity<MemberResponseDto> userDetail(String token) {
        String cleanToken = token.replace("Bearer ", "");
        UUID id = jwtUtil.getUserIdFromAccessToken(cleanToken);

        Members member = this.getMemberById(id);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        MemberResponseDto memberResponseDto = modelMapper.map(member, MemberResponseDto.class);
        return ResponseEntity.ok(memberResponseDto);
    }


}
