package br.com.yourpethealth.entity.alerta;

import br.com.yourpethealth.entity.pet.Pet;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "t_alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private String titulo;

    @Column(length = 1000)
    private String descricao;

    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private TipoAlerta tipo;

    @Enumerated(EnumType.STRING)
    private StatusAlerta status;

    public Alerta() {
    }

    public Alerta(Long id, Pet pet, String titulo, String descricao, LocalDate data, TipoAlerta tipo, StatusAlerta status) {
        this.id = id;
        this.pet = pet;
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.tipo = tipo;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public StatusAlerta getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setTipo(TipoAlerta tipo) {
        this.tipo = tipo;
    }

    public void setStatus(StatusAlerta status) {
        this.status = status;
    }
}
