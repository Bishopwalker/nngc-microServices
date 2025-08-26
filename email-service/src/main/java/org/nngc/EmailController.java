package org.nngc;

import com.sendgrid.Content;
import org.nngc.dto.PasswordResetEmailRequest;
import org.nngc.dto.RegistrationEmailRequest;
import org.nngc.dto.WelcomeEmailRequest;
import org.nngc.service.EmailTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = {"https://northernneckgarbage.com", "https://www.northernneckgarbage.com", "http://localhost:3000"})
public class EmailController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    public EmailController(EmailService emailService, EmailTemplateService emailTemplateService) {
        this.emailService = emailService;
        this.emailTemplateService = emailTemplateService;
    }
    
    @PostMapping("/send-registration")
    public ResponseEntity<String> sendRegistrationEmail(@RequestBody RegistrationEmailRequest request) {
        try {
            logger.info("Sending registration email to: {}", request.getEmail());
            
            Content emailContent = emailTemplateService.buildRegistrationEmail(request.getFirstName(), request.getLink());
            emailService.sendWithSendGrid(
                request.getEmail(),
                "Activate Your NNGC Account",
                emailContent
            );
            
            return ResponseEntity.ok("Registration email sent successfully");
        } catch (IOException e) {
            logger.error("Failed to send registration email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send registration email");
        }
    }
    
    @PostMapping("/send-password-reset")
    public ResponseEntity<String> sendPasswordResetEmail(@RequestBody PasswordResetEmailRequest request) {
        try {
            logger.info("Sending password reset email to: {}", request.getEmail());
            
            Content emailContent = emailTemplateService.buildPasswordResetEmail(request.getFirstName(), request.getLink());
            emailService.sendWithSendGrid(
                request.getEmail(),
                "Reset Your NNGC Password",
                emailContent
            );
            
            return ResponseEntity.ok("Password reset email sent successfully");
        } catch (IOException e) {
            logger.error("Failed to send password reset email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send password reset email");
        }
    }
    
    @PostMapping("/send-welcome")
    public ResponseEntity<String> sendWelcomeEmail(@RequestBody WelcomeEmailRequest request) {
        try {
            logger.info("Sending welcome email to: {}", request.getEmail());
            
            Content emailContent = emailTemplateService.buildWelcomeEmail(request.getFirstName());
            emailService.sendWithSendGrid(
                request.getEmail(),
                "Welcome to NNGC!",
                emailContent
            );
            
            return ResponseEntity.ok("Welcome email sent successfully");
        } catch (IOException e) {
            logger.error("Failed to send welcome email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send welcome email");
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Email Service is running");
    }
}