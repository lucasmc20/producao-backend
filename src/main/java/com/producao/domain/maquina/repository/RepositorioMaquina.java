package com.producao.domain.maquina.repository;

import com.producao.domain.maquina.model.Maquina;
import com.producao.domain.maquina.model.StatusMaquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioMaquina extends JpaRepository<Maquina, Long> {

    List<Maquina> findByStatus(StatusMaquina status);

    boolean existsByNome(String nome);
}
