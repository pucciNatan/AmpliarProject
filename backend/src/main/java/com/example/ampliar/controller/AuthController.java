package com.example.ampliar.controller;

import com.example.ampliar.dto.AuthRequestDTO;
import com.example.ampliar.dto.AuthResponseDTO;
import com.example.ampliar.dto.ForgotPasswordRequestDTO;
import com.example.ampliar.dto.ResetPasswordRequestDTO;
import com.example.ampliar.dto.psychologist.PsychologistCreateDTO;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.security.JwtUtil;
import com.example.ampliar.service.PasswordResetService;
import com.example.ampliar.service.PsychologistService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final PsychologistService psychologistService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;

    public AuthController(PsychologistService psychologistService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                          PasswordResetService passwordResetService) {
        this.psychologistService = psychologistService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody PsychologistCreateDTO request) {
        log.info("Recebida requisição POST /auth/register - Registrar psicólogo: {}", request.email());
        var result = psychologistService.createPsychologist(request);
        log.info("Registro realizado com sucesso - Email: {}, ID: {}", request.email(), result.id());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        log.info("Recebida requisição POST /auth/login - Login: {}", request.email());

        Optional<PsychologistModel> userOpt = psychologistService.findByEmail(request.email());

        if (userOpt.isEmpty()) {
            log.warn("Tentativa de login com email não encontrado: {}", request.email());
            return ResponseEntity.status(401).body(Map.of("message", "Credenciais inválidas"));
        }

        PsychologistModel user = userOpt.get();
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Tentativa de login com senha incorreta para: {}", request.email());
            return ResponseEntity.status(401).body(Map.of("message", "Credenciais inválidas"));
        }

        String token = jwtUtil.generateToken(user.getEmail());
        AuthResponseDTO response = new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getId(),
                user.getFullName()
        );

        log.info("Login realizado com sucesso - Email: {}, ID: {}", user.getEmail(), user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        log.info("Recebida requisição POST /auth/forgot-password para: {}", request.email());
        String token = passwordResetService.createPasswordResetToken(request.email());
        log.info("Token de redefinição gerado para {}", request.email());
        return ResponseEntity.ok(Map.of(
                "message", "Token de redefinição gerado com sucesso",
                "token", token
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        log.info("Recebida requisição POST /auth/reset-password");
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso"));
    }
}
