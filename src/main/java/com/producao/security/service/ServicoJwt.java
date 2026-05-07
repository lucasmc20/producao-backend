package com.producao.security.service;

import com.producao.domain.usuario.model.Usuario;
import com.producao.infra.exception.RegraDeNegocioException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class ServicoJwt {

    private final SecretKey chave;
    private final long expiracaoMs;

    public ServicoJwt(
            @Value("${seguranca.jwt.segredo}") String segredo,
            @Value("${seguranca.jwt.expiracao-ms}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMs;
    }

    /**
     * Gera um token JWT para o usuario autenticado.
     */
    public String gerarToken(UserDetails usuario) {
        String perfil = usuario.getAuthorities().iterator().next().getAuthority();
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoMs);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("perfil", perfil)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chave)
                .compact();
    }

    /**
     * Extrai o login (subject) de um token JWT.
     */
    public String extrairLogin(String token) {
        return extrairClaims(token).getSubject();
    }

    /**
     * Verifica se o token e valido para o usuario informado.
     */
    public boolean tokenValido(String token, UserDetails usuario) {
        String login = extrairLogin(token);
        return login.equals(usuario.getUsername()) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        Date expiracao = extrairClaims(token).getExpiration();
        return expiracao.before(new Date());
    }

    private Claims extrairClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw new RegraDeNegocioException("Token expirado");
        } catch (MalformedJwtException | SecurityException ex) {
            throw new RegraDeNegocioException("Token invalido");
        }
    }
}
