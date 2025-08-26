package org.nngc.dto;

public class TokenRequest {
    private Long customerId;
    private String token;
    
    public TokenRequest() {}
    
    public TokenRequest(Long customerId, String token) {
        this.customerId = customerId;
        this.token = token;
    }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}