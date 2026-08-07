package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.request.PetRequest;
import br.com.yourpethealth.dto.response.PetResponse;
import br.com.yourpethealth.entity.Consulta;
import br.com.yourpethealth.entity.Pet;
import br.com.yourpethealth.entity.enums.StatusConsulta;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.repository.ConsultaRepository;
import br.com.yourpethealth.repository.PetRepository;
import br.com.yourpethealth.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final ResponsavelRepository responsavelRepository;
    private final ConsultaRepository consultaRepository;

    @Transactional
    public PetResponse criar(PetRequest request) {
        var responsavel = responsavelRepository.findById(request.responsavelId())
                .orElseThrow(() -> new IdNaoEncontradoException("Responsável não encontrado"));

        var pet = Pet.builder()
                .responsavel(responsavel)
                .nome(request.nome())
                .raca(request.raca())
                .idade(request.idade())
                .peso(request.peso())
                .sexoPet(request.sexo())
                .build();

        return toResponse(petRepository.save(pet));
    }

    @Transactional(readOnly = true)
    public List<PetResponse> listar() {
        return petRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PetResponse buscarPorId(Long id) {
        return toResponse(carregar(id));
    }

    @Transactional(readOnly = true)
    public List<PetResponse> listarPorResponsavel(Long responsavelId) {
        return petRepository.findByResponsavelId(responsavelId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public PetResponse atualizar(Long id, PetRequest request) {
        var pet = carregar(id);

        pet.setNome(request.nome());
        pet.setRaca(request.raca());
        pet.setIdade(request.idade());
        pet.setPeso(request.peso());
        pet.setSexoPet(request.sexo());

        return toResponse(petRepository.save(pet));
    }

    @Transactional
    public void remover(Long id) {
        petRepository.delete(carregar(id));
    }

    private Pet carregar(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Pet não encontrado"));
    }

    private PetResponse toResponse(Pet pet) {
        long total = consultaRepository.countByPetId(pet.getId());

        LocalDateTime proxima = consultaRepository
                .findFirstByPetIdAndStatusAndDataAfterOrderByDataAsc(
                        pet.getId(), StatusConsulta.AGENDADA, LocalDateTime.now())
                .map(Consulta::getData)
                .orElse(null);

        return PetResponse.from(pet, total, proxima);
    }
}