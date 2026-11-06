package com.vitralux.controller;

import com.vitralux.domain.Pago;
import com.vitralux.domain.Venta;
import com.vitralux.service.PagoService;
import com.vitralux.service.VentaService;
import java.math.BigDecimal;
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
@RequestMapping("/ventas/{ventaId}/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final VentaService ventaService;

    public PagoController(PagoService pagoService, VentaService ventaService) {
        this.pagoService = pagoService;
        this.ventaService = ventaService;
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(@PathVariable("ventaId") Long ventaId, Model model) {
        Venta venta = ventaService.buscar(ventaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        BigDecimal saldo = pagoService.calcularSaldo(venta, pagoService.listarPorVenta(ventaId));

        if (saldo.signum() <= 0) {
            return "redirect:/ventas/" + ventaId;
        }

        model.addAttribute("venta", venta);
        model.addAttribute("saldo", saldo);
        return "pagos/formulario";
    }

    @PostMapping("/nuevo")
    public String registrar(@PathVariable("ventaId") Long ventaId,
                            @RequestParam("monto") BigDecimal monto,
                            @RequestParam("metodo") Pago.Metodo metodo,
                            RedirectAttributes atributos) {
        try {
            pagoService.registrar(ventaId, monto, metodo);
            atributos.addFlashAttribute("mensaje", "Abono registrado.");
            return "redirect:/ventas/" + ventaId;
        } catch (IllegalArgumentException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ventas/" + ventaId + "/pagos/nuevo";
        }
    }
}