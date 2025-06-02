package Capstone_Project_1.Evert.Organizer.service;
import Capstone_Project_1.Evert.Organizer.model.Event;
import Capstone_Project_1.Evert.Organizer.repository.EventRepository;
import Capstone_Project_1.Evert.Organizer.utils.SQLInjectionProtection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {
        // Add validation but don't break existing flow
        if (!SQLInjectionProtection.isSafe(event.getTitle()) ||
                !SQLInjectionProtection.isSafe(event.getDescription()) ||
                !SQLInjectionProtection.isSafe(event.getOrganizerUsername())) {
            throw new IllegalArgumentException("Potential SQL injection detected");
        }

        // Original functionality remains unchanged
        return eventRepository.save(event);
    }



    public Event updateEvent(Long eventId, Event updatedEvent) {
        if (!SQLInjectionProtection.isSafe(updatedEvent.getTitle()) ||
                !SQLInjectionProtection.isSafe(updatedEvent.getDescription()) ||
                !SQLInjectionProtection.isSafe(updatedEvent.getOrganizerUsername())) {
            throw new IllegalArgumentException("Potential SQL injection detected");
        }
        String organizerUsername = updatedEvent.getOrganizerUsername();

        Event event = eventRepository.findByIdAndOrganizerUsername(eventId, organizerUsername)
                .orElseThrow(() -> new RuntimeException("Event not found or unauthorized"));

        // Store original details before updating
        String originalDetails = String.format(
                "Title: %s, Description: %s, Date: %s, Seats: %d, Price: %.2f",
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getTotalSeats(),
                event.getPrice()
        );

        // Update all fields
        event.setEventImage(updatedEvent.getEventImage());
        event.setEventType(updatedEvent.getEventType());
        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setEventDate(updatedEvent.getEventDate());
        event.setTotalSeats(updatedEvent.getTotalSeats());
        event.setPrice(updatedEvent.getPrice());
        event.setFree(updatedEvent.isFree());
        event.setStatus("UPDATED"); // Set status to UPDATED
        event.setOriginalDetails(originalDetails); // Store original details

        return eventRepository.save(event);
    }

    // Fix this method in EventService
    public Event getEventByIdAndOrganizer(Long eventId, String organizerUsername) {
        return eventRepository.findByIdAndOrganizerUsername(eventId, organizerUsername)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public void deleteEvent(Long eventId, String organizerUsername) {
        Event event = eventRepository.findByIdAndOrganizerUsername(eventId, organizerUsername)
                .orElseThrow(() -> new RuntimeException("Event not found or unauthorized"));

        // Instead of deleting, mark as DELETED
        event.setStatus("DELETED");
        eventRepository.save(event);
    }
    public List<Event> getUpcomingEvents(String attendeeUsername) {
        List<Event> allEvents = eventRepository.findUpcomingEvents(LocalDateTime.now());
        List<Event> bookedEvents = eventRepository.findByAttendeeBookings(attendeeUsername);
        return allEvents.stream()
                .filter(event -> !bookedEvents.contains(event))
                .collect(Collectors.toList());
    }

    public List<Event> getAttendeeEvents(String attendeeUsername) {
        return eventRepository.findByAttendeeBookings(attendeeUsername);
    }

    public Event bookEvent(Long eventId, String attendeeUsername) {
        if (!SQLInjectionProtection.isSafe(attendeeUsername)) {
            throw new IllegalArgumentException("Invalid attendee username");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(event.getSeatsSold() >= event.getTotalSeats()) {
            throw new RuntimeException("No seats available");
        }

        if(event.getAttendees().contains(attendeeUsername)) {
            throw new RuntimeException("Already booked");
        }

        event.setSeatsSold(event.getSeatsSold() + 1);
        event.getAttendees().add(attendeeUsername);
        return eventRepository.save(event);
    }

    public Event cancelBooking(Long eventId, String attendeeUsername) {
        if (!SQLInjectionProtection.isSafe(attendeeUsername)) {
            throw new IllegalArgumentException("Invalid attendee username");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(!event.getAttendees().contains(attendeeUsername)) {
            throw new RuntimeException("No booking found");
        }

        event.setSeatsSold(event.getSeatsSold() - 1);
        event.getAttendees().remove(attendeeUsername);
        return eventRepository.save(event);
    }
    public List<Event> findUpcomingEventsForAttendee(String attendeeUsername) {
        return eventRepository.findUpcomingEventsForAttendee(attendeeUsername);
    }
    public List<Event> findByAttendeeUsername(String attendeeUsername) {
        return eventRepository.findByAttendeeUsername(attendeeUsername);
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void updateEventStatuses() {
        eventRepository.findAll().forEach(event -> {
            if ("CANCELLED".equals(event.getStatus())) {
                event.setOriginalDetails(event.toString());
            }
        });
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void cleanupPastEvents() {
        eventRepository.deleteByEventDateBefore(LocalDateTime.now());
    }
    public List<Event> getUpcomingEventsForAttendee(String attendeeUsername) {
        return eventRepository.findUpcomingEventsForAttendee(
                LocalDateTime.now(),
                attendeeUsername
        );
    }
    // In EventService.java
    public List<Event> getDeletedEventsForAttendee(String attendeeUsername) {
        return eventRepository.findAttendeeEventsIncludingDeleted(attendeeUsername)
                .stream()
                .filter(event -> "DELETED".equals(event.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Event> getBookedEventsForAttendee(String attendeeUsername) {
        return eventRepository.findByAttendeeBookings(attendeeUsername);
    }

    public List<Event> getEventsByOrganizer(String organizerUsername) {
        return eventRepository.findByOrganizerUsername(organizerUsername);
    }

    public List<Event> searchUpcomingEventsForAttendee(String attendeeUsername, String searchTerm) {
        List<Event> upcomingEvents = getUpcomingEventsForAttendee(attendeeUsername);
        return filterEventsBySearchTerm(upcomingEvents, searchTerm);
    }

    public List<Event> searchBookedEventsForAttendee(String attendeeUsername, String searchTerm) {
        List<Event> bookedEvents = getBookedEventsForAttendee(attendeeUsername);
        return filterEventsBySearchTerm(bookedEvents, searchTerm);
    }

    public List<Event> searchDeletedEventsForAttendee(String attendeeUsername, String searchTerm) {
        List<Event> deletedEvents = getDeletedEventsForAttendee(attendeeUsername);
        return filterEventsBySearchTerm(deletedEvents, searchTerm);
    }

    private List<Event> filterEventsBySearchTerm(List<Event> events, String searchTerm) {
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        return events.stream()
                .filter(event -> {
                    boolean matchesTitle = event.getTitle() != null &&
                            event.getTitle().toLowerCase().contains(lowerCaseSearchTerm);
                    boolean matchesDescription = event.getDescription() != null &&
                            event.getDescription().toLowerCase().contains(lowerCaseSearchTerm);
                    boolean matchesType = event.getEventType() != null &&
                            event.getEventType().toLowerCase().contains(lowerCaseSearchTerm);

                    return matchesTitle || matchesDescription || matchesType;
                })
                .collect(Collectors.toList());
    }

}



