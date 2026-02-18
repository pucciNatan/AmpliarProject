package com.example.ampliar.dto;

import java.util.List;

public record ErrorResponseDTO(String message, List<FieldErrorDTO> errors) {

    public static ErrorResponseDTO of(String message) {
        return new ErrorResponseDTO(message, null);
    }

    public static ErrorResponseDTO of(String message, List<FieldErrorDTO> errors) {
        return new ErrorResponseDTO(message, errors);
    }
}
