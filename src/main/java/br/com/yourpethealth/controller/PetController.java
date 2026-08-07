package br.com.yourpethealth.controller;

import br.com.yourpethealth.assembler.PetAssembler;
import br.com.yourpethealth.dto.request.PetRequest;
import br.com.yourpethealth.dto.response.PetResponse;
import br.com.yourpethealth.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pets")
@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;
    private final PetAssembler assembler;

    @Operation(summary = "Cadastra um pet", responses = {
            @ApiResponse(responseCode = "201", description = "Pet cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @PostMapping
    public ResponseEntity<EntityModel<PetResponse>> criar(@Valid @RequestBody PetRequest request) {
        var pet = assembler.toModel(service.criar(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(pet);
    }

    @Operation(summary = "Lista todos os pets", responses = {
            @ApiResponse(responseCode = "200", description = "Pets encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = PetResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<EntityModel<PetResponse>>> listar() {
        // TODO J3: filtrar pelos pets do responsável logado
        return ResponseEntity.ok(
                service.listar().stream().map(assembler::toModel).toList());
    }

    @Operation(summary = "Busca um pet pelo id", responses = {
            @ApiResponse(responseCode = "200", description = "Pet encontrado",
                    content = @Content(schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PetResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorId(id)));
    }

    @Operation(summary = "Lista pets de um responsável", responses = {
            @ApiResponse(responseCode = "200", description = "Pets encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = PetResponse.class))))
    })
    @GetMapping("/responsavel/{responsavelId}")
    public ResponseEntity<List<EntityModel<PetResponse>>> listarPorResponsavel(
            @PathVariable Long responsavelId) {
        // TODO J3: rota removida — GET /api/pets passa a fazer isso
        return ResponseEntity.ok(
                service.listarPorResponsavel(responsavelId).stream()
                        .map(assembler::toModel).toList());
    }

    @Operation(summary = "Atualiza um pet", responses = {
            @ApiResponse(responseCode = "200", description = "Pet atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PetResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody PetRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.atualizar(id, request)));
    }

    @Operation(summary = "Remove um pet", responses = {
            @ApiResponse(responseCode = "204", description = "Pet removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado"),
            @ApiResponse(responseCode = "409", description = "Pet possui consultas agendadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}