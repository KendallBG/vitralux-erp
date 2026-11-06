package com.vitralux.service;

import com.vitralux.domain.Rol;
import com.vitralux.domain.Usuario;
import com.vitralux.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByNombreAsc();
    }

    public boolean hayAdministrador() {
        return usuarioRepository.existsByRol(Rol.ADMINISTRADOR);
    }

    public void registrar(String nombre, String correo, String clave, Rol rol) {
        String nombreLimpio = nombre.strip();
        String correoLimpio = normalizarCorreo(correo);

        if (nombreLimpio.isEmpty() || nombreLimpio.length() > 120) {
            throw new IllegalArgumentException("Ingresá un nombre de hasta 120 caracteres.");
        }

        if (correoLimpio.length() > 150
                || !correoLimpio.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Ingresá un correo válido.");
        }

        if (clave.isBlank() || clave.length() < 8
                || clave.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "Usá una contraseña de al menos 8 caracteres y hasta 72 bytes.");
        }

        if (rol == null) {
            throw new IllegalArgumentException("Seleccioná un rol.");
        }

        Usuario usuario = new Usuario(
                nombreLimpio, correoLimpio, passwordEncoder.encode(clave), rol);

        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException ex) {
            if (usuarioRepository.findByCorreo(correoLimpio).isPresent()) {
                throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
            }
            throw ex;
        }
    }

    @Transactional
    public void cambiarRol(Long id, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.buscarParaActualizar(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (usuario.getRol() == Rol.ADMINISTRADOR || nuevoRol == Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException(
                    "El cambio de rol está habilitado para cuentas operativas.");
        }

        usuario.cambiarRol(nuevoRol);
    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(normalizarCorreo(correo))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getClaveHash())
                .roles(usuario.getRol().name())
                .build();
    }

    private String normalizarCorreo(String correo) {
        return correo.strip().toLowerCase(Locale.ROOT);
    }
}