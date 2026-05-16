package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.pet.PetAtualizarDTO;
import br.com.yourpethealth.dto.pet.PetCadastroDTO;
import br.com.yourpethealth.dto.pet.PetListagemDTO;
import br.com.yourpethealth.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PetListagemDTO> create(@Valid @RequestBody PetCadastroDTO dto) {
        PetListagemDTO novoPet = service.createPet(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoPet);
    }

    @GetMapping
    public ResponseEntity<List<PetListagemDTO>> read() {
        List<PetListagemDTO> pets = service.readAllPets();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetListagemDTO> readById(
            @PathVariable Long id) {

        PetListagemDTO pet =
                service.readPetById(id);

        return ResponseEntity.ok(pet);
    }

    @GetMapping("/users/{usuarioId}")
    public ResponseEntity<List<PetListagemDTO>> readByUsuario(
            @PathVariable Long usuarioId) {

        List<PetListagemDTO> pets =
                service.readPetsByUsuario(usuarioId);

        return ResponseEntity.ok(pets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetListagemDTO> update(@PathVariable Long id, @Valid @RequestBody PetAtualizarDTO dto) {
        PetListagemDTO atualizado =
                service.updatePet(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}