package com.fluxi.repository;

import com.fluxi.domain.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findAllByEmpresaIdOrderByDataHoraDesc(UUID empresaId);
    List<Movimentacao> findAllByEmpresaIdAndProdutoIdOrderByDataHoraDesc(UUID empresaId, Long produtoId);
}