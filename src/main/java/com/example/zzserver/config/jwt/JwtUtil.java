package com.example.zzserver.config.jwt;

import com.example.zzserver.config.dto.CustomUserInfoDto;
import com.example.zzserver.config.dto.TokenResponseDTO;
import com.example.zzserver.config.exception.UnauthorizedException;
import com.example.zzserver.config.message.Messages;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {
    private static final Logger logger = LogManager.getLogger(JwtUtil.class);

    private final Key key;
    private final long accessTokenExpTime;


    private final long refreshTokenExpTime;

    private final String userId = ""; // Refresh Token의 Subject로 사용될 값

    public JwtUtil(@Value("${jwt.secret}") final String secretKey, @Value("${jwt.expiration}") final long accessTokenExpTime, @Value("${jwt.refreshTokenExpiration}") final long refreshTokenExpTime) {
        this.refreshTokenExpTime = refreshTokenExpTime;
        Key secretKeys = Keys.hmacShaKeyFor(secretKey.getBytes());
        key = secretKeys;
        this.accessTokenExpTime = accessTokenExpTime;
    }


    /**
     * Access Token 생성
     *
     * @param member
     * @return Access Token String
     */

    public TokenResponseDTO createAccessToken(CustomUserInfoDto member) {
        String accessToken = createToken(member, accessTokenExpTime);
        String refreshToken = createRefreshToken(member, refreshTokenExpTime);
        logger.debug("accessToken" + accessToken);

        return new TokenResponseDTO( UUID.randomUUID(), accessToken, refreshToken, Messages.TOKEN_CREATED);
    }


    private String createToken(CustomUserInfoDto member, long expTime) {
        Claims claims = Jwts.claims();
        claims.put("id", member.getId());
        claims.put("email", member.getEmail());
        claims.put("name", member.getName());
        claims.put("role", member.getRole());
        logger.debug("Creating token for user ID: {}", member.getId());
        logger.debug("Token claims: {}", claims);
        logger.debug("member.getEmail: {}", member.getEmail());
        logger.debug("member.getRole: {}", member.getRole());



        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(accessTokenExpTime);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(tokenValidity.toInstant().plusMillis(expTime)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    private String createRefreshToken(CustomUserInfoDto member, long exTime) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime refreshTokenValidity = now.plusSeconds(exTime);
        Claims claims = Jwts.claims();
        claims.put("id", member.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(refreshTokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token에서 User ID 추출
     *
     * @param token
     * @return Claims
     */

    public String getEmail(String token) {
        logger.debug("Getting email for token: {}", token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.debug("토큰에서 이메일 추출 시도: {}", claims.get("email"));

        return claims.get("email", String.class);

    }

    /**
     * JWT 검증
     *
     * @param token
     * @return IsValidate
     */

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.info("Invalid JWT", e);
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT", e);
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT", e);
        } catch (IllegalArgumentException e) {
            logger.info("JWT claims string is empty", e);
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public UUID getUserIdFromAccessToken(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken).getBody();

            return UUID.fromString(claims.get("id", String.class));
        } catch (ExpiredJwtException e) {
            logger.error("Access token has expired", e);
            throw new UnauthorizedException("Access token has expired"); // 예외 던지기
        } catch (Exception e) {
            logger.error("Failed to parse access token", e);
            return null; // 토큰 파싱 실패 시 null 반환
        }
    }



    //    토큰 완료시간 확인
    public Date getExpirationDate(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("Failed to get expiration date from token", e);
            return null; // 토큰 파싱 실패 시 null 반환
        }
    }



    public TokenResponseDTO refreshBothTokens(String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new IllegalArgumentException("REFRESH_TOKEN_NULL");
            }

            // 2. Refresh Token 유효성 검증
            if (!isValidToken(refreshToken)) {
                throw new RuntimeException("REFRESH_TOKEN_INVALID");
            }
            Claims claims = parseClaims(refreshToken);
            String idClaim = claims.get("id", String.class); // 항상 String으로 꺼냄

            UUID userId;
            try {
                String userIdString = idClaim.toString();
                userId = UUID.fromString(userIdString);
                logger.debug("Successfully extracted user ID: {}", userId);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid UUID format in refresh token: {}", idClaim);
                throw new RuntimeException("INVALID_USER_ID_FORMAT");
            }
            CustomUserInfoDto member = new CustomUserInfoDto();
            member.setId(userId);
            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
            // 3. Access Token 생성
            tokenResponseDTO.setAccess_token(createToken(member, accessTokenExpTime));
            tokenResponseDTO.setRefresh_token(createRefreshToken(member, refreshTokenExpTime));
            return tokenResponseDTO;

        } catch (IllegalArgumentException e) {
            logger.error("Refresh token is null: {}", e.getMessage());
            throw new RuntimeException("LOGIN_REQUIRED", e);
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token has expired", e);
            throw new RuntimeException("LOGIN_REQUIRED", e);
        } catch (Exception e) {
            logger.error("Failed to refresh tokens", e);
            throw new RuntimeException("LOGIN_REQUIRED", e);
        }
    }

    public long getExpirationFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();
            if (expirationDate == null) {
                logger.error("Expiration date is null in token");
                return System.currentTimeMillis(); // 현재 시간 반환
            }
            return expirationDate.getTime();

        } catch (ExpiredJwtException e) {
            // 만료된 토큰이라도 실제 만료 시간을 반환해야 블랙리스트 TTL 설정 가능
            logger.warn("Token has expired, but returning actual expiration time");
            Date expiration = e.getClaims().getExpiration();
            return expiration != null ? expiration.getTime() : System.currentTimeMillis();

        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            return System.currentTimeMillis(); // 서명 오류 시 현재 시간

        } catch (Exception e) {
            logger.error("Failed to get expiration from token: {}", e.getMessage());
            return System.currentTimeMillis(); // 예외 던지지 말고 현재 시간 반환
        }
    }



}
