package com.temple.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user", uniqueConstraints = { @UniqueConstraint(name = "uk_app_user_email", columnNames = "email") })
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String email;

	@Column(nullable = false, length = 100)
	private String passwordHash;

	@Column(nullable = false, length = 50)
	private String role = "USER"; // later: ADMIN, etc.

	protected AppUser() {
	}

	public AppUser(String email, String passwordHash, String role) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getRole() {
		return role;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
