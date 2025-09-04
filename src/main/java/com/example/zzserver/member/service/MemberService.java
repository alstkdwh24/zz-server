package com.example.zzserver.member.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;

import jakarta.transaction.Transactional;

@Service
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    private final RefreshRepository refreshRepository;;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final BCryptPasswordEncoder encoder;
    private RedisTemplate<String, String> redisTemplate;
    private final ModelMapper modelMapper;

    public MemberService(JwtUtil jwtUtil, MemberRepository memberRepository, RefreshRepository refreshRepository,
            RefreshTokenRedisRepository refreshTokenRedisRepository, BCryptPasswordEncoder encoder,
            ModelMapper modelMapper, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.refreshRepository = refreshRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.encoder = encoder;
        this.modelMapper = modelMapper;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public TokenResponseDTO login(LoginRequestDto dto) {
        String email = dto.getEmail();
        String userPw = dto.getUserPw();
        // jpa
        Optional<Members> member = memberRepository.findMemberByEmail(email);
        // //redis

        if (member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        if (!encoder.matches(userPw, member.get().getUserPw())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        CustomUserInfoDto info = modelMapper.map(member.get(), CustomUserInfoDto.class);
        TokenResponseDTO tokenResponse = jwtUtil.createAccessToken(info);
        System.out.println("  1234  " + tokenResponse.getRefresh_token() + "  1234  ");
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(), member.get().getEmail(),
                tokenResponse.getRefresh_token());
        refreshRepository.save(refreshToken);

        String RedisAcess_token = tokenResponse.getAccess_token();
        String RedisRefresh_token = tokenResponse.getRefresh_token();
        RedisRefreshToken RedisUuid = this.RedisLoginSave(RedisAcess_token, RedisRefresh_token);
        UUID id = RedisUuid.getId();
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setId(id);
        tokenResponseDTO.setAccess_token(RedisUuid.getAccessToken());
        tokenResponseDTO.setRefresh_token(RedisUuid.getRefreshToken());
        return tokenResponseDTO;
    }

    @Transactional
    public String signup(Members member) {
        Optional<Members> validMember = memberRepository.findMemberByEmail(member.getEmail());

        if (validMember.isPresent()) {
            throw new IllegalArgumentException("This member UserId is already exist: " + member.getEmail());
        }
        member.setUserPw(encoder.encode(member.getUserPw())); // 비밀번호 암호화

        memberRepository.save(member);

        return member.getEmail();
    }

    @Transactional
    public Members getMemberById(UUID id) {
        Optional<Members> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return member.get();
        } else {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }

    public UUID getUserIdFromAccessToken(String accessToken) {
        UUID id = jwtUtil.getUserIdFromAccessToken(accessToken);
        Optional<Members> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return member.get().getId();
        } else {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }

    // 레디스로 토큰과 리프레쉬 토큰 저장
    public RedisRefreshToken RedisLoginSave(String accessToken, String refreshToken) {
        RedisRefreshToken refreshTokenEntity = new RedisRefreshToken();
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setAccessToken(accessToken);
        return refreshTokenRedisRepository.save(refreshTokenEntity);
    }

    public ResponseEntity<?> getRedisMemberById(UUID id, String accessToken, String refreshToken) {
        Optional<RedisRefreshToken> redisRefreshToken = refreshTokenRedisRepository.findById(id);
        RedisRefreshToken redisRefreshTokenEntity = redisRefreshToken.get();
        String RedisfreshToken = redisRefreshTokenEntity.getRefreshToken();
        if (refreshToken.equals(RedisfreshToken)) {

            if (accessToken == null) {

                TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                tokenResponseDTO.setRefresh_token(refreshToken);
                System.out.println(refreshToken);

                ResponseEntity.status(HttpStatus.OK).body(tokenResponseDTO);
                RestTemplate restTemplate = new RestTemplate();
                TokenResponseDTO response = restTemplate.getForObject(
                        "http://localhost:9090/auth/refresh?refresh_token=" + refreshToken, TokenResponseDTO.class);
                UUID ids = this.getUserIdFromAccessToken(response.getAccess_token());
                Members member = this.getMemberById(ids);

                if (member == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                return ResponseEntity.ok(member);

            } else {

                TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
                tokenResponseDTO.setRefresh_token(refreshToken);
                tokenResponseDTO.setAccess_token(accessToken);

                System.out.println(refreshToken);

                ResponseEntity.status(HttpStatus.OK).body(tokenResponseDTO);
                UUID ids = this.getUserIdFromAccessToken(accessToken);
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

    public ResponseEntity<Map<String, String>> getLogout(UUID id, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            String jwtToken = token.replace("Bearer ", "");

            redisTemplate.delete("refreshToken:" + id);

            try {
                UUID userId = jwtUtil.getUserIdFromAccessToken(jwtToken);
                redisTemplate.delete("refresh_token:" + userId);
            } catch (Exception e) {
            }
            long expiration = jwtUtil.getExpirationFromToken(jwtToken);
            long currentTime = System.currentTimeMillis();
            long timeToLive = Math.max(expiration - currentTime, 60000); // 최소 1분은 블랙리스트에 유지

            if (timeToLive > 0) {
                redisTemplate.opsForValue().set("blacklist:" + jwtToken, "logout", Duration.ofMillis(timeToLive));
            }
            refreshTokenRedisRepository.deleteById(id);
            response.put("message", "로그아웃 완료");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "로그아웃 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
