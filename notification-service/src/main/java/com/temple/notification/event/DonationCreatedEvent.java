package com.temple.notification.event;

import java.math.BigDecimal;

public record DonationCreatedEvent(
 Long donationId,
 String userEmail,
 BigDecimal amount,
 String purpose
){}