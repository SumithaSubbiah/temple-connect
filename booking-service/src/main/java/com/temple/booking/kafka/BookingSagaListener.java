package com.temple.booking.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temple.booking.entity.Booking;
import com.temple.booking.repo.BookingRepository;

@Service
public class BookingSagaListener {

	private final BookingRepository bookingRepo;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public BookingSagaListener(BookingRepository bookingRepo, KafkaTemplate<String, String> kafkaTemplate) {
		super();
		this.bookingRepo = bookingRepo;
		this.kafkaTemplate = kafkaTemplate;
	}

	@KafkaListener(topics = "payment.success", groupId = "booking-group")
	public void paymentSuccess(String message) throws JsonMappingException, JsonProcessingException {
		JsonNode json = objectMapper.readTree(message);
		Long bookingId = json.get("bookingId").asLong();

		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
		booking.setStatus("CONFIRMED");
		bookingRepo.save(booking);

		System.out.println("Booking " + bookingId + " confirmed");

	}

	@KafkaListener(topics = "payment.failed", groupId = "booking-group")
	public void paymentFailed(String message) throws Exception {

		JsonNode json = objectMapper.readTree(message);
		Long bookingId = json.get("bookingId").asLong();

		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
		booking.setStatus("CANCELLED");
		bookingRepo.save(booking);

		System.out.println("Booking " + bookingId + " cancelled");
		kafkaTemplate.send("slot.release", message);
		System.out.println("Published slot.release for booking " + bookingId);
	}
}
