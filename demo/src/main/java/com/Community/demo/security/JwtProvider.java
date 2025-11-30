package com.Community.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret:change-me-and-make-it-long}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    /** Old method (kept for compatibility) */
    public String generateToken(Long userId, String email) {
        return generateToken(userId, email, Set.of("ROLE_USER"));
    }

    /** New: include roles claim */
    public String generateToken(Long userId, String email, Set<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        Key key = getSigningKey();

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId != null ? userId.toString() : null);
        if (roles != null && !roles.isEmpty()) {
            claims.put("roles", roles); // stored as JSON array
        }

        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT: {}", e.getMessage());
        } catch (SecurityException | SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is invalid: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Unexpected error while validating JWT: {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            log.warn("Failed to parse username from token: {}", e.getMessage());
            return null;
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Object uid = claims.get("uid");
            return uid != null ? uid.toString() : null;
        } catch (Exception e) {
            log.warn("Failed to parse userId from token: {}", e.getMessage());
            return null;
        }
    }

    /** New: extract roles claim as a Set<String> */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object raw = claims.get("roles");
            if (raw == null) return Collections.emptySet();

            if (raw instanceof Collection<?> col) {
                Set<String> out = new HashSet<>();
                for (Object o : col) if (o != null) out.add(o.toString());
                return out;
            }
            // single role stored as string fallback
            return Set.of(raw.toString());
        } catch (Exception e) {
            log.warn("Failed to parse roles from token: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (Exception ignore) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret must be at least 256 bits (32 bytes). Current: " + (keyBytes.length * 8) + " bits"
            );
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
