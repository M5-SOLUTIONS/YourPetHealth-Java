package br.com.yourpethealth.entity.pet;

import jakarta.persistence.*;
import org.apache.catalina.User;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    private String nome;

    private String raca;

    private Integer idade;

    private Double peso;

    @Enumerated(EnumType.STRING)
    private SexoPet sexo;

    public Pet() {
    }

    public Pet(Long id, User usuario, String nome, String raca,
               Integer idade, Double peso, SexoPet sexo) {
        this.id = id;
        this.usuario = usuario;
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
        this.peso = peso;
        this.sexo = sexo;
    }

    public Long getId() {
        return id;
    }

    public User getUsuario() {
        return usuario;
    }

    public String getNome() {
        return nome;
    }

    public String getRaca() {
        return raca;
    }

    public Integer getIdade() {
        return idade;
    }

    public Double getPeso() {
        return peso;
    }

    public SexoPet getSexo() {
        return sexo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public void setSexo(SexoPet sexo) {
        this.sexo = sexo;
    }
}