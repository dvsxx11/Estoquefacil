package com.fluxi.service;

import com.fluxi.config.JwtService;
import com.fluxi.domain.entity.Usuario;
import com.fluxi.dto.LoginRequestDTO;
import com.fluxi.dto.LoginResponseDTO;
import com.fluxi.exception.RegraNegocioException;
import com.fluxi.exception.ResourceNotFoundException;
import com.fluxi.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RegraNegocioException("Credenciais inválidas.");
        }

        String token = jwtService.gerarToken(
                usuario.getEmail(),
                usuario.getEmpresa().getId(),
                usuario.getId(),
                usuario.getPerfil().name()
        );

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getEmpresa().getId(),
                usuario.getNome(),
                usuario.getPerfil().name()
        );
    }
}