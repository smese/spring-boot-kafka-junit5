package com.smese.samples.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smese.samples.services.EventProducerService;

@RestController
public class HelloWorldController {
	
	@Autowired
	private EventProducerService eventProducer;
	
	@GetMapping("/hello-world")
	public ResponseEntity helloWorld() {
		eventProducer.sendMessageToKafka("Hello World!");
		return ResponseEntity.ok().build();
	}
	
}
