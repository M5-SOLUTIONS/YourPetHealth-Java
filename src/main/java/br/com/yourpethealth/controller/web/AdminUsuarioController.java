package br.com.yourpethealth.controller.web;

import br.com.yourpethealth.entity.enums.Perfil;
import br.com.yourpethealth.exception.RegraNegocioException;
import br.com.yourpethealth.form.ResponsavelAdminForm;
import br.com.yourpethealth.form.VeterinarioAdminForm;
import br.com.yourpethealth.service.AdminUsuarioService;
import br.com.yourpethealth.service.ResponsavelService;
import br.com.yourpethealth.service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private final AdminUsuarioService adminUsuarioService;
    private final ResponsavelService responsavelService;
    private final VeterinarioService veterinarioService;

    // ---------- Contas ----------

    @GetMapping("/usuarios")
    public String listar(@RequestParam(required = false) Perfil perfil,
                         @RequestParam(required = false) String busca,
                         Model model) {
        model.addAttribute("usuarios", adminUsuarioService.listar(perfil, busca));
        model.addAttribute("perfilSelecionado", perfil);
        model.addAttribute("busca", busca);
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/status")
    public String alternarAcesso(@PathVariable Long id,
                                 @RequestParam(required = false) Perfil perfil,
                                 @RequestParam(required = false) String busca,
                                 RedirectAttributes redirect) {
        try {
            boolean ativo = adminUsuarioService.alternarAtivo(id);
            redirect.addFlashAttribute("sucesso", ativo
                    ? "Conta reativada. O acesso foi liberado."
                    : "Conta desativada. O acesso foi bloqueado.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }

        manterFiltros(redirect, perfil, busca);
        return "redirect:/admin/usuarios";
    }

    // ---------- Dados do responsável ----------

    @GetMapping("/responsaveis/{id}/editar")
    public String editarResponsavel(@PathVariable Long id, Model model) {
        var responsavel = responsavelService.buscarPorId(id);
        model.addAttribute("responsavel", responsavel);
        model.addAttribute("form", ResponsavelAdminForm.de(responsavel));
        return "admin/responsavel-form";
    }

    @PostMapping("/responsaveis/{id}")
    public String salvarResponsavel(@PathVariable Long id,
                                    @Valid @ModelAttribute("form") ResponsavelAdminForm form,
                                    BindingResult resultado,
                                    Model model,
                                    RedirectAttributes redirect) {
        var responsavel = responsavelService.buscarPorId(id);

        if (resultado.hasErrors()) {
            model.addAttribute("responsavel", responsavel);
            return "admin/responsavel-form";
        }

        responsavelService.atualizar(id, form.paraRequest(responsavel.email()));
        redirect.addFlashAttribute("sucesso", "Dados de " + form.getNome() + " atualizados.");
        return "redirect:/admin/usuarios";
    }

    // ---------- Dados do veterinário ----------

    @GetMapping("/veterinarios/{id}/editar")
    public String editarVeterinario(@PathVariable Long id, Model model) {
        var veterinario = veterinarioService.buscarPorId(id);
        model.addAttribute("veterinario", veterinario);
        model.addAttribute("form", VeterinarioAdminForm.de(veterinario));
        return "admin/veterinario-form";
    }

    @PostMapping("/veterinarios/{id}")
    public String salvarVeterinario(@PathVariable Long id,
                                    @Valid @ModelAttribute("form") VeterinarioAdminForm form,
                                    BindingResult resultado,
                                    Model model,
                                    RedirectAttributes redirect) {
        var veterinario = veterinarioService.buscarPorId(id);

        if (!resultado.hasErrors()) {
            try {
                veterinarioService.atualizar(id, form.paraRequest(veterinario.email()));
                redirect.addFlashAttribute("sucesso",
                        "Dados de " + form.getNome() + " atualizados.");
                return "redirect:/admin/usuarios";
            } catch (DataIntegrityViolationException e) {
                // O único UNIQUE editável nesta tela é o CRMV.
                resultado.rejectValue("crmv", "duplicado",
                        "Este CRMV já está cadastrado para outro veterinário");
            }
        }

        model.addAttribute("veterinario", veterinario);
        return "admin/veterinario-form";
    }

    private void manterFiltros(RedirectAttributes redirect, Perfil perfil, String busca) {
        if (perfil != null) {
            redirect.addAttribute("perfil", perfil);
        }
        if (busca != null && !busca.isBlank()) {
            redirect.addAttribute("busca", busca);
        }
    }
}