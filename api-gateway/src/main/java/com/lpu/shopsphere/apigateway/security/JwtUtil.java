package com.lpu.shopsphere.apigateway.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
  private final Key key;

  public JwtUtil(
      @Value("${app.jwt.secret}") String secret
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody(); 
  }

  public boolean validate(String token) {
    try {
      Claims claims = parseClaims(token);
      Date exp = claims.getExpiration();
      return exp != null && exp.after(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}

