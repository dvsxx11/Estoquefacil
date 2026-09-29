package com.fluxi.service;

import com.fluxi.domain.entity.Empresa;
import com.fluxi.domain.entity.Movimentacao;
import com.fluxi.domain.entity.Produto;
import com.fluxi.domain.enums.TipoMovimentacao;
import com.fluxi.exception.RegraNegocioException;
import com.fluxi.repository.EmpresaRepository;
import com.fluxi.repository.MovimentacaoRepository;
import com.fluxi.repository.ProdutoRepository;
import com.fluxi.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MovimentacaoServiceTest {
    @Mock private MovimentacaoRepository movimentacoes;
    @Mock private ProdutoRepository produtos;
    @Mock private UsuarioRepository usuarios;
    @Mock private EmpresaRepository empresas;
    private MovimentacaoService service;
    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MovimentacaoService(movimentacoes, produtos, usuarios, empresas);
    }

    private Produto preparar(TipoMovimentacao tipo, int saldo, int quantidade) {
        Produto produto = Produto.builder().id(7L).quantidadeAtual(saldo).build();
        Movimentacao mov = Movimentacao.builder()
                .id(3L).empresa(Empresa.builder().id(tenantId).build())
                .produto(produto).tipo(tipo).quantidade(quantidade).build();
        when(movimentacoes.findWithLockByIdAndEmpresaId(3L, tenantId)).thenReturn(Optional.of(mov));
        when(produtos.findWithLockByIdAndEmpresaId(7L, tenantId)).thenReturn(Optional.of(produto));
        return produto;
    }

    @Test
    void excluirEntradaReduzSaldo() {
        Produto produto = preparar(TipoMovimentacao.ENTRADA, 12, 5);
        service.excluir(3L, tenantId);
        assertEquals(7, produto.getQuantidadeAtual());
        verify(movimentacoes).delete(any(Movimentacao.class));
    }

    @Test
    void excluirSaidaDevolveUnidades() {
        Produto produto = preparar(TipoMovimentacao.SAIDA, 7, 5);
        service.excluir(3L, tenantId);
        assertEquals(12, produto.getQuantidadeAtual());
        verify(movimentacoes).delete(any(Movimentacao.class));
    }

    @Test
    void naoExcluiEntradaSeSaldoFicariaNegativo() {
        Produto produto = preparar(TipoMovimentacao.ENTRADA, 2, 5);
        assertThrows(RegraNegocioException.class, () -> service.excluir(3L, tenantId));
        assertEquals(2, produto.getQuantidadeAtual());
        verify(movimentacoes, never()).delete(any(Movimentacao.class));
    }
}
