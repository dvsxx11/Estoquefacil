package com.fluxi.dto;

import com.fluxi.domain.enums.TipoMovimentacao;
import java.math.BigDecimal;

public record MovimentacaoRequestDTO(
        Long produtoId,
        TipoMovimentacao tipo,
        Integer quantidade,
        BigDecimal precoUnitario,
        String observacao
) {}
