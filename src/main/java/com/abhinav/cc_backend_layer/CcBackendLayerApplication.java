package com.abhinav.cc_backend_layer;

import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CcBackendLayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcBackendLayerApplication.class, args);
	}

	@Bean
	OpenAiImageModel imageModel(@Value("${spring.ai.openai.api-key}") String apiKey) {
		return new OpenAiImageModel(new OpenAiImageApi(apiKey));
	}

}
