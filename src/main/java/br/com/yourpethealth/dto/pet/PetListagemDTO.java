package br.com.yourpethealth.dto.pet;

import br.com.yourpethealth.entity.pet.SexoPet;

public record PetListagemDTO(

        Long id,
        String nome,
        String raca,
        Integer idade,
        Double peso,
        SexoPet sexo

) {
}