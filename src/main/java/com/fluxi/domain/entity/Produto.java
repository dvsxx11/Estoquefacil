package com.fluxi.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(length = 50)
    private String sku;

    @Column(nullable = false, length = 100)
    private String nome;

    private String descricao;

    @Column(name = "preco_custo", nullable = false)
    private BigDecimal precoCusto = BigDecimal.ZERO;

    @Column(name = "preco_venda", nullable = false)
    private BigDecimal precoVenda = BigDecimal.ZERO;

    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual = 0;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo = 0;
}