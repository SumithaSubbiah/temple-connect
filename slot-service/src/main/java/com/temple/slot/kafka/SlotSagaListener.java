package com.temple.slot.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SlotSagaListener {
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	public SlotSagaListener(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	@KafkaListener(topics="booking.created", groupId = "slot-group") 
	public void reserveSlot(String message) {
		System.out.println("Slot service received booking.created: " +message);
		
		 // For now we mock slot reservation success
        String slotReservedEvent = message;

        System.out.println("Slot reserved. Publishing slot.reserved: " + slotReservedEvent);

        kafkaTemplate.send("slot.reserved", slotReservedEvent);
		
	}
	
    @KafkaListener(topics = "slot.release", groupId = "slot-group")
    public void releaseSlot(String message) {
        System.out.println("Slot release requested: " + message);
        System.out.println("Slot released successfully for event: " + message);
    }
	

}
