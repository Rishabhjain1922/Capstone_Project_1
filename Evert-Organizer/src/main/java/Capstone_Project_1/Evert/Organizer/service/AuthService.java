package Capstone_Project_1.Evert.Organizer.service;

import Capstone_Project_1.Evert.Organizer.dto.AuthResponse;
import Capstone_Project_1.Evert.Organizer.dto.LoginRequest;
import Capstone_Project_1.Evert.Organizer.dto.RegisterAttendeeRequest;
import Capstone_Project_1.Evert.Organizer.dto.RegisterOrganizerRequest;
import Capstone_Project_1.Evert.Organizer.model.Attendee;
import Capstone_Project_1.Evert.Organizer.model.Organizer;
import Capstone_Project_1.Evert.Organizer.repository.AttendeeRepository;
import Capstone_Project_1.Evert.Organizer.repository.OrganizerRepository;
import Capstone_Project_1.Evert.Organizer.utils.SQLInjectionProtection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthService {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AttendeeRepository attendeeRepository,
                       OrganizerRepository organizerRepository, PasswordEncoder passwordEncoder) {
        this.attendeeRepository = attendeeRepository;
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse registerAttendee(RegisterAttendeeRequest request) {
        String sanitizedUsername = SQLInjectionProtection.sanitize(request.getUsername());      //SQL Injection for checking any malicious code
        if (attendeeRepository.findByUsername(sanitizedUsername) != null) {
            return new AuthResponse(false, "Username already exists for attendee", null);
        }

        Attendee attendee = new Attendee();
        attendee.setFullName(SQLInjectionProtection.sanitize(request.getFullName()));
        attendee.setUsername(sanitizedUsername);
        attendee.setPassword(passwordEncoder.encode(SQLInjectionProtection.sanitize(request.getPassword())));
        attendee.setPhone(SQLInjectionProtection.sanitize(request.getPhone()));

        attendeeRepository.save(attendee);
        return new AuthResponse(true, "Attendee registered successfully", "attendee");
    }

    public AuthResponse registerOrganizer(RegisterOrganizerRequest request) {
        String sanitizedUsername = SQLInjectionProtection.sanitize(request.getUsername());
        if (organizerRepository.findByUsername(sanitizedUsername) != null) {
            return new AuthResponse(false, "Username already exists for organizer", null);
        }

        Organizer organizer = new Organizer();
        organizer.setOrganizationName(SQLInjectionProtection.sanitize(request.getOrganizationName()));
        organizer.setUsername(sanitizedUsername);
        organizer.setPassword(passwordEncoder.encode(SQLInjectionProtection.sanitize(request.getPassword())));
        organizer.setPhone(SQLInjectionProtection.sanitize(request.getPhone()));

        organizerRepository.save(organizer);
        return new AuthResponse(true, "Organizer registered successfully", "organizer");
    }


    public AuthResponse loginAttendee(LoginRequest request) {
        String sanitizedUsername = SQLInjectionProtection.sanitize(request.getUsername());
        Attendee attendee = attendeeRepository.findByUsername(sanitizedUsername);

        if (attendee == null) {
            return new AuthResponse(false, "Attendee not found", null);
        }

        if (!passwordEncoder.matches(SQLInjectionProtection.sanitize(request.getPassword()), attendee.getPassword())) {
            return new AuthResponse(false, "Invalid password", null);
        }

        return new AuthResponse(true, "Login successful", "attendee", attendee.getUsername());
    }

    public AuthResponse loginOrganizer(LoginRequest request) {
        String sanitizedUsername = SQLInjectionProtection.sanitize(request.getUsername());
        Organizer organizer = organizerRepository.findByUsername(sanitizedUsername);

        if (organizer == null) {
            return new AuthResponse(false, "Organizer not found", null);
        }

        if (!passwordEncoder.matches(SQLInjectionProtection.sanitize(request.getPassword()), organizer.getPassword())) {
            return new AuthResponse(false, "Invalid password", null);
        }

        return new AuthResponse(true, "Login successful", "organizer", organizer.getUsername());
    }
}
