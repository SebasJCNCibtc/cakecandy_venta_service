package com.proyecto.cakecandy_venta_service.application.dto;

import lombok.Data;

import java.math.BigDecimal;

// DTO para el detalle de cada producto en la venta
@Data
public class DetalleVentaConProductoDto {
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}