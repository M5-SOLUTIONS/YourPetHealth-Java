package br.com.yourpethealth.dto.historico;

import br.com.yourpethealth.entity.historico.TipoHistorico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record HistoricoClinicoCadastroDTO(
        @NotNull(message = "O id do pet é obrigatório")
        Long petId,

        @NotNull(message = "O tipo do histórico é obrigatório")
        TipoHistorico tipo,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        String descricao,

        @NotNull(message = "A data é obrigatória")
        LocalDate data
) {
}