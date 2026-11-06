package com.vitralux.repository;

import com.vitralux.domain.Venta;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @EntityGraph(attributePaths = {"cotizacion", "cotizacion.cliente"})
    List<Venta> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"cotizacion", "cotizacion.cliente"})
    Optional<Venta> findOneById(Long id);

    Optional<Venta> findByCotizacion_Id(Long cotizacionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Venta v where v.id = :id")
    Optional<Venta> buscarParaActualizar(@Param("id") Long id);
}