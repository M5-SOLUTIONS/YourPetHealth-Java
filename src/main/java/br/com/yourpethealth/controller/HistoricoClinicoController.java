package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.historico.HistoricoClinicoListagemDTO;
import br.com.yourpethealth.service.HistoricoClinicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Histórico Clínico")
@RestController
@RequestMapping("/historico")
public class HistoricoClinicoController {

    private final HistoricoClinicoService service;

    public HistoricoClinicoController(HistoricoClinicoService service) {
        this.service = service;
    }

    @Operation(summary = "Lista o histórico clínico de um pet", responses = {
            @ApiResponse(responseCode = "200", description = "Histórico encontrado",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(
                                    implementation = HistoricoClinicoListagemDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<HistoricoClinicoListagemDTO>> readByPet(@PathVariable Long petId) {
        List<HistoricoClinicoListagemDTO> historicos = service.readByPet(petId);
        return ResponseEntity.ok(historicos);
    }

    @Operation(summary = "Busca um item do histórico pelo id", responses = {
            @ApiResponse(responseCode = "200", description = "Histórico encontrado",
                    content = @Content(schema = @Schema(
                            implementation = HistoricoClinicoListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Histórico não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistoricoClinicoListagemDTO> readById(@PathVariable Long id) {
        HistoricoClinicoListagemDTO historico = service.readById(id);
        return ResponseEntity.ok(historico);
    }

    @Operation(summary = "Deleta um item do histórico clínico", responses = {
            @ApiResponse(responseCode = "204", description = "Histórico deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Histórico não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}