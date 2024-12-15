package com.abhinav.cc_backend_layer.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
		Date startTime = new Date();
		ChatResponse response = chatClient.prompt().user(question.question()).call().chatResponse();
		Date stopTime = new Date();
		log.info("For prompt: " + question.question() + ", response took: "
				+ TimeUnit.MILLISECONDS.convert(stopTime.getTime() - startTime.getTime(), TimeUnit.MILLISECONDS)
				+ " milliseconds");

		aiMasterRepository.save(AIMaster.builder().prompt(question.question())
				.answer(response.getResult().getOutput().getContent()).timeIn(String.valueOf(startTime))
				.timeOut(String.valueOf(stopTime))
				.respTime(
						TimeUnit.MILLISECONDS.convert(stopTime.getTime() - startTime.getTime(), TimeUnit.MILLISECONDS))
				.build());

		return new Answer(response.getResult().getOutput().getContent());
	}

}
