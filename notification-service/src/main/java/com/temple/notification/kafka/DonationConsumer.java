package com.temple.notification.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DonationConsumer {

    @KafkaListener(topics = "donation.created", groupId = "notification-group")
    public void consume(String message) {
        System.out.println("Donation event received: " + message);
        System.out.println("Mock email sent for donation event");
    }
}