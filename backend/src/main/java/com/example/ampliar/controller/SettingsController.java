package com.example.ampliar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ampliar.dto.settings.UserSettingsDTO;
import com.example.ampliar.dto.settings.UserSettingsUpdateDTO;
import com.example.ampliar.service.UserSettingsService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/settings")
@Slf4j
public class SettingsController {

    private final UserSettingsService userSettingsService;

    public SettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping("/{psychologistId}")
    public ResponseEntity<UserSettingsDTO> getSettings(@PathVariable Long psychologistId) {
        log.debug("Recebida requisição GET /settings/{}", psychologistId);
        UserSettingsDTO settings = userSettingsService.getSettings(psychologistId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/{psychologistId}")
    public ResponseEntity<UserSettingsDTO> updateSettings(
            @PathVariable Long psychologistId,
            @Valid @RequestBody UserSettingsUpdateDTO dto
    ) {
        log.info("Recebida requisição PUT /settings/{}", psychologistId);
        UserSettingsDTO updated = userSettingsService.updateSettings(psychologistId, dto);
        return ResponseEntity.ok(updated);
    }
}
