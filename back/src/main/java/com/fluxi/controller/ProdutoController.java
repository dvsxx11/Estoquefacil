package com.fluxi.controller;

import com.fluxi.dto.ProdutoDTO;
import com.fluxi.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listar(@RequestHeader("X-Tenant-ID") UUID tenantId) {
        return ResponseEntity.ok(produtoService.listarTodos(tenantId));
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@RequestBody ProdutoDTO dto,
                                            @RequestHeader("X-Tenant-ID") UUID tenantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(dto, tenantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestHeader("X-Tenant-ID") UUID tenantId) {
        produtoService.excluir(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}
