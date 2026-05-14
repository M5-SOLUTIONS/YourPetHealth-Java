package br.com.yourpethealth.repository;

import br.com.yourpethealth.entity.historico.HistoricoClinico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoClinicoRepository extends JpaRepository<HistoricoClinico, Long> {
}
