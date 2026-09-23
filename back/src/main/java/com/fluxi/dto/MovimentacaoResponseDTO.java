package com.fluxi.dto;

import com.fluxi.domain.enums.TipoMovimentacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoResponseDTO(
        Long id,
        Long produtoId,
        String nomeProduto,
        String nomeUsuario,
        TipoMovimentacao tipo,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal valorTotal,
        String observacao,
        LocalDateTime dataHora
) {}