package br.com.yourpethealth.entity.usuario;

import jakarta.persistence.*;

@Entity
@Table(name = "t_veterinarios")
public class Veterinario extends Usuario {

    private String crmv;

    private String especialidade;

    public Veterinario() {
    }

    public Veterinario(Long id, String nome, String email, String senha,
                       String telefone, String crmv, String especialidade) {

        setId(id);
        setNome(nome);
        setEmail(email);
        setSenha(senha);
        setTelefone(telefone);

        this.crmv = crmv;
        this.especialidade = especialidade;
    }

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}