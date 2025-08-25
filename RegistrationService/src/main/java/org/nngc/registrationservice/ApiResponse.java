package org.nngc.registrationservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private String message;
    private String status;
    private List<String> token;
    private Object customerDTO;
    private Object data;
    
    public ApiResponse() {}
    
    public ApiResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
    
    // Builder pattern
    public static ApiResponseBuilder builder() {
        return new ApiResponseBuilder();
    }
    
    public static class ApiResponseBuilder {
        private ApiResponse response = new ApiResponse();
        
        public ApiResponseBuilder message(String message) {
            response.message = message;
            return this;
        }
        
        public ApiResponseBuilder status(String status) {
            response.status = status;
            return this;
        }
        
        public ApiResponseBuilder token(List<String> token) {
            response.token = token;
            return this;
        }
        
        public ApiResponseBuilder customerDTO(Object customerDTO) {
            response.customerDTO = customerDTO;
            return this;
        }
        
        public ApiResponseBuilder data(Object data) {
            response.data = data;
            return this;
        }
        
        public ApiResponse build() {
            return response;
        }
    }
    
    // Getters and setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<String> getToken() {
        return token;
    }
    
    public void setToken(List<String> token) {
        this.token = token;
    }
    
    public Object getCustomerDTO() {
        return customerDTO;
    }
    
    public void setCustomerDTO(Object customerDTO) {
        this.customerDTO = customerDTO;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}