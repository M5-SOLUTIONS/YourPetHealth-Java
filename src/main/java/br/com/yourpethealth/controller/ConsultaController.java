package br.com.yourpethealth.controller;

import br.com.yourpethealth.service.ConsultaService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }
}
