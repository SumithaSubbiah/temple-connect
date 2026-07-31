package com.temple.booking.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.temple.booking.event.BookingCreatedEvent;

@Service
public class BookingProducer {
	
	private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

	public BookingProducer(KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate) {
		super();
		this.kafkaTemplate = kafkaTemplate;
		
	}
	
	public void publishBookingCreatedEvent(BookingCreatedEvent event) {
		kafkaTemplate.send("booking.created", String.valueOf(event.bookingId()), event);
	}
	
	

}
