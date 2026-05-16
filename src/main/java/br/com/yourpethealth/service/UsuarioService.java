package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.usuario.UsuarioAtualizarDTO;
import br.com.yourpethealth.dto.usuario.UsuarioCadastroDTO;
import br.com.yourpethealth.dto.usuario.UsuarioListagemDTO;
import br.com.yourpethealth.entity.usuario.Usuario;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioListagemDTO createUsuario(UsuarioCadastroDTO dto) {

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setTelefone(dto.telefone());

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioListagemDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTelefone()
        );
    }

    @Transactional(readOnly = true)
    public List<UsuarioListagemDTO> readAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> new UsuarioListagemDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getTelefone()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioListagemDTO readUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Usuário não encontrado"));

        return new UsuarioListagemDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone()
        );
    }

    @Transactional
    public UsuarioListagemDTO updateUsuario(Long id, UsuarioAtualizarDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Usuário não encontrado"));

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setTelefone(dto.telefone());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(dto.senha());
        }

        Usuario atualizado = usuarioRepository.save(usuario);

        return new UsuarioListagemDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getTelefone()
        );
    }

    @Transactional
    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Usuário não encontrado"));
        usuarioRepository.delete(usuario);
    }
}