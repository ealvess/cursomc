package com.evertonalves.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.evertonalves.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
}
