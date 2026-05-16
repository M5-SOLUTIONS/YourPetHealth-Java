package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.responsavel.ResponsavelAtualizarDTO;
import br.com.yourpethealth.dto.responsavel.ResponsavelCadastroDTO;
import br.com.yourpethealth.dto.responsavel.ResponsavelListagemDTO;
import br.com.yourpethealth.service.ResponsavelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/responsaveis")
public class ResponsavelController {

    private final ResponsavelService service;

    public ResponsavelController(ResponsavelService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ResponsavelListagemDTO> create(@Valid @RequestBody ResponsavelCadastroDTO dto) {
        ResponsavelListagemDTO novoResponsavel = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoResponsavel);
    }

    @GetMapping
    public ResponseEntity<List<ResponsavelListagemDTO>> read() {
        List<ResponsavelListagemDTO> responsaveis = service.readAll();
        return ResponseEntity.ok(responsaveis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsavelListagemDTO> readById(@PathVariable Long id) {
        ResponsavelListagemDTO responsavel = service.readById(id);
        return ResponseEntity.ok(responsavel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsavelListagemDTO> update(@PathVariable Long id, @Valid @RequestBody ResponsavelAtualizarDTO dto) {
        ResponsavelListagemDTO atualizado = service.update(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}