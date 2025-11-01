package com.example.ampliar.dto;


public record AuthResponseDTO(
        String token,      
        String email,       
        Long userId,       
        String fullName    
) {}