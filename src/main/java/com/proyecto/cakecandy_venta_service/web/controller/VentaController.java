package com.proyecto.cakecandy_venta_service.web.controller;

import com.proyecto.cakecandy_venta_service.application.dto.VentaDetalleDto;
import com.proyecto.cakecandy_venta_service.application.dto.VentaRequestDto;
import com.proyecto.cakecandy_venta_service.application.dto.VentaResponseDto;
import com.proyecto.cakecandy_venta_service.application.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponseDto> createVenta(@RequestBody VentaRequestDto requestDto, @AuthenticationPrincipal Jwt jwt) {
        // Extraemos el ID del usuario del token JWT
        Long idUsuario = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.createVenta(requestDto, idUsuario));
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDto>> getAllVentas() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDetalleDto> getVentaById(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.findVentaConDetalles(id));
    }

    @GetMapping("/reporte/pdf")
    public ResponseEntity<byte[]> exportPdf() {
        try {
            byte[] pdfReport = ventaService.exportVentasToPdf();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_ventas.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(pdfReport, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}/reporte/pdf")
    public ResponseEntity<byte[]> exportDetailPdf(@PathVariable Integer id) {
        try {
            byte[] pdfReport = ventaService.exportVentaDetailToPdf(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "detalle_venta_" + id + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(pdfReport, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/resumen/hoy")
    public ResponseEntity<BigDecimal> getTotalVentasHoy() {
        return ResponseEntity.ok(ventaService.getTotalVentasHoy());
    }
}