package Capstone_Project_1.Evert.Organizer.service;

import Capstone_Project_1.Evert.Organizer.model.Attendee;
import Capstone_Project_1.Evert.Organizer.model.Organizer;
import Capstone_Project_1.Evert.Organizer.repository.AttendeeRepository;
import Capstone_Project_1.Evert.Organizer.repository.OrganizerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AttendeeRepository attendeeRepository;
    private final OrganizerRepository organizerRepository;

    public CustomUserDetailsService(AttendeeRepository attendeeRepository,
                                    OrganizerRepository organizerRepository) {
        this.attendeeRepository = attendeeRepository;
        this.organizerRepository = organizerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check in both repositories
        Attendee attendee = attendeeRepository.findByUsername(username);
        if (attendee != null) {
            return new User(attendee.getUsername(), attendee.getPassword(), new ArrayList<>());
        }

        Organizer organizer = organizerRepository.findByUsername(username);
        if (organizer != null) {
            return new User(organizer.getUsername(), organizer.getPassword(), new ArrayList<>());
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}