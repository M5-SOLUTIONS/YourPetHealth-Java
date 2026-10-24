package br.com.yourpethealth.service;

import br.com.yourpethealth.entity.Consulta;
import br.com.yourpethealth.entity.enums.StatusConsulta;
import br.com.yourpethealth.exception.RegraNegocioException;
import br.com.yourpethealth.exception.ValidacaoException;
import br.com.yourpethealth.repository.ConsultaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegrasAgendamentoTest {

    private static final Long PET_ID = 10L;
    private static final Long VET_ID = 20L;

    private static final int ANTECEDENCIA_HORAS = 2;
    private static final int ABERTURA = 8;
    private static final int FECHAMENTO = 18;
    private static final int JANELA_MINUTOS = 60;

    @Mock
    private ConsultaRepository consultaRepository;

    private RegrasAgendamento regras;

    @BeforeEach
    void setUp() {
        regras = new RegrasAgendamento(
                consultaRepository, ANTECEDENCIA_HORAS, ABERTURA, FECHAMENTO, JANELA_MINUTOS);
    }

    /**
     * Datas calculadas a partir de hoje, a pelo menos uma semana de distância:
     * o teste não depende do dia em que roda e fica longe da antecedência mínima.
     */
    private static LocalDateTime proximo(DayOfWeek dia, int hora, int minuto) {
        return LocalDate.now()
                .plusWeeks(1)
                .with(TemporalAdjusters.next(dia))
                .atTime(hora, minuto);
    }

    private static Consulta consultaComId(Long id) {
        return Consulta.builder().id(id).build();
    }

    // ---------- Horário válido ----------

    @Test
    @DisplayName("segunda-feira às 10h, sem conflitos: aceita")
    void horarioValido() {
        assertThatCode(() -> regras.validar(PET_ID, VET_ID, proximo(DayOfWeek.MONDAY, 10, 0), null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("sábado também é dia de atendimento")
    void sabadoValido() {
        assertThatCode(() -> regras.validar(PET_ID, VET_ID, proximo(DayOfWeek.SATURDAY, 10, 0), null))
                .doesNotThrowAnyException();
    }

    // ---------- Regra 2: antecedência ----------

    @Test
    @DisplayName("menos de 2 horas de antecedência: recusa")
    void antecedenciaInsuficiente() {
        var daquiUmaHora = LocalDateTime.now().plusHours(1);

        assertThatThrownBy(() -> regras.validar(PET_ID, VET_ID, daquiUmaHora, null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("antecedência");
    }

    // ---------- Regra 3: horário comercial ----------

    @Test
    @DisplayName("domingo: recusa")
    void domingo() {
        assertThatThrownBy(() -> regras.validar(PET_ID, VET_ID, proximo(DayOfWeek.SUNDAY, 10, 0), null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("domingos");
    }

    @Test
    @DisplayName("antes das 08:00: recusa")
    void antesDaAbertura() {
        assertThatThrownBy(() -> regras.validar(PET_ID, VET_ID, proximo(DayOfWeek.MONDAY, 7, 59), null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("horário de atendimento");
    }

    @Test
    @DisplayName("exatamente 18:00 é o último horário aceito")
    void dezoitoHorasAceito() {
        assertThatCode(() -> regras.validar(PET_ID, VET_ID, proximo(DayOfWeek.MONDAY, 18, 0), null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("depois das 18:00: recusa")
    void depoisDoFechamento() {
        assertThatThrownBy(() -> regras.validar(PET_ID, VET_ID, proximo(DayOfWeek.MONDAY, 18, 30), null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("horário de atendimento");
    }

    // ---------- Regra 4: agenda do veterinário ----------

    @Test
    @DisplayName("veterinário com consulta dentro da janela de 60 minutos: recusa")
    void conflitoNaAgendaDoVeterinario() {
        var data = proximo(DayOfWeek.MONDAY, 10, 0);
        when(consultaRepository.findByVeterinarioIdAndStatusAndDataBetween(
                eq(VET_ID), eq(StatusConsulta.AGENDADA), any(), any()))
                .thenReturn(List.of(consultaComId(5L)));

        assertThatThrownBy(() -> regras.validar(PET_ID, VET_ID, data, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("veterinário");

        // A janela consultada é de 60 minutos para cada lado do horário pedido
        verify(consultaRepository).findByVeterinarioIdAndStatusAndDataBetween(
                VET_ID, StatusConsulta.AGENDADA, data.minusMinutes(60), data.plusMinutes(60));
    }

    @Test
    @DisplayName("reagendamento não conflita com a própria consulta")
    void reagendamentoIgnoraAPropriaConsulta() {
        var data = proximo(DayOfWeek.MONDAY, 10, 0);
        when(consultaRepository.findByVeterinarioIdAndStatusAndDataBetween(
                eq(VET_ID), eq(StatusConsulta.AGENDADA), any(), any()))
                .thenReturn(List.of(consultaComId(5L)));

        // consultaId = 5: a única consulta "no caminho" é ela mesma
        assertThatCode(() -> regras.validar(PET_ID, VET_ID, data, 5L))
                .doesNotThrowAnyException();
    }

    // ---------- Regra 5: agenda do pet ----------

    @Test
    @DisplayName("pet já agendado no mesmo horário: recusa")
    void conflitoNaAgendaDoPet() {
        var data = proximo(DayOfWeek.MONDAY, 10, 0);
        when(consultaRepository.findByPetIdAndStatusAndData(PET_ID, StatusConsulta.AGENDADA, data))
                .thenReturn(List.of(consultaComId(7L)));

        assertThatThrownBy(() -> regras.validar(PET_ID, VET_ID, data, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("pet");
    }
}