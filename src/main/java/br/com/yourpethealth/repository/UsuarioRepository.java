package br.com.yourpethealth.repository;

import br.com.yourpethealth.entity.Usuario;
import br.com.yourpethealth.entity.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPerfil(Perfil perfil);

    long countByAtivo(Boolean ativo);

    List<Usuario> findAllByOrderByCriadoEmDesc();
}