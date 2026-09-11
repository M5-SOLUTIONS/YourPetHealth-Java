package br.com.yourpethealth.controller.web;

import br.com.yourpethealth.dto.response.ConsultaResponse;
import br.com.yourpethealth.service.ConsultaService;
import br.com.yourpethealth.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeWebController {

    private static final String ROLE_VETERINARIO = "ROLE_VETERINARIO";

    private final PetService petService;
    private final ConsultaService consultaService;

    @GetMapping("/")
    public String raiz(Authentication auth) {
        return ehVeterinario(auth) ? "redirect:/vet/agenda" : "redirect:/app/home";
    }

    @GetMapping("/app/home")
    public String home(Model model) {
        var pets = petService.listar();
        var consultas = consultaService.listar();

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusHours(48);

        List<ConsultaResponse> proximas = consultas.stream()
                .filter(c -> "AGENDADA".equals(c.status().name()))
                .filter(c -> c.data().isAfter(agora) && c.data().isBefore(limite))
                .sorted((a, b) -> a.data().compareTo(b.data()))
                .toList();

        model.addAttribute("pets", pets);
        model.addAttribute("totalPets", pets.size());
        model.addAttribute("proximasConsultas", proximas);
        return "app/home";
    }

    private boolean ehVeterinario(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_VETERINARIO));
    }
}