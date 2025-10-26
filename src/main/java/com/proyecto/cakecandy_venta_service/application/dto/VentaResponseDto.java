package com.proyecto.cakecandy_venta_service.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// DTO para la respuesta de una venta creada
@Data
public class VentaResponseDto {
    private Integer idVenta;
    private Long idUsuario;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private List<DetalleVentaResponseDto> detalles;
}