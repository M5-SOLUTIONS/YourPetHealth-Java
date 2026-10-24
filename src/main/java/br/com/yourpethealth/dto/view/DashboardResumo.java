package br.com.yourpethealth.dto.view;

import java.util.List;

public record DashboardResumo(
        long totalResponsaveis,
        long totalVeterinarios,
        long totalPets,
        long totalConsultas,
        long agendadasProximos7Dias,
        long contasDesativadas,
        Integer taxaRealizacao,
        Serie consultasPorMes,
        Serie consultasPorStatus,
        Serie consultasPorTipo,
        List<DesempenhoVeterinario> rankingVeterinarios,
        List<ParticipacaoRaca> racasMaisComuns
) {

    /** Rótulos e valores alinhados por índice, no formato que o Chart.js consome. */
    public record Serie(List<String> rotulos, List<Long> valores) {}

    public record DesempenhoVeterinario(
            String nome, String especialidade, long agendadas, long realizadas) {}

    public record ParticipacaoRaca(String raca, long total, int percentual) {}
}