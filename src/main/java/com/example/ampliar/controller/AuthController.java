package com.example.ampliar.controller;

import com.example.ampliar.dto.AuthRequestDTO;
import com.example.ampliar.dto.psychologist.PsychologistCreateDTO;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.security.JwtUtil;
import com.example.ampliar.service.PsychologistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PsychologistService psychologistService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(PsychologistService psychologistService) {
        this.psychologistService = psychologistService;
        this.passwordEncoder = new BCryptPasswordEncoder(); // pode virar um @Bean se for compartilhado
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody PsychologistCreateDTO request) {
        return ResponseEntity.ok(psychologistService.createPsychologist(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        Optional<PsychologistModel> userOpt = psychologistService.findByEmail(request.email());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas"));
        }

        PsychologistModel user = userOpt.get();
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas"));
        }

        String token = JwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
