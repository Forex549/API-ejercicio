package com.API_Foro.Challenge.DTO;

import com.API_Foro.Challenge.domain.Status;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record RegistroTipocoDTO(
        @NotBlank
        String titulo,
        @NotBlank
        String mensaje,
        @NotBlank
        Long cursoId,
        @NotBlank
        Long autorId
) {
}
