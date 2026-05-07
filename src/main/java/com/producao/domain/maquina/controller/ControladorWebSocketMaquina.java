package com.producao.domain.maquina.controller;

import com.producao.domain.maquina.dto.EventoStatusMaquina;
import com.producao.domain.maquina.model.StatusMaquina;
import com.producao.domain.maquina.service.ServicoMaquina;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ControladorWebSocketMaquina {

    private final ServicoMaquina servicoMaquina;

    public ControladorWebSocketMaquina(ServicoMaquina servicoMaquina) {
        this.servicoMaquina = servicoMaquina;
    }

    /**
     * Recebe atualizacao de status via WebSocket e propaga para os inscritos.
     * Destino do cliente: /app/maquinas/{id}/status
     * Broadcast em: /topic/maquinas/{id}/status
     */
    @MessageMapping("/maquinas/{id}/status")
    public void atualizarStatusViaStomp(
            @DestinationVariable Long id,
            StatusMaquina novoStatus) {
        servicoMaquina.atualizarStatus(id, novoStatus);
    }
}
