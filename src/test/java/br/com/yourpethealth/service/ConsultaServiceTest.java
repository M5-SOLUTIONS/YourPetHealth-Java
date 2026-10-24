package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.request.ConsultaConclusaoRequest;
import br.com.yourpethealth.dto.request.ConsultaRequest;
import br.com.yourpethealth.entity.Consulta;
import br.com.yourpethealth.entity.HistoricoClinico;
import br.com.yourpethealth.entity.Pet;
import br.com.yourpethealth.entity.Responsavel;
import br.com.yourpethealth.entity.Veterinario;
import br.com.yourpethealth.entity.enums.SexoPet;
import br.com.yourpethealth.entity.enums.StatusConsulta;
import br.com.yourpethealth.entity.enums.TipoConsulta;
import br.com.yourpethealth.entity.enums.TipoHistorico;
import br.com.yourpethealth.exception.AcessoNegadoException;
import br.com.yourpethealth.exception.RegraNegocioException;
import br.com.yourpethealth.repository.ConsultaRepository;
import br.com.yourpethealth.repository.HistoricoClinicoRepository;
import br.com.yourpethealth.repository.PetRepository;
import br.com.yourpethealth.repository.VeterinarioRepository;
import br.com.yourpethealth.security.UsuarioLogado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    private static final Long DONO_ID = 1L;
    private static final Long OUTRO_RESPONSAVEL_ID = 2L;
    private static final Long PET_ID = 10L;
    private static final Long VET_ID = 20L;
    private static final Long OUTRO_VET_ID = 21L;
    private static final Long CONSULTA_ID = 100L;

    // Uma semana à frente: longe da janela de 24h que define podeEditar/podeCancelar
    private static final LocalDateTime DATA = LocalDateTime.now().plusDays(7)
            .withHour(10).withMinute(0).withSecond(0).withNano(0);

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private HistoricoClinicoRepository historicoRepository;

    @Mock
    private UsuarioLogado usuarioLogado;

    @Mock
    private RegrasAgendamento regrasAgendamento;

    @InjectMocks
    private ConsultaService service;

    private Pet thor;
    private Veterinario vet;

    @BeforeEach
    void setUp() {
        var dono = Responsavel.builder()
                .id(DONO_ID)
                .nome("Ana Souza")
                .email("ana@email.com")
                .build();

        thor = Pet.builder()
                .id(PET_ID)
                .responsavel(dono)
                .nome("Thor")
                .idade(3)
                .sexoPet(SexoPet.MACHO)
                .build();

        vet = Veterinario.builder()
                .id(VET_ID)
                .nome("Dra. Carla Lima")
                .email("carla@clinica.com")
                .crmv("SP-12345")
                .especialidade("Clínica geral")
                .build();
    }

    private Consulta consulta(StatusConsulta status) {
        return Consulta.builder()
                .id(CONSULTA_ID)
                .pet(thor)
                .veterinario(vet)
                .tipo(TipoConsulta.ROTINA)
                .descricao("Check-up semestral")
                .data(DATA)
                .status(status)
                .build();
    }

    // ---------- Agendar ----------

    @Test
    @DisplayName("criar: valida as regras de agendamento e salva como AGENDADA")
    void agendaConsulta() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));
        when(veterinarioRepository.findById(VET_ID)).thenReturn(Optional.of(vet));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        var request = new ConsultaRequest(PET_ID, VET_ID, TipoConsulta.ROTINA, "Check-up semestral", DATA);
        var resposta = service.criar(request);

        // As regras precisam rodar ANTES de gravar
        InOrder ordem = inOrder(regrasAgendamento, consultaRepository);
        ordem.verify(regrasAgendamento).validar(PET_ID, VET_ID, DATA, null);
        ordem.verify(consultaRepository).save(any(Consulta.class));

        assertThat(resposta.status()).isEqualTo(StatusConsulta.AGENDADA);
        assertThat(resposta.petId()).isEqualTo(PET_ID);
        assertThat(resposta.veterinarioId()).isEqualTo(VET_ID);
        assertThat(resposta.podeEditar()).isTrue();
    }

    @Test
    @DisplayName("criar: não agenda para pet de outro responsável")
    void naoAgendaParaPetDeOutroResponsavel() {
        when(usuarioLogado.responsavelId()).thenReturn(OUTRO_RESPONSAVEL_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));

        var request = new ConsultaRequest(PET_ID, VET_ID, TipoConsulta.ROTINA, null, DATA);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(AcessoNegadoException.class);

        verifyNoInteractions(regrasAgendamento);
        verify(consultaRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar: se uma regra de agendamento falha, nada é salvo")
    void regraVioladaNaoSalva() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));
        when(veterinarioRepository.findById(VET_ID)).thenReturn(Optional.of(vet));
        doThrow(new RegraNegocioException("O veterinário já possui uma consulta agendada próxima a este horário"))
                .when(regrasAgendamento).validar(PET_ID, VET_ID, DATA, null);

        var request = new ConsultaRequest(PET_ID, VET_ID, TipoConsulta.ROTINA, null, DATA);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class);

        verify(consultaRepository, never()).save(any());
    }

    // ---------- Cancelar ----------

    @Test
    @DisplayName("cancelar: muda o status para CANCELADA e mantém o registro")
    void cancelaConsultaAgendada() {
        var consulta = consulta(StatusConsulta.AGENDADA);
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(consultaRepository.findById(CONSULTA_ID)).thenReturn(Optional.of(consulta));
        when(consultaRepository.save(consulta)).thenReturn(consulta);

        var resposta = service.cancelar(CONSULTA_ID);

        assertThat(resposta.status()).isEqualTo(StatusConsulta.CANCELADA);
        verify(consultaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("cancelar: consulta já realizada não pode ser cancelada")
    void naoCancelaConsultaRealizada() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(consultaRepository.findById(CONSULTA_ID))
                .thenReturn(Optional.of(consulta(StatusConsulta.REALIZADA)));

        assertThatThrownBy(() -> service.cancelar(CONSULTA_ID))
                .isInstanceOf(RegraNegocioException.class);

        verify(consultaRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelar: outro responsável recebe acesso negado")
    void outroResponsavelNaoCancela() {
        when(usuarioLogado.responsavelId()).thenReturn(OUTRO_RESPONSAVEL_ID);
        when(consultaRepository.findById(CONSULTA_ID))
                .thenReturn(Optional.of(consulta(StatusConsulta.AGENDADA)));

        assertThatThrownBy(() -> service.cancelar(CONSULTA_ID))
                .isInstanceOf(AcessoNegadoException.class);

        verify(consultaRepository, never()).save(any());
    }

    // ---------- Concluir ----------

    @Test
    @DisplayName("concluir: marca como REALIZADA e gera o registro no histórico clínico")
    void concluiConsultaEGeraHistorico() {
        var consulta = consulta(StatusConsulta.AGENDADA);
        when(usuarioLogado.veterinarioId()).thenReturn(VET_ID);
        when(consultaRepository.findById(CONSULTA_ID)).thenReturn(Optional.of(consulta));
        when(consultaRepository.save(consulta)).thenReturn(consulta);

        var observacoes = "Animal saudável, vacinas em dia. Retorno em seis meses.";
        var resposta = service.concluir(CONSULTA_ID, new ConsultaConclusaoRequest(observacoes));

        assertThat(resposta.status()).isEqualTo(StatusConsulta.REALIZADA);
        assertThat(resposta.observacoes()).isEqualTo(observacoes);

        var historico = ArgumentCaptor.forClass(HistoricoClinico.class);
        verify(historicoRepository).save(historico.capture());

        assertThat(historico.getValue().getPet()).isSameAs(thor);
        assertThat(historico.getValue().getTipo()).isEqualTo(TipoHistorico.CONSULTA);
        assertThat(historico.getValue().getDescricao()).isEqualTo(observacoes);
        assertThat(historico.getValue().getData()).isNotNull();
    }

    @Test
    @DisplayName("concluir: veterinário não conclui consulta de outro veterinário")
    void outroVeterinarioNaoConclui() {
        var consulta = consulta(StatusConsulta.AGENDADA);
        when(usuarioLogado.veterinarioId()).thenReturn(OUTRO_VET_ID);
        when(consultaRepository.findById(CONSULTA_ID)).thenReturn(Optional.of(consulta));

        var request = new ConsultaConclusaoRequest("Tentativa de concluir consulta alheia.");

        assertThatThrownBy(() -> service.concluir(CONSULTA_ID, request))
                .isInstanceOf(AcessoNegadoException.class);

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.AGENDADA);
        verifyNoInteractions(historicoRepository);
    }

    @Test
    @DisplayName("concluir: consulta já realizada não gera um segundo histórico")
    void naoConcluiDuasVezes() {
        when(usuarioLogado.veterinarioId()).thenReturn(VET_ID);
        when(consultaRepository.findById(CONSULTA_ID))
                .thenReturn(Optional.of(consulta(StatusConsulta.REALIZADA)));

        var request = new ConsultaConclusaoRequest("Segunda conclusão da mesma consulta.");

        assertThatThrownBy(() -> service.concluir(CONSULTA_ID, request))
                .isInstanceOf(RegraNegocioException.class);

        verifyNoInteractions(historicoRepository);
    }

    // ---------- Excluir ----------

    @Test
    @DisplayName("remover: o dono exclui uma consulta agendada")
    void excluiConsultaAgendada() {
        var consulta = consulta(StatusConsulta.AGENDADA);
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(consultaRepository.findById(CONSULTA_ID)).thenReturn(Optional.of(consulta));

        service.remover(CONSULTA_ID);

        verify(consultaRepository).delete(consulta);
    }

    @Test
    @DisplayName("remover: consulta cancelada não pode ser excluída")
    void naoExcluiConsultaCancelada() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(consultaRepository.findById(CONSULTA_ID))
                .thenReturn(Optional.of(consulta(StatusConsulta.CANCELADA)));

        assertThatThrownBy(() -> service.remover(CONSULTA_ID))
                .isInstanceOf(RegraNegocioException.class);

        verify(consultaRepository, never()).delete(any());
    }
}