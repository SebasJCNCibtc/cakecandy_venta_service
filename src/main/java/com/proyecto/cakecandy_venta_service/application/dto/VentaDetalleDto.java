package com.proyecto.cakecandy_venta_service.application.dto;

import com.proyecto.cakecandy_venta_service.infraestructure.dto.UsuarioDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaDetalleDto {
    private Integer idVenta;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private UsuarioDto cliente;
    private List<DetalleVentaConProductoDto> detalles;
}