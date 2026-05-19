package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.responsavel.ResponsavelAtualizarDTO;
import br.com.yourpethealth.dto.responsavel.ResponsavelCadastroDTO;
import br.com.yourpethealth.dto.responsavel.ResponsavelListagemDTO;
import br.com.yourpethealth.service.ResponsavelService;
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

@Tag(name = "Responsáveis")
@RestController
@RequestMapping("/responsaveis")
public class ResponsavelController {

    private final ResponsavelService service;

    public ResponsavelController(ResponsavelService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra um responsável", responses = {
            @ApiResponse(responseCode = "201", description = "Responsável cadastrado com sucesso",
                    content = @Content(schema = @Schema(
                            implementation = ResponsavelListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar responsável")
    })
    @PostMapping
    public ResponseEntity<ResponsavelListagemDTO> create(@Valid @RequestBody ResponsavelCadastroDTO dto) {
        ResponsavelListagemDTO novoResponsavel = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoResponsavel);
    }

    @Operation(summary = "Lista todos os responsáveis", responses = {
            @ApiResponse(responseCode = "200", description = "Responsáveis encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(
                                    implementation = ResponsavelListagemDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<ResponsavelListagemDTO>> read() {
        List<ResponsavelListagemDTO> responsaveis = service.readAll();
        return ResponseEntity.ok(responsaveis);
    }

    @Operation(summary = "Busca um responsável pelo id", responses = {
            @ApiResponse(responseCode = "200", description = "Responsável encontrado",
                    content = @Content(schema = @Schema(
                            implementation = ResponsavelListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponsavelListagemDTO> readById(@PathVariable Long id) {
        ResponsavelListagemDTO responsavel = service.readById(id);
        return ResponseEntity.ok(responsavel);
    }

    @Operation(summary = "Atualiza um responsável", responses = {
            @ApiResponse(responseCode = "200", description = "Responsável atualizado com sucesso",
                    content = @Content(schema = @Schema(
                            implementation = ResponsavelListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar responsável"),
            @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponsavelListagemDTO> update(@PathVariable Long id, @Valid @RequestBody ResponsavelAtualizarDTO dto) {
        ResponsavelListagemDTO atualizado = service.update(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(summary = "Deleta um responsável", responses = {
            @ApiResponse(responseCode = "204", description = "Responsável deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}