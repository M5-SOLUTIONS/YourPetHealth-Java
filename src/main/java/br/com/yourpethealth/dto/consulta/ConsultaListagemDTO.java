package br.com.yourpethealth.dto.consulta;

import br.com.yourpethealth.entity.consulta.StatusConsulta;

import java.time.LocalDate;

public record ConsultaListagemDTO(
        Long id,
        Long petId,
        Long veterinarioId,
        String tipo,
        String descricao,
        LocalDate data,
        String observacoes,
        StatusConsulta status
) {}