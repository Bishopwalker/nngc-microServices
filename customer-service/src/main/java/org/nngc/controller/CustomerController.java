package org.nngc.controller;


import org.nngc.dto.CustomerDTO;
import org.nngc.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
//private final TokenRepository tokenRepository;


//@PostMapping("/add_bulk")
//public ResponseEntity addBulkCustomers(@RequestBody List<Customer> customers) {
//    customerService.addBulkCustomers(customers);
//return ResponseEntity.ok(customers.size() + "Customers added: " + customers);
//
//}
//
//    @GetMapping("/stripe_id/{id}")
//    public ResponseEntity<CustomerDTO> getCustomerByStripeId(@PathVariable String id) {
//
//        //customerService.getCustomerByStripeId(id);
//        return  ResponseEntity.ok(customerService.getCustomerByStripeId(id);
//    }
//
//
//    @GetMapping("/customers")
//   public ResponseEntity<List<Customer>> getAllCustomers(@RequestHeader("Authorization") String headers) {
//       log.info(headers);
//       var user=tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
//        if(user==null){
//            return ResponseEntity.badRequest().body(ApiResponse.<List<Customer>>builder().message("You are not authorized to view this page").build());
//        }
//         if(user.toString().equals("ADMIN")){
//                return ResponseEntity.ok(customerService.getCustomers());
//         }
//        return ResponseEntity.badRequest().body(ApiResponse.<List<Customer>>builder().message("You are not authorized to view this page").build());
//
//    }
//    @GetMapping("/customers/")
//    public ResponseEntity<StripeRegistrationResponse<Optional<Customer>>> getCustomerByEmail(@RequestHeader("Authorization") String headers, @RequestParam String email){
//        log.info(headers);
//        var user = tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
//        if(user==null){
//            return ResponseEntity.badRequest().body(StripeRegistrationResponse.<Optional<Customer>>builder().message("You are not authorized to view this page").build());
//        }
//        log.info(user.toString());
//        if(user.toString().equals("ADMIN") || user.toString().equals("STRIPE_CUSTOMER")){
//            return ResponseEntity.ok(customerService.findByEmail(email));
//        }
//        return ResponseEntity.badRequest().body(StripeRegistrationResponse.<Optional<Customer>>builder().message("You are not authorized to view this page").build());
//    }


//    @GetMapping("/customers/{id}")
//    public ResponseEntity<Customer> getCustomerById(@RequestHeader("Authorization") String headers, @PathVariable Long id) {
//        log.info(headers);
//        var tokenOpt = tokenRepository.findByToken(headers);
//        if (tokenOpt.isEmpty()) {
//            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("Invalid or missing token").build());
//        }
//
//        var user = tokenOpt.get().getCustomer();
//        if (user == null) {
//            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
//        }
//        log.info(user.toString());
//        if ("ADMIN".equals(user.getAppUserRoles().toString()) || user.getId().equals(id)) {
//            var customer = customerService.getCustomerById(id);
//            if (customer == null) {
//                return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("Customer not found").build());
//            }
//            return ResponseEntity.ok(ApiResponse.<Customer>builder()
//                    .customerDTO(customer.getCustomerDTO())
//                    .message("Customer found")
//                    .token(customer.getToken())
//                    .build());
//        }
//        return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
//    }

@ResponseStatus(code = org.springframework.http.HttpStatus.OK)
@GetMapping("/{id}")
public CustomerDTO getCustomerById(@PathVariable Long id) {
     return  customerService.getCustomerById(id);
}
//
//@GetMapping("/update_stripe")
//public void updateStripeForAllUsers() throws StripeException {
//    customerService.updateStripeForAllUsers();
//}
//
//
//    @PutMapping("/customers/{id}")
//    public ResponseEntity<Customer> updateCustomer(@RequestHeader("Authorization") String headers, @RequestBody Customer customer, @PathVariable Long id) throws StripeException {
//
//        var user = tokenRepository.findByToken(headers).get().getCustomer();
//log.info(user.toString());
//        if(user==null){
//            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
//        }
//       if (user.getAppUserRoles().toString().equals("ADMIN") || user.getId()==id){
//           return ResponseEntity.ok(customerService.updateCustomer(customer, id));
//         }
//        return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
//    }
//@PutMapping("/customers/email/{email}")
//public ResponseEntity<Customer> updateCustomer( @RequestBody Customer customer, @PathVariable String email) throws StripeException, IOException {
//   log.info(email);
//    var user = customerService.findByEmail(email).getCustomerDTO();
//
//    if (user.getEmail().equals(email)){
//        return ResponseEntity.ok(customerService.updateCustomer(customer, email));
//    }
//    return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
//
//
//}
//    @DeleteMapping("/customers/{id}")
//    public ResponseEntity<ApiResponse> deleteCustomer(@RequestHeader("Authorization") String headers, @PathVariable Long id) {
//        var user = tokenRepository.findByToken(headers).get().getCustomer();
//        if(user==null){
//            return ResponseEntity.badRequest().body(ApiResponse.<Customer>builder().message("You are not authorized to view this page").build());
//        }
//        if (user.getAppUserRoles().toString().equals("ADMIN") || user.getId()==id){
//            customerService.deleteCustomer(id);
//            return ResponseEntity.ok(ApiResponse.builder()
//                    .message("Customer deleted")
//                    .build());
//        }
//
//        return   ResponseEntity.ok(ApiResponse.builder()
//                .message("Customer not deleted because you don't have permission to delete this customer")
//                .build());
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity processRegister(@RequestHeader("Authorization") String headers, @RequestBody  Customer customer) {
//        var user = tokenRepository.findByToken(headers).get().getCustomer().getAppUserRoles();
//        if(user==null || !user.equals("ADMIN")){
//            return ResponseEntity.badRequest().body("You Don't have authorization to add a new customers");
//        }
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encodedPassword = passwordEncoder.encode(customer.getPassword());
//        customer.setPassword(encodedPassword);
//
//        log.info("New customer object created"+customer);
//        customerService.addCustomer(customer);
//        return ResponseEntity.ok("Customer added successfully: " + customer.toString() + " ");
//    }
//
//
//
//

}
