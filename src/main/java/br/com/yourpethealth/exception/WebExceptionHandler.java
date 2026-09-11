package br.com.yourpethealth.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "br.com.yourpethealth.controller.web")
public class WebExceptionHandler {

    @ExceptionHandler(AcessoNegadoException.class)
    public String acessoNegado() {
        return "redirect:/403";
    }

    @ExceptionHandler({IdNaoEncontradoException.class, RegraNegocioException.class,
            ValidacaoException.class})
    public String erroDeNegocio(RuntimeException ex, Model model) {
        model.addAttribute("mensagem", ex.getMessage());
        return "erro";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String integridade(Model model) {
        model.addAttribute("mensagem",
                "Não foi possível concluir a operação porque este registro possui "
                        + "vínculos com outros dados no sistema.");
        return "erro";
    }
}