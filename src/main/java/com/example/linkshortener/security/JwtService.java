package com.example.linkshortener.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private static final String SIGN_IN_SECRET = "6A586E327235753878214125442A472D4B6150645367566B5970337336763979";
    private final static Logger LOGGER = LoggerFactory.getLogger(JwtService.class);


    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    public String generateJwt(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isJwtValid(String jwt, UserDetails userDetails) {
        return extractUsername(jwt).equals(userDetails.getUsername());
    }

    private Claims extractAllClaims(String jwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            LOGGER.error("Bad JWT :" + jwt + e.getMessage());
            throw new JwtException("Bad JWT :" + jwt);
        }
    }
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SIGN_IN_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
