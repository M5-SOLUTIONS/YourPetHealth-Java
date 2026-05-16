package br.com.yourpethealth.dto.responsavel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResponsavelAtualizarDTO(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        String senha,

        String telefone

) {
}