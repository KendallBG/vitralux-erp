package com.vitralux.controller;

import com.vitralux.domain.Cliente;
import com.vitralux.service.ClienteService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @PostMapping("/nuevo")
    public String registrar(@ModelAttribute("cliente") Cliente cliente) {
        cliente.setId(null);
        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        model.addAttribute("cliente", cliente);
        return "clientes/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable("id") Long id,
                             @ModelAttribute("cliente") Cliente datos) {
        Cliente cliente = clienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        cliente.setNombre(datos.getNombre());
        cliente.setTelefono(datos.getTelefono());
        cliente.setCorreo(datos.getCorreo());
        cliente.setDireccion(datos.getDireccion());

        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id,
                           RedirectAttributes atributos) {
        try {
            clienteService.eliminar(id);
            atributos.addFlashAttribute("mensaje", "Cliente eliminado.");
        } catch (DataIntegrityViolationException ex) {
            atributos.addFlashAttribute("error",
                    "No se puede eliminar el cliente porque tiene registros asociados.");
        }

        return "redirect:/clientes";
    }
}