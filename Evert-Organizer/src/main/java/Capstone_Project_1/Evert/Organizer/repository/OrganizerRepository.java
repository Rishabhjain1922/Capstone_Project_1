package Capstone_Project_1.Evert.Organizer.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import Capstone_Project_1.Evert.Organizer.model.Organizer;


//// This interface extends JpaRepository to provide CRUD operations for the Organizer entity.
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
    Organizer findByUsername(String username);
}