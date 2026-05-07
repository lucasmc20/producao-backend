package com.producao.domain.insumo.service;

import com.producao.domain.insumo.dto.RequisicaoAtualizacaoInsumo;
import com.producao.domain.insumo.dto.RequisicaoCadastroInsumo;
import com.producao.domain.insumo.dto.RequisicaoEntradaEstoque;
import com.producao.domain.insumo.dto.RespostaInsumo;
import com.producao.domain.insumo.dto.RespostaSaldoEstoque;
import com.producao.domain.insumo.model.Insumo;
import com.producao.domain.insumo.repository.RepositorioInsumo;
import com.producao.infra.exception.RecursoNaoEncontradoException;
import com.producao.infra.exception.SaldoInsuficienteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicoInsumo {

    private static final Logger log = LoggerFactory.getLogger(ServicoInsumo.class);

    private final RepositorioInsumo repositorioInsumo;

    public ServicoInsumo(RepositorioInsumo repositorioInsumo) {
        this.repositorioInsumo = repositorioInsumo;
    }

    /**
     * Lista todos os insumos cadastrados.
     */
    @Transactional(readOnly = true)
    public List<RespostaInsumo> listarTodos() {
        return repositorioInsumo.findAll()
                .stream()
                .map(RespostaInsumo::de)
                .toList();
    }

    /**
     * Busca um insumo pelo identificador.
     *
     * @param id identificador do insumo
     * @return dados do insumo encontrado
     * @throws RecursoNaoEncontradoException se o insumo nao existir
     */
    @Transactional(readOnly = true)
    public RespostaInsumo buscarPorId(Long id) {
        Insumo insumo = buscarEntidadePorId(id);
        return RespostaInsumo.de(insumo);
    }

    /**
     * Consulta o saldo disponivel de um insumo para uma quantidade requerida.
     *
     * @param id                  identificador do insumo
     * @param quantidadeRequerida quantidade a verificar
     * @return saldo do estoque com indicador de suficiencia
     */
    @Transactional(readOnly = true)
    public RespostaSaldoEstoque consultarSaldo(Long id, BigDecimal quantidadeRequerida) {
        Insumo insumo = buscarEntidadePorId(id);
        return RespostaSaldoEstoque.de(insumo, quantidadeRequerida);
    }

    /**
     * Registra o consumo de um insumo, subtraindo o saldo disponivel.
     *
     * @param id         identificador do insumo
     * @param quantidade quantidade a consumir
     * @throws SaldoInsuficienteException se o saldo for insuficiente
     */
    @Transactional
    public void registrarConsumo(Long id, BigDecimal quantidade) {
        Insumo insumo = buscarEntidadePorId(id);

        if (insumo.getSaldoDisponivel().compareTo(quantidade) < 0) {
            throw new SaldoInsuficienteException(
                    String.format("Saldo insuficiente. Disponivel: %s %s",
                            insumo.getSaldoDisponivel(), insumo.getUnidadeMedida())
            );
        }

        insumo.setSaldoDisponivel(insumo.getSaldoDisponivel().subtract(quantidade));
        repositorioInsumo.save(insumo);
        log.info("Consumo registrado: insumo={}, quantidade={}", insumo.getNome(), quantidade);
    }

    /**
     * Retorna a entidade Insumo pelo id, lancando excecao se nao encontrada.
     */
    public Insumo buscarEntidadePorId(Long id) {
        return repositorioInsumo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Insumo nao encontrado para o id: " + id));
    }

    /**
     * Cadastra um novo insumo no sistema.
     */
    @Transactional
    public RespostaInsumo cadastrar(RequisicaoCadastroInsumo req) {
        Insumo insumo = new Insumo();
        insumo.setNome(req.nome());
        insumo.setDescricao(req.descricao());
        insumo.setUnidadeMedida(req.unidadeMedida());
        insumo.setSaldoDisponivel(req.saldoInicial());
        insumo.setEstoqueMinimo(req.estoqueMinimo());
        Insumo salvo = repositorioInsumo.save(insumo);
        log.info("Insumo cadastrado: id={}, nome={}", salvo.getId(), salvo.getNome());
        return RespostaInsumo.de(salvo);
    }

    /**
     * Atualiza dados cadastrais de um insumo (nao altera saldo).
     */
    @Transactional
    public RespostaInsumo atualizar(Long id, RequisicaoAtualizacaoInsumo req) {
        Insumo insumo = buscarEntidadePorId(id);
        insumo.setNome(req.nome());
        insumo.setDescricao(req.descricao());
        insumo.setUnidadeMedida(req.unidadeMedida());
        insumo.setEstoqueMinimo(req.estoqueMinimo());
        repositorioInsumo.save(insumo);
        log.info("Insumo atualizado: id={}, nome={}", insumo.getId(), insumo.getNome());
        return RespostaInsumo.de(insumo);
    }

    /**
     * Remove permanentemente um insumo (somente se saldo for zero).
     */
    @Transactional
    public void remover(Long id) {
        Insumo insumo = buscarEntidadePorId(id);
        if (insumo.getSaldoDisponivel().compareTo(BigDecimal.ZERO) > 0) {
            throw new com.producao.infra.exception.RegraDeNegocioException(
                    "Nao e possivel remover insumo com saldo disponivel. Zere o estoque antes.");
        }
        repositorioInsumo.delete(insumo);
        log.info("Insumo removido: id={}, nome={}", id, insumo.getNome());
    }

    /**
     * Registra entrada de estoque (adiciona ao saldo disponivel).
     */
    @Transactional
    public RespostaInsumo registrarEntrada(Long id, RequisicaoEntradaEstoque req) {
        Insumo insumo = buscarEntidadePorId(id);
        insumo.setSaldoDisponivel(insumo.getSaldoDisponivel().add(req.quantidade()));
        repositorioInsumo.save(insumo);
        log.info("Entrada de estoque registrada: insumo={}, quantidade={}", insumo.getNome(), req.quantidade());
        return RespostaInsumo.de(insumo);
    }
}
