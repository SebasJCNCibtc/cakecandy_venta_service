package com.proyecto.cakecandy_venta_service.application.service;

import com.proyecto.cakecandy_venta_service.application.dto.VentaDetalleDto;
import com.proyecto.cakecandy_venta_service.application.dto.VentaRequestDto;
import com.proyecto.cakecandy_venta_service.application.dto.VentaResponseDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface VentaService {
    VentaResponseDto createVenta(VentaRequestDto requestDto, Long idUsuario);
    List<VentaResponseDto> findAll();
    VentaDetalleDto findVentaConDetalles(Integer idVenta);
    byte[] exportVentasToPdf() throws IOException;
    byte[] exportVentaDetailToPdf(Integer idVenta) throws IOException;
    BigDecimal getTotalVentasHoy();
}