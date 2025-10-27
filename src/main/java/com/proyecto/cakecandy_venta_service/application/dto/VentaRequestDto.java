package com.proyecto.cakecandy_venta_service.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class VentaRequestDto {
    private List<ItemVentaDto> items;
}