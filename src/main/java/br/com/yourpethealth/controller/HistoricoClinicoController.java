package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.historico.HistoricoClinicoListagemDTO;
import br.com.yourpethealth.service.HistoricoClinicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historico")
public class HistoricoClinicoController {

    private final HistoricoClinicoService service;

    public HistoricoClinicoController(HistoricoClinicoService service) {
        this.service = service;
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<HistoricoClinicoListagemDTO>> readByPet(@PathVariable Long petId) {
        List<HistoricoClinicoListagemDTO> historicos = service.readByPet(petId);
        return ResponseEntity.ok(historicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricoClinicoListagemDTO> readById(@PathVariable Long id) {
        HistoricoClinicoListagemDTO historico = service.readById(id);
        return ResponseEntity.ok(historico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}