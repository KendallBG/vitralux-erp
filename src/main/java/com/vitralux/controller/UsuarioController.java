package com.vitralux.controller;

import com.vitralux.domain.Rol;
import com.vitralux.service.UsuarioService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final List<Rol> ROLES_OPERATIVOS = List.of(
            Rol.VENDEDOR, Rol.COMPRAS, Rol.PRODUCCION, Rol.INSTALADOR);

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("roles", ROLES_OPERATIVOS);
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("roles", Rol.values());
        return "usuarios/formulario";
    }

    @PostMapping("/nuevo")
    public String registrar(@RequestParam("nombre") String nombre,
                            @RequestParam("correo") String correo,
                            @RequestParam("clave") String clave,
                            @RequestParam("rol") Rol rol,
                            Model model,
                            RedirectAttributes atributos) {
        try {
            usuarioService.registrar(nombre, correo, clave, rol);
            atributos.addFlashAttribute("mensaje", "Usuario registrado.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("nombre", nombre);
            model.addAttribute("correo", correo);
            model.addAttribute("rolSeleccionado", rol);
            model.addAttribute("error", ex.getMessage());
            return "usuarios/formulario";
        }
    }

    @PostMapping("/{id}/rol")
    public String cambiarRol(@PathVariable("id") Long id,
                             @RequestParam("rol") Rol rol,
                             RedirectAttributes atributos) {
        try {
            usuarioService.cambiarRol(id, rol);
            atributos.addFlashAttribute("mensaje",
                    "Rol actualizado. Se aplicará al volver a iniciar sesión.");
        } catch (IllegalArgumentException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }
}