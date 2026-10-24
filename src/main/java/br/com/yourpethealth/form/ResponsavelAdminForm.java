package br.com.yourpethealth.form;

import br.com.yourpethealth.dto.request.ResponsavelRequest;
import br.com.yourpethealth.dto.response.ResponsavelResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsavelAdminForm {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    public static ResponsavelAdminForm de(ResponsavelResponse responsavel) {
        var form = new ResponsavelAdminForm();
        form.nome = responsavel.nome();
        form.telefone = responsavel.telefone();
        return form;
    }

    /** O e-mail é a credencial de acesso: é mantido, nunca editado por aqui. */
    public ResponsavelRequest paraRequest(String emailAtual) {
        return new ResponsavelRequest(nome, emailAtual, telefone);
    }
}