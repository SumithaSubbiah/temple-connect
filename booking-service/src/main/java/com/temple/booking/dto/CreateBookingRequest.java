package com.temple.booking.dto;

public class CreateBookingRequest {
	
	private String poojaName;
	private String bookingDate;
	public String getPoojaName() {
		return poojaName;
	}
	public void setPoojaName(String poojaName) {
		this.poojaName = poojaName;
	}
	public String getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}
	
}
