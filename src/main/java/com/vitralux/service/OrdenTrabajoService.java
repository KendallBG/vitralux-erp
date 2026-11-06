package com.vitralux.service;

import com.vitralux.domain.OrdenTrabajo;
import com.vitralux.domain.Venta;
import com.vitralux.repository.OrdenTrabajoRepository;
import com.vitralux.repository.VentaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenRepository;
    private final VentaRepository ventaRepository;

    public OrdenTrabajoService(OrdenTrabajoRepository ordenRepository,
                               VentaRepository ventaRepository) {
        this.ordenRepository = ordenRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<OrdenTrabajo> listar() {
        return ordenRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public OrdenTrabajo crear(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Seleccioná una venta válida."));

        return ordenRepository.save(new OrdenTrabajo(venta));
    }
}