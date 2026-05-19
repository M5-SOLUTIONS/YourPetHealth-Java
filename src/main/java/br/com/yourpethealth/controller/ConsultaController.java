package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.consulta.ConsultaAtualizarDTO;
import br.com.yourpethealth.dto.consulta.ConsultaCadastroDTO;
import br.com.yourpethealth.dto.consulta.ConsultaListagemDTO;
import br.com.yourpethealth.service.ConsultaService;
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

@Tag(name = "Consultas")
@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra uma consulta", responses = {
            @ApiResponse(responseCode = "201", description = "Consulta cadastrada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar consulta")
    })
    @PostMapping
    public ResponseEntity<ConsultaListagemDTO> create(@Valid @RequestBody ConsultaCadastroDTO dto) {
        ConsultaListagemDTO novaConsulta = service.createConsulta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConsulta);
    }

    @Operation(summary = "Busca uma consulta pelo id", responses = {
            @ApiResponse(responseCode = "200", description = "Consulta encontrada",
                    content = @Content(schema = @Schema(implementation = ConsultaListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaListagemDTO> readById(@PathVariable Long id) {
        ConsultaListagemDTO consulta = service.readConsultaById(id);
        return ResponseEntity.ok(consulta);
    }

    @Operation(summary = "Lista consultas de um pet", responses = {
            @ApiResponse(responseCode = "200", description = "Consultas encontradas",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ConsultaListagemDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<ConsultaListagemDTO>> readByPet(@PathVariable Long petId) {
        List<ConsultaListagemDTO> consultas = service.readConsultasByPet(petId);
        return ResponseEntity.ok(consultas);
    }

    @Operation(summary = "Lista consultas de um veterinário", responses = {
            @ApiResponse(responseCode = "200", description = "Consultas encontradas",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ConsultaListagemDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<ConsultaListagemDTO>> readByVeterinario(@PathVariable Long veterinarioId) {
        List<ConsultaListagemDTO> consultas = service.readConsultasByVeterinario(veterinarioId);
        return ResponseEntity.ok(consultas);
    }

    @Operation(summary = "Atualiza uma consulta", responses = {
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar consulta"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaListagemDTO> update(@PathVariable Long id, @Valid @RequestBody ConsultaAtualizarDTO dto) {
        ConsultaListagemDTO atualizada = service.updateConsulta(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    @Operation(summary = "Conclui uma consulta", responses = {
            @ApiResponse(responseCode = "200", description = "Consulta concluída com sucesso",
                    content = @Content(schema = @Schema(implementation = ConsultaListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ConsultaListagemDTO> concluir(@PathVariable Long id) {
        ConsultaListagemDTO consulta = service.concluirConsulta(id);
        return ResponseEntity.ok(consulta);
    }

    @Operation(summary = "Deleta uma consulta", responses = {
            @ApiResponse(responseCode = "204", description = "Consulta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteConsulta(id);
        return ResponseEntity.noContent().build();
    }
}