package br.com.yourpethealth.form;

import br.com.yourpethealth.dto.request.VeterinarioRequest;
import br.com.yourpethealth.dto.response.VeterinarioResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VeterinarioAdminForm {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @NotBlank(message = "CRMV é obrigatório")
    @Size(max = 20, message = "CRMV deve ter no máximo 20 caracteres")
    private String crmv;

    @Size(max = 100, message = "Especialidade deve ter no máximo 100 caracteres")
    private String especialidade;

    public static VeterinarioAdminForm de(VeterinarioResponse vet) {
        var form = new VeterinarioAdminForm();
        form.nome = vet.nome();
        form.telefone = vet.telefone();
        form.crmv = vet.crmv();
        form.especialidade = vet.especialidade();
        return form;
    }

    /** O e-mail é a credencial de acesso: é mantido, nunca editado por aqui. */
    public VeterinarioRequest paraRequest(String emailAtual) {
        return new VeterinarioRequest(nome, emailAtual, telefone, crmv, especialidade);
    }
}