package br.com.yourpethealth.entity.usuario;

import jakarta.persistence.*;

@Entity
@Table(name = "t_responsaveis")
public class Responsavel extends Usuario {

    public Responsavel() {
    }

    public Responsavel(Long id, String nome, String email, String senha, String telefone) {
        setId(id);
        setNome(nome);
        setEmail(email);
        setSenha(senha);
        setTelefone(telefone);
    }
}