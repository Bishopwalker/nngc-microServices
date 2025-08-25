package org.nngc.dto;

import org.nngc.roles.AppUserRoles;

public class CustomerDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private AddressDTO address;
    private AppUserRoles role;
    private String stripeCustomerId;
    private String geoLocation;
    private boolean enabled;
    private String receiptURL;
    private String invoiceURL;
    private boolean changePassword;
    private String service;

    public CustomerDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public AddressDTO getAddress() { return address; }
    public void setAddress(AddressDTO address) { this.address = address; }

    public AppUserRoles getRole() { return role; }
    public void setRole(AppUserRoles role) { this.role = role; }

    public String getStripeCustomerId() { return stripeCustomerId; }
    public void setStripeCustomerId(String stripeCustomerId) { this.stripeCustomerId = stripeCustomerId; }

    public String getGeoLocation() { return geoLocation; }
    public void setGeoLocation(String geoLocation) { this.geoLocation = geoLocation; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getReceiptURL() { return receiptURL; }
    public void setReceiptURL(String receiptURL) { this.receiptURL = receiptURL; }

    public String getInvoiceURL() { return invoiceURL; }
    public void setInvoiceURL(String invoiceURL) { this.invoiceURL = invoiceURL; }

    public boolean isChangePassword() { return changePassword; }
    public void setChangePassword(boolean changePassword) { this.changePassword = changePassword; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
}