package com.smese.samples.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {

	private final Logger logger = LogManager.getLogger(EventConsumerService.class);

	@KafkaListener(topics = {"spring-boot-test-topic"}, groupId = "#{T(java.util.UUID).randomUUID().toString()}")
	public void consumeContactSupportResponse(String incomingMessage) {
		logger.info("Message received from event handler '{}'", incomingMessage);
	}
}