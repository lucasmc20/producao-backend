package com.producao.domain.lote.service;

import com.producao.domain.insumo.model.Insumo;
import com.producao.domain.insumo.service.ServicoInsumo;
import com.producao.domain.lote.dto.RequisicaoRegistroInsumo;
import com.producao.domain.lote.dto.RespostaLote;
import com.producao.domain.lote.model.Lote;
import com.producao.domain.lote.model.StatusLote;
import com.producao.domain.lote.repository.RepositorioLote;
import com.producao.domain.ordemproducao.model.OrdemProducao;
import com.producao.domain.ordemproducao.model.StatusOrdem;
import com.producao.domain.ordemproducao.repository.RepositorioOrdemProducao;
import com.producao.domain.usuario.model.Usuario;
import com.producao.infra.exception.RecursoNaoEncontradoException;
import com.producao.infra.exception.RegraDeNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoLote {

    private static final Logger log = LoggerFactory.getLogger(ServicoLote.class);

    private final RepositorioLote repositorioLote;
    private final RepositorioOrdemProducao repositorioOrdemProducao;
    private final ServicoInsumo servicoInsumo;

    public ServicoLote(
            RepositorioLote repositorioLote,
            RepositorioOrdemProducao repositorioOrdemProducao,
            ServicoInsumo servicoInsumo) {
        this.repositorioLote = repositorioLote;
        this.repositorioOrdemProducao = repositorioOrdemProducao;
        this.servicoInsumo = servicoInsumo;
    }

    /**
     * Lista todos os lotes de uma ordem de producao.
     *
     * @param ordemProducaoId identificador da ordem
     * @return lista de lotes da ordem
     */
    @Transactional(readOnly = true)
    public List<RespostaLote> listarPorOrdem(Long ordemProducaoId) {
        return repositorioLote.findByOrdemProducaoId(ordemProducaoId)
                .stream()
                .map(RespostaLote::de)
                .toList();
    }

    /**
     * Busca um lote pelo identificador.
     *
     * @param id identificador do lote
     * @return dados do lote encontrado
     * @throws RecursoNaoEncontradoException se o lote nao existir
     */
    @Transactional(readOnly = true)
    public RespostaLote buscarPorId(Long id) {
        return RespostaLote.de(buscarEntidadePorId(id));
    }

    /**
     * Registra o consumo de um insumo em uma ordem de producao, criando um novo lote.
     * A ordem deve estar com status EM_ANDAMENTO para permitir o registro.
     *
     * @param ordemProducaoId identificador da ordem de producao
     * @param requisicao      dados do registro de consumo
     * @return lote criado
     * @throws RegraDeNegocioException se a ordem nao estiver EM_ANDAMENTO
     */
    @Transactional
    public RespostaLote registrarConsumo(Long ordemProducaoId, RequisicaoRegistroInsumo requisicao) {
        OrdemProducao ordem = repositorioOrdemProducao.findById(ordemProducaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ordem de producao nao encontrada para o id: " + ordemProducaoId));

        if (ordem.getStatus() != StatusOrdem.EM_ANDAMENTO) {
            throw new RegraDeNegocioException(
                    "Nao e possivel registrar consumo em uma ordem que nao esta EM_ANDAMENTO. Status atual: "
                            + ordem.getStatus());
        }

        if (repositorioLote.existsByNumeroLote(requisicao.numeroLote())) {
            throw new RegraDeNegocioException(
                    "Numero de lote ja utilizado: " + requisicao.numeroLote());
        }

        Insumo insumo = servicoInsumo.buscarEntidadePorId(requisicao.insumoId());
        servicoInsumo.registrarConsumo(requisicao.insumoId(), requisicao.quantidadeConsumida());

        Usuario operadorLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Lote lote = new Lote();
        lote.setNumeroLote(requisicao.numeroLote());
        lote.setOrdemProducao(ordem);
        lote.setStatus(StatusLote.ABERTO);
        lote.setInsumo(insumo);
        lote.setQuantidadeConsumida(requisicao.quantidadeConsumida());
        lote.setRegistradoPor(operadorLogado);

        Lote salvo = repositorioLote.save(lote);
        log.info("Lote registrado: numero={}, ordem={}, insumo={}, quantidade={}",
                salvo.getNumeroLote(), ordem.getCodigoOrdem(), insumo.getNome(), requisicao.quantidadeConsumida());

        return RespostaLote.de(salvo);
    }

    /**
     * Fecha um lote, impedindo novos registros nele.
     *
     * @param id identificador do lote
     * @return lote atualizado
     * @throws RegraDeNegocioException se o lote ja estiver fechado ou cancelado
     */
    @Transactional
    public RespostaLote fecharLote(Long id) {
        Lote lote = buscarEntidadePorId(id);

        if (lote.getStatus() != StatusLote.ABERTO) {
            throw new RegraDeNegocioException(
                    "Nao e possivel fechar um lote com status: " + lote.getStatus());
        }

        lote.setStatus(StatusLote.FECHADO);
        return RespostaLote.de(repositorioLote.save(lote));
    }

    /**
     * Retorna a entidade Lote pelo id, lancando excecao se nao encontrada.
     */
    Lote buscarEntidadePorId(Long id) {
        return repositorioLote.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lote nao encontrado para o id: " + id));
    }
}
