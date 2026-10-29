package br.com.yourpethealth.dto.view;

import br.com.yourpethealth.entity.enums.Perfil;

import java.time.LocalDateTime;

public record UsuarioAdminView(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        boolean ativo,
        LocalDateTime criadoEm,
        Long responsavelId,
        Long veterinarioId
) {}