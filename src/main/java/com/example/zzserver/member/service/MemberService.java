package com.example.zzserver.member.service;

import com.example.zzserver.config.JwtUtil;
import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.member.dto.request.LoginRequestDto;
import com.example.zzserver.member.dto.request.MemberUpdateDTO;
import com.example.zzserver.member.dto.response.MemberResponseDto;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.entity.RefreshToken;
import com.example.zzserver.member.entity.Role;
import com.example.zzserver.member.entity.redis.RedisRefreshToken;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import com.example.zzserver.member.repository.jpa.RefreshRepository;
import com.example.zzserver.member.repository.redis.RefreshTokenRedisRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    private final RefreshRepository refreshRepository;
    ;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final BCryptPasswordEncoder encoder;
    @Qualifier("redisTemplate")

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<Object, Object> redisTemplate2;
    private final ModelMapper modelMapper;

    public MemberService(JwtUtil jwtUtil, MemberRepository memberRepository, RefreshRepository refreshRepository,
                         RefreshTokenRedisRepository refreshTokenRedisRepository, BCryptPasswordEncoder encoder, RedisTemplate<String, Object> redisTemplate, RedisTemplate<Object, Object> redisTemplate2,
                         ModelMapper modelMapper) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.refreshRepository = refreshRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.encoder = encoder;
        this.redisTemplate = redisTemplate;
        this.redisTemplate2 = redisTemplate2;
        this.modelMapper = modelMapper;
    }

    //로그인

    public TokenResponseDTO login(LoginRequestDto dto) {
        String email = dto.getEmail();
        String userPw = dto.getUserPw();
        // jpa
        Optional<Members> member = memberRepository.findMemberByEmail(email);
        // //redis
        System.out.println("member.id="+ member.get().getId() );
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        if (!encoder.matches(userPw, member.get().getUserPw())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
         dto.setId( member.get().getId());
        System.out.println(dto.getId());


        CustomUserInfoDto info = modelMapper.map(member.get(), CustomUserInfoDto.class);

        System.out.println("info.getId() = " + info.getId());
        TokenResponseDTO tokenResponse = jwtUtil.createAccessToken(info);
        System.out.println("  1234  " + tokenResponse.getRefresh_token() + "  1234  ");
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(), member.get().getEmail(),
                tokenResponse.getRefresh_token());
        refreshRepository.save(refreshToken);

        String RedisAcess_token = tokenResponse.getAccess_token();
        String RedisRefresh_token = tokenResponse.getRefresh_token();
        RedisRefreshToken RedisUuid = this.RedisLoginSave(RedisAcess_token, RedisRefresh_token);
        String id = RedisUuid.getId();
        UUID uuid = UUID.fromString(id);
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setId(uuid);
        tokenResponseDTO.setAccess_token(RedisUuid.getAccessToken());
        tokenResponseDTO.setRefresh_token(RedisUuid.getRefreshToken());
        System.out.println("tokenResponseDTO = " + tokenResponseDTO.getId());
        return tokenResponseDTO;
    }

    //회원가입

    public String signup(Members member) {
        member.setRole(Role.ROLE_USER); // 기본 권한 설정
        Optional<Members> validMember = memberRepository.findMemberByEmail(member.getEmail());

        if (validMember.isPresent()) {
            throw new IllegalArgumentException("This member UserId is already exist: " + member.getEmail());
        }
        member.setUserPw(encoder.encode(member.getUserPw())); // 비밀번호 암호화

        memberRepository.save(member);

        return member.getEmail();
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

    // AccessToken에서 userId 추출
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

    // 유저 정보 가져오기
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

    // 로그아웃

    public ResponseEntity<Map<String, String>> getLogout(UUID id, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            String jwtToken = token.replace("Bearer ", "");

            redisTemplate2.delete("refreshToken:" + id);

            try {
                UUID userId = jwtUtil.getUserIdFromAccessToken(jwtToken);
                redisTemplate2.delete("refresh_token:" + userId);
            } catch (Exception e) {
            }
            long expiration = jwtUtil.getExpirationFromToken(jwtToken);
            long currentTime = System.currentTimeMillis();
            long timeToLive = Math.max(expiration - currentTime, 60000); // 최소 1분은 블랙리스트에 유지

            if (timeToLive > 0) {
                redisTemplate2.opsForValue().set("blacklist:" + jwtToken, "logout", Duration.ofMillis(timeToLive));
            }
            refreshTokenRedisRepository.deleteById(id);
            response.put("message", "로그아웃 완료");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "로그아웃 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public void updateMember(UUID id, MemberUpdateDTO dto) {
        Members member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));

        member.setName(dto.getName());
        member.setNickname(dto.getNickname());
        memberRepository.save(member);
    }


    public ResponseEntity<MemberResponseDto> userDetail(String token) {
        String cleanToken = token.replace("Bearer ", "");
        UUID id = jwtUtil.getUserIdFromAccessToken(cleanToken);
        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Members member = this.getMemberById(id);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        MemberResponseDto memberResponseDto = modelMapper.map(member, MemberResponseDto.class);
        return ResponseEntity.ok(memberResponseDto);
    }

//유저 삭제
public void deleteMember(String token) {
    UUID id = jwtUtil.getUserIdFromAccessToken(token);
    redisTemplate2.delete("refresh_token:" + id);
    System.out.println("Redis 토큰 삭제 완료, email = " + id);


    Members member = memberRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));



    memberRepository.delete(member);
    System.out.println("회원 삭제 완료, id = " + id);
}
}
