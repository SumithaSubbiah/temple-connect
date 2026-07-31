package com.temple.auth.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.temple.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(auth != null && auth.startsWith("Bearer ")) {
        	String token = auth.substring("Bearer ".length()).trim();
        	try {
        		JwtService.DecodedToken decoded = jwtService.verifyAndDecode(token);
        		
        		var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + decoded.role()));
        		var authentication =  new UsernamePasswordAuthenticationToken(decoded.email(), null, authorities);
        		SecurityContextHolder.getContext().setAuthentication(authentication);
        	} catch (Exception ignored) {
        		SecurityContextHolder.clearContext();
        	}
        }
        filterChain.doFilter(request, response);
	}

}
