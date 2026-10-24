package br.com.yourpethealth.config;

import br.com.yourpethealth.entity.Usuario;
import br.com.yourpethealth.entity.enums.Perfil;
import br.com.yourpethealth.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Garante que exista um administrador. O cadastro público recusa o perfil ADMIN,
 * então esta é a única porta de entrada para ele.
 */
@Component
public class AdminInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicializador.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String senha;

    public AdminInicializador(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${app.admin.email}") String email,
                              @Value("${app.admin.senha}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.senha = senha;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByPerfil(Perfil.ADMIN)) {
            return;
        }

        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Administrador não criado: o e-mail {} já pertence a outro usuário", email);
            return;
        }

        usuarioRepository.save(Usuario.builder()
                .email(email)
                .senhaHash(passwordEncoder.encode(senha))
                .perfil(Perfil.ADMIN)
                .ativo(true)
                .build());

        log.info("Usuário administrador criado: {}", email);
    }
}