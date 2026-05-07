package com.producao.domain.ordemproducao.service;

import com.producao.domain.maquina.model.Maquina;
import com.producao.domain.maquina.model.StatusMaquina;
import com.producao.domain.maquina.repository.RepositorioMaquina;
import com.producao.domain.ordemproducao.dto.RequisicaoCriarOrdem;
import com.producao.domain.ordemproducao.dto.RequisicaoFinalizarLote;
import com.producao.domain.ordemproducao.dto.RequisicaoIniciarLote;
import com.producao.domain.ordemproducao.dto.RespostaOrdemProducao;
import com.producao.domain.ordemproducao.model.OrdemProducao;
import com.producao.domain.ordemproducao.model.StatusOrdem;
import com.producao.domain.ordemproducao.repository.RepositorioOrdemProducao;
import com.producao.domain.usuario.model.Usuario;
import com.producao.domain.usuario.repository.RepositorioUsuario;
import com.producao.infra.exception.RecursoNaoEncontradoException;
import com.producao.infra.exception.RegraDeNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ServicoOrdemProducao {

    private static final Logger log = LoggerFactory.getLogger(ServicoOrdemProducao.class);

    private final RepositorioOrdemProducao repositorioOrdemProducao;
    private final RepositorioMaquina repositorioMaquina;
    private final RepositorioUsuario repositorioUsuario;

    public ServicoOrdemProducao(
            RepositorioOrdemProducao repositorioOrdemProducao,
            RepositorioMaquina repositorioMaquina,
            RepositorioUsuario repositorioUsuario) {
        this.repositorioOrdemProducao = repositorioOrdemProducao;
        this.repositorioMaquina = repositorioMaquina;
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Lista todas as ordens de producao cadastradas.
     */
    @Transactional(readOnly = true)
    public List<RespostaOrdemProducao> listarTodas() {
        return repositorioOrdemProducao.findAll()
                .stream()
                .map(RespostaOrdemProducao::de)
                .toList();
    }

    /**
     * Lista ordens de producao filtrando pelo status.
     *
     * @param status status a filtrar
     * @return lista de ordens com o status informado
     */
    @Transactional(readOnly = true)
    public List<RespostaOrdemProducao> listarPorStatus(StatusOrdem status) {
        return repositorioOrdemProducao.findByStatus(status)
                .stream()
                .map(RespostaOrdemProducao::de)
                .toList();
    }

    /**
     * Busca uma ordem de producao pelo identificador.
     *
     * @param id identificador da ordem
     * @return dados da ordem encontrada
     * @throws RecursoNaoEncontradoException se a ordem nao existir
     */
    @Transactional(readOnly = true)
    public RespostaOrdemProducao buscarPorId(Long id) {
        return RespostaOrdemProducao.de(buscarEntidadePorId(id));
    }

    /**
     * Cria uma nova ordem de producao. A maquina informada nao pode estar OPERANDO.
     *
     * @param requisicao dados da nova ordem
     * @return ordem criada
     * @throws RegraDeNegocioException se a maquina ja estiver operando
     */
    @Transactional
    public RespostaOrdemProducao criar(RequisicaoCriarOrdem requisicao) {
        Maquina maquina = repositorioMaquina.findById(requisicao.maquinaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Maquina nao encontrada para o id: " + requisicao.maquinaId()));

        if (maquina.getStatus() == StatusMaquina.OPERANDO) {
            throw new RegraDeNegocioException(
                    "Nao e possivel criar uma ordem para a maquina que ja esta OPERANDO: " + maquina.getNome());
        }

        Usuario operador = repositorioUsuario.findById(requisicao.operadorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Operador nao encontrado para o id: " + requisicao.operadorId()));

        OrdemProducao ordem = new OrdemProducao();
        ordem.setCodigoOrdem(gerarCodigoOrdem(requisicao.codigoOrdem()));
        ordem.setDescricao(requisicao.descricao());
        ordem.setStatus(StatusOrdem.PENDENTE);
        ordem.setMaquina(maquina);
        ordem.setOperador(operador);
        ordem.setQuantidadePlanejada(requisicao.quantidadePlanejada());
        ordem.setQuantidadeProduzida(java.math.BigDecimal.ZERO);

        OrdemProducao salva = repositorioOrdemProducao.save(ordem);
        log.info("Ordem criada: codigo={}, maquina={}", salva.getCodigoOrdem(), maquina.getNome());

        return RespostaOrdemProducao.de(salva);
    }

    /**
     * Inicia o lote de uma ordem, mudando o status para EM_ANDAMENTO.
     * A ordem deve estar PENDENTE ou PAUSADA. A maquina associada passa a OPERANDO.
     *
     * @param id         identificador da ordem
     * @param requisicao dados de inicio
     * @return ordem atualizada
     * @throws RegraDeNegocioException se a ordem ja estiver em andamento
     */
    @Transactional
    public RespostaOrdemProducao iniciarLote(Long id, RequisicaoIniciarLote requisicao) {
        OrdemProducao ordem = buscarEntidadePorId(id);

        if (ordem.getStatus() == StatusOrdem.EM_ANDAMENTO) {
            throw new RegraDeNegocioException(
                    "Nao e possivel iniciar uma ordem ja em andamento");
        }

        if (ordem.getStatus() == StatusOrdem.CONCLUIDA || ordem.getStatus() == StatusOrdem.CANCELADA) {
            throw new RegraDeNegocioException(
                    "Nao e possivel iniciar uma ordem com status: " + ordem.getStatus());
        }

        if (requisicao.operadorId() != null) {
            Usuario operador = repositorioUsuario.findById(requisicao.operadorId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Operador nao encontrado para o id: " + requisicao.operadorId()));
            ordem.setOperador(operador);
        }

        ordem.setStatus(StatusOrdem.EM_ANDAMENTO);
        ordem.setIniciadoEm(Instant.now());

        if (ordem.getMaquina() != null) {
            ordem.getMaquina().setStatus(StatusMaquina.OPERANDO);
            repositorioMaquina.save(ordem.getMaquina());
        }

        OrdemProducao salva = repositorioOrdemProducao.save(ordem);
        log.info("Ordem iniciada: codigo={}", salva.getCodigoOrdem());

        return RespostaOrdemProducao.de(salva);
    }

    /**
     * Finaliza uma ordem de producao, mudando o status para CONCLUIDA.
     * A maquina associada volta para INATIVA.
     *
     * @param id         identificador da ordem
     * @param requisicao dados de finalizacao com quantidade produzida
     * @return ordem atualizada
     * @throws RegraDeNegocioException se a ordem nao estiver EM_ANDAMENTO
     */
    @Transactional
    public RespostaOrdemProducao finalizarLote(Long id, RequisicaoFinalizarLote requisicao) {
        OrdemProducao ordem = buscarEntidadePorId(id);

        if (ordem.getStatus() != StatusOrdem.EM_ANDAMENTO) {
            throw new RegraDeNegocioException(
                    "Nao e possivel finalizar uma ordem que nao esta EM_ANDAMENTO. Status atual: "
                            + ordem.getStatus());
        }

        ordem.setStatus(StatusOrdem.CONCLUIDA);
        ordem.setQuantidadeProduzida(requisicao.quantidadeProduzida());
        ordem.setFinalizadoEm(Instant.now());

        if (ordem.getMaquina() != null) {
            ordem.getMaquina().setStatus(StatusMaquina.INATIVA);
            repositorioMaquina.save(ordem.getMaquina());
        }

        OrdemProducao salva = repositorioOrdemProducao.save(ordem);
        log.info("Ordem finalizada: codigo={}, qtdProduzida={}", salva.getCodigoOrdem(), requisicao.quantidadeProduzida());

        return RespostaOrdemProducao.de(salva);
    }

    /**
     * Cancela uma ordem de producao.
     *
     * @param id identificador da ordem
     * @return ordem cancelada
     * @throws RegraDeNegocioException se a ordem ja estiver concluida ou cancelada
     */
    @Transactional
    public RespostaOrdemProducao cancelar(Long id) {
        OrdemProducao ordem = buscarEntidadePorId(id);

        if (ordem.getStatus() == StatusOrdem.CONCLUIDA || ordem.getStatus() == StatusOrdem.CANCELADA) {
            throw new RegraDeNegocioException(
                    "Nao e possivel cancelar uma ordem com status: " + ordem.getStatus());
        }

        if (ordem.getStatus() == StatusOrdem.EM_ANDAMENTO && ordem.getMaquina() != null) {
            ordem.getMaquina().setStatus(StatusMaquina.INATIVA);
            repositorioMaquina.save(ordem.getMaquina());
        }

        ordem.setStatus(StatusOrdem.CANCELADA);
        OrdemProducao salva = repositorioOrdemProducao.save(ordem);
        log.info("Ordem cancelada: codigo={}", salva.getCodigoOrdem());

        return RespostaOrdemProducao.de(salva);
    }

    /**
     * Retorna a entidade OrdemProducao pelo id, lancando excecao se nao encontrada.
     */
    OrdemProducao buscarEntidadePorId(Long id) {
        return repositorioOrdemProducao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ordem de producao nao encontrada para o id: " + id));
    }

    private String gerarCodigoOrdem(String codigoInformado) {
        if (codigoInformado != null && !codigoInformado.isBlank()) {
            return codigoInformado;
        }
        return "OP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
