package com.vitralux.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String mostrarInicio(HttpServletRequest solicitud, Model model) {
        boolean administrador = solicitud.isUserInRole("ADMINISTRADOR");
        boolean vendedor = solicitud.isUserInRole("VENDEDOR");
        boolean produccion = solicitud.isUserInRole("PRODUCCION");

        model.addAttribute("mostrarComercial", administrador || vendedor);
        model.addAttribute("mostrarVentas", administrador || vendedor || produccion);
        model.addAttribute("mostrarOrdenes", administrador || produccion);
        model.addAttribute("mostrarUsuarios", administrador);

        return "index";
    }
}