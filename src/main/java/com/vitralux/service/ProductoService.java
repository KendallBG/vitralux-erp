package com.vitralux.service;

import com.vitralux.domain.Producto;
import com.vitralux.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listar() {
        return productoRepository.findAllByOrderByNombreAsc();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public void registrar(Producto producto) {
        productoRepository.save(producto);
    }

    @Transactional
    public boolean actualizar(Long id, Producto datos) {
        Optional<Producto> encontrado = productoRepository.findById(id);

        if (encontrado.isEmpty()) {
            return false;
        }

        Producto producto = encontrado.get();
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setTipo(datos.getTipo());

        return true;
    }
}