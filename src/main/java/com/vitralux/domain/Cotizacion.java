package com.vitralux.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cotizaciones")
public class Cotizacion {

    public enum Estado {
        PENDIENTE, APROBADA, RECHAZADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Estado estado;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.PERSIST)
    @OrderBy("id ASC")
    private List<DetalleCotizacion> detalles = new ArrayList<>();

    protected Cotizacion() {
    }

    public Cotizacion(Cliente cliente) {
        this.cliente = cliente;
        this.fecha = LocalDate.now();
        this.estado = Estado.PENDIENTE;
        this.total = BigDecimal.ZERO;
    }

    public void agregarConcepto(Producto producto, int cantidad) {
        DetalleCotizacion detalle = new DetalleCotizacion(this, producto, cantidad);
        detalles.add(detalle);
        total = total.add(detalle.getSubtotal());
    }

    public void resolver(Estado nuevoEstado) {
        if (estado != Estado.PENDIENTE) {
            throw new IllegalStateException("La cotización ya fue resuelta.");
        }
        if (nuevoEstado != Estado.APROBADA && nuevoEstado != Estado.RECHAZADA) {
            throw new IllegalArgumentException("Seleccioná Aprobar o Rechazar.");
        }
        estado = nuevoEstado;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<DetalleCotizacion> getDetalles() {
        return detalles;
    }
}