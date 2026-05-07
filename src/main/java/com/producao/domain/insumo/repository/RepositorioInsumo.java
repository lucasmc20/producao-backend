package com.producao.domain.insumo.repository;

import com.producao.domain.insumo.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioInsumo extends JpaRepository<Insumo, Long> {

    Optional<Insumo> findByNome(String nome);

    boolean existsByNome(String nome);
}
