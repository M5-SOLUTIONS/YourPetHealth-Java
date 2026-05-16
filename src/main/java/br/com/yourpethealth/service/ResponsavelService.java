package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.responsavel.ResponsavelAtualizarDTO;
import br.com.yourpethealth.dto.responsavel.ResponsavelCadastroDTO;
import br.com.yourpethealth.dto.responsavel.ResponsavelListagemDTO;
import br.com.yourpethealth.entity.usuario.Responsavel;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.repository.ResponsavelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResponsavelService {

    private final ResponsavelRepository repository;

    public ResponsavelService(ResponsavelRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ResponsavelListagemDTO create(ResponsavelCadastroDTO dto) {

        Responsavel resp = new Responsavel();

        resp.setNome(dto.nome());
        resp.setEmail(dto.email());
        resp.setSenha(dto.senha());
        resp.setTelefone(dto.telefone());

        Responsavel salvo = repository.save(resp);

        return new ResponsavelListagemDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTelefone()
        );
    }

    @Transactional(readOnly = true)
    public List<ResponsavelListagemDTO> readAll() {
        return repository.findAll()
                .stream()
                .map(r -> new ResponsavelListagemDTO(
                        r.getId(),
                        r.getNome(),
                        r.getEmail(),
                        r.getTelefone()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ResponsavelListagemDTO readById(Long id) {

        Responsavel resp = repository.findById(id)
                .orElseThrow(() ->
                        new IdNaoEncontradoException("Responsável não encontrado"));

        return new ResponsavelListagemDTO(
                resp.getId(),
                resp.getNome(),
                resp.getEmail(),
                resp.getTelefone()
        );
    }

    @Transactional
    public ResponsavelListagemDTO update(Long id, ResponsavelAtualizarDTO dto) {

        Responsavel resp = repository.findById(id)
                .orElseThrow(() ->
                        new IdNaoEncontradoException("Responsável não encontrado"));

        resp.setNome(dto.nome());
        resp.setEmail(dto.email());
        resp.setTelefone(dto.telefone());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            resp.setSenha(dto.senha());
        }

        Responsavel atualizado = repository.save(resp);

        return new ResponsavelListagemDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getTelefone()
        );
    }

    @Transactional
    public void delete(Long id) {

        Responsavel resp = repository.findById(id)
                .orElseThrow(() ->
                        new IdNaoEncontradoException("Responsável não encontrado"));

        repository.delete(resp);
    }
}