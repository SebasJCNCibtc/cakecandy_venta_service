package com.proyecto.cakecandy_venta_service.application.dto;

import lombok.Data;

// DTO para recibir cada item en el carrito
@Data
public class ItemVentaDto {
    private Integer idProducto;
    private Integer cantidad;
}