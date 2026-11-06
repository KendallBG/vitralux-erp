package com.vitralux.repository;

import com.vitralux.domain.Cotizacion;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    @EntityGraph(attributePaths = "cliente")
    List<Cotizacion> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"cliente", "detalles"})
    Optional<Cotizacion> findOneById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cotizacion c where c.id = :id")
    Optional<Cotizacion> buscarParaActualizar(@Param("id") Long id);
}