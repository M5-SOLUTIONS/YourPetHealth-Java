package br.com.yourpethealth.controller.web;

import br.com.yourpethealth.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    public String raiz() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("resumo", dashboardService.resumo());
        model.addAttribute("geradoEm", LocalDateTime.now());
        return "admin/dashboard";
    }
}