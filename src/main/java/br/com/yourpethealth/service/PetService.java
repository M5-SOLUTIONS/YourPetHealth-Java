package br.com.yourpethealth.service;

import br.com.yourpethealth.repository.PetRepository;
import org.springframework.stereotype.Service;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }
}
