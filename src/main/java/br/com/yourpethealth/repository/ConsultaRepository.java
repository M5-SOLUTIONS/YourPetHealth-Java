package br.com.yourpethealth.repository;

import br.com.yourpethealth.entity.consulta.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}
