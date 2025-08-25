package com.northernneckgarbage.nngc.controller;


import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.registration.RegistrationRequest;
import com.northernneckgarbage.nngc.registration.RegistrationService;
import com.northernneckgarbage.nngc.repository.TokenRepository;
import com.northernneckgarbage.nngc.service.CustomerService;
import com.northernneckgarbage.nngc.service.TokenService;
import com.northernneckgarbage.nngc.token.Token;
import com.northernneckgarbage.nngc.token.auth.AuthenticationRequest;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("auth/nngc/")
@RequiredArgsConstructor
public class RegistrationController {
    private final TokenRepository tokenRepository;

    private final CustomerService customerService;

    private final RegistrationService service;
    private final TokenService tokenService;
    private final CustomerServiceClient customerServiceClient;

    @Value("${spring.profiles.active}")
    private String env;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("admin/register")
    public String processRegister(@RequestBody Customer customer) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
        StringBuilder sb = new StringBuilder("New customer object created" + customer);
        log.info(sb.toString());
        customerService.addCustomer(customer);
        return sb.toString();
    }

    @GetMapping("admin/tokens/{id}")
    public ResponseEntity<List<Token>> getAllTokensForUserById(@RequestHeader("Authorization") String headers, @PathVariable long id) {
        var user = tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (user.toString().equals("ADMIN")) {
            return ResponseEntity.ok(tokenRepository.findAllValidTokenByUser(id));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("registration")
    public Mono<ResponseEntity<ApiResponse>> register(
            @RequestBody RegistrationRequest request
    ) {
        log.info("Registration request received for: {}", request.getEmail());
        
        return customerServiceClient.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> {
                    log.error("Registration failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("Registration failed: " + error.getMessage())
                            .status("FAILED")
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }


    @GetMapping("/resend-token/{email}")
    public Mono<ResponseEntity<ApiResponse>> resendToken(@PathVariable String email) {
        log.info("Resend token request for: {}", email);
        
        return customerServiceClient.resendVerificationEmail(email)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> {
                    log.error("Resend token failed: ", error);
                    ApiResponse errorResponse = ApiResponse.builder()
                            .message("Failed to resend token: " + error.getMessage())
                            .status("FAILED")
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }


    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/authenticate")
    public  ApiResponse<Token> authenticate(
            @RequestBody AuthenticationRequest request
    ) throws StripeException, IOException, InterruptedException, ApiException {
      var response = tokenService.authenticate(request);
      if(response.getCustomerDTO().isEnabled()){
          return ApiResponse.<Token>builder()
                  .token(response.getToken())
                  .customerDTO(response.getCustomerDTO())
                  .message("good shit pimp")
                  .status(response.getStatus())
                  .build();
      } else{
          return ApiResponse.<Token>builder()
                  .message("You Fucked up or something")
                  .build();
      }
    }



    @GetMapping("/confirm")
    public void confirmMail(@RequestParam("token") String token, HttpServletResponse response) 
            throws IOException {
        log.info("Email confirmation request received");
        
        customerServiceClient.confirmEmail(token)
                .subscribe(
                    result -> {
                        try {
                            if (result.getHeaders().containsKey("Location")) {
                                String location = result.getHeaders().getFirst("Location");
                                if (location != null) {
                                    response.sendRedirect(location);
                                } else {
                                    response.sendRedirect(getDefaultSuccessUrl());
                                }
                            } else {
                                response.sendRedirect(getDefaultSuccessUrl());
                            }
                        } catch (IOException e) {
                            log.error("Error redirecting after email confirmation: ", e);
                        }
                    },
                    error -> {
                        try {
                            log.error("Email confirmation failed: ", error);
                            response.sendRedirect(getDefaultErrorUrl());
                        } catch (IOException e) {
                            log.error("Error redirecting after email confirmation error: ", e);
                        }
                    }
                );
    }

    //endpoint to retrieve a token by customer ID
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/token/{id}")
    public ApiResponse<Customer> getToken(@PathVariable("id") Long id) {
        log.info("id: " + id);
        // Call the getToken method from the TokenService and get the token
        // value =tokenService.findByCustomerId(id);
        var token = tokenService.findByCustomerId(id);
        // Return the response entity with the token
        return ApiResponse.<Customer>builder()
                .token(token.getToken())
                .build();
    }

    @GetMapping("/token_status")
    public Mono<ResponseEntity<String>> tokenStatus(@RequestParam("token") String token) {
        log.info("Token status check request received");
        
        return customerServiceClient.getTokenStatus(token)
                .map(response -> {
                    String status = "invalid";
                    if ("SUCCESS".equals(response.getStatus()) || "ALREADY_CONFIRMED".equals(response.getStatus())) {
                        status = "good";
                    } else if ("EXPIRED".equals(response.getStatus())) {
                        status = "expired";
                    }
                    
                    log.info("Token status response: {}", status);
                    return ResponseEntity.ok(status);
                })
                .onErrorResume(error -> {
                    log.error("Token status check failed: ", error);
                    return Mono.just(ResponseEntity.ok("invalid"));
                });
    }


    private boolean isProduction() {
        // Implement your logic to determine if the application is running in production
        // For example, you can check an environment variable
        return "prod".equals(env);
    }
    
    private String getDefaultSuccessUrl() {
        return isProduction() ? 
            "https://northernneckgarbage.com/email-verification-success" :
            "http://localhost:5173/email-verification-success";
    }
    
    private String getDefaultErrorUrl() {
        return isProduction() ? 
            "https://northernneckgarbage.com/email-verification-failed" :
            "http://localhost:5173/email-verification-failed";
    }

//    @GetMapping("/google/login")
//    public String redirectToGoogle() {
//        // Redirect to the URL that initiates OAuth2 login with Google
//        return "redirect:/oauth2/authorization/google";
//    }
//
//
//    @GetMapping("/loginSuccess")
//    public String getLoginInfo(@NotNull OAuth2AuthenticationToken authentication) {
//        OAuth2User oAuth2User = authentication.getPrincipal();
//        String name = oAuth2User.getAttribute("name");
//        log.info("name: " + name);
//        String email = oAuth2User.getAttribute("email");
//        log.info("email: " + email);
//        // add more attributes as needed
//        return "Hello, " + name + "!" + " Your email is " + email;
//    }
//
//    @GetMapping("/google/login/error")
//    public ResponseEntity<?> googleLoginError(@RequestParam(value = "error", required = false) String error) {
//        if (error != null) {
//            log.error("Google login error occurred: " + error);
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body("Google login failed. Please try again or contact support if the problem persists.");
//        }
//
//        // If there's no error, handle accordingly (redirect or another action)
//        return ResponseEntity.ok("No error detected. Redirecting...");
//    }
//

}
