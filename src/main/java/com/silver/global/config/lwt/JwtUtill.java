package com.silver.global.config.lwt;

import org.springframework.stereotype.Component;

import java.util.Date;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtill {
    private final JwtProperties jwtProperties;

    public JwtUtill(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public JwtToken generateToken(String email) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + 1000 * 60 * 60 * 24); //24시간
        String accessToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();

        Date refreshTokenExpiresIn = new Date(now + 604800000);
        String refreshToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();

        return JwtToken.builder()
                .grantType(jwtProperties.getAuthType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String extractUseremail(String accessToken) {

        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String accessToken, String email) {
        return email.equals(extractUseremail(accessToken)) && !isTokenExpired(accessToken);
    }

    public boolean isTokenExpired(String accessToken) {

        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration()
                .before(new Date());

    }
}