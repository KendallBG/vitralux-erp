package com.vitralux.repository;

import com.vitralux.domain.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findAllByVenta_IdOrderByIdAsc(Long ventaId);
}