package com.abhinav.cc_backend_layer.service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import com.abhinav.cc_backend_layer.model.AIMaster;
import com.abhinav.cc_backend_layer.model.Answer;
import com.abhinav.cc_backend_layer.model.Question;
import com.abhinav.cc_backend_layer.repository.AIMasterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpenAIService {

	private ChatClient chatClient;
	private AIMasterRepository aiMasterRepository;

	public OpenAIService(ChatClient.Builder chatClientBuilder, AIMasterRepository aiMasterRepository) {
		this.chatClient = chatClientBuilder.build();
		this.aiMasterRepository = aiMasterRepository;
	}

	public Answer getAnswer(Question question) {
		ZoneId asiaKolkata = ZoneId.of("Asia/Kolkata");
		ZonedDateTime startTime = ZonedDateTime.ofInstant(Instant.now(), asiaKolkata);
		ChatResponse response = chatClient.prompt().user(question.question()).call().chatResponse();
		ZonedDateTime stopTime = ZonedDateTime.ofInstant(Instant.now(), asiaKolkata);
		long timeTaken = Duration.between(startTime, stopTime).toMillis();
		log.info("For prompt: " + question.question() + ", response took: "
				+ timeTaken
				+ " milliseconds");

		String respText = response.getResult().getOutput().getText();
		
		if(respText.length()>1000) {
			respText = respText.substring(0, 1000);
		}
		
		aiMasterRepository.save(AIMaster.builder().prompt(question.question())
				.answer(respText).timeIn(String.valueOf(startTime))
				.timeOut(String.valueOf(stopTime))
				.respTime(timeTaken)
				.build());

		return new Answer(response.getResult().getOutput().getText());
	}

}
