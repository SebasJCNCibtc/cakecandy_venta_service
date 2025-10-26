package com.proyecto.cakecandy_venta_service.application.dto;

import lombok.Data;

import java.util.List;

// DTO para la petición de crear una venta
@Data
public class VentaRequestDto {
    private List<ItemVentaDto> items;
}