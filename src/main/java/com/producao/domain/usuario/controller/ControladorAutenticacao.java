package com.producao.domain.usuario.controller;

import com.producao.domain.usuario.dto.RequisicaoCadastroUsuario;
import com.producao.domain.usuario.dto.RequisicaoLogin;
import com.producao.domain.usuario.service.ServicoUsuario;
import com.producao.infra.response.RespostaPadrao;
import com.producao.security.model.RespostaAutenticacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacao", description = "Login e cadastro de usuarios")
public class ControladorAutenticacao {

    private final ServicoUsuario servicoUsuario;

    public ControladorAutenticacao(ServicoUsuario servicoUsuario) {
        this.servicoUsuario = servicoUsuario;
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica usuario e retorna token JWT")
    public ResponseEntity<RespostaPadrao<RespostaAutenticacao>> login(
            @RequestBody @Valid RequisicaoLogin requisicao) {
        RespostaAutenticacao resposta = servicoUsuario.autenticar(requisicao.login(), requisicao.senha());
        return ResponseEntity.ok(RespostaPadrao.sucesso(resposta));
    }

    @PostMapping("/cadastro")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cadastra novo usuario (apenas ADMINISTRADOR)")
    public ResponseEntity<RespostaPadrao<Void>> cadastrar(
            @RequestBody @Valid RequisicaoCadastroUsuario requisicao) {
        servicoUsuario.cadastrar(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(RespostaPadrao.sucesso(null));
    }
}
