package com.vitralux.service;

import com.vitralux.domain.Cliente;
import com.vitralux.domain.Cotizacion;
import com.vitralux.domain.Producto;
import com.vitralux.repository.ClienteRepository;
import com.vitralux.repository.CotizacionRepository;
import com.vitralux.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public CotizacionService(CotizacionRepository cotizacionRepository,
                             ClienteRepository clienteRepository,
                             ProductoRepository productoRepository) {
        this.cotizacionRepository = cotizacionRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    public List<Cotizacion> listar() {
        return cotizacionRepository.findAllByOrderByIdDesc();
    }

    public Optional<Cotizacion> buscar(Long id) {
        return cotizacionRepository.findOneById(id);
    }

    @Transactional
    public Cotizacion crear(Long clienteId, List<Long> productos, List<Integer> cantidades) {
        if (productos == null || cantidades == null || productos.isEmpty()
                || productos.size() != cantidades.size() || productos.size() > 50) {
            throw new IllegalArgumentException("Agregá entre 1 y 50 conceptos completos.");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Seleccioná un cliente válido."));
        Cotizacion cotizacion = new Cotizacion(cliente);

        for (int i = 0; i < productos.size(); i++) {
            Long productoId = productos.get(i);
            Integer cantidad = cantidades.get(i);
            if (productoId == null || cantidad == null || cantidad < 1 || cantidad > 9999) {
                throw new IllegalArgumentException(
                        "Seleccioná un producto y una cantidad de 1 a 9999 en cada línea.");
            }

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Uno de los conceptos ya no está disponible en el catálogo."));
            cotizacion.agregarConcepto(producto, cantidad);
        }

        return cotizacionRepository.save(cotizacion);
    }

    @Transactional
    public void resolver(Long id, Cotizacion.Estado estado) {
        Cotizacion cotizacion = cotizacionRepository.buscarParaActualizar(id)
                .orElseThrow(() -> new IllegalArgumentException("Cotización no encontrada."));
        cotizacion.resolver(estado);
    }
}