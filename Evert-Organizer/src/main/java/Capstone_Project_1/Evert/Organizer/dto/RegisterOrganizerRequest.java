package Capstone_Project_1.Evert.Organizer.dto;


public class RegisterOrganizerRequest {
    private String organizationName;
    private String username;
    private String password;
    private String phone;

    // Getters and setters
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

