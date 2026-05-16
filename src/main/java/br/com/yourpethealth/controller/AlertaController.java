package br.com.yourpethealth.controller;

import br.com.yourpethealth.service.AlertaService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }
}
