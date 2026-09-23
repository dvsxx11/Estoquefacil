package com.fluxi.service;

import com.fluxi.domain.entity.Empresa;
import com.fluxi.domain.entity.Movimentacao;
import com.fluxi.domain.entity.Produto;
import com.fluxi.domain.entity.Usuario;
import com.fluxi.domain.enums.TipoMovimentacao;
import com.fluxi.dto.MovimentacaoRequestDTO;
import com.fluxi.dto.MovimentacaoResponseDTO;
import com.fluxi.exception.RegraNegocioException;
import com.fluxi.exception.ResourceNotFoundException;
import com.fluxi.repository.EmpresaRepository;
import com.fluxi.repository.MovimentacaoRepository;
import com.fluxi.repository.ProdutoRepository;
import com.fluxi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               ProdutoRepository produtoRepository,
                               UsuarioRepository usuarioRepository,
                               EmpresaRepository empresaRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public MovimentacaoResponseDTO registrarMovimentacao(MovimentacaoRequestDTO dto, UUID empresaId, Long usuarioId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada."));

        Produto produto = produtoRepository.findByIdAndEmpresaId(dto.produtoId(), empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        Usuario usuario = usuarioRepository.findByIdAndEmpresaId(usuarioId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (dto.tipo() == TipoMovimentacao.SAIDA && produto.getQuantidadeAtual() < dto.quantidade()) {
            throw new RegraNegocioException("Saldo insuficiente em estoque para realizar a saída.");
        }

        int novoSaldo = (dto.tipo() == TipoMovimentacao.ENTRADA)
                ? produto.getQuantidadeAtual() + dto.quantidade()
                : produto.getQuantidadeAtual() - dto.quantidade();

        produto.setQuantidadeAtual(novoSaldo);
        produtoRepository.save(produto);

        BigDecimal valorTotal = dto.precoUnitario().multiply(BigDecimal.valueOf(dto.quantidade()));

        Movimentacao mov = Movimentacao.builder()
                .empresa(empresa)
                .produto(produto)
                .usuario(usuario)
                .tipo(dto.tipo())
                .quantidade(dto.quantidade())
                .precoUnitario(dto.precoUnitario())
                .valorTotal(valorTotal)
                .observacao(dto.observacao())
                .build();

        Movimentacao salva = movimentacaoRepository.save(mov);

        return converterParaResponseDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarHistorico(UUID empresaId) {
        return movimentacaoRepository.findAllByEmpresaIdOrderByDataHoraDesc(empresaId).stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarHistoricoPorProduto(UUID empresaId, Long produtoId) {
        return movimentacaoRepository.findAllByEmpresaIdAndProdutoIdOrderByDataHoraDesc(empresaId, produtoId).stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    private MovimentacaoResponseDTO converterParaResponseDTO(Movimentacao mov) {
        return new MovimentacaoResponseDTO(
                mov.getId(),
                mov.getProduto().getId(),
                mov.getProduto().getNome(),
                mov.getUsuario().getNome(),
                mov.getTipo(),
                mov.getQuantidade(),
                mov.getPrecoUnitario(),
                mov.getValorTotal(),
                mov.getObservacao(),
                mov.getDataHora()
        );
    }
}