package com.producao.domain.lote.repository;

import com.producao.domain.lote.model.Lote;
import com.producao.domain.lote.model.StatusLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioLote extends JpaRepository<Lote, Long> {

    List<Lote> findByOrdemProducaoId(Long ordemProducaoId);

    List<Lote> findByOrdemProducaoIdAndStatus(Long ordemProducaoId, StatusLote status);

    Optional<Lote> findByNumeroLote(String numeroLote);

    boolean existsByNumeroLote(String numeroLote);
}
