package com.producao.domain.usuario.controller;

import com.producao.domain.usuario.dto.RequisicaoAtualizacaoUsuario;
import com.producao.domain.usuario.dto.RespostaUsuario;
import com.producao.domain.usuario.service.ServicoUsuario;
import com.producao.infra.response.RespostaPadrao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gerenciamento de usuarios do sistema")
public class ControladorUsuario {

    private final ServicoUsuario servicoUsuario;

    public ControladorUsuario(ServicoUsuario servicoUsuario) {
        this.servicoUsuario = servicoUsuario;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMINISTRADOR')")
    @Operation(summary = "Lista todos os usuarios ativos")
    public ResponseEntity<RespostaPadrao<List<RespostaUsuario>>> listar() {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoUsuario.listarTodos()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualiza dados de um usuario (ADMINISTRADOR)")
    public ResponseEntity<RespostaPadrao<RespostaUsuario>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RequisicaoAtualizacaoUsuario requisicao) {
        return ResponseEntity.ok(RespostaPadrao.sucesso(servicoUsuario.atualizar(id, requisicao)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Desativa um usuario (soft delete, ADMINISTRADOR)")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        servicoUsuario.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
