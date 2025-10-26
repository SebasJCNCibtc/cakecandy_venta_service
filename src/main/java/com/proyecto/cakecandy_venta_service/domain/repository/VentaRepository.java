package com.proyecto.cakecandy_venta_service.domain.repository;

import com.proyecto.cakecandy_venta_service.domain.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByFechaVentaBetween(LocalDateTime start, LocalDateTime end);
}