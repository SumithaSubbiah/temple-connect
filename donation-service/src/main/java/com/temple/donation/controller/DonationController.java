package com.temple.donation.controller;

import com.temple.donation.dto.CreateDonationRequest;
import com.temple.donation.entity.Donation;
import com.temple.donation.event.DonationCreatedEvent;
import com.temple.donation.kafka.DonationProducer;
import com.temple.donation.repo.DonationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/donations")
public class DonationController {

    private final DonationRepository donationRepository;
    private final DonationProducer donationProducer;

    public DonationController(DonationRepository donationRepository, DonationProducer donationProducer) {
        this.donationRepository = donationRepository;
		this.donationProducer = donationProducer;
    }

    @PostMapping
    public Donation createDonation(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestBody CreateDonationRequest request) {

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }

        if (request.getPurpose() == null || request.getPurpose().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purpose is required");
        }

        Donation donation = new Donation(
                userEmail,
                request.getAmount(),
                request.getPurpose(),
                "SUCCESS"
        );

        Donation saved = donationRepository.save(donation);

        donationProducer.publish(new DonationCreatedEvent(
                saved.getId(),
                saved.getUserEmail(),
                saved.getAmount(),
                saved.getPurpose()
        ));

        return saved;
    }

    @GetMapping("/my")
    public List<Donation> myDonations(@RequestHeader("X-User-Email") String userEmail) {
        return donationRepository.findByUserEmailOrderByIdDesc(userEmail);
    }
}