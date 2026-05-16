package br.com.yourpethealth.controller;

import br.com.yourpethealth.service.ExameService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExameController {

    private final ExameService exameService;

    public ExameController(ExameService exameService) {
        this.exameService = exameService;
    }
}
