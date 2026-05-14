package br.com.yourpethealth.service;

import br.com.yourpethealth.repository.ExameRepository;
import org.springframework.stereotype.Service;

@Service
public class ExameService {

    private final ExameRepository exameRepository;

    public ExameService(ExameRepository exameRepository) {
        this.exameRepository = exameRepository;
    }
}
