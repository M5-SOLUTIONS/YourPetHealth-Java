package br.com.yourpethealth.entity.exame;

import br.com.yourpethealth.entity.pet.Pet;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "exames")
public class Exame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Column(name = "nome_exame")
    private String nomeExame;

    private String status;

    private LocalDate data;

    @Column(length = 1000)
    private String observacoes;

    public Exame() {
    }

    public Exame(Long id, Pet pet, String nomeExame,
                 String status, LocalDate data, String observacoes) {
        this.id = id;
        this.pet = pet;
        this.nomeExame = nomeExame;
        this.status = status;
        this.data = data;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public String getNomeExame() {
        return nomeExame;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getData() {
        return data;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setNomeExame(String nomeExame) {
        this.nomeExame = nomeExame;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
