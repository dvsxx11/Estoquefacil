package com.fluxi.service;

import com.fluxi.domain.entity.Categoria;
import com.fluxi.domain.entity.Empresa;
import com.fluxi.dto.CategoriaDTO;
import com.fluxi.exception.ResourceNotFoundException;
import com.fluxi.repository.CategoriaRepository;
import com.fluxi.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, EmpresaRepository empresaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas(UUID empresaId) {
        return categoriaRepository.findAllByEmpresaId(empresaId).stream()
                .map(c -> new CategoriaDTO(c.getId(), c.getNome()))
                .toList();
    }

    @Transactional
    public CategoriaDTO criar(CategoriaDTO dto, UUID empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada."));

        Categoria categoria = Categoria.builder()
                .empresa(empresa)
                .nome(dto.nome())
                .build();

        Categoria salva = categoriaRepository.save(categoria);
        return new CategoriaDTO(salva.getId(), salva.getNome());
    }
}