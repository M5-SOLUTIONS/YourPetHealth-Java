package br.com.yourpethealth.service;

import br.com.yourpethealth.repository.AlertaRepository;
import org.springframework.stereotype.Service;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }
}
