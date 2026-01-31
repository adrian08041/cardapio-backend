package com.cardapiopro.dto.request;

import com.cardapiopro.entity.Product;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
public record CreateProductRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
        String name,

        @NotBlank(message = "Slug é obrigatório")
        @Size(min = 2, max = 150, message = "Slug deve ter entre 2 e 150 caracteres")
        String slug,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String description,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        BigDecimal price,

        @DecimalMin(value = "0.01", message = "Preço promocional deve ser maior que zero")
        BigDecimal promotionalPrice,

        @Size(max = 500)
        String imageUrl,

        @Min(value = 1, message = "Tempo de preparo deve ser pelo menos 1 minuto")
        Integer preparationTime,

        @Min(value = 1, message = "Serve pelo menos 1 pessoa")
        Integer serves,

        Integer displayOrder,

        @NotNull(message = "Categoria é obrigatória")
        UUID categoryId
) {
    public Product toEntity() {
        return Product.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .price(price)
                .promotionalPrice(promotionalPrice)
                .imageUrl(imageUrl)
                .preparationTime(preparationTime)
                .serves(serves)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .available(true)
                .active(true)
                .build();
    }
}