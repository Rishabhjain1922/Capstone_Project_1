package Capstone_Project_1.Evert.Organizer.controller;

import Capstone_Project_1.Evert.Organizer.dto.*;
import Capstone_Project_1.Evert.Organizer.service.AuthService;
import Capstone_Project_1.Evert.Organizer.utils.SQLInjectionProtection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
// Redirect to login page when accessing the root URL
    @GetMapping
    public RedirectView redirectToLogin() {
        return new RedirectView("/login.html");
    }


// Posting a request to register an attendee
    @PostMapping("/register/attendee")
    public ResponseEntity<AuthResponse> registerAttendee(@RequestBody RegisterAttendeeRequest request) {
        if (!SQLInjectionProtection.isSafe(request.getUsername()) ||
                !SQLInjectionProtection.isSafe(request.getPassword()) ||
                !SQLInjectionProtection.isSafe(request.getFullName()) ||      // For the SQL injection protection
                !SQLInjectionProtection.isSafe(request.getPhone())) {

            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid input detected", null));
        }
        AuthResponse response = authService.registerAttendee(request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }
// Posting a request to register an organizer
    @PostMapping("/register/organizer")
    public ResponseEntity<AuthResponse> registerOrganizer(@RequestBody RegisterOrganizerRequest request) {
        if (!SQLInjectionProtection.isSafe(request.getUsername()) ||
                !SQLInjectionProtection.isSafe(request.getPassword()) ||
                !SQLInjectionProtection.isSafe(request.getOrganizationName()) ||
                !SQLInjectionProtection.isSafe(request.getPhone())) {

            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid input detected", null));
        }
        AuthResponse response = authService.registerOrganizer(request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }
// Posting a request to login an attendee
    @PostMapping("/login/attendee")
    public ResponseEntity<AuthResponse> loginAttendee(@RequestBody LoginRequest request) {
        if (!SQLInjectionProtection.isSafe(request.getUsername()) ||
                !SQLInjectionProtection.isSafe(request.getPassword())) {

            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid input detected", null));
        }
        AuthResponse response = authService.loginAttendee(request);
        if (response.isSuccess()) {
            response.setUsername(request.getUsername());
            response.setRole("attendee");
        }
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED)
                .body(response);
    }
// Posting a request to login an organizer
    @PostMapping("/login/organizer")
    public ResponseEntity<AuthResponse> loginOrganizer(@RequestBody LoginRequest request) {
        if (!SQLInjectionProtection.isSafe(request.getUsername()) ||
                !SQLInjectionProtection.isSafe(request.getPassword())) {

            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid input detected", null));
        }
        AuthResponse response = authService.loginOrganizer(request);
        if (response.isSuccess()) {
            response.setUsername(request.getUsername());
            response.setRole("organizer");
        }
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In JWT, logout is handled client-side by discarding the token
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new AuthResponse(true, "Logout successful", null));
    }
}