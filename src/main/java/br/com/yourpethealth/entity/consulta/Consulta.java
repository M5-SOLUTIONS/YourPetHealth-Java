package br.com.yourpethealth.entity.consulta;

import br.com.yourpethealth.entity.pet.Pet;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private String tipo;

    @Column(length = 1000)
    private String descricao;

    private LocalDate data;

    private String veterinario;

    @Column(length = 1000)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    private StatusConsulta status;

    public Consulta() {
    }

    public Consulta(Long id, Pet pet, String tipo, String descricao,
                    LocalDate data, String veterinario,
                    String observacoes, StatusConsulta status) {
        this.id = id;
        this.pet = pet;
        this.tipo = tipo;
        this.descricao = descricao;
        this.data = data;
        this.veterinario = veterinario;
        this.observacoes = observacoes;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
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

    public String getVeterinario() {
        return veterinario;
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

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
    }
}
