package com.fluxi.service;

import com.fluxi.domain.entity.Produto;
import com.fluxi.exception.RegraNegocioException;
import com.fluxi.repository.CategoriaRepository;
import com.fluxi.repository.EmpresaRepository;
import com.fluxi.repository.MovimentacaoRepository;
import com.fluxi.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {
    @Test
    void naoExcluiProdutoComHistorico() {
        UUID tenantId = UUID.randomUUID();
        ProdutoRepository produtos = mock(ProdutoRepository.class);
        MovimentacaoRepository movimentacoes = mock(MovimentacaoRepository.class);
        Produto produto = Produto.builder().id(7L).build();
        when(produtos.findWithLockByIdAndEmpresaId(7L, tenantId)).thenReturn(Optional.of(produto));
        when(movimentacoes.existsByProdutoIdAndEmpresaId(7L, tenantId)).thenReturn(true);
        ProdutoService service = new ProdutoService(produtos, mock(EmpresaRepository.class),
                mock(CategoriaRepository.class), movimentacoes);

        assertThrows(RegraNegocioException.class, () -> service.excluir(7L, tenantId));
        verify(produtos, never()).delete(any(Produto.class));
    }
}
