package br.com.yourpethealth.dto.consulta;

import br.com.yourpethealth.entity.consulta.StatusConsulta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ConsultaAtualizarDTO(

        @NotNull(message = "O id do veterinário é obrigatório")
        Long veterinarioId,

        @NotBlank(message = "O tipo da consulta é obrigatório")
        @Size(max = 100, message = "O tipo deve ter no máximo 100 caracteres")
        String tipo,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        String descricao,

        @NotNull(message = "A data da consulta é obrigatória")
        LocalDate data,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String observacoes,

        @NotNull(message = "O status da consulta é obrigatório")
        StatusConsulta status
) {
}