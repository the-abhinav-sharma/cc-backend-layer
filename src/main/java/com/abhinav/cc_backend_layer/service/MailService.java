package com.abhinav.cc_backend_layer.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	public boolean sendEmail(String body) {		
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom("info@cc-dashboard.com");
			msg.setTo("abhinav.sharma@hotmail.com");
			msg.setSubject("Pending Payments Report");
			msg.setText(body);

			javaMailSender.send(msg);
			log.info("Email Sent Successfully!!");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
	}

	public void sendMailWithAttachment(String subject, String body, File fileToAttach) {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setTo("abhinav.sharma@hotmail.com");
			helper.setSubject(subject);
			helper.setText(body);
			helper.addAttachment(fileToAttach.getName(), fileToAttach);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		javaMailSender.send(message);
		log.info("Backup Email Sent Successfully!!");
	}

}