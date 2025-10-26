package com.proyecto.cakecandy_venta_service.infraestructure.client;

import com.proyecto.cakecandy_venta_service.infraestructure.dto.UsuarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuarios-service")
public interface UsuarioFeignClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDto findById(@PathVariable("id") Long id);
}