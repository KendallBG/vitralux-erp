package com.vitralux.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ventas")
public class Venta {

    public enum Estado {
        PENDIENTE, PAGADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cotizacion_id", nullable = false, unique = true)
    private Cotizacion cotizacion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Estado estado;

    protected Venta() {
    }

    public Venta(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
        this.fecha = LocalDate.now();
        this.total = cotizacion.getTotal();
        actualizarEstado(total);
    }

    public void actualizarEstado(BigDecimal saldo) {
        estado = saldo.signum() == 0 ? Estado.PAGADA : Estado.PENDIENTE;
    }

    public Long getId() {
        return id;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public Cliente getCliente() {
        return cotizacion.getCliente();
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Estado getEstado() {
        return estado;
    }
}