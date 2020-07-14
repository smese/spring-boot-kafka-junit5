package com.smese.samples.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducerService {

	private final Logger logger = LogManager.getLogger(EventProducerService.class);

	private static final String TOPIC_NAME = "spring-boot-test-topic";

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessageToKafka(String message) {
		logger.info("Sending message to kafka '{}'", message);
		this.kafkaTemplate.send(TOPIC_NAME, message);
	}
}
