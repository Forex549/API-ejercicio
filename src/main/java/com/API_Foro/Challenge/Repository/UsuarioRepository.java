package com.API_Foro.Challenge.Repository;

import com.API_Foro.Challenge.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByLogin(String username);
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
}
