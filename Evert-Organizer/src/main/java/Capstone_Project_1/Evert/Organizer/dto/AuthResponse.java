package Capstone_Project_1.Evert.Organizer.dto;

public class AuthResponse {
    private boolean success;
    private String message;
    private String role;
    private String username;
    private String token; // Add this field

    // Constructors
    public AuthResponse(boolean success, String message, String role) {
        this(success, message, role, null, null);
    }

    public AuthResponse(boolean success, String message, String role, String username, String token) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.username = username;
        this.token = token;
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
    public String getToken() { return token; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
    public void setToken(String token) { this.token = token; }
}