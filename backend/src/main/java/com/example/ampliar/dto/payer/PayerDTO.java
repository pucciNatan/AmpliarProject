package com.example.ampliar.dto.payer;

public record PayerDTO(
        Long id,
        String fullName,
        String cpf,
        String phoneNumber
) {}
