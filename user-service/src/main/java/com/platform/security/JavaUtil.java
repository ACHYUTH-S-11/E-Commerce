package com.platform.security;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.platform.model.Role;
import com.platform.model.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JavaUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    // private static final String SECRET_KEY = Base64.getEncoder().encodeToString("myverysecureandlongsecretkey1234567890".getBytes());
    // "mysecretkeyisSecuredBecauseItIsLongAndComplex1234567890"; // Use a secure key in production
    private static final long EXPIRATION_TIME = 3600000; // 1 hour in milliseconds

    public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getUsername());
    claims.put("username", user.getUsername());
    // Add roles as a comma-separated string
    claims.put("roles", user.getRoles().stream().map(Role::name).collect(Collectors.joining(",")));
    Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(key)
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch ( IllegalArgumentException | JwtException e) {
            // Token is invalid or expired
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }
}
