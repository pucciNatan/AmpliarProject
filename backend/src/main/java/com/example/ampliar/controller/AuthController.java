package com.example.ampliar.controller;

import com.example.ampliar.dto.AuthRequestDTO;
import com.example.ampliar.dto.AuthResponseDTO;
import com.example.ampliar.dto.psychologist.PsychologistCreateDTO;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.security.JwtUtil;
import com.example.ampliar.service.PsychologistService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final PsychologistService psychologistService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // Adicionar JwtUtil injetado

    // CORREÇÃO: Adicionar JwtUtil no construtor
    public AuthController(PsychologistService psychologistService, JwtUtil jwtUtil) {
        this.psychologistService = psychologistService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody PsychologistCreateDTO request) {
        log.info("Recebida requisição POST /auth/register - Registrar psicólogo: {}", request.email());
        try {
            var result = psychologistService.createPsychologist(request);
            log.info("Registro realizado com sucesso - Email: {}, ID: {}", request.email(), result.id());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação no registro - Email: {} - {}", request.email(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erro interno no registro - Email: {} - {}", request.email(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro interno no servidor"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        log.info("Recebida requisição POST /auth/login - Login: {}", request.email());
        
        try {
            Optional<PsychologistModel> userOpt = psychologistService.findByEmail(request.email());

            if (userOpt.isEmpty()) {
                log.warn("Tentativa de login com email não encontrado: {}", request.email());
                return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas"));
            }

            PsychologistModel user = userOpt.get();
            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                log.warn("Tentativa de login com senha incorreta para: {}", request.email());
                return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas"));
            }

            // CORREÇÃO: Usar jwtUtil injetado em vez de método estático
            String token = jwtUtil.generateToken(user.getEmail());
            
            // CORREÇÃO: AuthResponseDTO sendo utilizado corretamente
            AuthResponseDTO response = new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getId(),
                user.getFullName()
            );
            
            log.info("Login realizado com sucesso - Email: {}, ID: {}, Token gerado", user.getEmail(), user.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro interno no login - Email: {} - {}", request.email(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro interno no servidor"));
        }
    }
}