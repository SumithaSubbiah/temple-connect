package com.temple.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.temple.auth.entity.AppUser;
import com.temple.auth.exception.EmailAlreadyRegisteredException;
import com.temple.auth.repo.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public String signup(String email, String password) {
		String normEmail = normalizeEmail(email);

		if (userRepository.existsByEmail(normEmail)) {
		    throw new EmailAlreadyRegisteredException("Email already registered");
		}

		if (password == null || password.length() < 6) {
			throw new IllegalArgumentException("Password must be at least 6 characters");
		}

		String hash = passwordEncoder.encode(password);
		AppUser user = new AppUser(normEmail, hash, "USER");
		userRepository.save(user);

		return jwtService.generateToken(user.getEmail(), user.getRole());
	}

	public String login(String email, String password) {
		String normEmail = normalizeEmail(email);

		AppUser user = userRepository.findByEmail(normEmail)
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		if (!passwordEncoder.matches(password, user.getPasswordHash())) {
			throw new IllegalArgumentException("Invalid email or password");
		}

		return jwtService.generateToken(user.getEmail(), user.getRole());
	}

	private String normalizeEmail(String email) {
		if (email == null)
			throw new IllegalArgumentException("Email is required");
		String trimmed = email.trim().toLowerCase();
		if (!trimmed.contains("@"))
			throw new IllegalArgumentException("Invalid email");
		return trimmed;
	}
}
