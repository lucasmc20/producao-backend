package com.producao.domain.lote.controller;

import com.producao.domain.lote.dto.RequisicaoRegistroInsumo;
import com.producao.domain.lote.dto.RespostaLote;
import com.producao.domain.lote.service.ServicoLote;
import com.producao.infra.response.RespostaPadrao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders/{ordemId}/lots")
@Tag(name = "Lotes", description = "Registro de consumo de insumos por lote em ordens de producao")
public class ControladorLote {

    private final ServicoLote servicoLote;

    public ControladorLote(ServicoLote servicoLote) {
        this.servicoLote = servicoLote;
    }

    @GetMapping
    @Operation(summary = "Lista todos os lotes de uma ordem de producao")
    public ResponseEntity<RespostaPadrao<List<RespostaLote>>> listarPorOrdem(@PathVariable Long ordemId) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoLote.listarPorOrdem(ordemId)));
    }

    @GetMapping("/{loteId}")
    @Operation(summary = "Busca lote por ID")
    public ResponseEntity<RespostaPadrao<RespostaLote>> buscarPorId(
            @PathVariable Long ordemId,
            @PathVariable Long loteId) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoLote.buscarPorId(loteId)));
    }

    @PostMapping
    @Operation(summary = "Registra consumo de insumo e cria novo lote")
    public ResponseEntity<RespostaPadrao<RespostaLote>> registrarConsumo(
            @PathVariable Long ordemId,
            @RequestBody @Valid RequisicaoRegistroInsumo requisicao) {
        RespostaLote lote = servicoLote.registrarConsumo(ordemId, requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(RespostaPadrao.sucesso(lote));
    }

    @PatchMapping("/{loteId}/fechar")
    @Operation(summary = "Fecha um lote aberto")
    public ResponseEntity<RespostaPadrao<RespostaLote>> fecharLote(
            @PathVariable Long ordemId,
            @PathVariable Long loteId) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoLote.fecharLote(loteId)));
    }
}
