package com.producao.domain.usuario.service;

import com.producao.domain.usuario.dto.RequisicaoAtualizacaoUsuario;
import com.producao.domain.usuario.dto.RequisicaoCadastroUsuario;
import com.producao.domain.usuario.dto.RespostaUsuario;
import com.producao.domain.usuario.model.Usuario;
import com.producao.domain.usuario.repository.RepositorioUsuario;
import com.producao.infra.exception.RegraDeNegocioException;
import com.producao.security.model.RespostaAutenticacao;
import com.producao.security.service.ServicoJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoUsuario {

    private static final Logger log = LoggerFactory.getLogger(ServicoUsuario.class);

    private final RepositorioUsuario repositorioUsuario;
    private final AuthenticationManager authenticationManager;
    private final ServicoJwt servicoJwt;
    private final PasswordEncoder passwordEncoder;
    private final long expiracaoMs;

    public ServicoUsuario(
            RepositorioUsuario repositorioUsuario,
            AuthenticationManager authenticationManager,
            ServicoJwt servicoJwt,
            PasswordEncoder passwordEncoder,
            @Value("${seguranca.jwt.expiracao-ms}") long expiracaoMs) {
        this.repositorioUsuario = repositorioUsuario;
        this.authenticationManager = authenticationManager;
        this.servicoJwt = servicoJwt;
        this.passwordEncoder = passwordEncoder;
        this.expiracaoMs = expiracaoMs;
    }

    /**
     * Autentica o usuario e retorna um token JWT.
     *
     * @param login login do usuario
     * @param senha senha em texto plano
     * @return resposta com token e dados do usuario autenticado
     */
    @Transactional(readOnly = true)
    public RespostaAutenticacao autenticar(String login, String senha) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, senha)
        );

        Usuario usuario = (Usuario) autenticacao.getPrincipal();
        String token = servicoJwt.gerarToken(usuario);

        log.info("Usuario autenticado: login={}, perfil={}", usuario.getLogin(), usuario.getPerfilAcesso());

        return new RespostaAutenticacao(
                usuario.getId(),
                token,
                usuario.getLogin(),
                usuario.getNome(),
                usuario.getPerfilAcesso(),
                expiracaoMs
        );
    }

    /**
     * Cadastra um novo usuario no sistema.
     *
     * @param requisicao dados do novo usuario
     * @throws RegraDeNegocioException se o login ja estiver em uso
     */
    @Transactional
    public void cadastrar(RequisicaoCadastroUsuario requisicao) {
        if (repositorioUsuario.existsByLogin(requisicao.login())) {
            throw new RegraDeNegocioException("Login ja esta em uso: " + requisicao.login());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(requisicao.nome());
        usuario.setLogin(requisicao.login());
        usuario.setSenha(passwordEncoder.encode(requisicao.senha()));
        usuario.setPerfilAcesso(requisicao.perfilAcesso());
        usuario.setAtivo(true);

        repositorioUsuario.save(usuario);
        log.info("Novo usuario cadastrado: login={}, perfil={}", usuario.getLogin(), usuario.getPerfilAcesso());
    }

    /** Lista todos os usuarios ativos do sistema */
    @Transactional(readOnly = true)
    public List<RespostaUsuario> listarTodos() {
        return repositorioUsuario.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
                .map(RespostaUsuario::de)
                .collect(Collectors.toList());
    }

    /** Atualiza dados de um usuario (nome, perfil e opcionalmente senha). */
    @Transactional
    public RespostaUsuario atualizar(Long id, RequisicaoAtualizacaoUsuario req) {
        Usuario usuario = repositorioUsuario.findById(id)
                .orElseThrow(() -> new com.producao.infra.exception.RecursoNaoEncontradoException(
                        "Usuario nao encontrado: " + id));
        usuario.setNome(req.nome());
        usuario.setPerfilAcesso(req.perfilAcesso());
        if (req.novaSenha() != null && !req.novaSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(req.novaSenha()));
        }
        repositorioUsuario.save(usuario);
        log.info("Usuario atualizado: id={}, login={}", usuario.getId(), usuario.getLogin());
        return RespostaUsuario.de(usuario);
    }

    /** Desativa um usuario (soft delete). */
    @Transactional
    public void desativar(Long id) {
        Usuario usuario = repositorioUsuario.findById(id)
                .orElseThrow(() -> new com.producao.infra.exception.RecursoNaoEncontradoException(
                        "Usuario nao encontrado: " + id));
        usuario.setAtivo(false);
        repositorioUsuario.save(usuario);
        log.info("Usuario desativado: id={}, login={}", usuario.getId(), usuario.getLogin());
    }
}
