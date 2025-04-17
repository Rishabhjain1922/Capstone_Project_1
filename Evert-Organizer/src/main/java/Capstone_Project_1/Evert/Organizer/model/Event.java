package Capstone_Project_1.Evert.Organizer.model;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                        // Id for easy identification and used as a primary key

    @Column(name = "organizer_username")
    private String organizerUsername;                      // organizer username who created the event

    @Column(name = "event_image")
    private String eventImage;                              // Image for the event which is used in the event card

    @Column(name = "event_type")
    private String eventType;                               //Selecting the Type of Event

    private String title;
    private String description;                             // Description of the event

    @Column(name = "event_date")
    private LocalDateTime eventDate;                        //Stored the time of the event

    @Column(name = "total_seats")                           // Total number of seats available for the event
    private int totalSeats;

    @Column(name = "seats_sold")
    private int seatsSold;                                  // For Storing the number of seats sold for the event

    private Double price;                                   // Price of the event Ticket Per Person

    @Column(name = "is_free")
    private boolean isFree;                                 // If the event is free or not

    @Column(name = "created_at")
    private LocalDateTime createdAt;                            //Stored the event creation time will be used in future for some features like event expiry or inactivity

    @ElementCollection
    @CollectionTable(name = "event_attendees", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "attendee_username")
    private Set<String> attendees = new HashSet<>();
    public Set<String> getAttendees() { return attendees; }
    public void setAttendees(Set<String> attendees) { this.attendees = attendees; }



    public Event() {
        this.createdAt = LocalDateTime.now();
    }
    // Add these fields
    @Column(name = "status")
    private String status = "ACTIVE"; // can be ACTIVE, CANCELLED, UPDATED, DELETED

    @Column(name = "original_details", columnDefinition = "TEXT")
    private String originalDetails;


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOriginalDetails() { return originalDetails; }
    public void setOriginalDetails(String originalDetails) { this.originalDetails = originalDetails; }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrganizerUsername() { return organizerUsername; }
    public void setOrganizerUsername(String organizerUsername) { this.organizerUsername = organizerUsername; }
    public String getEventImage() { return eventImage; }
    public void setEventImage(String eventImage) { this.eventImage = eventImage; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public int getSeatsSold() { return seatsSold; }
    public void setSeatsSold(int seatsSold) { this.seatsSold = seatsSold; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
