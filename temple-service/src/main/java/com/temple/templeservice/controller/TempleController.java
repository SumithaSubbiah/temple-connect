package com.temple.templeservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TempleController {

	@GetMapping("/temple/health")
	public Map<String, String> health() {
		return Map.of("status", "OK");
	}

	@GetMapping("/temple/info")
	public Map<String, String> info() throws InterruptedException {
		Thread.sleep(30000);
		return Map.of("name", "Sri Maha Temple", "location", "Bangalore");
	}
	@GetMapping("/debug/headers")
    public Map<String, Object> debugHeaders(
            @RequestHeader HttpHeaders headers
    ) {
        Map<String, Object> response = new HashMap<>();

        response.put("x-user-email", headers.getFirst("X-User-Email"));
        response.put("x-user-role", headers.getFirst("X-User-Role"));

        // Optional: show all headers for inspection
        Map<String, String> all = new HashMap<>();
        headers.forEach((k, v) -> all.put(k, String.join(",", v)));
        response.put("allHeaders", all);

        return response;
    }
}
