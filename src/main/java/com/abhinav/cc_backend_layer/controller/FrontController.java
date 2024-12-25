package com.abhinav.cc_backend_layer.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.AmountPerMonth;
import com.abhinav.cc_backend_layer.model.Answer;
import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.Question;
import com.abhinav.cc_backend_layer.service.CCMasterService;
import com.abhinav.cc_backend_layer.service.OpenAIService;

@RestController
@CrossOrigin
public class FrontController {

	@Autowired
	CCMasterService ccMasterService;
	
	@Autowired
	private OpenAIService openAIService;
	
	@Autowired
	OpenAiImageModel openAiImageModel;

	@GetMapping("/health")
	public String health() {
		return "Spring Boot is up and running";
	}
	
	@PostMapping(path = "/ask", consumes = "application/json", produces = "application/json")
	public Answer getAnswer(@RequestBody Question question) {
		return openAIService.getAnswer(question);
	}
	
	@PostMapping(path = "/image", produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getImage(@RequestBody Question question) throws IOException {
		 ImageOptions options = ImageOptionsBuilder.builder()
	                .withModel("dall-e-3")
                    .withN(1)
                    .withHeight(1024)
                    .withWidth(1024).withResponseFormat("b64_json")
	                .build();
	        ImagePrompt imagePrompt = new ImagePrompt(question.question(), options);
	        ImageResponse response = openAiImageModel.call(imagePrompt);

	        byte[] decodedBytes = Base64.decodeBase64(response.getResult().getOutput().getB64Json());
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            BufferedImage image = ImageIO.read(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            
            return baos.toByteArray();
	}


	@GetMapping("/get")
	public List<CCMaster> get() {
		return ccMasterService.getAll();
	}

	@GetMapping("/get/{code}/{monthYear}")
	public CCMaster getByCodeAndMonthYear(@PathVariable String code, @PathVariable String monthYear) {
		return ccMasterService.getByPrimaryKey(code, monthYear);
	}

	@GetMapping("/get/{param}")
	public List<CCMaster> getByCodeAndMonthYear(@PathVariable String param) {
		if (param.length() == 6) {
			return ccMasterService.getByMonthYear(param);
		} else {
			return ccMasterService.getByCode(param);
		}
	}
	
	@GetMapping("/monthlyTotal")
	public List<AmountPerMonth> monthlyTotal() {
		return ccMasterService.getAmountPerMonth();
	}

}
