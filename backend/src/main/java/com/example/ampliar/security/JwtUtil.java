package com.example.ampliar.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey key;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret:minhaChaveSecretaSuperSegura123!}") String secret,
                   @Value("${jwt.expiration:86400000}") long expirationTime) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationTime = expirationTime;
        log.info("JwtUtil configurado - Expiração: {}ms", expirationTime);
    }

    public String generateToken(String username) {
        log.debug("Gerando token para: {}", username);
        try {
            String token = Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(key)
                    .compact();
            log.debug("Token gerado com sucesso para: {}", username);
            return token;
        } catch (Exception e) {
            log.error("Erro ao gerar token para: {}", username, e);
            throw new RuntimeException("Erro ao gerar token", e);
        }
    }

    public String extractEmail(String token) {
        log.debug("Extraindo email do token");
        try {
            String email = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            log.debug("Email extraído do token: {}", email);
            return email;
        } catch (Exception e) {
            log.error("Erro ao extrair email do token", e);
            throw new RuntimeException("Token inválido", e);
        }
    }

    public boolean validateToken(String token){
        log.debug("Validando token");
        try{
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            log.debug("Token validado com sucesso");
            return true;
        } catch (Exception e){
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }
}