package com.vitralux.controller;

import com.vitralux.domain.Cotizacion;
import com.vitralux.service.ClienteService;
import com.vitralux.service.CotizacionService;
import com.vitralux.service.ProductoService;
import com.vitralux.service.VentaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final VentaService ventaService;

    public CotizacionController(CotizacionService cotizacionService,
                                ClienteService clienteService,
                                ProductoService productoService,
                                VentaService ventaService) {
        this.cotizacionService = cotizacionService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.ventaService = ventaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cotizaciones", cotizacionService.listar());
        return "cotizaciones/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("productos", productoService.listar());
        return "cotizaciones/formulario";
    }

    @PostMapping("/nueva")
    public String crear(@RequestParam("clienteId") Long clienteId,
                        @RequestParam(name = "productoId", required = false) List<Long> productos,
                        @RequestParam(name = "cantidad", required = false) List<Integer> cantidades,
                        RedirectAttributes atributos) {
        try {
            Cotizacion cotizacion = cotizacionService.crear(clienteId, productos, cantidades);
            return "redirect:/cotizaciones/" + cotizacion.getId();
        } catch (IllegalArgumentException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
            return "redirect:/cotizaciones/nueva";
        }
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable("id") Long id, Model model) {
        Cotizacion cotizacion = cotizacionService.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("cotizacion", cotizacion);
        model.addAttribute("ventaId", ventaService.buscarIdPorCotizacion(id).orElse(null));
        return "cotizaciones/detalle";
    }

    @PostMapping("/{id}/estado")
    public String resolver(@PathVariable("id") Long id,
                           @RequestParam("estado") Cotizacion.Estado estado,
                           RedirectAttributes atributos) {
        try {
            cotizacionService.resolver(id, estado);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cotizaciones/" + id;
    }
}