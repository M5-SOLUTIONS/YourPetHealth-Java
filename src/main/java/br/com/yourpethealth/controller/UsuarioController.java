package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.usuario.UsuarioAtualizarDTO;
import br.com.yourpethealth.dto.usuario.UsuarioCadastroDTO;
import br.com.yourpethealth.dto.usuario.UsuarioListagemDTO;
import br.com.yourpethealth.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioListagemDTO> create(@Valid @RequestBody UsuarioCadastroDTO dto) {
        UsuarioListagemDTO novoUsuario = service.createUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioListagemDTO>> read() {
        List<UsuarioListagemDTO> usuarios = service.readAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioListagemDTO> readById(@PathVariable Long id) {
        UsuarioListagemDTO usuario = service.readUsuarioById(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioListagemDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioAtualizarDTO dto) {
        UsuarioListagemDTO atualizado = service.updateUsuario(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}