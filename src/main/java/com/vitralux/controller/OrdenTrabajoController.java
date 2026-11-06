package com.vitralux.controller;

import com.vitralux.service.OrdenTrabajoService;
import com.vitralux.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ordenes")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenService;
    private final VentaService ventaService;

    public OrdenTrabajoController(OrdenTrabajoService ordenService,
                                  VentaService ventaService) {
        this.ordenService = ordenService;
        this.ventaService = ventaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ordenes", ordenService.listar());
        return "ordenes/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("ventas", ventaService.listar());
        return "ordenes/formulario";
    }

    @PostMapping("/nueva")
    public String registrar(@RequestParam("ventaId") Long ventaId,
                            RedirectAttributes atributos) {
        try {
            ordenService.crear(ventaId);
            atributos.addFlashAttribute("mensaje", "Orden de trabajo creada.");
            return "redirect:/ordenes";
        } catch (IllegalArgumentException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ordenes/nueva";
        }
    }
}