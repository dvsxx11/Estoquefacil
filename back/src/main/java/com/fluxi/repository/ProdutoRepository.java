package com.fluxi.repository;

import com.fluxi.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByEmpresaId(UUID empresaId);
    Optional<Produto> findByIdAndEmpresaId(Long id, UUID empresaId);
}
