package com.abhinav.cc_backend_layer.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Value(value = "email.to")
	String to_email;
	
	public void sendTestEmail() {		
		String text = "Backup taken taken at:" + new Date();
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo("theabhinavsharma@hotmail.com");
			msg.setSubject("Test Data Backup Email");
			msg.setText(text);

			javaMailSender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Email Sent Successfully!!");
	}

	public void sendEmail(List<String> list, String vaccine, int districtId) {
		String text = "";
		for (String string : list) {
			text = text.concat(string);
		}
		text = "Snapshot taken at:" + new Date() + "\n\n" + text;
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(to_email);
			msg.setSubject("Data Backup");
			msg.setText(text);

			javaMailSender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Email Sent Successfully!!");
	}

	public void sendMailWithAttachment(String subject, String body, File fileToAttach) {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setTo("theabhinavsharma@hotmail.com");
			helper.setSubject(subject);
			helper.setText(body);
			helper.addAttachment(fileToAttach.getName(), fileToAttach);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		javaMailSender.send(message);
		System.out.println("Backup Email Sent Successfully!!");
	}

}