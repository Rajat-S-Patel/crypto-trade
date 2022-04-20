package com.pirimid.cryptotrade.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("jwt.secret")
    private String JWT_SECRET_KEY;
    private final long JWT_TOKEN_VALIDITY = 10*60*60;

    public String getEmailFromToken(String token){
        return getClaimFromToken(token,Claims::getSubject);
    }

    public Date getExpirationTimeFromToken(String token){
        return getClaimFromToken(token,Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver){
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token){
        return getExpirationTimeFromToken(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject){
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_KEY).compact();
    }

    public void expireToken(String token){
        Claims claims = getAllClaimsFromToken(token);
        claims.setExpiration(null);
    }

    public boolean validateToken(String token, UserDetails userDetails){
        return (userDetails.getUsername().equals(getEmailFromToken(token)));
    }
}
