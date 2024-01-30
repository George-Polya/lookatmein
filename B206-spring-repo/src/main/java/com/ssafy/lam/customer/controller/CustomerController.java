package com.ssafy.lam.customer.controller;


import com.ssafy.lam.customer.model.service.CustomerService;
import com.ssafy.lam.entity.Customer;
import com.ssafy.lam.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController{

    private final CustomerService customerService;

    private Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    public CustomerController(CustomerService customerService) {
        log.info("CustomerController init");
        this.customerService = customerService;
    }

    @PostMapping("/regist")
    @Operation(summary = "고객 회원가입")
    public ResponseEntity<Void> createCustomer(@RequestBody User customer) {

        log.info("login userId : {}", customer.getUserId());
        customerService.createCustomer(customer);
        return ResponseEntity.ok().build();
    }


}