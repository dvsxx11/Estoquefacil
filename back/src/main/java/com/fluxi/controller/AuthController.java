package com.fluxi.controller;

import com.fluxi.config.JwtService;
import com.fluxi.domain.entity.Usuario;
import com.fluxi.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String senha = body.get("senha");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            return ResponseEntity.badRequest().body("Credenciais inválidas.");
        }

        String token = jwtService.gerarToken(
                usuario.getEmail(),
                usuario.getEmpresa().getId(),
                usuario.getId(),
                usuario.getPerfil().name()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "tipo", "Bearer",
                "usuarioId", usuario.getId(),
                "tenantId", usuario.getEmpresa().getId(),
                "nome", usuario.getNome(),
                "perfil", usuario.getPerfil().name()
        ));
    }
}