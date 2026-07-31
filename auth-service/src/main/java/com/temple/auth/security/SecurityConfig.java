package com.temple.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter, OAuth2LoginSuccessHandler  successHandler) throws Exception {

		http
		.csrf(csrf -> csrf.disable())
		.cors(Customizer.withDefaults())
		.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/signup", "/auth/login", "/actuator/health").permitAll()
				.requestMatchers("/oauth2/**", "/login/**").permitAll()
				.anyRequest().authenticated())
		.oauth2Login(oauth -> oauth.successHandler(successHandler)) ///oauth2/** and /login/** are needed for Spring OAuth flow.
		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();

	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
