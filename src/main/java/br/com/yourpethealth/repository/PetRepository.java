package br.com.yourpethealth.repository;

import br.com.yourpethealth.entity.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByResponsavelId(Long responsavelId);
}
