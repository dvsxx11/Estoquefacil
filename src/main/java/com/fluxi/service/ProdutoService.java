package com.fluxi.service;

import com.fluxi.domain.entity.Categoria;
import com.fluxi.domain.entity.Empresa;
import com.fluxi.domain.entity.Produto;
import com.fluxi.dto.ProdutoDTO;
import com.fluxi.exception.ResourceNotFoundException;
import com.fluxi.repository.CategoriaRepository;
import com.fluxi.repository.EmpresaRepository;
import com.fluxi.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          EmpresaRepository empresaRepository,
                          CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.empresaRepository = empresaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> listarTodos(UUID empresaId) {
        return produtoRepository.findAllByEmpresaId(empresaId).stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoDTO buscarPorId(Long id, UUID empresaId) {
        Produto produto = produtoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
        return converterParaDTO(produto);
    }

    @Transactional
    public ProdutoDTO criar(ProdutoDTO dto, UUID empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada."));

        Categoria categoria = null;
        if (dto.categoriaId() != null) {
            categoria = categoriaRepository.findByIdAndEmpresaId(dto.categoriaId(), empresaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        }

        Produto produto = Produto.builder()
                .empresa(empresa)
                .categoria(categoria)
                .sku(dto.sku())
                .nome(dto.nome())
                .descricao(dto.descricao())
                .precoCusto(dto.precoCusto())
                .precoVenda(dto.precoVenda())
                .quantidadeAtual(dto.quantidadeAtual() != null ? dto.quantidadeAtual() : 0)
                .estoqueMinimo(dto.estoqueMinimo() != null ? dto.estoqueMinimo() : 0)
                .build();

        Produto salvo = produtoRepository.save(produto);
        return converterParaDTO(salvo);
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO dto, UUID empresaId) {
        Produto produto = produtoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (dto.categoriaId() != null) {
            Categoria categoria = categoriaRepository.findByIdAndEmpresaId(dto.categoriaId(), empresaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
            produto.setCategoria(categoria);
        } else {
            produto.setCategoria(null);
        }

        produto.setSku(dto.sku());
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPrecoCusto(dto.precoCusto());
        produto.setPrecoVenda(dto.precoVenda());
        produto.setEstoqueMinimo(dto.estoqueMinimo());

        Produto atualizado = produtoRepository.save(produto);
        return converterParaDTO(atualizado);
    }

    private ProdutoDTO converterParaDTO(Produto produto) {
        return new ProdutoDTO(
                produto.getId(),
                produto.getCategoria() != null ? produto.getCategoria().getId() : null,
                produto.getSku(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPrecoCusto(),
                produto.getPrecoVenda(),
                produto.getQuantidadeAtual(),
                produto.getEstoqueMinimo()
        );
    }
}