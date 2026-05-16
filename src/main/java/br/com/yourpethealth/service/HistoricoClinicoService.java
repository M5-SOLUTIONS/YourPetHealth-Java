package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.historico.HistoricoClinicoListagemDTO;
import br.com.yourpethealth.entity.historico.HistoricoClinico;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.repository.HistoricoClinicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoricoClinicoService {

    private final HistoricoClinicoRepository repository;

    public HistoricoClinicoService(HistoricoClinicoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<HistoricoClinicoListagemDTO> readByPet(Long petId) {
        return repository.findByPetId(petId)
                .stream()
                .map(historico -> new HistoricoClinicoListagemDTO(
                        historico.getId(),
                        historico.getPet().getId(),
                        historico.getTipo(),
                        historico.getDescricao(),
                        historico.getData()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public HistoricoClinicoListagemDTO readById(Long id) {
        HistoricoClinico historico = repository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Histórico clínico não encontrado"));
        return new HistoricoClinicoListagemDTO(
                historico.getId(),
                historico.getPet().getId(),
                historico.getTipo(),
                historico.getDescricao(),
                historico.getData()
        );
    }

    @Transactional
    public void delete(Long id) {
        HistoricoClinico historico = repository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Histórico clínico não encontrado"));
        repository.delete(historico);
    }
}