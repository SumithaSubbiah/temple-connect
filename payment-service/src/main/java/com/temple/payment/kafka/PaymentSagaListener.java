package com.temple.payment.kafka;

import java.util.Random;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentSagaListener {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final Random random = new Random();

	public PaymentSagaListener(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	@KafkaListener(topics = "slot.reserved", groupId = "payment-group")
    public void processPayment(String message) {
        System.out.println("Payment Service received slot.reserved: " + message);

        boolean success = random.nextBoolean();

        if (success) {
            System.out.println("Payment success. Publishing payment.success");
            kafkaTemplate.send("payment.success", message);
        } else {
            System.out.println("Payment failed. Publishing payment.failed");
            kafkaTemplate.send("payment.failed", message);
        }
    }

}
