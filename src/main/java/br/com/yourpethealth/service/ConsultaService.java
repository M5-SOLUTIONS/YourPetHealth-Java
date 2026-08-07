package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.request.ConsultaAtualizacaoRequest;
import br.com.yourpethealth.dto.request.ConsultaConclusaoRequest;
import br.com.yourpethealth.dto.request.ConsultaRequest;
import br.com.yourpethealth.dto.response.ConsultaResponse;
import br.com.yourpethealth.entity.Consulta;
import br.com.yourpethealth.entity.HistoricoClinico;
import br.com.yourpethealth.entity.enums.StatusConsulta;
import br.com.yourpethealth.entity.enums.TipoHistorico;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.repository.ConsultaRepository;
import br.com.yourpethealth.repository.HistoricoClinicoRepository;
import br.com.yourpethealth.repository.PetRepository;
import br.com.yourpethealth.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final HistoricoClinicoRepository historicoRepository;

    @Transactional
    public ConsultaResponse criar(ConsultaRequest request) {
        var pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new IdNaoEncontradoException("Pet não encontrado"));
        var veterinario = veterinarioRepository.findById(request.veterinarioId())
                .orElseThrow(() -> new IdNaoEncontradoException("Veterinário não encontrado"));

        // TODO J4: as 5 regras de agendamento entram aqui

        var consulta = Consulta.builder()
                .pet(pet)
                .veterinario(veterinario)
                .tipo(request.tipo())
                .descricao(request.descricao())
                .data(request.data())
                .status(StatusConsulta.AGENDADA)   // nunca vem do cliente
                .build();

        return ConsultaResponse.from(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarPorPet(Long petId) {
        return consultaRepository.findByPetId(petId)
                .stream().map(ConsultaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarPorVeterinario(Long veterinarioId) {
        return consultaRepository.findByVeterinarioId(veterinarioId)
                .stream().map(ConsultaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscarPorId(Long id) {
        return ConsultaResponse.from(carregar(id));
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, ConsultaAtualizacaoRequest request) {
        var consulta = carregar(id);

        consulta.setTipo(request.tipo());
        consulta.setDescricao(request.descricao());
        consulta.setData(request.data());

        return ConsultaResponse.from(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaResponse cancelar(Long id) {
        var consulta = carregar(id);
        consulta.setStatus(StatusConsulta.CANCELADA);
        return ConsultaResponse.from(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaResponse concluir(Long id, ConsultaConclusaoRequest request) {
        var consulta = carregar(id);

        consulta.setStatus(StatusConsulta.REALIZADA);
        consulta.setObservacoes(request.observacoes());

        var historico = HistoricoClinico.builder()
                .pet(consulta.getPet())
                .tipo(TipoHistorico.CONSULTA)
                .descricao(request.observacoes())
                .data(LocalDateTime.now())
                .build();
        historicoRepository.save(historico);

        return ConsultaResponse.from(consultaRepository.save(consulta));
    }

    @Transactional
    public void remover(Long id) {
        consultaRepository.delete(carregar(id));
    }

    private Consulta carregar(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Consulta não encontrada"));
    }
}