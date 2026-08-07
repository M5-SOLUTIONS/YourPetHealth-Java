package br.com.yourpethealth.controller;

import br.com.yourpethealth.assembler.ConsultaAssembler;
import br.com.yourpethealth.dto.request.ConsultaAtualizacaoRequest;
import br.com.yourpethealth.dto.request.ConsultaConclusaoRequest;
import br.com.yourpethealth.dto.request.ConsultaRequest;
import br.com.yourpethealth.dto.response.ConsultaResponse;
import br.com.yourpethealth.service.ConsultaService;
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

@Tag(name = "Consultas")
@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService service;
    private final ConsultaAssembler assembler;

    @Operation(summary = "Agenda uma consulta", responses = {
            @ApiResponse(responseCode = "201", description = "Consulta agendada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Pet ou veterinário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário")
    })
    @PostMapping
    public ResponseEntity<EntityModel<ConsultaResponse>> criar(
            @Valid @RequestBody ConsultaRequest request) {
        var consulta = assembler.toModel(service.criar(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(consulta);
    }

    @Operation(summary = "Busca uma consulta pelo id", responses = {
            @ApiResponse(responseCode = "200", description = "Consulta encontrada",
                    content = @Content(schema = @Schema(implementation = ConsultaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ConsultaResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorId(id)));
    }

    @Operation(summary = "Lista consultas de um pet", responses = {
            @ApiResponse(responseCode = "200", description = "Consultas encontradas",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ConsultaResponse.class))))
    })
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<EntityModel<ConsultaResponse>>> listarPorPet(
            @PathVariable Long petId) {
        return ResponseEntity.ok(
                service.listarPorPet(petId).stream().map(assembler::toModel).toList());
    }

    @Operation(summary = "Lista consultas de um veterinário", responses = {
            @ApiResponse(responseCode = "200", description = "Consultas encontradas",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ConsultaResponse.class))))
    })
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<EntityModel<ConsultaResponse>>> listarPorVeterinario(
            @PathVariable Long veterinarioId) {
        // TODO J3: vira GET /api/consultas/agenda?data= usando o veterinário logado
        return ResponseEntity.ok(
                service.listarPorVeterinario(veterinarioId).stream()
                        .map(assembler::toModel).toList());
    }

    @Operation(summary = "Reagenda uma consulta", responses = {
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ConsultaResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody ConsultaAtualizacaoRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.atualizar(id, request)));
    }

    @Operation(summary = "Cancela uma consulta, mantendo o registro", responses = {
            @ApiResponse(responseCode = "200", description = "Consulta cancelada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EntityModel<ConsultaResponse>> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.cancelar(id)));
    }

    @Operation(summary = "Conclui uma consulta e gera o histórico clínico", responses = {
            @ApiResponse(responseCode = "200", description = "Consulta concluída com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Observações inválidas"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PatchMapping("/{id}/concluir")
    public ResponseEntity<EntityModel<ConsultaResponse>> concluir(
            @PathVariable Long id, @Valid @RequestBody ConsultaConclusaoRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.concluir(id, request)));
    }

    @Operation(summary = "Remove uma consulta", responses = {
            @ApiResponse(responseCode = "204", description = "Consulta removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}