package br.com.yourpethealth.dto.veterinario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VeterinarioCadastroDTO(

        @NotBlank
        String nome,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String senha,

        String telefone,

        @NotBlank
        String crmv,

        String especialidade
) {}