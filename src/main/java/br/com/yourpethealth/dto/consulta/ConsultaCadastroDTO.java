package br.com.yourpethealth.dto.consulta;

import br.com.yourpethealth.entity.consulta.StatusConsulta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConsultaCadastroDTO(

        @NotNull(message = "O pet é obrigatório")
        Long petId,

        @NotBlank(message = "O tipo é obrigatório")
        String tipo,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        String veterinario,

        String observacoes,

        @NotNull(message = "O status é obrigatório")
        StatusConsulta status

) {
}