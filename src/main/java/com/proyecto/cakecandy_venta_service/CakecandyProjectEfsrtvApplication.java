package com.proyecto.cakecandy_venta_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CakecandyProjectEfsrtvApplication {

	public static void main(String[] args) {
		SpringApplication.run(CakecandyProjectEfsrtvApplication.class, args);
	}

}
