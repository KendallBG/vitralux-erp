package com.vitralux.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccesoController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }
}