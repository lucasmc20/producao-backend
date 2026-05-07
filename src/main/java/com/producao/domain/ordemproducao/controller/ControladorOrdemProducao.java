package com.producao.domain.ordemproducao.controller;

import com.producao.domain.ordemproducao.dto.RequisicaoCriarOrdem;
import com.producao.domain.ordemproducao.dto.RequisicaoFinalizarLote;
import com.producao.domain.ordemproducao.dto.RequisicaoIniciarLote;
import com.producao.domain.ordemproducao.dto.RespostaOrdemProducao;
import com.producao.domain.ordemproducao.model.StatusOrdem;
import com.producao.domain.ordemproducao.service.ServicoOrdemProducao;
import com.producao.infra.response.RespostaPadrao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Ordens de Producao", description = "Gerenciamento do ciclo de vida das ordens de producao")
public class ControladorOrdemProducao {

    private final ServicoOrdemProducao servicoOrdemProducao;

    public ControladorOrdemProducao(ServicoOrdemProducao servicoOrdemProducao) {
        this.servicoOrdemProducao = servicoOrdemProducao;
    }

    @GetMapping
    @Operation(summary = "Lista todas as ordens de producao")
    public ResponseEntity<RespostaPadrao<List<RespostaOrdemProducao>>> listarTodas(
            @RequestParam(required = false) StatusOrdem status) {
        List<RespostaOrdemProducao> resultado = status != null
                ? servicoOrdemProducao.listarPorStatus(status)
                : servicoOrdemProducao.listarTodas();
        return ResponseEntity.ok(RespostaPadrao.sucesso(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca ordem de producao por ID")
    public ResponseEntity<RespostaPadrao<RespostaOrdemProducao>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoOrdemProducao.buscarPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMINISTRADOR')")
    @Operation(summary = "Cria nova ordem de producao (GESTOR ou ADMINISTRADOR)")
    public ResponseEntity<RespostaPadrao<RespostaOrdemProducao>> criar(
            @RequestBody @Valid RequisicaoCriarOrdem requisicao) {
        RespostaOrdemProducao ordem = servicoOrdemProducao.criar(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(RespostaPadrao.sucesso(ordem));
    }

    @PatchMapping("/{id}/iniciar")
    @Operation(summary = "Inicia o lote de uma ordem de producao")
    public ResponseEntity<RespostaPadrao<RespostaOrdemProducao>> iniciarLote(
            @PathVariable Long id,
            @RequestBody @Valid RequisicaoIniciarLote requisicao) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoOrdemProducao.iniciarLote(id, requisicao)));
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finaliza o lote de uma ordem de producao")
    public ResponseEntity<RespostaPadrao<RespostaOrdemProducao>> finalizarLote(
            @PathVariable Long id,
            @RequestBody @Valid RequisicaoFinalizarLote requisicao) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoOrdemProducao.finalizarLote(id, requisicao)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cancela uma ordem de producao (apenas ADMINISTRADOR)")
    public ResponseEntity<RespostaPadrao<RespostaOrdemProducao>> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoOrdemProducao.cancelar(id)));
    }
}
