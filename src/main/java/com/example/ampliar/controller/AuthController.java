package com.example.ampliar.controller;

import com.example.ampliar.dto.AuthRequestDTO;
import com.example.ampliar.dto.PsychologistCreateDTO;
import com.example.ampliar.mapper.PsychologistDTOMapper;
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

    @Autowired
    private PsychologistDTOMapper psychologistDTOMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody PsychologistCreateDTO request) {
        return ResponseEntity.ok(psychologistService.createPsychologist(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        Optional<PsychologistModel> userOpt = psychologistService.findByEmail(request.email());

        if (userOpt.isPresent()) {
            PsychologistModel user = userOpt.get();
            boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPassword());

            if (passwordMatches) {
                String token = JwtUtil.generateToken(user.getEmail());
                return ResponseEntity.ok(Map.of("token", token));
            }
        }

        return ResponseEntity.status(401).body("Credenciais inválidas");
    }
}
