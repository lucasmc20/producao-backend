package com.producao.domain.insumo.controller;

import com.producao.domain.insumo.dto.RequisicaoAtualizacaoInsumo;
import com.producao.domain.insumo.dto.RequisicaoCadastroInsumo;
import com.producao.domain.insumo.dto.RequisicaoEntradaEstoque;
import com.producao.domain.insumo.dto.RespostaInsumo;
import com.producao.domain.insumo.dto.RespostaSaldoEstoque;
import com.producao.domain.insumo.service.ServicoInsumo;
import com.producao.infra.response.RespostaPadrao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/insumos")
@Tag(name = "Insumos", description = "Gerenciamento de insumos e estoque")
public class ControladorInsumo {

    private final ServicoInsumo servicoInsumo;

    public ControladorInsumo(ServicoInsumo servicoInsumo) {
        this.servicoInsumo = servicoInsumo;
    }

    @GetMapping
    @Operation(summary = "Lista todos os insumos")
    public ResponseEntity<RespostaPadrao<List<RespostaInsumo>>> listarTodos() {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoInsumo.listarTodos()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca insumo por ID")
    public ResponseEntity<RespostaPadrao<RespostaInsumo>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoInsumo.buscarPorId(id)));
    }

    @GetMapping("/{id}/saldo")
    @Operation(summary = "Consulta saldo disponivel de um insumo")
    public ResponseEntity<RespostaPadrao<RespostaSaldoEstoque>> consultarSaldo(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") BigDecimal quantidade) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoInsumo.consultarSaldo(id, quantidade)));
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo insumo")
    public ResponseEntity<RespostaPadrao<RespostaInsumo>> cadastrar(
            @Valid @RequestBody RequisicaoCadastroInsumo requisicao) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaPadrao.sucesso(servicoInsumo.cadastrar(requisicao)));
    }

    @PatchMapping("/{id}/entrada")
    @Operation(summary = "Registra entrada de estoque para um insumo")
    public ResponseEntity<RespostaPadrao<RespostaInsumo>> registrarEntrada(
            @PathVariable Long id,
            @Valid @RequestBody RequisicaoEntradaEstoque requisicao) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoInsumo.registrarEntrada(id, requisicao)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados cadastrais de um insumo")
    public ResponseEntity<RespostaPadrao<RespostaInsumo>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RequisicaoAtualizacaoInsumo requisicao) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoInsumo.atualizar(id, requisicao)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um insumo (somente se saldo for zero)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        servicoInsumo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
