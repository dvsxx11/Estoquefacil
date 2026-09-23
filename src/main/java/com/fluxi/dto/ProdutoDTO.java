package com.fluxi.dto;

import java.math.BigDecimal;

public record ProdutoDTO(
        Long id,
        Long categoriaId,
        String sku,
        String nome,
        String descricao,
        BigDecimal precoCusto,
        BigDecimal precoVenda,
        Integer quantidadeAtual,
        Integer estoqueMinimo
) {}
