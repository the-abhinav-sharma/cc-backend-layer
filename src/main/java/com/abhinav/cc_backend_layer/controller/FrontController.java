package com.abhinav.cc_backend_layer.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.AmountPerMonth;
import com.abhinav.cc_backend_layer.model.Answer;
import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.Question;
import com.abhinav.cc_backend_layer.model.UserSession;
import com.abhinav.cc_backend_layer.model.Users;
import com.abhinav.cc_backend_layer.service.CCMasterService;
import com.abhinav.cc_backend_layer.service.CSVService;
import com.abhinav.cc_backend_layer.service.LoginService;
import com.abhinav.cc_backend_layer.service.MailService;
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
	
	@Autowired
	MailService mailService;
	
	@Autowired
	CSVService csvService;
	
	@Autowired
	LoginService loginService;

	@GetMapping("/health")
	public String health() {
		return "CC Backend Spring Boot is up and running";
	}
	
	@PostMapping(path = "/ask", consumes = "application/json", produces = "application/json")
	public Answer getAnswer(@RequestBody Question question) {
		return openAIService.getAnswer(question);
	}
	
	@PostMapping(path = "/image", produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getImage(@RequestBody Question question) throws IOException {
		 ImageOptions options = ImageOptionsBuilder.builder()
	                .model("dall-e-3")
                    .N(1)
                    .height(1024)
                    .width(1024).responseFormat("b64_json")
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
	public List<CCMaster> get(@RequestHeader("Authorization") String authHeader) {
		if(!checkAuthToken(authHeader)) {
        	return null;
        }
		return ccMasterService.getAll();
	}
	
	//@GetMapping("/getPrompts")
	public void getPrompts() {
		openAIService.getPromptsByDate();
	}
	
	@PostMapping("/create")
	public CCMaster create(@RequestBody CCMaster ccMaster, @RequestHeader("Authorization") String authHeader) {
		if(!checkAuthToken(authHeader)) {
        	return null;
        }
		return ccMasterService.create(ccMaster);
	}

	@GetMapping("/get/{code}/{monthYear}")
	public CCMaster getByCodeAndMonthYear(@PathVariable String code, @PathVariable String monthYear, @RequestHeader("Authorization") String authHeader) {
		if(!checkAuthToken(authHeader)) {
        	return null;
        }
		return ccMasterService.getByPrimaryKey(code, monthYear);
	}

	@GetMapping("/get/{param}")
	public List<CCMaster> getByCodeAndMonthYear(@PathVariable String param, @RequestHeader("Authorization") String authHeader) {
		if(!checkAuthToken(authHeader)) {
        	return null;
        }
		
		if (param.length() == 6) {
			return ccMasterService.getByMonthYear(param);
		} else {
			return ccMasterService.getByCode(param);
		}
	}
	
	@GetMapping("/monthlyTotal/{year}")
	public List<AmountPerMonth> monthlyTotal(@PathVariable String year, @RequestHeader("Authorization") String authHeader) {
        if(!checkAuthToken(authHeader)) {
        	return null;
        }
		return ccMasterService.getAmountPerMonth(year);
	}
	
	@GetMapping("/cardlyTotal/{year}")
	public List<AmountPerMonth> cardlyTotal(@PathVariable String year, @RequestHeader("Authorization") String authHeader) {
		if(!checkAuthToken(authHeader)) {
        	return null;
        }
		return ccMasterService.getAmountPerCard(year);
	}
	
	@GetMapping("/cardNames")
	public Map<String, String> loadCardNames(@RequestHeader("Authorization") String authHeader) {
		if(!checkAuthToken(authHeader)) {
        	return null;
        }
		return ccMasterService.codeNames;
	}
	
	//@GetMapping("/pending")
	public String getPendingPayments() {
		return ccMasterService.getPendingPayments();
	}
	
	@PostMapping("/login")
	public UserSession login(@RequestBody Users users) {
		return loginService.login(users);
	}
	
	@PostMapping("/logout")
	public boolean logout(@RequestBody Users users) {
		loginService.logout(users.getUsername());
		return true;
	}
	
	private boolean checkAuthToken(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return false;
		}
		return loginService.isValidToken(authHeader.substring(7));
	}
}
