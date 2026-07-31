package com.temple.booking.event;

public record BookingCreatedEvent(
        Long bookingId,
        String userEmail,
        String poojaName,
        String bookingDate
) {}