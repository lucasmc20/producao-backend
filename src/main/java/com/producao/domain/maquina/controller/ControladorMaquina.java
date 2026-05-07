package com.producao.domain.maquina.controller;

import com.producao.domain.maquina.dto.RespostaMaquina;
import com.producao.domain.maquina.model.StatusMaquina;
import com.producao.domain.maquina.service.ServicoMaquina;
import com.producao.infra.response.RespostaPadrao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/machines")
@Tag(name = "Maquinas", description = "Gerenciamento de maquinas do chao de fabrica")
public class ControladorMaquina {

    private final ServicoMaquina servicoMaquina;

    public ControladorMaquina(ServicoMaquina servicoMaquina) {
        this.servicoMaquina = servicoMaquina;
    }

    @GetMapping
    @Operation(summary = "Lista todas as maquinas")
    public ResponseEntity<RespostaPadrao<List<RespostaMaquina>>> listarTodas(
            @RequestParam(required = false) StatusMaquina status) {
        List<RespostaMaquina> resultado = status != null
                ? servicoMaquina.listarPorStatus(status)
                : servicoMaquina.listarTodas();
        return ResponseEntity.ok(RespostaPadrao.sucesso(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca maquina por ID")
    public ResponseEntity<RespostaPadrao<RespostaMaquina>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoMaquina.buscarPorId(id)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMINISTRADOR')")
    @Operation(summary = "Atualiza o status de uma maquina")
    public ResponseEntity<RespostaPadrao<RespostaMaquina>> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusMaquina novoStatus) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoMaquina.atualizarStatus(id, novoStatus)));
    }
}
