package com.proyecto.cakecandy_venta_service.application.service.impl;

import com.proyecto.cakecandy_venta_service.application.dto.*;
import com.proyecto.cakecandy_venta_service.application.service.VentaService;
import com.proyecto.cakecandy_venta_service.domain.model.DetalleVenta;
import com.proyecto.cakecandy_venta_service.domain.model.Venta;
import com.proyecto.cakecandy_venta_service.domain.repository.VentaRepository;
import com.proyecto.cakecandy_venta_service.infraestructure.client.ProductoFeignClient;
import com.proyecto.cakecandy_venta_service.infraestructure.client.UsuarioFeignClient;
import com.proyecto.cakecandy_venta_service.infraestructure.dto.ProductoDto;
import com.proyecto.cakecandy_venta_service.infraestructure.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoFeignClient productoFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;
    private final PdfReportServiceImpl pdfReportService;

    @Override
    @Transactional
    public VentaResponseDto createVenta(VentaRequestDto requestDto, Long idUsuario) {
        BigDecimal totalVenta = BigDecimal.ZERO;
        List<DetalleVenta> detallesParaGuardar = new ArrayList<>();

        for (ItemVentaDto item : requestDto.getItems()) {
            ProductoDto producto = productoFeignClient.findById(item.getIdProducto());
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombreProducto());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(item.getIdProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());
            BigDecimal subtotal = producto.getPrecioVenta().multiply(new BigDecimal(item.getCantidad()));
            detalle.setSubtotal(subtotal);

            detallesParaGuardar.add(detalle);
            totalVenta = totalVenta.add(subtotal);
        }

        Venta nuevaVenta = new Venta();
        nuevaVenta.setIdUsuario(idUsuario);
        nuevaVenta.setTotal(totalVenta);

        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        for (DetalleVenta detalle : detallesParaGuardar) {
            detalle.setVenta(ventaGuardada);
        }
        ventaGuardada.setDetalles(detallesParaGuardar);

        for (ItemVentaDto item : requestDto.getItems()) {
            productoFeignClient.updateStock(item.getIdProducto(), item.getCantidad());
        }

        return buildResponseDto(ventaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDto> findAll() {
        return ventaRepository.findAll().stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDetalleDto findVentaConDetalles(Integer idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + idVenta));

        UsuarioDto clienteDto = usuarioFeignClient.findById(venta.getIdUsuario());

        List<DetalleVentaConProductoDto> detallesEnriquecidos = venta.getDetalles().stream().map(detalle -> {
            ProductoDto productoDto = productoFeignClient.findById(detalle.getIdProducto());

            DetalleVentaConProductoDto detalleDto = new DetalleVentaConProductoDto();
            detalleDto.setNombreProducto(productoDto.getNombreProducto());
            detalleDto.setCantidad(detalle.getCantidad());
            detalleDto.setPrecioUnitario(detalle.getPrecioUnitario());
            detalleDto.setSubtotal(detalle.getSubtotal());
            return detalleDto;
        }).collect(Collectors.toList());

        VentaDetalleDto ventaDetalleDto = new VentaDetalleDto();
        ventaDetalleDto.setIdVenta(venta.getIdVenta());
        ventaDetalleDto.setFechaVenta(venta.getFechaVenta());
        ventaDetalleDto.setTotal(venta.getTotal());
        ventaDetalleDto.setCliente(clienteDto);
        ventaDetalleDto.setDetalles(detallesEnriquecidos);

        return ventaDetalleDto;
    }

    @Override
    public byte[] exportVentasToPdf() throws IOException {
        List<VentaResponseDto> ventas = this.findAll();
        return pdfReportService.generateVentasReport(ventas);
    }

    @Override
    public byte[] exportVentaDetailToPdf(Integer idVenta) throws IOException {
        VentaDetalleDto ventaDetalle = this.findVentaConDetalles(idVenta);
        return pdfReportService.generateVentaDetailReport(ventaDetalle);
    }

    public BigDecimal getTotalVentasHoy() {
        // Define el inicio y el fin del día actual
        LocalDateTime inicioDelDia = java.time.LocalDate.now().atStartOfDay();
        LocalDateTime finDelDia = java.time.LocalDate.now().atTime(23, 59, 59);

        // Busca las ventas en el repositorio dentro de ese rango de tiempo
        List<Venta> ventasDeHoy = ventaRepository.findByFechaVentaBetween(inicioDelDia, finDelDia);

        // Suma los totales de todas las ventas encontradas
        return ventasDeHoy.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private VentaResponseDto buildResponseDto(Venta venta) {
        List<DetalleVentaResponseDto> detallesDto = venta.getDetalles().stream().map(detalle -> {
            DetalleVentaResponseDto dto = new DetalleVentaResponseDto();
            dto.setIdProducto(detalle.getIdProducto());
            dto.setCantidad(detalle.getCantidad());
            dto.setPrecioUnitario(detalle.getPrecioUnitario());
            dto.setSubtotal(detalle.getSubtotal());
            return dto;
        }).collect(Collectors.toList());

        VentaResponseDto response = new VentaResponseDto();
        response.setIdVenta(venta.getIdVenta());
        response.setIdUsuario(venta.getIdUsuario());
        response.setFechaVenta(venta.getFechaVenta());
        response.setTotal(venta.getTotal());
        response.setDetalles(detallesDto);
        return response;
    }

}