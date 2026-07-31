package com.temple.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.temple.booking.client.PaymentClient;
import com.temple.booking.dto.PaymentResponse;

@Service
public class BookingService {
	
	@Autowired
	PaymentClient  client;

	public void callPayment() {
		PaymentResponse response = client.payBooking();   
		System.out.println("Payment Client" + response);
		
	}
	
	

}
