package br.com.yourpethealth.entity.historico;

import br.com.yourpethealth.entity.pet.Pet;
import jakarta.persistence.*;

import java.time.LocalDate;

//@Entity
//@Table(name = "t_historico_clinico")
public class HistoricoClinico {

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

    public HistoricoClinico() {
    }

    public HistoricoClinico(Long id, Pet pet, String tipo,
                            String descricao, LocalDate data) {
        this.id = id;
        this.pet = pet;
        this.tipo = tipo;
        this.descricao = descricao;
        this.data = data;
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
}
