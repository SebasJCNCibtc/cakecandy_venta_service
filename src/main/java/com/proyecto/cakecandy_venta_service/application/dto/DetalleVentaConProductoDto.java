package com.proyecto.cakecandy_venta_service.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaConProductoDto {
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}