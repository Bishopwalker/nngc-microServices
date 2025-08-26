package org.nngc.registrationservice.registration;

import java.util.Objects;

public class RegistrationRequest {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String phone;
    private final String houseNumber;
    private final String streetName;
    private final String city;
    private final String state;
    private final String service;
    private final String zipCode;

    public RegistrationRequest(String firstName, String lastName, String email, String password, 
                             String phone, String houseNumber, String streetName, String city, 
                             String state, String service, String zipCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.city = city;
        this.state = state;
        this.service = service;
        this.zipCode = zipCode;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getHouseNumber() { return houseNumber; }
    public String getStreetName() { return streetName; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getService() { return service; }
    public String getZipCode() { return zipCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationRequest that = (RegistrationRequest) o;
        return Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(email, that.email) &&
               Objects.equals(password, that.password) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(houseNumber, that.houseNumber) &&
               Objects.equals(streetName, that.streetName) &&
               Objects.equals(city, that.city) &&
               Objects.equals(state, that.state) &&
               Objects.equals(service, that.service) &&
               Objects.equals(zipCode, that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, password, phone, 
                          houseNumber, streetName, city, state, service, zipCode);
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", houseNumber='" + houseNumber + '\'' +
               ", streetName='" + streetName + '\'' +
               ", city='" + city + '\'' +
               ", state='" + state + '\'' +
               ", service='" + service + '\'' +
               ", zipCode='" + zipCode + '\'' +
               '}';
    }
}