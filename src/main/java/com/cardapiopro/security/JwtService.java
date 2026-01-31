package com.cardapiopro.security;

import com.cardapiopro.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret:cardapiopro-secret-key-deve-ter-256-bits-minimo-32-caracteres}")
    private String secretKey;

    @Value("${jwt.access-token-expiration:3600000}") // 1 hora
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}") // 7 dias
    private long refreshTokenExpiration;

    public String generateAccessToken(User user) {
        return generateToken(new HashMap<>(), user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user, refreshTokenExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, User user, long expiration) {
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("name", user.getName());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, User user) {
        final String email = extractEmail(token);
        return (email.equals(user.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration / 1000;
    }
}
