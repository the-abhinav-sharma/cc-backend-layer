package com.abhinav.cc_backend_layer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CcBackendLayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcBackendLayerApplication.class, args);
	}
}
