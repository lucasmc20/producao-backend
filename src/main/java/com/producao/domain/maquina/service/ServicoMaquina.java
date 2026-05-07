package com.producao.domain.maquina.service;

import com.producao.domain.maquina.dto.EventoStatusMaquina;
import com.producao.domain.maquina.dto.RespostaMaquina;
import com.producao.domain.maquina.model.Maquina;
import com.producao.domain.maquina.model.StatusMaquina;
import com.producao.domain.maquina.repository.RepositorioMaquina;
import com.producao.infra.exception.RecursoNaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ServicoMaquina {

    private static final Logger log = LoggerFactory.getLogger(ServicoMaquina.class);

    private final RepositorioMaquina repositorioMaquina;
    private final SimpMessagingTemplate messagingTemplate;

    public ServicoMaquina(RepositorioMaquina repositorioMaquina, SimpMessagingTemplate messagingTemplate) {
        this.repositorioMaquina = repositorioMaquina;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Lista todas as maquinas cadastradas.
     */
    @Transactional(readOnly = true)
    public List<RespostaMaquina> listarTodas() {
        return repositorioMaquina.findAll()
                .stream()
                .map(RespostaMaquina::de)
                .toList();
    }

    /**
     * Busca uma maquina pelo identificador.
     *
     * @param id identificador da maquina
     * @return dados da maquina encontrada
     * @throws RecursoNaoEncontradoException se a maquina nao existir
     */
    @Transactional(readOnly = true)
    public RespostaMaquina buscarPorId(Long id) {
        return RespostaMaquina.de(buscarEntidadePorId(id));
    }

    /**
     * Lista maquinas filtrando pelo status.
     *
     * @param status status a filtrar
     * @return lista de maquinas com o status informado
     */
    @Transactional(readOnly = true)
    public List<RespostaMaquina> listarPorStatus(StatusMaquina status) {
        return repositorioMaquina.findByStatus(status)
                .stream()
                .map(RespostaMaquina::de)
                .toList();
    }

    /**
     * Atualiza o status de uma maquina e emite evento WebSocket para os inscritos.
     *
     * @param id        identificador da maquina
     * @param novoStatus novo status a aplicar
     * @return dados atualizados da maquina
     */
    @Transactional
    public RespostaMaquina atualizarStatus(Long id, StatusMaquina novoStatus) {
        Maquina maquina = buscarEntidadePorId(id);
        StatusMaquina statusAnterior = maquina.getStatus();

        maquina.setStatus(novoStatus);
        repositorioMaquina.save(maquina);

        EventoStatusMaquina evento = new EventoStatusMaquina(
                maquina.getId(),
                maquina.getNome(),
                statusAnterior,
                novoStatus,
                Instant.now()
        );

        messagingTemplate.convertAndSend("/topic/maquinas/" + id + "/status", evento);
        log.info("Status atualizado: maquina={}, de={}, para={}", maquina.getNome(), statusAnterior, novoStatus);

        return RespostaMaquina.de(maquina);
    }

    /**
     * Retorna a entidade Maquina pelo id, lancando excecao se nao encontrada.
     */
    Maquina buscarEntidadePorId(Long id) {
        return repositorioMaquina.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Maquina nao encontrada para o id: " + id));
    }
}
