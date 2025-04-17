package Capstone_Project_1.Evert.Organizer.controller;

import Capstone_Project_1.Evert.Organizer.model.Event;
import Capstone_Project_1.Evert.Organizer.service.EventService;
import Capstone_Project_1.Evert.Organizer.utils.SQLInjectionProtection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    // Posting a request to create an event
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        try {
            // Input validation
            if (event.getOrganizerUsername() == null) {
                return ResponseEntity.badRequest().body("Organizer username is required");
            }

            if (!SQLInjectionProtection.isSafe(event.getOrganizerUsername()) ||
                    !SQLInjectionProtection.isSafe(event.getTitle()) ||
                    !SQLInjectionProtection.isSafe(event.getDescription())) {
                return ResponseEntity.badRequest().body("Invalid input detected");
            }

            Event createdEvent = eventService.createEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating event: " + e.getMessage());
        }
    }


    // Getting the events  by organizer
    @GetMapping
    public ResponseEntity<?> getEventsByOrganizer(@RequestParam String organizerUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(organizerUsername)) {
                return ResponseEntity.badRequest().body("Invalid organizer username");
            }

            List<Event> events = eventService.getEventsByOrganizer(organizerUsername);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching events: " + e.getMessage());
        }
    }


    // Getting the event by id and organizer
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(
            @PathVariable Long eventId,
            @RequestParam String organizerUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(organizerUsername)) {
                return ResponseEntity.badRequest().body("Invalid organizer username");
            }

            Event event = eventService.getEventByIdAndOrganizer(eventId, organizerUsername);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching event: " + e.getMessage());
        }
    }


    // Updating the event by id and organizer
    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long eventId,
            @RequestBody Event updatedEvent) {
        try {
            if (updatedEvent.getOrganizerUsername() == null) {
                return ResponseEntity.badRequest().body("Organizer username is required");
            }

            if (!SQLInjectionProtection.isSafe(updatedEvent.getOrganizerUsername()) ||
                    !SQLInjectionProtection.isSafe(updatedEvent.getTitle()) ||
                    !SQLInjectionProtection.isSafe(updatedEvent.getDescription())) {
                return ResponseEntity.badRequest().body("Invalid input detected");
            }

            updatedEvent.setStatus("UPDATED");
            Event event = eventService.updateEvent(eventId, updatedEvent);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating event: " + e.getMessage());
        }
    }


    // Deleting the event by id and organizer
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable Long eventId,
            @RequestParam String organizerUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(organizerUsername)) {
                return ResponseEntity.badRequest().body("Invalid organizer username");
            }

            eventService.deleteEvent(eventId, organizerUsername);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting event: " + e.getMessage());
        }
    }


    // Getting the events by attendee
    @GetMapping("/attendee/events")
    public ResponseEntity<?> getAttendeeEvents(@RequestParam String attendeeUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(attendeeUsername)) {
                return ResponseEntity.badRequest().body("Invalid attendee username");
            }

            List<Event> events = eventService.getAttendeeEvents(attendeeUsername);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching events: " + e.getMessage());
        }
    }


    // Booking and canceling the event by id and attendee
    @PostMapping("/{eventId}/book")
    public ResponseEntity<?> bookEvent(
            @PathVariable Long eventId,
            @RequestParam String attendeeUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(attendeeUsername)) {
                return ResponseEntity.badRequest().body("Invalid attendee username");
            }

            Event event = eventService.bookEvent(eventId, attendeeUsername);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error booking event: " + e.getMessage());
        }
    }


    // Booking and canceling the event by id and attendee
    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long eventId,
            @RequestParam String attendeeUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(attendeeUsername)) {
                return ResponseEntity.badRequest().body("Invalid attendee username");
            }

            Event event = eventService.cancelBooking(eventId, attendeeUsername);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error canceling booking: " + e.getMessage());
        }
    }


    // Getting the upcoming events for the attendee
    @GetMapping("/attendee/upcoming")
    public ResponseEntity<?> getUpcomingEventsForAttendee(
            @RequestParam String attendeeUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(attendeeUsername)) {
                return ResponseEntity.badRequest().body("Invalid attendee username");
            }

            List<Event> events = eventService.getUpcomingEventsForAttendee(attendeeUsername);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching events: " + e.getMessage());
        }
    }


    // Getting the booked events for the attendee
    @GetMapping("/attendee/booked")
    public ResponseEntity<?> getBookedEventsForAttendee(
            @RequestParam String attendeeUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(attendeeUsername)) {
                return ResponseEntity.badRequest().body("Invalid attendee username");
            }

            List<Event> events = eventService.getBookedEventsForAttendee(attendeeUsername);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching events: " + e.getMessage());
        }
    }


    // Getting the deleted events for the attendee
    @GetMapping("/attendee/deleted")
    public ResponseEntity<?> getDeletedEventsForAttendee(@RequestParam String attendeeUsername) {
        try {
            if (!SQLInjectionProtection.isSafe(attendeeUsername)) {
                return ResponseEntity.badRequest().body("Invalid attendee username");
            }

            List<Event> events = eventService.getDeletedEventsForAttendee(attendeeUsername);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching deleted events: " + e.getMessage());
        }
    }
}