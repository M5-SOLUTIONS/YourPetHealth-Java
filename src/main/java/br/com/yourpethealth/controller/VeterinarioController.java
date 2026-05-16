package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.veterinario.VeterinarioAtualizarDTO;
import br.com.yourpethealth.dto.veterinario.VeterinarioCadastroDTO;
import br.com.yourpethealth.dto.veterinario.VeterinarioListagemDTO;
import br.com.yourpethealth.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService service;

    public VeterinarioController(VeterinarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VeterinarioListagemDTO> create(@Valid @RequestBody VeterinarioCadastroDTO dto) {
        VeterinarioListagemDTO novoVeterinario = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoVeterinario);
    }

    @GetMapping
    public ResponseEntity<List<VeterinarioListagemDTO>> read() {
        List<VeterinarioListagemDTO> veterinarios = service.readAll();
        return ResponseEntity.ok(veterinarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioListagemDTO> readById(@PathVariable Long id) {
        VeterinarioListagemDTO veterinario = service.readById(id);
        return ResponseEntity.ok(veterinario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioListagemDTO> update(@PathVariable Long id, @Valid @RequestBody VeterinarioAtualizarDTO dto) {
        VeterinarioListagemDTO atualizado = service.update(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}