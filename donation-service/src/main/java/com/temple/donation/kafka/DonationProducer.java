package com.temple.donation.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.temple.donation.event.DonationCreatedEvent;

@Service
public class DonationProducer {
	
	private final KafkaTemplate<String, DonationCreatedEvent> kafkaTemplate;

	public DonationProducer(KafkaTemplate<String, DonationCreatedEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void publish(DonationCreatedEvent event) {
		System.out.println("Publishing donation event: " + event);
		kafkaTemplate.send("donation.created", String.valueOf(event.donationId()), event);
	}
	

}
