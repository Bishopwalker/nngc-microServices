package org.nngc.dto;

public class WelcomeEmailRequest {
    private String email;
    private String firstName;
    
    public WelcomeEmailRequest() {}
    
    public WelcomeEmailRequest(String email, String firstName) {
        this.email = email;
        this.firstName = firstName;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
}