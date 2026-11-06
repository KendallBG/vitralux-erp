package com.vitralux.controller;

import com.vitralux.domain.Pago;
import com.vitralux.domain.Venta;
import com.vitralux.service.PagoService;
import com.vitralux.service.VentaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final PagoService pagoService;

    public VentaController(VentaService ventaService, PagoService pagoService) {
        this.ventaService = ventaService;
        this.pagoService = pagoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listar());
        return "ventas/lista";
    }

    @PostMapping("/desde-cotizacion/{id}")
    public String generar(@PathVariable("id") Long id, RedirectAttributes atributos) {
        try {
            Venta venta = ventaService.generar(id);
            return "redirect:/ventas/" + venta.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
            return "redirect:/cotizaciones/" + id;
        }
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable("id") Long id, Model model) {
        Venta venta = ventaService.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<Pago> pagos = pagoService.listarPorVenta(id);
        model.addAttribute("venta", venta);
        model.addAttribute("pagos", pagos);
        model.addAttribute("saldo", pagoService.calcularSaldo(venta, pagos));
        return "ventas/detalle";
    }
}