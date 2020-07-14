package com.smese.samples.services.unit;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import com.smese.samples.services.EventProducerService;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, controlledShutdown = false, topics = { "spring-boot-test-topic" }, brokerProperties = {
		"listeners=PLAINTEXT://localhost:3333", "port=3333" })
public class EventProducerServiceTest {

	private static String SENDER_TOPIC = "spring-boot-test-topic";

	@Autowired
	private EventProducerService eventProducerService;

	@Autowired
	private EmbeddedKafkaBroker embeddedKafka;

	private KafkaMessageListenerContainer<String, String> container;

	private BlockingQueue<ConsumerRecord<String, String>> records;

	@BeforeEach
	public void setUp() throws Exception {
		System.setProperty(EmbeddedKafkaBroker.BROKER_LIST_PROPERTY, embeddedKafka.getBrokersAsString());
		Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("sender", "false", embeddedKafka);
		DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<String, String>(
				consumerProperties);
		ContainerProperties containerProperties = new ContainerProperties(SENDER_TOPIC);
		container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

		records = new LinkedBlockingQueue<>();

		container.setupMessageListener(new MessageListener<String, String>() {
			@Override
			public void onMessage(ConsumerRecord<String, String> record) {
				records.add(record);
			}
		});

		container.start();

		ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
	}

	@AfterEach
	public void tearDown() {
		container.stop();
	}

	@Test
	public void testSend() throws InterruptedException {
		eventProducerService.sendMessageToKafka("Test Message");

		var received = records.poll(30, TimeUnit.SECONDS);

		Assertions.assertEquals("Test Message", received.value());
	}

}
