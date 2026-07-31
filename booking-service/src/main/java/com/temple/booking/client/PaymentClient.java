package com.temple.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.temple.booking.dto.PaymentResponse;

@FeignClient(name="payment-service", url = "http://payment-service:8087")
public interface PaymentClient {

    @GetMapping("/payment/pay")
    PaymentResponse payBooking();
}            
