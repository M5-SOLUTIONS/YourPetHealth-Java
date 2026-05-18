package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.consulta.ConsultaAtualizarDTO;
import br.com.yourpethealth.dto.consulta.ConsultaCadastroDTO;
import br.com.yourpethealth.dto.consulta.ConsultaListagemDTO;
import br.com.yourpethealth.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConsultaListagemDTO> create(@Valid @RequestBody ConsultaCadastroDTO dto) {
        ConsultaListagemDTO novaConsulta = service.createConsulta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConsulta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaListagemDTO> readById(@PathVariable Long id) {
        ConsultaListagemDTO consulta = service.readConsultaById(id);
        return ResponseEntity.ok(consulta);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<ConsultaListagemDTO>> readByPet(@PathVariable Long petId) {
        List<ConsultaListagemDTO> consultas = service.readConsultasByPet(petId);
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<ConsultaListagemDTO>> readByVeterinario(@PathVariable Long veterinarioId) {
        List<ConsultaListagemDTO> consultas = service.readConsultasByVeterinario(veterinarioId);
        return ResponseEntity.ok(consultas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaListagemDTO> update(@PathVariable Long id, @Valid @RequestBody ConsultaAtualizarDTO dto) {
        ConsultaListagemDTO atualizada = service.updateConsulta(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ConsultaListagemDTO> concluir(@PathVariable Long id) {
        ConsultaListagemDTO consulta = service.concluirConsulta(id);
        return ResponseEntity.ok(consulta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteConsulta(id);
        return ResponseEntity.noContent().build();
    }
}