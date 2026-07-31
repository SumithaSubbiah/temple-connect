package com.temple.auth.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.temple.auth.entity.AppUser;
import com.temple.auth.repo.UserRepository;
import com.temple.auth.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final String redirectUri;

    public OAuth2LoginSuccessHandler(UserRepository userRepository,
                                     JwtService jwtService,
                                     @Value("${app.oauth2.redirectUri}") String redirectUri) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        String email = token.getPrincipal().getAttribute("email");
        if (email == null || email.isBlank()) {
            response.sendError(400, "Google account email not found");
            return;
        }

        String normEmail = email.trim().toLowerCase();

        // Upsert user (no password for Google accounts)
        AppUser user = userRepository.findByEmail(normEmail)
                .orElseGet(() -> userRepository.save(new AppUser(normEmail, "{OAUTH2}", "USER")));

        String jwt = jwtService.generateToken(user.getEmail(), user.getRole());
        System.out.println("Redirecting to UI: " + redirectUri);

        String url = redirectUri + "?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);
        System.out.println("Final Redirecting to UI: " + url);

        response.sendRedirect(url);
    }
}
