package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/public/health")
    public String publicHealth() {
        return "API pública funcionando";
    }

    @GetMapping("/despachador")
    @PreAuthorize("hasRole('DESPACHADOR')")
    public String despachador() {
        return "Acceso de despachador";
    }

    @GetMapping("/conductor")
    @PreAuthorize("hasRole('CONDUCTOR')")
    public String conductor() {
        return "Acceso de conductor";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String admin() {
        return "Acceso de administrador";
    }
}
