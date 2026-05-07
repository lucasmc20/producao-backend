package com.producao.infra.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespostaPadrao<T>(
        boolean sucesso,
        T dados,
        String erro,
        Instant timestamp
) {

    public static <T> RespostaPadrao<T> sucesso(T dados) {
        return new RespostaPadrao<>(true, dados, null, Instant.now());
    }

    public static <T> RespostaPadrao<T> erro(String mensagem) {
        return new RespostaPadrao<>(false, null, mensagem, Instant.now());
    }
}
