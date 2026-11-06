package com.vitralux.repository;

import com.vitralux.domain.OrdenTrabajo;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    @EntityGraph(attributePaths = {
        "venta", "venta.cotizacion", "venta.cotizacion.cliente"
    })
    List<OrdenTrabajo> findAllByOrderByIdDesc();
}