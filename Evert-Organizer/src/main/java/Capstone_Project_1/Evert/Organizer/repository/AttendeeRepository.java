package Capstone_Project_1.Evert.Organizer.repository;
import Capstone_Project_1.Evert.Organizer.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;


// This interface extends JpaRepository to provide CRUD operations for the Attendee entity.
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
    Attendee findByUsername(String username);
}