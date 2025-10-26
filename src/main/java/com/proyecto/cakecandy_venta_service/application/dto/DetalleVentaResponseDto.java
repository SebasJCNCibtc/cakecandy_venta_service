package com.proyecto.cakecandy_venta_service.application.dto;

import lombok.Data;

import java.math.BigDecimal;

// DTO anidado para los detalles en la respuesta
@Data
public class DetalleVentaResponseDto {
    private Integer idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}