package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.consulta.ConsultaAtualizarDTO;
import br.com.yourpethealth.dto.consulta.ConsultaCadastroDTO;
import br.com.yourpethealth.dto.consulta.ConsultaListagemDTO;
import br.com.yourpethealth.entity.consulta.Consulta;
import br.com.yourpethealth.entity.historico.HistoricoClinico;
import br.com.yourpethealth.entity.historico.TipoHistorico;
import br.com.yourpethealth.entity.pet.Pet;
import br.com.yourpethealth.entity.usuario.Veterinario;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.repository.ConsultaRepository;
import br.com.yourpethealth.repository.HistoricoClinicoRepository;
import br.com.yourpethealth.repository.PetRepository;
import br.com.yourpethealth.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final HistoricoClinicoRepository historicoRepository;

    public ConsultaService(ConsultaRepository consultaRepository, PetRepository petRepository,
            VeterinarioRepository veterinarioRepository,
            HistoricoClinicoRepository historicoRepository) {

        this.consultaRepository = consultaRepository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.historicoRepository = historicoRepository;
    }

    @Transactional
    public ConsultaListagemDTO createConsulta(ConsultaCadastroDTO dto) {
        Pet pet = petRepository.findById(dto.petId())
                .orElseThrow(() -> new IdNaoEncontradoException("Pet não encontrado"));

        Veterinario veterinario = veterinarioRepository.findById(dto.veterinarioId())
                                .orElseThrow(() -> new IdNaoEncontradoException("Veterinário não encontrado"));

        Consulta consulta = new Consulta();
        consulta.setPet(pet);
        consulta.setVeterinario(veterinario);
        consulta.setTipo(dto.tipo());
        consulta.setDescricao(dto.descricao());
        consulta.setData(dto.data());
        consulta.setObservacoes(dto.observacoes());
        consulta.setStatus(dto.status());
        Consulta salva = consultaRepository.save(consulta);

        HistoricoClinico historico = new HistoricoClinico();
        historico.setPet(pet);
        historico.setTipo(TipoHistorico.CONSULTA);
        historico.setDescricao("Consulta cadastrada: " + consulta.getDescricao());
        historico.setData(consulta.getData());
        historicoRepository.save(historico);

        return new ConsultaListagemDTO(
                salva.getId(),
                salva.getPet().getId(),
                salva.getVeterinario().getId(),
                salva.getTipo(),
                salva.getDescricao(),
                salva.getData(),
                salva.getObservacoes(),
                salva.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public List<ConsultaListagemDTO> readConsultasByPet(Long petId) {
        return consultaRepository.findByPetId(petId)
                .stream()
                .map(consulta -> new ConsultaListagemDTO(
                        consulta.getId(),
                        consulta.getPet().getId(),
                        consulta.getVeterinario().getId(),
                        consulta.getTipo(),
                        consulta.getDescricao(),
                        consulta.getData(),
                        consulta.getObservacoes(),
                        consulta.getStatus()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsultaListagemDTO readConsultaById(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Consulta não encontrada"));
        return new ConsultaListagemDTO(
                consulta.getId(),
                consulta.getPet().getId(),
                consulta.getVeterinario().getId(),
                consulta.getTipo(),
                consulta.getDescricao(),
                consulta.getData(),
                consulta.getObservacoes(),
                consulta.getStatus()
        );
    }

    @Transactional
    public ConsultaListagemDTO updateConsulta(Long id, ConsultaAtualizarDTO dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Consulta não encontrada"));

        consulta.setTipo(dto.tipo());
        consulta.setDescricao(dto.descricao());
        consulta.setData(dto.data());
        consulta.setObservacoes(dto.observacoes());
        consulta.setStatus(dto.status());
        Consulta atualizada = consultaRepository.save(consulta);

        return new ConsultaListagemDTO(
                atualizada.getId(),
                atualizada.getPet().getId(),
                atualizada.getVeterinario().getId(),
                atualizada.getTipo(),
                atualizada.getDescricao(),
                atualizada.getData(),
                atualizada.getObservacoes(),
                atualizada.getStatus()
        );
    }

    @Transactional
    public void deleteConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                        .orElseThrow(() -> new IdNaoEncontradoException("Consulta não encontrada"));
        consultaRepository.delete(consulta);
    }

    @Transactional(readOnly = true)
    public List<ConsultaListagemDTO> readConsultasByVeterinario(Long veterinarioId) {
        return consultaRepository.findByVeterinarioId(veterinarioId)
                .stream()
                .map(consulta -> new ConsultaListagemDTO(
                        consulta.getId(),
                        consulta.getPet().getId(),
                        consulta.getVeterinario().getId(),
                        consulta.getTipo(),
                        consulta.getDescricao(),
                        consulta.getData(),
                        consulta.getObservacoes(),
                        consulta.getStatus()
                ))
                .toList();
    }
}