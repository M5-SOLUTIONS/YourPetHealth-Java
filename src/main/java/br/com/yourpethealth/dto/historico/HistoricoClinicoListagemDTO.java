package br.com.yourpethealth.dto.historico;

import br.com.yourpethealth.entity.historico.TipoHistorico;

import java.time.LocalDate;

public record HistoricoClinicoListagemDTO(
        Long id,
        Long petId,
        TipoHistorico tipo,
        String descricao,
        LocalDate data
) {
}