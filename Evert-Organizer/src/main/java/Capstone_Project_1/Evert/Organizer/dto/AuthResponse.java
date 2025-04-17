package Capstone_Project_1.Evert.Organizer.dto;

public class AuthResponse {

    // Indicates whether the authentication was successful
    private boolean success;
    private String message;
    private String role;
    private String username;


    // Constructor for successful authentication
    public AuthResponse(boolean success, String message, String role) {
        this(success, message, role, null);
    }


    // Constructor for failed authentication
    public AuthResponse(boolean success, String message, String role, String username) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.username = username;
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
}


// Here I have to create the getter and setter manually because lombok is not working and giving some error