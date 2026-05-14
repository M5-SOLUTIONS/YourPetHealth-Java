package br.com.yourpethealth.controller;

import br.com.yourpethealth.service.HistoricoClinicoService;
import br.com.yourpethealth.service.PetService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoricoClinicoController {

    private final HistoricoClinicoService historicoClinicoService;

    public HistoricoClinicoController(HistoricoClinicoService historicoClinicoService) {
        this.historicoClinicoService = historicoClinicoService;
    }
}
