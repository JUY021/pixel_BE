package project.game.pixel.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.game.pixel.entity.RefreshToken;
import project.game.pixel.repository.RefreshTokenRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey secretKey;

    private final long accessTokenValidity = 1000L * 60 * 60;
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostConstruct
    public void init() {
        // secretKeyString 문자열을 바이트로 변환해 SecretKey 생성
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성
    public String createAccessToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // RefreshToken 생성 및 저장
    public String createRefreshToken(Long userDbId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity);

        String token = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();

        RefreshToken refreshtoken = new RefreshToken();
        refreshtoken.setUserId(userDbId);
        refreshtoken.setToken(token);
        refreshtoken.setExpiryDate(expiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        refreshTokenRepository.save(refreshtoken);
        return token;
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public long getAccessTokenValidityInSeconds() {
        return accessTokenValidity/1000;
    }
}
