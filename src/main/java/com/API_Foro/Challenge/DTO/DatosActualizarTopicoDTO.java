package com.API_Foro.Challenge.DTO;

import com.API_Foro.Challenge.domain.Status;

public record DatosActualizarTopicoDTO(
        String titulo,
        String mensaje,
        Status status

) {
}
