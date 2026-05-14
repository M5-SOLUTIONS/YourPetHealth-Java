package br.com.yourpethealth.repository;

import br.com.yourpethealth.entity.exame.Exame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExameRepository extends JpaRepository<Exame, Long> {
}
