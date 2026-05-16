package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.veterinario.VeterinarioAtualizarDTO;
import br.com.yourpethealth.dto.veterinario.VeterinarioCadastroDTO;
import br.com.yourpethealth.dto.veterinario.VeterinarioListagemDTO;
import br.com.yourpethealth.entity.usuario.Veterinario;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeterinarioService {

    private final VeterinarioRepository repository;

    public VeterinarioService(VeterinarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VeterinarioListagemDTO create(VeterinarioCadastroDTO dto) {

        Veterinario vet = new Veterinario();

        vet.setNome(dto.nome());
        vet.setEmail(dto.email());
        vet.setSenha(dto.senha());
        vet.setTelefone(dto.telefone());
        vet.setCrmv(dto.crmv());
        vet.setEspecialidade(dto.especialidade());

        Veterinario salvo = repository.save(vet);

        return new VeterinarioListagemDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTelefone(),
                salvo.getCrmv(),
                salvo.getEspecialidade()
        );
    }

    @Transactional(readOnly = true)
    public List<VeterinarioListagemDTO> readAll() {
        return repository.findAll()
                .stream()
                .map(v -> new VeterinarioListagemDTO(
                        v.getId(),
                        v.getNome(),
                        v.getEmail(),
                        v.getTelefone(),
                        v.getCrmv(),
                        v.getEspecialidade()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public VeterinarioListagemDTO readById(Long id) {

        Veterinario vet = repository.findById(id)
                .orElseThrow(() ->
                        new IdNaoEncontradoException("Veterinário não encontrado"));

        return new VeterinarioListagemDTO(
                vet.getId(),
                vet.getNome(),
                vet.getEmail(),
                vet.getTelefone(),
                vet.getCrmv(),
                vet.getEspecialidade()
        );
    }

    @Transactional
    public VeterinarioListagemDTO update(Long id, VeterinarioAtualizarDTO dto) {

        Veterinario vet = repository.findById(id)
                .orElseThrow(() ->
                        new IdNaoEncontradoException("Veterinário não encontrado"));

        vet.setNome(dto.nome());
        vet.setEmail(dto.email());
        vet.setTelefone(dto.telefone());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            vet.setSenha(dto.senha());
        }

        vet.setCrmv(dto.crmv());
        vet.setEspecialidade(dto.especialidade());

        Veterinario atualizado = repository.save(vet);

        return new VeterinarioListagemDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getTelefone(),
                atualizado.getCrmv(),
                atualizado.getEspecialidade()
        );
    }

    @Transactional
    public void delete(Long id) {

        Veterinario vet = repository.findById(id)
                .orElseThrow(() ->
                        new IdNaoEncontradoException("Veterinário não encontrado"));

        repository.delete(vet);
    }
}