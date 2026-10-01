package com.kristalball.assetmanagement.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String secretKey;
    private final long expirationTime;

    public JwtService(
            @Value("${app.jwt.secret}") String secretKey,
            @Value("${app.jwt.expiration-ms:3600000}") long expirationTime) {

        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String role) {

        Date now = new Date();
        Date expiration =
                new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {

        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
