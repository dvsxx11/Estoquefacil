package com.fluxi.repository;

import com.fluxi.domain.entity.Movimentacao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findAllByEmpresaIdOrderByDataHoraDesc(UUID empresaId);
    List<Movimentacao> findAllByEmpresaIdAndProdutoIdOrderByDataHoraDesc(UUID empresaId, Long produtoId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Movimentacao> findWithLockByIdAndEmpresaId(Long id, UUID empresaId);
    boolean existsByProdutoIdAndEmpresaId(Long produtoId, UUID empresaId);
}
