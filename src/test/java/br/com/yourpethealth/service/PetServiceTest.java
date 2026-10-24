package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.request.PetRequest;
import br.com.yourpethealth.entity.Pet;
import br.com.yourpethealth.entity.Responsavel;
import br.com.yourpethealth.entity.enums.SexoPet;
import br.com.yourpethealth.entity.enums.StatusConsulta;
import br.com.yourpethealth.exception.AcessoNegadoException;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.exception.RegraNegocioException;
import br.com.yourpethealth.repository.ConsultaRepository;
import br.com.yourpethealth.repository.PetRepository;
import br.com.yourpethealth.repository.ResponsavelRepository;
import br.com.yourpethealth.security.UsuarioLogado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    private static final Long DONO_ID = 1L;
    private static final Long OUTRO_RESPONSAVEL_ID = 2L;
    private static final Long PET_ID = 10L;

    @Mock
    private PetRepository petRepository;

    @Mock
    private ResponsavelRepository responsavelRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private UsuarioLogado usuarioLogado;

    @InjectMocks
    private PetService service;

    private Responsavel dono;
    private Pet thor;

    @BeforeEach
    void setUp() {
        dono = Responsavel.builder()
                .id(DONO_ID)
                .nome("Ana Souza")
                .email("ana@email.com")
                .build();

        thor = Pet.builder()
                .id(PET_ID)
                .responsavel(dono)
                .nome("Thor")
                .raca("Golden Retriever")
                .idade(3)
                .peso(new BigDecimal("32.5"))
                .sexoPet(SexoPet.MACHO)
                .build();
    }

    // ---------- Criar ----------

    @Test
    @DisplayName("criar: o dono do pet é o responsável do token")
    void criarUsaResponsavelDoToken() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(responsavelRepository.findById(DONO_ID)).thenReturn(Optional.of(dono));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        var request = new PetRequest("Thor", "Golden Retriever", 3, new BigDecimal("32.5"), SexoPet.MACHO);
        var resposta = service.criar(request);

        var salvo = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(salvo.capture());

        assertThat(salvo.getValue().getResponsavel()).isSameAs(dono);
        assertThat(resposta.nome()).isEqualTo("Thor");
        assertThat(resposta.sexo()).isEqualTo(SexoPet.MACHO);
        assertThat(resposta.responsavelId()).isEqualTo(DONO_ID);
    }

    // ---------- Listar ----------

    @Test
    @DisplayName("listar: devolve só os pets do responsável logado")
    void listarFiltraPeloResponsavelLogado() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(petRepository.findByResponsavelId(DONO_ID)).thenReturn(List.of(thor));

        var pets = service.listar();

        assertThat(pets).hasSize(1);
        assertThat(pets.get(0).nome()).isEqualTo("Thor");
        verify(petRepository, never()).findAll();
    }

    // ---------- Buscar por id ----------

    @Test
    @DisplayName("buscarPorId: o dono consegue ler o próprio pet")
    void donoLePet() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));

        var resposta = service.buscarPorId(PET_ID);

        assertThat(resposta.id()).isEqualTo(PET_ID);
    }

    @Test
    @DisplayName("buscarPorId: outro responsável recebe acesso negado")
    void outroResponsavelNaoLePet() {
        when(usuarioLogado.responsavelId()).thenReturn(OUTRO_RESPONSAVEL_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));

        assertThatThrownBy(() -> service.buscarPorId(PET_ID))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    @DisplayName("buscarPorId: veterinário pode ler o pet de qualquer responsável")
    void veterinarioLeQualquerPet() {
        when(usuarioLogado.ehVeterinario()).thenReturn(true);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));

        var resposta = service.buscarPorId(PET_ID);

        assertThat(resposta.nome()).isEqualTo("Thor");
    }

    @Test
    @DisplayName("buscarPorId: id inexistente lança IdNaoEncontradoException")
    void petInexistente() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(IdNaoEncontradoException.class);
    }

    // ---------- Atualizar ----------

    @Test
    @DisplayName("atualizar: o dono altera os dados do pet")
    void donoAtualizaPet() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        var request = new PetRequest("Thor Jr.", "Golden Retriever", 4, new BigDecimal("34.0"), SexoPet.MACHO);
        var resposta = service.atualizar(PET_ID, request);

        assertThat(resposta.nome()).isEqualTo("Thor Jr.");
        assertThat(resposta.idade()).isEqualTo(4);
        assertThat(resposta.peso()).isEqualByComparingTo("34.0");
    }

    @Test
    @DisplayName("atualizar: outro responsável não altera nem salva nada")
    void outroResponsavelNaoAtualiza() {
        when(usuarioLogado.responsavelId()).thenReturn(OUTRO_RESPONSAVEL_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));

        var request = new PetRequest("Invasor", null, 1, null, SexoPet.FEMEA);

        assertThatThrownBy(() -> service.atualizar(PET_ID, request))
                .isInstanceOf(AcessoNegadoException.class);

        assertThat(thor.getNome()).isEqualTo("Thor");
        verify(petRepository, never()).save(any());
    }

    // ---------- Remover ----------

    @Test
    @DisplayName("remover: o dono remove um pet sem consultas agendadas")
    void donoRemovePet() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));
        when(consultaRepository.existsByPetIdAndStatus(PET_ID, StatusConsulta.AGENDADA)).thenReturn(false);

        service.remover(PET_ID);

        verify(petRepository).delete(thor);
    }

    @Test
    @DisplayName("remover: pet com consulta agendada não pode ser removido")
    void naoRemovePetComConsultaAgendada() {
        when(usuarioLogado.responsavelId()).thenReturn(DONO_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));
        when(consultaRepository.existsByPetIdAndStatus(PET_ID, StatusConsulta.AGENDADA)).thenReturn(true);

        assertThatThrownBy(() -> service.remover(PET_ID))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("consultas agendadas");

        verify(petRepository, never()).delete(any());
    }

    @Test
    @DisplayName("remover: outro responsável recebe acesso negado antes de qualquer verificação")
    void outroResponsavelNaoRemove() {
        when(usuarioLogado.responsavelId()).thenReturn(OUTRO_RESPONSAVEL_ID);
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(thor));

        assertThatThrownBy(() -> service.remover(PET_ID))
                .isInstanceOf(AcessoNegadoException.class);

        verify(consultaRepository, never()).existsByPetIdAndStatus(any(), any());
        verify(petRepository, never()).delete(any());
    }
}