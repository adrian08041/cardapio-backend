package com.cardapiopro.dto.response;

import com.cardapiopro.entity.Customer;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String phone
) {
    public static CustomerResponse fromEntity(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}