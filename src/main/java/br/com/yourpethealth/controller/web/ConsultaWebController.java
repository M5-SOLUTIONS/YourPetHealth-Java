package br.com.yourpethealth.controller.web;

import br.com.yourpethealth.entity.enums.TipoConsulta;
import br.com.yourpethealth.exception.AcessoNegadoException;
import br.com.yourpethealth.exception.RegraNegocioException;
import br.com.yourpethealth.exception.ValidacaoException;
import br.com.yourpethealth.form.ConsultaForm;
import br.com.yourpethealth.service.ConsultaService;
import br.com.yourpethealth.service.PetService;
import br.com.yourpethealth.service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/app/consultas")
@RequiredArgsConstructor
public class ConsultaWebController {

    private final ConsultaService consultaService;
    private final PetService petService;
    private final VeterinarioService veterinarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultas", consultaService.listar());
        return "app/consultas-lista";
    }

    @GetMapping("/nova")
    public String formularioNovo(@RequestParam(required = false) Long petId, Model model) {
        var form = new ConsultaForm();
        form.setPetId(petId);

        model.addAttribute("form", form);
        model.addAttribute("edicao", false);
        preencherSelects(model);
        return "app/consulta-form";
    }

    @PostMapping
    public String agendar(@Valid @ModelAttribute("form") ConsultaForm form,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes flash) {

        return salvar(null, form, resultado, model, flash,
                () -> consultaService.criar(form.paraRequest()));
    }

    @GetMapping("/{id}/editar")
    public String formularioEdicao(@PathVariable Long id, Model model) {
        model.addAttribute("form", ConsultaForm.de(consultaService.buscarPorId(id)));
        model.addAttribute("consultaId", id);
        model.addAttribute("edicao", true);
        preencherSelects(model);
        return "app/consulta-form";
    }

    @PostMapping("/{id}")
    public String reagendar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ConsultaForm form,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes flash) {

        return salvar(id, form, resultado, model, flash,
                () -> consultaService.atualizar(id, form.paraAtualizacao()));
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes flash) {
        consultaService.cancelar(id);
        flash.addFlashAttribute("sucesso", "Consulta cancelada.");
        return "redirect:/app/consultas";
    }

    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long id, RedirectAttributes flash) {
        consultaService.remover(id);
        flash.addFlashAttribute("sucesso", "Consulta excluída.");
        return "redirect:/app/consultas";
    }

    private String salvar(Long id, ConsultaForm form, BindingResult resultado,
                          Model model, RedirectAttributes flash, Runnable operacao) {

        boolean edicao = id != null;

        if (resultado.hasErrors()) {
            prepararModelDoFormulario(model, id, edicao);
            return "app/consulta-form";
        }

        try {
            operacao.run();
        } catch (ValidacaoException | RegraNegocioException | AcessoNegadoException e) {
            resultado.reject("agendamento", e.getMessage());
            prepararModelDoFormulario(model, id, edicao);
            return "app/consulta-form";
        }

        flash.addFlashAttribute("sucesso",
                edicao ? "Consulta reagendada com sucesso." : "Consulta agendada com sucesso.");
        return "redirect:/app/consultas";
    }

    private void prepararModelDoFormulario(Model model, Long id, boolean edicao) {
        if (edicao) model.addAttribute("consultaId", id);
        model.addAttribute("edicao", edicao);
        preencherSelects(model);
    }

    private void preencherSelects(Model model) {
        model.addAttribute("pets", petService.listar());
        model.addAttribute("veterinarios", veterinarioService.listar());
    }

    @ModelAttribute("tipos")
    public TipoConsulta[] tipos() {
        return TipoConsulta.values();
    }
}