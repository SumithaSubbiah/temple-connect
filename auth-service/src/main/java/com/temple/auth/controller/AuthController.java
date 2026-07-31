package com.temple.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.temple.auth.dto.AuthResponse;
import com.temple.auth.dto.LoginRequest;
import com.temple.auth.dto.MeResponse;
import com.temple.auth.dto.SignupRequest;
import com.temple.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest req) {
System.out.println("Request coming here");
		String token = authService.signup(req.getEmail(), req.getPassword());
		return ResponseEntity.ok(new AuthResponse(token));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
		String token = authService.login(req.getEmail(), req.getPassword());
		return ResponseEntity.ok(new AuthResponse(token));
	}

	@GetMapping("/me")
	public ResponseEntity<MeResponse> me(Authentication authentication) {
		// authentication.getName() is the email we stored in
		// UsernamePasswordAuthenticationToken
		String email = authentication.getName();
		String role = authentication.getAuthorities().stream().findFirst()
				.map(a -> a.getAuthority().replace("ROLE_", "")).orElse("USER");

		return ResponseEntity.ok(new MeResponse(email, role));
	}

}
