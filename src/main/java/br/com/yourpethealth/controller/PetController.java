package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.pet.PetAtualizarDTO;
import br.com.yourpethealth.dto.pet.PetCadastroDTO;
import br.com.yourpethealth.dto.pet.PetListagemDTO;
import br.com.yourpethealth.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pets")
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra um pet", responses = {
            @ApiResponse(responseCode = "201", description = "Pet cadastrado com sucesso",
                    content = @Content(schema = @Schema(
                            implementation = PetListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar pet")
    })
    @PostMapping
    public ResponseEntity<PetListagemDTO> create(@Valid @RequestBody PetCadastroDTO dto) {
        PetListagemDTO novoPet = service.createPet(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPet);
    }

    @Operation(summary = "Lista todos os pets", responses = {
            @ApiResponse(responseCode = "200", description = "Pets encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(
                                    implementation = PetListagemDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<PetListagemDTO>> read() {
        List<PetListagemDTO> pets = service.readAllPets();
        return ResponseEntity.ok(pets);
    }

    @Operation(summary = "Busca um pet pelo id", responses = {
            @ApiResponse(responseCode = "200", description = "Pet encontrado",
                    content = @Content(schema = @Schema(
                            implementation = PetListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PetListagemDTO> readById(@PathVariable Long id) {
        PetListagemDTO pet = service.readPetById(id);
        return ResponseEntity.ok(pet);
    }

    @Operation(summary = "Lista pets de um responsável", responses = {
            @ApiResponse(responseCode = "200", description = "Pets encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(
                                    implementation = PetListagemDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @GetMapping("/responsavel/{responsavelId}")
    public ResponseEntity<List<PetListagemDTO>> readByResponsavel(@PathVariable Long responsavelId) {
        List<PetListagemDTO> pets = service.readPetsByResponsavel(responsavelId);
        return ResponseEntity.ok(pets);
    }

    @Operation(summary = "Atualiza um pet", responses = {
            @ApiResponse(responseCode = "200", description = "Pet atualizado com sucesso",
                    content = @Content(schema = @Schema(
                            implementation = PetListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar pet"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PetListagemDTO> update(@PathVariable Long id, @Valid @RequestBody PetAtualizarDTO dto) {
        PetListagemDTO atualizado = service.updatePet(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(summary = "Deleta um pet", responses = {
            @ApiResponse(responseCode = "204", description = "Pet deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}