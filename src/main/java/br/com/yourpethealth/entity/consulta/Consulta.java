package br.com.yourpethealth.entity.consulta;

import br.com.yourpethealth.entity.pet.Pet;
import br.com.yourpethealth.entity.usuario.Veterinario;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "t_consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    private String tipo;

    @Column(length = 1000)
    private String descricao;

    private LocalDate data;

    @Column(length = 1000)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    private StatusConsulta status;

    public Consulta() {
    }

    public Consulta(Long id, Pet pet, Veterinario veterinario, String tipo, String descricao, LocalDate data, String observacoes, StatusConsulta status) {
        this.id = id;
        this.pet = pet;
        this.veterinario = veterinario;
        this.tipo = tipo;
        this.descricao = descricao;
        this.data = data;
        this.observacoes = observacoes;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
    }
}