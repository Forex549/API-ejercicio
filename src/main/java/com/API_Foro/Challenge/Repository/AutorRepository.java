package com.API_Foro.Challenge.Repository;

import com.API_Foro.Challenge.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Usuario, Long> {
}
