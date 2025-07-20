package com.example.ampliar.controller;

import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.security.JwtUtil;
import com.example.ampliar.service.PsychologistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PsychologistService psychologistService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        PsychologistModel newUser = new PsychologistModel(
                request.get("fullName"),
                request.get("cpf"),
                request.get("phoneNumber"),
                request.get("email"),
                request.get("password")
        );
        return ResponseEntity.ok(psychologistService.createPsychologist(newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        Optional<PsychologistModel> userOpt = psychologistService.findByEmail(request.get("email"));

        if (userOpt.isPresent()) {
            PsychologistModel user = userOpt.get();
            boolean passwordMatches = passwordEncoder.matches(request.get("password"), user.getPassword());

            if (passwordMatches) {
                String token = JwtUtil.generateToken(user.getEmail());
                return ResponseEntity.ok(Map.of("token", token));
            }
        }

        return ResponseEntity.status(401).body("Credenciais inválidas");
    }
}
