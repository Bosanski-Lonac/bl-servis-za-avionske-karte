package com.bosanskilonac.szak.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.LetDto;
import utility.BLURL;

@Component
public class RemoveFlightListener {
	
	private ObjectMapper objectMapper;
	private KartaService kartaService;

	public RemoveFlightListener(ObjectMapper objectMapper, KartaService kartaService) {
		this.objectMapper = objectMapper;
		this.kartaService = kartaService;
	}

	@JmsListener(destination = BLURL.AMQUEUE_FIDS, concurrency = "5-10")
	public void handle(Message message) {
		try {
			String jsonText = ((TextMessage)message).getText();
			LetDto letDto = objectMapper.readValue(jsonText, LetDto.class);
			kartaService.deleteByLet(letDto);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
