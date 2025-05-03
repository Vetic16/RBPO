package ru.mtuci.rbpo_2024_praktika.configuration;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessExpiration}")
    private long accessExpiration; // Срок действия Access-токена

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration; // Срок действия Refresh-токена

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Метод для создания Access-токена
    public String createAccessToken(String username, Set<GrantedAuthority> authorities) {
        return createToken(username, authorities, "access", accessExpiration, null);
    }

    // Метод для создания Refresh-токена (с привязкой к deviceId)
    public String createRefreshToken(String username, Set<GrantedAuthority> authorities, String deviceId) {
        return createToken(username, authorities, "refresh", refreshExpiration, deviceId);
    }

    // Общий метод создания токена
    public String createToken(String username, Set<GrantedAuthority> authorities, String tokenType, long expirationTime, String deviceId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        Set<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(username)
                .claim("roles", authorityNames)
                .claim("token_type", tokenType) // Добавляем token_type
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256);

        // Если токен refresh, добавляем deviceId
        if ("refresh".equals(tokenType) && deviceId != null) {
            jwtBuilder.claim("device_id", deviceId);
        }

        return jwtBuilder.compact();
    }

    // Метод для валидации токена
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Метод для извлечения имени пользователя из токена
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Метод для получения типа токена
    public String getTokenType(String token) {
        return getClaims(token).get("token_type", String.class);
    }

    // Метод для получения deviceId (если токен refresh)
    public String getDeviceId(String token) {
        return getClaims(token).get("device_id", String.class);
    }

    // Вспомогательный метод для извлечения claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
