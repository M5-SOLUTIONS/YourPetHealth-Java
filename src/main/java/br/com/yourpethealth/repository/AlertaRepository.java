package br.com.yourpethealth.repository;

import br.com.yourpethealth.entity.alerta.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}
