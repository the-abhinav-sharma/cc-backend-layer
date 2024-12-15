package com.abhinav.cc_backend_layer.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import com.abhinav.cc_backend_layer.model.Answer;
import com.abhinav.cc_backend_layer.model.Question;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpenAIService {

	private ChatClient chatClient;

	public OpenAIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

	public Answer getAnswer(Question question) {
		Instant before = Instant.now();
		log.info("*************************************************");
		log.info("API invoked at "+new Date());
		ChatResponse response = chatClient.prompt().user(question.question()).call().chatResponse();
		Instant after = Instant.now();
		log.info("Response received at "+new Date());
		log.info("Time taken: "+Duration.between(before, after).toSeconds()+" seconds");
		log.info("*************************************************");
		return new Answer(response.getResult().getOutput().getContent());
	}

}
