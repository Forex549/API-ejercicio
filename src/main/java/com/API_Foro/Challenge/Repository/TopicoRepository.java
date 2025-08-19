package com.API_Foro.Challenge.Repository;

import com.API_Foro.Challenge.domain.Topico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    @Query("SELECT t FROM Topico t WHERE t.mensaje = :mensaje AND t.titulo = :titulo")
    Optional<Topico> buscsarPorMensajeTitulo (String titulo, String mensaje);
}
