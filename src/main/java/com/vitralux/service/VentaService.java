package com.vitralux.service;

import com.vitralux.domain.Cotizacion;
import com.vitralux.domain.Venta;
import com.vitralux.repository.CotizacionRepository;
import com.vitralux.repository.VentaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final CotizacionRepository cotizacionRepository;

    public VentaService(VentaRepository ventaRepository,
                        CotizacionRepository cotizacionRepository) {
        this.ventaRepository = ventaRepository;
        this.cotizacionRepository = cotizacionRepository;
    }

    public List<Venta> listar() {
        return ventaRepository.findAllByOrderByIdDesc();
    }

    public Optional<Venta> buscar(Long id) {
        return ventaRepository.findOneById(id);
    }

    public Optional<Long> buscarIdPorCotizacion(Long cotizacionId) {
        return ventaRepository.findByCotizacion_Id(cotizacionId).map(Venta::getId);
    }

    @Transactional
    public Venta generar(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.buscarParaActualizar(cotizacionId)
                .orElseThrow(() -> new IllegalArgumentException("Cotización no encontrada."));

        if (cotizacion.getEstado() != Cotizacion.Estado.APROBADA) {
            throw new IllegalStateException("Solo se puede generar una venta de una cotización aprobada.");
        }

        return ventaRepository.findByCotizacion_Id(cotizacionId)
                .orElseGet(() -> ventaRepository.save(new Venta(cotizacion)));
    }
}