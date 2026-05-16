package br.com.yourpethealth.dto.pet;

import br.com.yourpethealth.entity.pet.SexoPet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PetCadastroDTO(

        @NotNull(message = "O responsável é obrigatório")
        Long responsavelId,

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "A raça é obrigatória")
        String raca,

        @Positive(message = "A idade deve ser maior que zero")
        Integer idade,

        @Positive(message = "O peso deve ser maior que zero")
        Double peso,

        @NotNull(message = "O sexo é obrigatório")
        SexoPet sexo

) {
}