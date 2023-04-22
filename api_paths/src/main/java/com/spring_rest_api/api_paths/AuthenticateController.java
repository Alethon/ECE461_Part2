package com.spring_rest_api.api_paths;

import com.spring_rest_api.api_paths.entity.AuthenticationRequest;
import com.spring_rest_api.api_paths.entity.User;
import com.spring_rest_api.api_paths.service.AuthenticateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

@RestController
public class AuthenticateController {

    @Autowired
    private AuthenticateService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticateService.class);

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthenticationRequest request) {
        try {
            User registeredUser = userService.saveUser(request.getUser(), request.getUserAuthenticationInfo());
            if (registeredUser != null) {
                return ResponseEntity.ok(registeredUser);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed.");
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering the user.");
        }
    }
    

    @PutMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        logger.info("User: {}", request.getUser());
        logger.info("UserAuthenticationInfo: {}", request.getUserAuthenticationInfo());
        String token;
        try {
            token = userService.authenticateUser(request.getUser(), request.getUserAuthenticationInfo());
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating JWT token.");
        }

        if (token != null) {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable String userId) {
        try {
            userService.removeUser(userId);
            return ResponseEntity.ok("User successfully removed.");
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing the user.");
        }
    }
}
