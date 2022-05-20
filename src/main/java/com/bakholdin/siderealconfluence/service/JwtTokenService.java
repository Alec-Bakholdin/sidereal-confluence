package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenService {

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    @Value("${auth.cookie.hmac-key:secret-key}")
    private String secretKey;

    public String generateJwtToken(UserDto userDto) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDto.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSigningKey(), SIGNATURE_ALGORITHM)
                .compact();
    }

    public boolean validateJwtToken(String jwtToken, UserDto userDto) {
        Claims parsedClaims = parseJwtToken(jwtToken);
        return userDto.getUsername().equals(parsedClaims.getSubject());
    }

    public String getUsernameFromToken(String token) {
        return parseJwtToken(token).getSubject();
    }

    public Claims parseJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SIGNATURE_ALGORITHM.getJcaName());
    }
}
