package com.example.zzserver.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Key;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
@Component
public class JwtUtil {
    private static final Logger logger = LogManager.getLogger(JwtUtil.class);

    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(@Value("${jwt.secret}") final String secretKey, @Value("${jwt.expiration}") final long accessTokenExpTime) {
        Key secretKeys = Keys.hmacShaKeyFor(secretKey.getBytes());        System.out.println("JwtUtil initialized with secretKey: " + secretKey + " and accessTokenExpTime: " + accessTokenExpTime);
        key = secretKeys;
        this.accessTokenExpTime = accessTokenExpTime;
    }


    /**
     * Access Token 생성
     *
     * @param member
     * @return Access Token String
     */

    public String createAccessToken(CustomUserInfoDto member) {
        System.out.println("member24"+member.getEmail()+ member.getName() + member.getUserId() + " " + accessTokenExpTime);
        return createToken(member, accessTokenExpTime);
    }


    private String createToken(CustomUserInfoDto member, long expTime) {
        Claims claims = Jwts.claims();
        claims.put("id", member.getId());
        claims.put("userId", member.getUserId());
        claims.put("userPw", member.getUserPw());
        claims.put("email", member.getEmail());
        claims.put("name", member.getName());
        claims.put("role", member.getRole());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(accessTokenExpTime);
        System.out.println("claims"+ claims.getId() + claims.get("id") + " "+ claims.get("userId") + " "+claims.get("userPw") + " "+claims.get("email") +" "+ claims.get("name") + " "+claims.get("role"));




        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(tokenValidity.toInstant().plusMillis(expTime)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    /**
     * Token에서 User ID 추출
     *
     * @param token
     * @return Claims
     */

    public String getUserId(String token) {
        return parseClaims(token).get("id", String.class);
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
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
