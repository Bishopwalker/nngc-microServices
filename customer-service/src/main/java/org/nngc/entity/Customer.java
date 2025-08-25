package org.nngc.entity;

import jakarta.persistence.*;
import org.nngc.dto.AddressDTO;
import org.nngc.dto.CustomerDTO;
import org.nngc.dto.CustomerRouteInfoDTO;
import org.nngc.roles.AppUserRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Entity
@Table(name = "customer")
public class Customer implements UserDetails {

    @SequenceGenerator(
            name = "customer_seq",
            sequenceName = "customer_seq",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "customer_seq")
    private Long id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "phone", length = 13, unique = true)
    private String phone;

    @Column(name = "house_number", length = 8)
    private String houseNumber;

    @Column(name = "street_name", length = 50)
    private String streetName;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "state", length = 2)
    private String state;

    @Column(name = "zip_code", length = 5)
    private String zipCode;

    @Column(name = "county", length = 50)
    private String county;

    @Column(name = "geo_location", length = 10000, unique = true)
    private String geoLocation;

    @Column(name = "receipt_url", length = 350)
    private String receiptURL;

    @Column(name = "invoice_url", length = 350)
    private String invoiceURL;

    @Column(name = "latitude", length = 150)
    private Double latitude;

    @Column(name = "longitude", length = 150)
    private Double longitude;

    @Column(name = "service", length = 150)
    private String service;

    @Column(name = "stripe_customer_id", length = 50)
    private String stripeCustomerId;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

    private boolean changePassword = false;
    
    @Column(name = "keycloak_user_id", length = 100)
    private String keycloakUserId;

    @Enumerated(EnumType.STRING)
    private AppUserRoles appUserRoles;

    // Constructors
    public Customer() {}

    public Customer(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Builder pattern
    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }
    
    public static class CustomerBuilder {
        private Customer customer = new Customer();
        
        public CustomerBuilder firstName(String firstName) {
            customer.firstName = firstName;
            return this;
        }
        
        public CustomerBuilder lastName(String lastName) {
            customer.lastName = lastName;
            return this;
        }
        
        public CustomerBuilder email(String email) {
            customer.email = email;
            return this;
        }
        
        public CustomerBuilder password(String password) {
            customer.password = password;
            return this;
        }
        
        public CustomerBuilder phone(String phone) {
            customer.phone = phone;
            return this;
        }
        
        public CustomerBuilder houseNumber(String houseNumber) {
            customer.houseNumber = houseNumber;
            return this;
        }
        
        public CustomerBuilder streetName(String streetName) {
            customer.streetName = streetName;
            return this;
        }
        
        public CustomerBuilder city(String city) {
            customer.city = city;
            return this;
        }
        
        public CustomerBuilder state(String state) {
            customer.state = state;
            return this;
        }
        
        public CustomerBuilder zipCode(String zipCode) {
            customer.zipCode = zipCode;
            return this;
        }
        
        public CustomerBuilder service(String service) {
            customer.service = service;
            return this;
        }
        
        public CustomerBuilder appUserRoles(AppUserRoles appUserRoles) {
            customer.appUserRoles = appUserRoles;
            return this;
        }
        
        public CustomerBuilder enabled(boolean enabled) {
            customer.enabled = enabled;
            return this;
        }
        
        public CustomerBuilder keycloakUserId(String keycloakUserId) {
            customer.keycloakUserId = keycloakUserId;
            return this;
        }
        
        public Customer build() {
            return customer;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }

    public String getGeoLocation() { return geoLocation; }
    public void setGeoLocation(String geoLocation) { this.geoLocation = geoLocation; }

    public String getReceiptURL() { return receiptURL; }
    public void setReceiptURL(String receiptURL) { this.receiptURL = receiptURL; }

    public String getInvoiceURL() { return invoiceURL; }
    public void setInvoiceURL(String invoiceURL) { this.invoiceURL = invoiceURL; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getStripeCustomerId() { return stripeCustomerId; }
    public void setStripeCustomerId(String stripeCustomerId) { this.stripeCustomerId = stripeCustomerId; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isChangePassword() { return changePassword; }
    public void setChangePassword(boolean changePassword) { this.changePassword = changePassword; }
    
    public String getKeycloakUserId() { return keycloakUserId; }
    public void setKeycloakUserId(String keycloakUserId) { this.keycloakUserId = keycloakUserId; }

    public AppUserRoles getAppUserRoles() { return appUserRoles; }
    public void setAppUserRoles(AppUserRoles appUserRoles) { this.appUserRoles = appUserRoles; }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (appUserRoles == null) {
            return Collections.emptyList();
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUserRoles.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }

    // DTO conversion methods
    public CustomerRouteInfoDTO toCustomerRouteInfoDTO() {
        CustomerRouteInfoDTO dto = new CustomerRouteInfoDTO();
        dto.setId(id);
        dto.setFullName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
        dto.setPhoneNumber(phone);
        
        StringBuilder address = new StringBuilder();
        if (houseNumber != null) address.append(houseNumber).append(" ");
        if (streetName != null) address.append(streetName);
        if (city != null) address.append(", ").append(city);
        if (state != null) address.append(" ").append(state);
        if (zipCode != null) address.append(" ").append(zipCode);
        
        dto.setAddress(address.toString().trim());
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        return dto;
    }

    public CustomerDTO toCustomerDTO() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(id);
        dto.setFullName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
        dto.setEmail(email);
        dto.setPhoneNumber(phone);
        
        // Create address DTO
        AddressDTO addressDTO = new AddressDTO();
        String line1 = "";
        if (houseNumber != null) line1 += houseNumber + " ";
        if (streetName != null) line1 += streetName;
        addressDTO.setLine1(line1.trim());
        addressDTO.setCity(city);
        addressDTO.setState(state);
        addressDTO.setZipCode(zipCode);
        addressDTO.setLatitude(latitude);
        addressDTO.setLongitude(longitude);
        dto.setAddress(addressDTO);
        
        dto.setRole(appUserRoles);
        dto.setEnabled(enabled);
        dto.setStripeCustomerId(stripeCustomerId);
        dto.setGeoLocation(geoLocation);
        dto.setReceiptURL(receiptURL);
        dto.setInvoiceURL(invoiceURL);
        dto.setChangePassword(changePassword);
        dto.setService(service);
        return dto;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}