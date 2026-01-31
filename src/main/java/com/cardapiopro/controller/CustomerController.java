package com.cardapiopro.controller;

import com.cardapiopro.dto.request.CreateCustomerRequest;
import com.cardapiopro.dto.response.CustomerResponse;
import com.cardapiopro.entity.Customer;
import com.cardapiopro.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Cadastrar novo cliente")
    @PostMapping
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = Customer.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .password(request.password()) // Em breve criptografado
                .build();

        Customer saved = customerService.register(customer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomerResponse.fromEntity(saved));
    }
}