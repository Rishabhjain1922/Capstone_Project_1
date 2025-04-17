package Capstone_Project_1.Evert.Organizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendees")
public class Attendee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                          // Id for easy identification and used as a primary key

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String username;                                 // In the Project Template, it is mentioned to use the Email but i have use username because it is more common and easier to use  If need to use email, then we can change it to email

    @Column(nullable = false)
    private String password;                                 // Password with which 255 length max length (for hash coding atleast 60 length is required)

    @Column(nullable = false)
    private String phone;                                   // Phone number with 10 digits it can be used in future for adding some extra features like OTP verification

    @Column(name = "created_at")
    private LocalDateTime createdAt;                            //Stored the account creation time will be used in future for some features like account expiry or inactivity

    // Constructors, Getters, and Setters
    public Attendee() {
        this.createdAt = LocalDateTime.now();
    }
    // Manually need to create the constructor because lombok is not working and giving some error
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
