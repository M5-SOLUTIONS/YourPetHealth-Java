package br.com.yourpethealth.service;

import br.com.yourpethealth.repository.HistoricoClinicoRepository;
import org.springframework.stereotype.Service;

@Service
public class HistoricoClinicoService {

    private final HistoricoClinicoRepository historicoClinicoRepository;

    public HistoricoClinicoService(HistoricoClinicoRepository historicoClinicoRepository) {
        this.historicoClinicoRepository = historicoClinicoRepository;
    }
}
