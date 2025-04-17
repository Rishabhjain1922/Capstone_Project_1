package Capstone_Project_1.Evert.Organizer.repository;

import Capstone_Project_1.Evert.Organizer.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


// This interface extends JpaRepository to provide CRUD operations for the Event entity.
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find all events for an organizer


    // Custom delete method with organizer validation
    @Transactional
    @Modifying
    @Query("DELETE FROM Event e WHERE e.id = :eventId AND e.organizerUsername = :organizerUsername")
    int deleteByIdAndOrganizerUsername(@Param("eventId") Long eventId,
                                       @Param("organizerUsername") String organizerUsername);

    // Cleanup past events
    @Transactional
    @Modifying
    @Query("DELETE FROM Event e WHERE e.eventDate < :currentDate")
    int deleteByEventDateBefore(@Param("currentDate") LocalDateTime currentDate);

    // Find specific event with organizer validation
    Optional<Event> findByIdAndOrganizerUsername(Long id, String organizerUsername);

    @Query("SELECT e FROM Event e WHERE :attendeeUsername MEMBER OF e.attendees")
    List<Event> findByAttendeeUsername(@Param("attendeeUsername") String attendeeUsername);

    @Query("SELECT e FROM Event e WHERE e.eventDate > CURRENT_TIMESTAMP AND :attendeeUsername NOT MEMBER OF e.attendees")
    List<Event> findUpcomingEventsForAttendee(@Param("attendeeUsername") String attendeeUsername);

    @Query("SELECT e FROM Event e WHERE e.eventDate > :currentDate AND :attendeeUsername NOT MEMBER OF e.attendees")
    List<Event> findUpcomingEventsForAttendee(
            @Param("currentDate") LocalDateTime currentDate,
            @Param("attendeeUsername") String attendeeUsername
    );

    List<Event> findByAttendeesContains(String attendeeUsername);
    // In EventRepository.java
// Modify existing queries to exclude deleted events
    @Query("SELECT e FROM Event e WHERE e.organizerUsername = :organizerUsername AND e.status != 'DELETED'")
    List<Event> findByOrganizerUsername(@Param("organizerUsername") String organizerUsername);

    @Query("SELECT e FROM Event e WHERE e.eventDate > :now AND e.status != 'DELETED'")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE :attendee MEMBER OF e.attendees AND e.status != 'DELETED'")
    List<Event> findByAttendeeBookings(@Param("attendee") String attendee);

    // Add a new method to find events including deleted ones for attendees
    @Query("SELECT e FROM Event e WHERE :attendee MEMBER OF e.attendees")
    List<Event> findAttendeeEventsIncludingDeleted(@Param("attendee") String attendee);
}