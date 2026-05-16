package br.com.yourpethealth.dto.veterinario;

public record VeterinarioListagemDTO(

        Long id,
        String nome,
        String email,
        String telefone,
        String crmv,
        String especialidade
) {}