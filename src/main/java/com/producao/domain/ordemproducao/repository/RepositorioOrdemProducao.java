package com.producao.domain.ordemproducao.repository;

import com.producao.domain.ordemproducao.model.OrdemProducao;
import com.producao.domain.ordemproducao.model.StatusOrdem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioOrdemProducao extends JpaRepository<OrdemProducao, Long> {

    Optional<OrdemProducao> findByCodigoOrdem(String codigoOrdem);

    List<OrdemProducao> findByStatus(StatusOrdem status);

    List<OrdemProducao> findByOperadorId(Long operadorId);

    List<OrdemProducao> findByMaquinaId(Long maquinaId);

    boolean existsByCodigoOrdem(String codigoOrdem);
}
