package com.temple.booking.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.temple.booking.dto.CreateBookingRequest;
import com.temple.booking.entity.Booking;
import com.temple.booking.event.BookingCreatedEvent;
import com.temple.booking.kafka.BookingProducer;
import com.temple.booking.kafka.BookingSagaListener;
import com.temple.booking.repo.BookingRepository;
import com.temple.booking.service.BookingService;

@RequestMapping("/bookings")
@RestController
public class BookingController {

	private final BookingRepository bookingRepo;
	private final BookingProducer bookingProducer;	
	private final BookingService bookingService;

	public BookingController(BookingRepository bookingRepo, BookingProducer bookingProducer, BookingService service) {
		this.bookingRepo = bookingRepo;
		this.bookingProducer = bookingProducer;
		this.bookingService = service;
	}

	@PostMapping
	public Booking createBooking(@RequestHeader("X-User-Email") String userEmail,
			@RequestBody CreateBookingRequest request) {

		if (request.getPoojaName() == null || request.getPoojaName().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pooja name is mandatory");
		}

		if (request.getBookingDate() == null || request.getBookingDate().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking  date is mandatory");
		}

		LocalDate date;

		try {
			date = LocalDate.parse(request.getBookingDate());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking  date must be in yyyy-MM-dd format");
		}

		Booking booking = new Booking(userEmail, request.getPoojaName(), date, "PENDING");
		Booking saved = bookingRepo.save(booking);
		
		bookingProducer.publishBookingCreatedEvent(new BookingCreatedEvent(saved.getId(), saved.getUserEmail(), saved.getPoojaName(), saved.getBookingDate().toString()));
		
		bookingService.callPayment();
		return saved;
	}

	@GetMapping("/myBooking")
	public List<Booking> myBookings(@RequestHeader("X-User-Email") String userEmail) {

		return bookingRepo.findByUserEmailOrderByIdDesc(userEmail);

	}

	@PatchMapping("/{id}/cancel")
	public Booking cancelBooking(@RequestHeader("X-User-Email") String userEmail, @PathVariable("id") Long id) {
		
		Booking booking = bookingRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
		
		if (!booking.getUserEmail().equals(userEmail)) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot cancel this booking");
	    }

		if ("CANCELLED".equals(booking.getStatus())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking already cancelled");
	    }
		
		booking.setStatus("CANCELLED");
		return bookingRepo.save(booking); 
	}

}
