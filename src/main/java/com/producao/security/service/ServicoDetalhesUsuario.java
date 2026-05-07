package com.producao.security.service;

import com.producao.domain.usuario.repository.RepositorioUsuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicoDetalhesUsuario implements UserDetailsService {

    private final RepositorioUsuario repositorioUsuario;

    public ServicoDetalhesUsuario(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return repositorioUsuario.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado com login: " + login));
    }
}
