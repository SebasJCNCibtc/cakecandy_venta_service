package com.proyecto.cakecandy_venta_service.domain.repository;

import com.proyecto.cakecandy_venta_service.domain.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
}