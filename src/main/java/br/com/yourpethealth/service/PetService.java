package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.pet.PetAtualizarDTO;
import br.com.yourpethealth.dto.pet.PetCadastroDTO;
import br.com.yourpethealth.dto.pet.PetListagemDTO;
import br.com.yourpethealth.entity.pet.Pet;
import br.com.yourpethealth.entity.usuario.Usuario;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.repository.PetRepository;
import br.com.yourpethealth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UsuarioRepository usuarioRepository;

    public PetService(PetRepository petRepository, UsuarioRepository usuarioRepository) {
        this.petRepository = petRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PetListagemDTO createPet(PetCadastroDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new IdNaoEncontradoException("Usuário não encontrado"));

        Pet pet = new Pet();
        pet.setUsuario(usuario);
        pet.setNome(dto.nome());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        pet.setPeso(dto.peso());
        pet.setSexo(dto.sexo());
        Pet salvo = petRepository.save(pet);

        return new PetListagemDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getRaca(),
                salvo.getIdade(),
                salvo.getPeso(),
                salvo.getSexo()
        );
    }

    @Transactional(readOnly = true)
    public List<PetListagemDTO> readAllPets() {
        return petRepository.findAll()
                .stream()
                .map(pet -> new PetListagemDTO(
                        pet.getId(),
                        pet.getNome(),
                        pet.getRaca(),
                        pet.getIdade(),
                        pet.getPeso(),
                        pet.getSexo()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public PetListagemDTO readPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Pet não encontrado"));

        return new PetListagemDTO(
                pet.getId(),
                pet.getNome(),
                pet.getRaca(),
                pet.getIdade(),
                pet.getPeso(),
                pet.getSexo()
        );
    }

    @Transactional(readOnly = true)
    public List<PetListagemDTO> readPetsByUsuario(Long usuarioId) {
        return petRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(pet -> new PetListagemDTO(
                        pet.getId(),
                        pet.getNome(),
                        pet.getRaca(),
                        pet.getIdade(),
                        pet.getPeso(),
                        pet.getSexo()
                ))
                .toList();
    }

    @Transactional
    public PetListagemDTO updatePet(Long id, PetAtualizarDTO dto) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() ->
                        new IdNaoEncontradoException("Pet não encontrado"));

        pet.setNome(dto.nome());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        pet.setPeso(dto.peso());
        pet.setSexo(dto.sexo());
        Pet atualizado = petRepository.save(pet);

        return new PetListagemDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getRaca(),
                atualizado.getIdade(),
                atualizado.getPeso(),
                atualizado.getSexo()
        );
    }

    @Transactional
    public void deletePet(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Pet não encontrado"));

        petRepository.delete(pet);
    }
}