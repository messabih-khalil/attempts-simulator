package com.attemptes.app.controllers;
import com.attemptes.app.enums.AttemptType;
import com.attemptes.app.services.redis.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AttemptService attemptService;

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestHeader("X-Forwarded-For") String ipAddress) {

        // Check if user is blocked
        if (attemptService.isBlocked(AttemptType.AUTH_WRONG_CREDENTIALS, username)) {
            return ResponseEntity.status(403).body("User is blocked for 24 hours");
        }

        // Check IP requests per second
        if (!attemptService.checkAttempts(AttemptType.REQUESTS_PER_SECOND, ipAddress)) {
            attemptService.block(AttemptType.REQUESTS_PER_SECOND, ipAddress);
            return ResponseEntity.status(429).body("Too many requests from this IP");
        }

        // Check authentication attempts
        if (!attemptService.checkAttempts(AttemptType.AUTH_WRONG_CREDENTIALS, username)) {
            attemptService.block(AttemptType.AUTH_WRONG_CREDENTIALS, username);
            return ResponseEntity.status(403).body("Too many failed attempts. User blocked for 24 hours");
        }

        // Simulate authentication
        if (!"correctPassword".equals(password)) {
            return ResponseEntity.status(401).body("Authentication failed");
        }

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/unblock")
    public ResponseEntity<String> unblockUser(@RequestParam String username) {
        attemptService.unblock(AttemptType.AUTH_WRONG_CREDENTIALS, username);
        return ResponseEntity.ok("User unblocked");
    }
}