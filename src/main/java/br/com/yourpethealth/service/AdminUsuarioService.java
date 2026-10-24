package br.com.yourpethealth.service;

import br.com.yourpethealth.dto.view.UsuarioAdminView;
import br.com.yourpethealth.entity.Responsavel;
import br.com.yourpethealth.entity.Usuario;
import br.com.yourpethealth.entity.Veterinario;
import br.com.yourpethealth.entity.enums.Perfil;
import br.com.yourpethealth.exception.IdNaoEncontradoException;
import br.com.yourpethealth.exception.RegraNegocioException;
import br.com.yourpethealth.repository.ResponsavelRepository;
import br.com.yourpethealth.repository.UsuarioRepository;
import br.com.yourpethealth.repository.VeterinarioRepository;
import br.com.yourpethealth.security.UsuarioLogado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUsuarioService {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final UsuarioRepository usuarioRepository;
    private final ResponsavelRepository responsavelRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioLogado usuarioLogado;

    @Transactional(readOnly = true)
    public List<UsuarioAdminView> listar(Perfil perfil, String busca) {
        // Nomes carregados de uma vez, em vez de uma consulta por linha da tabela.
        var nomesResponsaveis = nomesPorId(responsavelRepository.findAll(),
                Responsavel::getId, Responsavel::getNome);
        var nomesVeterinarios = nomesPorId(veterinarioRepository.findAll(),
                Veterinario::getId, Veterinario::getNome);

        String termo = busca == null ? "" : busca.trim().toLowerCase(PT_BR);

        return usuarioRepository.findAllByOrderByCriadoEmDesc().stream()
                .filter(u -> perfil == null || u.getPerfil() == perfil)
                .map(u -> paraView(u, nomeDe(u, nomesResponsaveis, nomesVeterinarios)))
                .filter(v -> termo.isEmpty() || contem(v, termo))
                .toList();
    }

    /** Inverte o status de acesso da conta e devolve o novo estado. */
    @Transactional
    public boolean alternarAtivo(Long usuarioId) {
        if (usuarioId.equals(usuarioLogado.id())) {
            throw new RegraNegocioException("Você não pode desativar a própria conta");
        }

        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IdNaoEncontradoException("Usuário não encontrado"));

        boolean novoEstado = !Boolean.TRUE.equals(usuario.getAtivo());
        usuario.setAtivo(novoEstado);
        return novoEstado;
    }

    private String nomeDe(Usuario usuario,
                          Map<Long, String> nomesResponsaveis,
                          Map<Long, String> nomesVeterinarios) {
        if (usuario.getResponsavelId() != null) {
            return nomesResponsaveis.get(usuario.getResponsavelId());
        }
        if (usuario.getVeterinarioId() != null) {
            return nomesVeterinarios.get(usuario.getVeterinarioId());
        }
        return "Administrador";
    }

    private UsuarioAdminView paraView(Usuario usuario, String nome) {
        return new UsuarioAdminView(
                usuario.getId(),
                nome,
                usuario.getEmail(),
                usuario.getPerfil(),
                Boolean.TRUE.equals(usuario.getAtivo()),
                usuario.getCriadoEm(),
                usuario.getResponsavelId(),
                usuario.getVeterinarioId());
    }

    private boolean contem(UsuarioAdminView usuario, String termo) {
        return usuario.nome().toLowerCase(PT_BR).contains(termo)
                || usuario.email().toLowerCase(PT_BR).contains(termo);
    }

    private <T> Map<Long, String> nomesPorId(List<T> itens,
                                             Function<T, Long> id,
                                             Function<T, String> nome) {
        return itens.stream().collect(Collectors.toMap(id, nome));
    }
}