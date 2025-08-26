package org.nngc.dto;

public class RegistrationEmailRequest {
    private String email;
    private String firstName;
    private String link;
    
    public RegistrationEmailRequest() {}
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}