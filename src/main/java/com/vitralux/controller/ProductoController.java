package com.vitralux.controller;

import com.vitralux.domain.Producto;
import com.vitralux.service.ProductoService;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private static final BigDecimal PRECIO_MAXIMO =
            new BigDecimal("9999999999.99");

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/formulario";
    }

    @PostMapping("/nuevo")
    public String registrar(@ModelAttribute("producto") Producto producto,
                            BindingResult resultado,
                            RedirectAttributes atributos) {
        validar(producto, resultado);

        if (resultado.hasErrors()) {
            return "productos/formulario";
        }

        productoService.registrar(producto);
        atributos.addFlashAttribute("mensaje", "Registro agregado al catálogo.");
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id,
                                            Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Registro del catálogo no encontrado"));

        model.addAttribute("producto", producto);
        model.addAttribute("productoId", id);
        return "productos/formulario";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id,
                         @ModelAttribute("producto") Producto producto,
                         BindingResult resultado,
                         Model model,
                         RedirectAttributes atributos) {
        validar(producto, resultado);

        if (resultado.hasErrors()) {
            model.addAttribute("productoId", id);
            return "productos/formulario";
        }

        if (!productoService.actualizar(id, producto)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Registro del catálogo no encontrado");
        }

        atributos.addFlashAttribute("mensaje", "Registro del catálogo actualizado.");
        return "redirect:/productos";
    }

    private void validar(Producto producto, BindingResult resultado) {
        String nombre = producto.getNombre();
        String descripcion = producto.getDescripcion();
        BigDecimal precio = producto.getPrecio();

        if (nombre == null || nombre.isBlank() || nombre.length() > 120) {
            resultado.rejectValue("nombre", "nombre.invalido",
                    "Ingresá un nombre de hasta 120 caracteres.");
        }

        if (descripcion == null || descripcion.isBlank()
                || descripcion.length() > 255) {
            resultado.rejectValue("descripcion", "descripcion.invalida",
                    "Ingresá una descripción de hasta 255 caracteres.");
        }

        if (!resultado.hasFieldErrors("precio")
                && (precio == null || precio.signum() < 0
                || precio.scale() > 2 || precio.compareTo(PRECIO_MAXIMO) > 0)) {
            resultado.rejectValue("precio", "precio.invalido",
                    "Ingresá un precio válido con hasta dos decimales.");
        }

        if (!"PRODUCTO".equals(producto.getTipo())
                && !"SERVICIO".equals(producto.getTipo())) {
            resultado.rejectValue("tipo", "tipo.invalido",
                    "Seleccioná Producto o Servicio.");
        }
    }
}