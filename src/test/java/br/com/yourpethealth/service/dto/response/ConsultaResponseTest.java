package br.com.yourpethealth.dto.response;

import br.com.yourpethealth.entity.Consulta;
import br.com.yourpethealth.entity.Pet;
import br.com.yourpethealth.entity.Responsavel;
import br.com.yourpethealth.entity.Veterinario;
import br.com.yourpethealth.entity.enums.SexoPet;
import br.com.yourpethealth.entity.enums.StatusConsulta;
import br.com.yourpethealth.entity.enums.TipoConsulta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regra das 24 horas: o app e as telas web decidem se mostram
 * "Reagendar" e "Cancelar" a partir de podeEditar e podeCancelar.
 */
class ConsultaResponseTest {

    private static Consulta consulta(LocalDateTime data, StatusConsulta status) {
        var dono = Responsavel.builder().id(1L).nome("Ana Souza").email("ana@email.com").build();
        var pet = Pet.builder().id(10L).responsavel(dono).nome("Thor").idade(3).sexoPet(SexoPet.MACHO).build();
        var vet = Veterinario.builder().id(20L).nome("Dra. Carla Lima").email("carla@clinica.com").crmv("SP-12345").build();

        return Consulta.builder()
                .id(100L)
                .pet(pet)
                .veterinario(vet)
                .tipo(TipoConsulta.ROTINA)
                .data(data)
                .status(status)
                .build();
    }

    @Test
    @DisplayName("agendada para daqui a mais de 24h: pode editar e cancelar")
    void maisDe24Horas() {
        var resposta = ConsultaResponse.from(
                consulta(LocalDateTime.now().plusHours(25), StatusConsulta.AGENDADA));

        assertThat(resposta.podeEditar()).isTrue();
        assertThat(resposta.podeCancelar()).isTrue();
    }

    @Test
    @DisplayName("agendada para daqui a menos de 24h: não pode editar nem cancelar")
    void menosDe24Horas() {
        var resposta = ConsultaResponse.from(
                consulta(LocalDateTime.now().plusHours(23), StatusConsulta.AGENDADA));

        assertThat(resposta.podeEditar()).isFalse();
        assertThat(resposta.podeCancelar()).isFalse();
    }

    @Test
    @DisplayName("consulta realizada nunca é editável, mesmo com data distante")
    void realizadaNaoEditavel() {
        var resposta = ConsultaResponse.from(
                consulta(LocalDateTime.now().plusDays(10), StatusConsulta.REALIZADA));

        assertThat(resposta.podeEditar()).isFalse();
        assertThat(resposta.podeCancelar()).isFalse();
    }

    @Test
    @DisplayName("converte os dados de pet e veterinário para o JSON")
    void mapeiaCampos() {
        var resposta = ConsultaResponse.from(
                consulta(LocalDateTime.now().plusDays(3), StatusConsulta.AGENDADA));

        assertThat(resposta.petId()).isEqualTo(10L);
        assertThat(resposta.petNome()).isEqualTo("Thor");
        assertThat(resposta.veterinarioId()).isEqualTo(20L);
        assertThat(resposta.veterinarioNome()).isEqualTo("Dra. Carla Lima");
        assertThat(resposta.tipo()).isEqualTo(TipoConsulta.ROTINA);
    }
}