package br.com.yourpethealth.repository;

import br.com.yourpethealth.entity.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
