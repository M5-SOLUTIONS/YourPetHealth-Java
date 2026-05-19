package br.com.yourpethealth.controller;

import br.com.yourpethealth.dto.veterinario.VeterinarioAtualizarDTO;
import br.com.yourpethealth.dto.veterinario.VeterinarioCadastroDTO;
import br.com.yourpethealth.dto.veterinario.VeterinarioListagemDTO;
import br.com.yourpethealth.service.VeterinarioService;
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

@Tag(name = "Veterinários")
@RestController
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService service;

    public VeterinarioController(VeterinarioService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra um veterinário", responses = {
            @ApiResponse(responseCode = "201", description = "Veterinário cadastrado com sucesso",
                    content = @Content(schema = @Schema(
                            implementation = VeterinarioListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar veterinário")
    })
    @PostMapping
    public ResponseEntity<VeterinarioListagemDTO> create(@Valid @RequestBody VeterinarioCadastroDTO dto) {
        VeterinarioListagemDTO novoVeterinario = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoVeterinario);
    }

    @Operation(summary = "Lista todos os veterinários", responses = {
            @ApiResponse(responseCode = "200", description = "Veterinários encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(
                                    implementation = VeterinarioListagemDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<VeterinarioListagemDTO>> read() {
        List<VeterinarioListagemDTO> veterinarios = service.readAll();
        return ResponseEntity.ok(veterinarios);
    }

    @Operation(summary = "Busca um veterinário pelo id", responses = {
            @ApiResponse(responseCode = "200", description = "Veterinário encontrado",
                    content = @Content(schema = @Schema(
                            implementation = VeterinarioListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioListagemDTO> readById(@PathVariable Long id) {
        VeterinarioListagemDTO veterinario = service.readById(id);
        return ResponseEntity.ok(veterinario);
    }

    @Operation(summary = "Atualiza um veterinário", responses = {
            @ApiResponse(responseCode = "200", description = "Veterinário atualizado com sucesso",
                    content = @Content(schema = @Schema(
                            implementation = VeterinarioListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar veterinário"),
            @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioListagemDTO> update(@PathVariable Long id, @Valid @RequestBody VeterinarioAtualizarDTO dto) {
        VeterinarioListagemDTO atualizado = service.update(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(summary = "Deleta um veterinário", responses = {
            @ApiResponse(responseCode = "204", description = "Veterinário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}