package com.proyecto.cakecandy_venta_service.infraestructure.client;

import com.proyecto.cakecandy_venta_service.infraestructure.dto.ProductoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "producto-service")
public interface ProductoFeignClient {

    @GetMapping("/api/productos/{id}")
    ProductoDto findById(@PathVariable("id") Integer id);

    @PutMapping("/api/productos/{id}/stock")
    void updateStock(@PathVariable("id") Integer id, @RequestParam("cantidad") Integer cantidad);
}