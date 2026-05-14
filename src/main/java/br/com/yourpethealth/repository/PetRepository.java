package br.com.yourpethealth.repository;

import br.com.yourpethealth.entity.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
