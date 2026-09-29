package com.fluxi.controller;

import com.fluxi.dto.MovimentacaoRequestDTO;
import com.fluxi.dto.MovimentacaoResponseDTO;
import com.fluxi.service.MovimentacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponseDTO> registrar(
            @RequestBody MovimentacaoRequestDTO dto,
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestHeader("X-Usuario-ID") Long usuarioId) {

        MovimentacaoResponseDTO resposta = movimentacaoService.registrarMovimentacao(dto, tenantId, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponseDTO>> historico(@RequestHeader("X-Tenant-ID") UUID tenantId) {
        return ResponseEntity.ok(movimentacaoService.listarHistorico(tenantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestHeader("X-Tenant-ID") UUID tenantId) {
        movimentacaoService.excluir(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}
