package com.temple.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.temple.payment.dto.PaymentRequest;
import com.temple.payment.dto.PaymentResponse;

@RestController
@RequestMapping("/payment")
public class PaymentController {
	
	@RequestMapping(method=RequestMethod.GET, value="/pay") 
	public ResponseEntity<PaymentResponse> payBooking() {
		
		PaymentResponse response = new PaymentResponse(1, "Success");
		return new ResponseEntity<PaymentResponse>(response,HttpStatus.OK);
		
	}

}
