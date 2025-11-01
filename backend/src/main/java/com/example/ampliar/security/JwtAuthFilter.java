package com.example.ampliar.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    // ✅ CORREÇÃO: Adicionar JwtUtil na injeção
    public JwtAuthFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();
        
        log.debug("Processando requisição: {} {} - Filter: JwtAuthFilter", method, path);

        // 1) Não interceptar rotas públicas
        if (path.startsWith("/auth/")) {
            log.debug("Rota pública detectada: {} - Pulando filtro JWT", path);
            chain.doFilter(request, response);
            return;
        }

        // 2) Ler header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Requisição sem token Bearer - {} {}", method, path);
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.debug("Token JWT encontrado - Tamanho: {} caracteres", token.length());

        try {
            // ✅ CORREÇÃO: Usar jwtUtil injetado em vez de método estático
            String username = jwtUtil.extractEmail(token);
            log.debug("Email extraído do token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Carregando UserDetails para: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // ✅ CORREÇÃO: Usar jwtUtil injetado
                if (jwtUtil.validateToken(token)) {
                    var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Usuário autenticado via JWT: {} - {} {}", username, method, path);
                } else {
                    log.warn("Token JWT inválido para: {} - {} {}", username, method, path);
                }
            } else if (username == null) {
                log.warn("Não foi possível extrair email do token - {} {}", method, path);
            } else {
                log.debug("Usuário já autenticado no contexto: {} - {} {}", username, method, path);
            }

        } catch (Exception e) {
            log.error("Erro durante processamento do token JWT - {} {}: {}", 
                     method, path, e.getMessage());
            // Não interrompe a cadeia - continua para próximo filtro
        }

        // 3) SEMPRE continuar a cadeia
        chain.doFilter(request, response);
        log.debug("Filtro JWT concluído para: {} {}", method, path);
    }
}