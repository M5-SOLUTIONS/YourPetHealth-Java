package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.view.DashboardResumo;
import br.com.yourpethealth.dto.view.DashboardResumo.DesempenhoVeterinario;
import br.com.yourpethealth.dto.view.DashboardResumo.ParticipacaoRaca;
import br.com.yourpethealth.dto.view.DashboardResumo.Serie;
import br.com.yourpethealth.entity.Pet;
import br.com.yourpethealth.entity.enums.StatusConsulta;
import br.com.yourpethealth.entity.enums.TipoConsulta;
import br.com.yourpethealth.repository.ConsultaRepository;
import br.com.yourpethealth.repository.PetRepository;
import br.com.yourpethealth.repository.ResponsavelRepository;
import br.com.yourpethealth.repository.UsuarioRepository;
import br.com.yourpethealth.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int MESES_NO_GRAFICO = 6;
    private static final int LIMITE_RANKING = 5;
    private static final int DIAS_DE_ANTECEDENCIA = 7;
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final ResponsavelRepository responsavelRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public DashboardResumo resumo() {
        LocalDateTime agora = LocalDateTime.now();

        return new DashboardResumo(
                responsavelRepository.count(),
                veterinarioRepository.count(),
                petRepository.count(),
                consultaRepository.count(),
                consultaRepository.countByStatusAndDataBetween(
                        StatusConsulta.AGENDADA, agora, agora.plusDays(DIAS_DE_ANTECEDENCIA)),
                usuarioRepository.countByAtivo(false),
                taxaRealizacao(),
                consultasPorMes(YearMonth.from(agora)),
                serie(StatusConsulta.values(), this::rotuloStatus, consultaRepository::countByStatus),
                serie(TipoConsulta.values(), this::rotuloTipo, consultaRepository::countByTipo),
                rankingVeterinarios(),
                racasMaisComuns());
    }

    /** Realizadas sobre as consultas já encerradas (realizadas + canceladas). */
    private Integer taxaRealizacao() {
        long realizadas = consultaRepository.countByStatus(StatusConsulta.REALIZADA);
        long canceladas = consultaRepository.countByStatus(StatusConsulta.CANCELADA);
        long encerradas = realizadas + canceladas;

        return encerradas == 0 ? null : Math.round(realizadas * 100f / encerradas);
    }

    private Serie consultasPorMes(YearMonth mesAtual) {
        List<String> rotulos = new ArrayList<>();
        List<Long> valores = new ArrayList<>();

        for (int recuo = MESES_NO_GRAFICO - 1; recuo >= 0; recuo--) {
            YearMonth mes = mesAtual.minusMonths(recuo);
            rotulos.add(rotuloDoMes(mes));
            valores.add(consultaRepository.countByDataBetween(
                    mes.atDay(1).atStartOfDay(),
                    mes.atEndOfMonth().atTime(LocalTime.MAX)));
        }
        return new Serie(rotulos, valores);
    }

    private <E extends Enum<E>> Serie serie(E[] valores,
                                            Function<E, String> rotulo,
                                            ToLongFunction<E> contagem) {
        return new Serie(
                Arrays.stream(valores).map(rotulo).toList(),
                Arrays.stream(valores).map(v -> contagem.applyAsLong(v)).toList());
    }

    private List<DesempenhoVeterinario> rankingVeterinarios() {
        return veterinarioRepository.findAll().stream()
                .map(v -> new DesempenhoVeterinario(
                        v.getNome(),
                        v.getEspecialidade(),
                        consultaRepository.countByVeterinarioIdAndStatus(
                                v.getId(), StatusConsulta.AGENDADA),
                        consultaRepository.countByVeterinarioIdAndStatus(
                                v.getId(), StatusConsulta.REALIZADA)))
                .sorted(Comparator.comparingLong(DesempenhoVeterinario::realizadas)
                        .thenComparingLong(DesempenhoVeterinario::agendadas)
                        .reversed())
                .limit(LIMITE_RANKING)
                .toList();
    }

    private List<ParticipacaoRaca> racasMaisComuns() {
        List<Pet> pets = petRepository.findAll();
        if (pets.isEmpty()) {
            return List.of();
        }

        Map<String, Long> totalPorRaca = pets.stream()
                .collect(Collectors.groupingBy(this::racaInformada, Collectors.counting()));

        return totalPorRaca.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(LIMITE_RANKING)
                .map(e -> new ParticipacaoRaca(e.getKey(), e.getValue(),
                        Math.round(e.getValue() * 100f / pets.size())))
                .toList();
    }

    private String racaInformada(Pet pet) {
        String raca = pet.getRaca();
        return raca == null || raca.isBlank() ? "Não informada" : raca.trim();
    }

    private String rotuloStatus(StatusConsulta status) {
        return switch (status) {
            case AGENDADA -> "Agendadas";
            case REALIZADA -> "Realizadas";
            case CANCELADA -> "Canceladas";
        };
    }

    private String rotuloTipo(TipoConsulta tipo) {
        return switch (tipo) {
            case ROTINA -> "Rotina";
            case VACINACAO -> "Vacinação";
            case EMERGENCIA -> "Emergência";
            case RETORNO -> "Retorno";
            case EXAME -> "Exame";
        };
    }

    private String rotuloDoMes(YearMonth mes) {
        String nome = mes.getMonth().getDisplayName(TextStyle.SHORT, PT_BR).replace(".", "");
        return nome + "/" + String.format("%02d", mes.getYear() % 100);
    }
}