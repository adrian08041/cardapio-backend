package com.cardapiopro.service;

import com.cardapiopro.entity.Customer;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer findById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com email: " + email));
    }

    @Transactional
    public Customer register(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        if (customerRepository.existsByPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Telefone já cadastrado");
        }
        return customerRepository.save(customer);
    }
}