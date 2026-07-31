package com.temple.donation.dto;

import java.math.BigDecimal;

public class CreateDonationRequest {

	private BigDecimal amount;
	private String purpose;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
}