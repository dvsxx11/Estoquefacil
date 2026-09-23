package com.fluxi.dto;

import java.util.UUID;

public record LoginResponseDTO(
        String token,
        String tipo,
        Long usuarioId,
        UUID tenantId,
        String nome,
        String perfil
) {}