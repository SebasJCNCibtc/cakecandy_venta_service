package com.proyecto.cakecandy_venta_service.infraestructure.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoDto {
    private Integer idProducto;
    private String nombreProducto;
    private BigDecimal precioVenta;
    private Integer stock;
}