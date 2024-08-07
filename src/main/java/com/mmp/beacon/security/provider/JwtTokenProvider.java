package com.mmp.beacon.security.provider;

import com.mmp.beacon.security.application.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.io.Encoders.BASE64;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneId.systemDefault;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String TOKEN_HEADER_PREFIX = "Bearer ";
    private static final String ISSUER = "http://localhost";
    private static final int ACCESS_VALID_HOUR = 1;  // Access Token validity period
    private static final int REFRESH_VALID_DAYS = 7; // Refresh Token validity period

    @Value("${jwt.secret}")
    private String secretKey;

    private Key signingKey;

    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(encodedSecretKey(secretKey).getBytes(UTF_8));
    }

    // JWT 토큰 생성 메서드
    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = getAtExpireTimeFrom(now, ACCESS_VALID_HOUR);

        return Jwts.builder()
                .setHeader(getDefaultHeader())
                .setSubject(userDetails.getUsername())
                .claim("name", userDetails.getName())
                .claim("role", userDetails.getUser().getRole().name())
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT 리프레시 토큰 생성 메서드
    public String generateRefreshToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = getAtExpireTimeFrom(now, REFRESH_VALID_DAYS * 24);  // Calculate in days

        return Jwts.builder()
                .setHeader(getDefaultHeader())
                .setSubject(userDetails.getUsername())
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT 토큰 검증 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(removePrefix(token));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 만료 여부 확인 메서드
    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(removePrefix(token)).before(new Date());
    }

    // 토큰에서 사용자 이름 추출 메서드
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 토큰에서 만료 날짜 추출 메서드
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 토큰에서 특정 클레임 추출 메서드
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(removePrefix(token));
        return claimsResolver.apply(claims);
    }

    // 토큰에서 모든 클레임 추출 메서드
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 시크릿 키 인코딩 메서드
    private String encodedSecretKey(String key) {
        return BASE64.encode(key.getBytes());
    }

    // 토큰 헤더에서 Bearer 접두사 제거 메서드
    private String removePrefix(String token) {
        if (token.startsWith(TOKEN_HEADER_PREFIX)) {
            token = token.substring(TOKEN_HEADER_PREFIX.length());
        }
        return token;
    }

    // 기본 JWT 헤더 설정 메서드
    @SuppressWarnings("unchecked")
    private Map<String, Object> getDefaultHeader() {
        @SuppressWarnings("rawtypes")
        Header header = Jwts.header();
        header.put("typ", "JWT");
        return header;
    }

    // 토큰 만료 시간 계산 메서드
    private Date getAtExpireTimeFrom(Date issuedTime, int hours) {
        LocalDateTime expireTime = issuedTime.toInstant()
                .atZone(systemDefault())
                .toLocalDateTime()
                .plusHours(hours);
        return Date.from(expireTime.atZone(systemDefault()).toInstant());
    }
}
