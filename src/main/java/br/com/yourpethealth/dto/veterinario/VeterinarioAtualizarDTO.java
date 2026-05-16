package br.com.yourpethealth.dto.veterinario;

public record VeterinarioAtualizarDTO(

        String nome,
        String email,
        String senha,
        String telefone,
        String crmv,
        String especialidade
) {}