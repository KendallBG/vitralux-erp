package com.vitralux.service;

import com.vitralux.domain.Pago;
import com.vitralux.domain.Venta;
import com.vitralux.repository.PagoRepository;
import com.vitralux.repository.VentaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;

    public PagoService(PagoRepository pagoRepository, VentaRepository ventaRepository) {
        this.pagoRepository = pagoRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Pago> listarPorVenta(Long ventaId) {
        return pagoRepository.findAllByVenta_IdOrderByIdAsc(ventaId);
    }

    public BigDecimal calcularSaldo(Venta venta, List<Pago> pagos) {
        BigDecimal abonado = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return venta.getTotal().subtract(abonado);
    }

    @Transactional
    public void registrar(Long ventaId, BigDecimal monto, Pago.Metodo metodo) {
        if (monto == null || monto.signum() <= 0 || monto.scale() > 2 || metodo == null) {
            throw new IllegalArgumentException(
                    "Ingresá un monto mayor a cero con hasta dos decimales y un método de pago.");
        }

        Venta venta = ventaRepository.buscarParaActualizar(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));
        BigDecimal saldo = calcularSaldo(venta, listarPorVenta(ventaId));

        if (monto.compareTo(saldo) > 0) {
            throw new IllegalArgumentException("El abono supera el saldo pendiente.");
        }

        pagoRepository.save(new Pago(venta, monto, metodo));
        venta.actualizarEstado(saldo.subtract(monto));
    }
}